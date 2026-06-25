/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * File-backed account and draft ownership store for the web UI.
 */
public final class AccountStore {

    // *** MEMBERS ***
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9_.-]{3,40}");
    private static final int USER_ID_BYTES = 18;
    private static final int PASSWORD_SALT_BYTES = 18;
    private static final int PASSWORD_ITERATIONS = 120_000;
    private static final int PASSWORD_BITS = 256;
    private static final int MAX_ACCOUNTS = 10;
    private static final int MAX_DRAFTS_PER_ACCOUNT = 2;
    private static final String USER_PREFIX = "users.";
    private static final String DRAFT_PREFIX = "drafts.";

    private final Path accountFile;
    private final Object lock = new Object();
    private final SecureRandom random = new SecureRandom();

    // *** CONSTRUCTORS ***
    public AccountStore(WebConfig config) {
        this.accountFile = Objects.requireNonNullElseGet(config, WebConfig::load).getAccountsFile();
        ensureAccountDirectory();
    }

    // *** METHODS ***
    public Account createAccount(String username, String password) throws IOException {
        String safeUsername = normalizeUsername(username);
        validateUsername(safeUsername);
        validatePassword(password);
        synchronized (lock) {
            Properties properties = loadProperties();
            if (properties.containsKey(userKey(safeUsername, "id"))) {
                throw new IllegalArgumentException("Username already exists.");
            }
            if (countAccounts(properties) >= MAX_ACCOUNTS) {
                throw new IllegalArgumentException("Server account limit reached. This server supports up to 10 accounts.");
            }
            String userId = generateUserId(properties);
            byte[] salt = randomBytes(PASSWORD_SALT_BYTES);
            byte[] hash = hashPassword(password, salt);
            properties.setProperty(userKey(safeUsername, "id"), userId);
            properties.setProperty(userKey(safeUsername, "username"), safeUsername);
            properties.setProperty(userKey(safeUsername, "salt"), encode(salt));
            properties.setProperty(userKey(safeUsername, "hash"), encode(hash));
            properties.setProperty(userKey(safeUsername, "createdAt"), Instant.now().toString());
            saveProperties(properties);
            return new Account(userId, safeUsername, false);
        }
    }

    public Optional<Account> authenticate(String username, String password) throws IOException {
        String safeUsername = normalizeUsername(username);
        if (safeUsername.isEmpty() || Objects.toString(password, "").isEmpty()) {
            return Optional.empty();
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeUsername, "id")), "");
            String saltValue = Objects.toString(properties.getProperty(userKey(safeUsername, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeUsername, "hash")), "");
            if (userId.isEmpty() || saltValue.isEmpty() || hashValue.isEmpty()) {
                return Optional.empty();
            }
            byte[] salt = decode(saltValue);
            byte[] expected = decode(hashValue);
            byte[] actual = hashPassword(password, salt);
            if (!MessageDigest.isEqual(expected, actual)) {
                return Optional.empty();
            }
            return Optional.of(new Account(userId, safeUsername, false));
        }
    }

    public AccountDeletion deleteAccount(String username, String password) throws IOException {
        String safeUsername = normalizeUsername(username);
        String safePassword = Objects.toString(password, "");
        if (safeUsername.isEmpty() || safePassword.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeUsername, "id")), "");
            String saltValue = Objects.toString(properties.getProperty(userKey(safeUsername, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeUsername, "hash")), "");
            if (userId.isEmpty() || saltValue.isEmpty() || hashValue.isEmpty()) {
                throw new IllegalArgumentException("Invalid username or password.");
            }
            byte[] salt = decode(saltValue);
            byte[] expected = decode(hashValue);
            byte[] actual = hashPassword(safePassword, salt);
            if (!MessageDigest.isEqual(expected, actual)) {
                throw new IllegalArgumentException("Invalid username or password.");
            }
            List<String> draftIds = readDraftIds(properties, userId);
            String userPrefix = userKey(safeUsername, "");
            ArrayList<String> accountKeys = new ArrayList<>();
            for (String key : properties.stringPropertyNames()) {
                if (key.startsWith(userPrefix)) {
                    accountKeys.add(key);
                }
            }
            for (String key : accountKeys) {
                properties.remove(key);
            }
            properties.remove(draftKey(userId));
            saveProperties(properties);
            return new AccountDeletion(new Account(userId, safeUsername, false), draftIds);
        }
    }

    public void addDraft(String userId, String draftId) throws IOException {
        String safeUserId = normalizeId(userId);
        String safeDraftId = normalizeId(draftId);
        if (safeUserId.isEmpty() || safeDraftId.isEmpty()) {
            return;
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            Set<String> draftIds = new LinkedHashSet<>(readDraftIds(properties, safeUserId));
            if (!draftIds.contains(safeDraftId) && draftIds.size() >= MAX_DRAFTS_PER_ACCOUNT) {
                throw new IllegalStateException("Each account can save up to two rulesets for this PoC.");
            }
            draftIds.add(safeDraftId);
            properties.setProperty(draftKey(safeUserId), String.join(",", draftIds));
            saveProperties(properties);
        }
    }

    public void removeDraft(String userId, String draftId) throws IOException {
        String safeUserId = normalizeId(userId);
        String safeDraftId = normalizeId(draftId);
        if (safeUserId.isEmpty() || safeDraftId.isEmpty()) {
            return;
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            ArrayList<String> draftIds = new ArrayList<>(readDraftIds(properties, safeUserId));
            draftIds.removeIf(safeDraftId::equals);
            if (draftIds.isEmpty()) {
                properties.remove(draftKey(safeUserId));
            } else {
                properties.setProperty(draftKey(safeUserId), String.join(",", draftIds));
            }
            saveProperties(properties);
        }
    }

    public boolean canAddDraft(String userId) throws IOException {
        String safeUserId = normalizeId(userId);
        if (safeUserId.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            return readDraftIds(loadProperties(), safeUserId).size() < MAX_DRAFTS_PER_ACCOUNT;
        }
    }

    public boolean userOwnsDraft(String userId, String draftId) throws IOException {
        String safeUserId = normalizeId(userId);
        String safeDraftId = normalizeId(draftId);
        if (safeUserId.isEmpty() || safeDraftId.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            return readDraftIds(properties, safeUserId).contains(safeDraftId);
        }
    }

    public List<String> listDraftIds(String userId) throws IOException {
        String safeUserId = normalizeId(userId);
        if (safeUserId.isEmpty()) {
            return List.of();
        }
        synchronized (lock) {
            return readDraftIds(loadProperties(), safeUserId);
        }
    }

    private void validateUsername(String username) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-40 characters using letters, numbers, dots, underscores, or hyphens.");
        }
    }

    private void validatePassword(String password) {
        String safePassword = Objects.toString(password, "");
        if (safePassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
    }

    private List<String> readDraftIds(Properties properties, String userId) {
        String raw = Objects.toString(properties.getProperty(draftKey(userId)), "");
        if (raw.isBlank()) {
            return List.of();
        }
        ArrayList<String> ids = new ArrayList<>();
        String[] parts = raw.split(",");
        for (String part : parts) {
            String safePart = normalizeId(part);
            if (!safePart.isEmpty() && !ids.contains(safePart)) {
                ids.add(safePart);
            }
        }
        return ids;
    }

    private int countAccounts(Properties properties) {
        int count = 0;
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(USER_PREFIX) && key.endsWith(".id")) {
                count++;
            }
        }
        return count;
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        if (!Files.exists(accountFile)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(accountFile)) {
            properties.load(input);
        }
        return properties;
    }

    private void saveProperties(Properties properties) throws IOException {
        ensureAccountDirectory();
        Path tempFile = accountFile.resolveSibling(accountFile.getFileName() + ".tmp");
        try (OutputStream output = Files.newOutputStream(tempFile)) {
            properties.store(output, "GMRules web accounts");
        }
        Files.move(tempFile, accountFile, StandardCopyOption.REPLACE_EXISTING);
    }

    private void ensureAccountDirectory() {
        Path parent = Objects.requireNonNullElseGet(accountFile.getParent(), () -> Path.of(""));
        if (!parent.toString().isEmpty() && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ignored) {
                // Directory creation failure is surfaced by save operations.
            }
        }
    }

    private String generateUserId(Properties properties) {
        String userId = encode(randomBytes(USER_ID_BYTES));
        while (properties.containsValue(userId)) {
            userId = encode(randomBytes(USER_ID_BYTES));
        }
        return userId;
    }

    private byte[] hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(Objects.toString(password, "").toCharArray(), salt, PASSWORD_ITERATIONS, PASSWORD_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash account password.", e);
        }
    }

    private byte[] randomBytes(int length) {
        byte[] bytes = new byte[Math.max(1, length)];
        random.nextBytes(bytes);
        return bytes;
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(Objects.toString(value, ""));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Objects.requireNonNullElseGet(value, () -> new byte[0]));
    }

    private String normalizeUsername(String username) {
        return Objects.toString(username, "").trim();
    }

    private String normalizeId(String value) {
        return Objects.toString(value, "").trim();
    }

    private String userKey(String username, String suffix) {
        return USER_PREFIX + normalizeUsername(username).toLowerCase() + "." + Objects.toString(suffix, "");
    }

    private String draftKey(String userId) {
        return DRAFT_PREFIX + normalizeId(userId);
    }

    public static final class Account {

        // *** MEMBERS ***
        private final String id;
        private final String username;
        private final boolean legacyGuest;

        // *** CONSTRUCTORS ***
        private Account(String id, String username, boolean legacyGuest) {
            this.id = Objects.toString(id, "");
            this.username = Objects.toString(username, "");
            this.legacyGuest = legacyGuest;
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public boolean isLegacyGuest() {
            return legacyGuest;
        }
    }

    public static final class AccountDeletion {

        // *** MEMBERS ***
        private final Account account;
        private final List<String> draftIds;

        // *** CONSTRUCTORS ***
        private AccountDeletion(Account account, List<String> draftIds) {
            this.account = Objects.requireNonNullElseGet(account, () -> new Account("", "", false));
            this.draftIds = List.copyOf(Objects.requireNonNullElseGet(draftIds, List::of));
        }

        // *** METHODS ***
        public Account getAccount() {
            return account;
        }

        public List<String> getDraftIds() {
            return draftIds;
        }
    }
}
