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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * File-backed account and draft ownership store for the web UI.
 */
public final class AccountStore {

    // *** MEMBERS ***
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    private static final int USER_ID_BYTES = 18;
    private static final int PASSWORD_SALT_BYTES = 18;
    private static final int PASSWORD_ITERATIONS = 120_000;
    private static final int PASSWORD_BITS = 256;
    private static final int VERIFICATION_TOKEN_BYTES = 32;
    private static final Duration VERIFICATION_TTL = Duration.ofHours(24);
    private static final int MAX_ACCOUNTS = 10;
    private static final int MAX_DRAFTS_PER_ACCOUNT = 2;
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;
    private static final String USER_PREFIX = "users.";
    private static final String PENDING_PREFIX = "pending.";
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
    public VerificationRequest createVerificationRequest(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        validateEmail(safeEmail);
        synchronized (lock) {
            Properties properties = loadProperties();
            String existingId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (!existingId.isEmpty()) {
                throw new IllegalArgumentException("This email is already registered. Proceed to login.");
            }
            if (hasActivePendingForEmail(properties, safeEmail, Instant.now())) {
                throw new IllegalArgumentException("A verification email is already pending. Check your email and use that link before requesting another.");
            }
            if (countAccounts(properties) >= MAX_ACCOUNTS) {
                throw new IllegalArgumentException("Closed beta account limit reached.");
            }
            removePendingForEmail(properties, safeEmail);
            String token = generateVerificationToken(properties);
            Instant now = Instant.now();
            properties.setProperty(pendingKey(token, "email"), safeEmail);
            properties.setProperty(pendingKey(token, "createdAt"), now.toString());
            properties.setProperty(pendingKey(token, "expiresAt"), now.plus(VERIFICATION_TTL).toString());
            saveProperties(properties);
            return new VerificationRequest(safeEmail, token);
        }
    }

    public Account verifyAccount(String token) throws IOException {
        String safeToken = normalizeId(token);
        if (safeToken.isEmpty()) {
            throw new IllegalArgumentException("Verification token is required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String safeEmail = Objects.toString(properties.getProperty(pendingKey(safeToken, "email")), "");
            if (safeEmail.isEmpty()) {
                throw new IllegalArgumentException("Verification link is invalid or already used.");
            }
            Instant expiresAt = parseInstant(properties.getProperty(pendingKey(safeToken, "expiresAt")));
            if (expiresAt.isBefore(Instant.now())) {
                removePendingToken(properties, safeToken);
                saveProperties(properties);
                throw new IllegalArgumentException("Verification link has expired. Submit the beta form again.");
            }
            String existingId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (!existingId.isEmpty()) {
                removePendingToken(properties, safeToken);
                saveProperties(properties);
                return new Account(existingId, safeEmail, false);
            }
            if (countAccounts(properties) >= MAX_ACCOUNTS) {
                throw new IllegalArgumentException("Closed beta account limit reached.");
            }
            String userId = generateUserId(properties);
            properties.setProperty(userKey(safeEmail, "id"), userId);
            properties.setProperty(userKey(safeEmail, "username"), safeEmail);
            properties.setProperty(userKey(safeEmail, "salt"), "");
            properties.setProperty(userKey(safeEmail, "hash"), "");
            properties.setProperty(userKey(safeEmail, "createdAt"), Instant.now().toString());
            properties.setProperty(userKey(safeEmail, "verifiedAt"), Instant.now().toString());
            properties.setProperty(userKey(safeEmail, "failedLoginAttempts"), "0");
            properties.setProperty(userKey(safeEmail, "loginLockedAt"), "");
            removePendingToken(properties, safeToken);
            saveProperties(properties);
            return new Account(userId, safeEmail, false);
        }
    }

    public AccountLookup lookupAccount(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            return new AccountLookup("", false, false, false);
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            String saltValue = Objects.toString(properties.getProperty(userKey(safeEmail, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeEmail, "hash")), "");
            return new AccountLookup(
                safeEmail,
                !userId.isEmpty(),
                !saltValue.isEmpty() && !hashValue.isEmpty(),
                isLoginLocked(properties, safeEmail)
            );
        }
    }

    public Account setInitialPassword(String email, String password) throws IOException {
        String safeEmail = normalizeEmail(email);
        validateEmail(safeEmail);
        validatePassword(password);
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (userId.isEmpty()) {
                throw new IllegalArgumentException("This email is not on the closed beta list.");
            }
            String saltValue = Objects.toString(properties.getProperty(userKey(safeEmail, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeEmail, "hash")), "");
            if (!saltValue.isEmpty() || !hashValue.isEmpty()) {
                throw new IllegalArgumentException("Password is already set for this account.");
            }
            byte[] salt = randomBytes(PASSWORD_SALT_BYTES);
            byte[] hash = hashPassword(password, salt);
            properties.setProperty(userKey(safeEmail, "salt"), encode(salt));
            properties.setProperty(userKey(safeEmail, "hash"), encode(hash));
            clearLoginFailures(properties, safeEmail);
            saveProperties(properties);
            return new Account(userId, safeEmail, false);
        }
    }

    public AuthenticationResult authenticate(String username, String password) throws IOException {
        String safeUsername = normalizeEmail(username);
        if (safeUsername.isEmpty() || Objects.toString(password, "").isEmpty()) {
            return AuthenticationResult.failed();
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeUsername, "id")), "");
            String saltValue = Objects.toString(properties.getProperty(userKey(safeUsername, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeUsername, "hash")), "");
            if (userId.isEmpty() || saltValue.isEmpty() || hashValue.isEmpty()) {
                return AuthenticationResult.failed();
            }
            Account account = new Account(userId, safeUsername, false);
            if (isLoginLocked(properties, safeUsername)) {
                return AuthenticationResult.locked(account, readFailedLoginAttempts(properties, safeUsername));
            }
            byte[] salt = decode(saltValue);
            byte[] expected = decode(hashValue);
            byte[] actual = hashPassword(password, salt);
            if (!MessageDigest.isEqual(expected, actual)) {
                int failedAttempts = recordFailedLoginAttempt(properties, safeUsername);
                saveProperties(properties);
                if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                    return AuthenticationResult.locked(account, failedAttempts);
                }
                return AuthenticationResult.failed(failedAttempts);
            }
            clearLoginFailures(properties, safeUsername);
            saveProperties(properties);
            return AuthenticationResult.authenticated(account);
        }
    }

    public void recordSuccessfulLogin(String email, String ipAddress) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            return;
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (userId.isEmpty()) {
                return;
            }
            properties.setProperty(userKey(safeEmail, "lastLoginAt"), Instant.now().toString());
            properties.setProperty(userKey(safeEmail, "lastLoginIp"), Objects.toString(ipAddress, "").trim());
            saveProperties(properties);
        }
    }

    public Account lockedAccount(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (userId.isEmpty() || !isLoginLocked(properties, safeEmail)) {
                throw new IllegalArgumentException("This account is not locked.");
            }
            return new Account(userId, safeEmail, false);
        }
    }

    public AccountDeletion deleteAccount(String username, String password) throws IOException {
        String safeUsername = normalizeEmail(username);
        String safePassword = Objects.toString(password, "");
        if (safeUsername.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeUsername, "id")), "");
            String saltValue = Objects.toString(properties.getProperty(userKey(safeUsername, "salt")), "");
            String hashValue = Objects.toString(properties.getProperty(userKey(safeUsername, "hash")), "");
            if (userId.isEmpty()) {
                throw new IllegalArgumentException("Invalid email or password.");
            }
            if (saltValue.isEmpty() || hashValue.isEmpty()) {
                if (!safePassword.isEmpty()) {
                    throw new IllegalArgumentException("This account does not have a password yet. Leave password blank to delete it.");
                }
            } else {
                if (safePassword.isEmpty()) {
                    throw new IllegalArgumentException("Password is required.");
                }
                byte[] salt = decode(saltValue);
                byte[] expected = decode(hashValue);
                byte[] actual = hashPassword(safePassword, salt);
                if (!MessageDigest.isEqual(expected, actual)) {
                    throw new IllegalArgumentException("Invalid email or password.");
                }
            }
            List<String> draftIds = removeAccountProperties(properties, safeUsername, userId);
            saveProperties(properties);
            return new AccountDeletion(new Account(userId, safeUsername, false), draftIds);
        }
    }

    public AccountDeletion deleteAccountAsAdmin(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (userId.isEmpty()) {
                throw new IllegalArgumentException("Account not found.");
            }
            List<String> draftIds = removeAccountProperties(properties, safeEmail, userId);
            saveProperties(properties);
            return new AccountDeletion(new Account(userId, safeEmail, false), draftIds);
        }
    }

    public void unlockAccount(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        synchronized (lock) {
            Properties properties = loadProperties();
            String userId = Objects.toString(properties.getProperty(userKey(safeEmail, "id")), "");
            if (userId.isEmpty()) {
                throw new IllegalArgumentException("Account not found.");
            }
            clearLoginFailures(properties, safeEmail);
            saveProperties(properties);
        }
    }

    public List<AccountSummary> listAccounts() throws IOException {
        synchronized (lock) {
            Properties properties = loadProperties();
            ArrayList<AccountSummary> accounts = new ArrayList<>();
            for (String key : properties.stringPropertyNames()) {
                if (!key.startsWith(USER_PREFIX) || !key.endsWith(".id")) {
                    continue;
                }
                String email = key.substring(USER_PREFIX.length(), key.length() - ".id".length());
                String userId = Objects.toString(properties.getProperty(key), "");
                String saltValue = Objects.toString(properties.getProperty(userKey(email, "salt")), "");
                String hashValue = Objects.toString(properties.getProperty(userKey(email, "hash")), "");
                accounts.add(new AccountSummary(
                    userId,
                    email,
                    !saltValue.isEmpty() && !hashValue.isEmpty(),
                    isLoginLocked(properties, email),
                    readFailedLoginAttempts(properties, email),
                    Objects.toString(properties.getProperty(userKey(email, "createdAt")), ""),
                    Objects.toString(properties.getProperty(userKey(email, "verifiedAt")), ""),
                    Objects.toString(properties.getProperty(userKey(email, "lastLoginAt")), ""),
                    Objects.toString(properties.getProperty(userKey(email, "lastLoginIp")), ""),
                    readDraftIds(properties, userId)
                ));
            }
            accounts.sort((left, right) -> left.getEmail().compareToIgnoreCase(right.getEmail()));
            return List.copyOf(accounts);
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

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
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

    private boolean isLoginLocked(Properties properties, String email) {
        return !Objects.toString(properties.getProperty(userKey(email, "loginLockedAt")), "").isBlank();
    }

    private int readFailedLoginAttempts(Properties properties, String email) {
        String raw = Objects.toString(properties.getProperty(userKey(email, "failedLoginAttempts")), "0").trim();
        try {
            return Math.max(0, Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int recordFailedLoginAttempt(Properties properties, String email) {
        int failedAttempts = readFailedLoginAttempts(properties, email) + 1;
        properties.setProperty(userKey(email, "failedLoginAttempts"), Integer.toString(failedAttempts));
        if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS && !isLoginLocked(properties, email)) {
            properties.setProperty(userKey(email, "loginLockedAt"), Instant.now().toString());
        }
        return failedAttempts;
    }

    private void clearLoginFailures(Properties properties, String email) {
        properties.setProperty(userKey(email, "failedLoginAttempts"), "0");
        properties.setProperty(userKey(email, "loginLockedAt"), "");
    }

    private List<String> removeAccountProperties(Properties properties, String email, String userId) {
        List<String> draftIds = readDraftIds(properties, userId);
        String userPrefix = userKey(email, "");
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
        return draftIds;
    }

    private void removePendingForEmail(Properties properties, String email) {
        String safeEmail = normalizeEmail(email);
        ArrayList<String> pendingTokens = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(PENDING_PREFIX) && key.endsWith(".email")) {
                String pendingEmail = Objects.toString(properties.getProperty(key), "");
                if (safeEmail.equals(pendingEmail)) {
                    String token = key.substring(PENDING_PREFIX.length(), key.length() - ".email".length());
                    pendingTokens.add(token);
                }
            }
        }
        for (String token : pendingTokens) {
            removePendingToken(properties, token);
        }
    }

    private boolean hasActivePendingForEmail(Properties properties, String email, Instant now) {
        String safeEmail = normalizeEmail(email);
        boolean activePending = false;
        ArrayList<String> expiredTokens = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(PENDING_PREFIX) && key.endsWith(".email")) {
                String pendingEmail = Objects.toString(properties.getProperty(key), "");
                if (!safeEmail.equals(pendingEmail)) {
                    continue;
                }
                String token = key.substring(PENDING_PREFIX.length(), key.length() - ".email".length());
                Instant expiresAt = parseInstant(properties.getProperty(pendingKey(token, "expiresAt")));
                if (expiresAt.isAfter(now)) {
                    activePending = true;
                } else {
                    expiredTokens.add(token);
                }
            }
        }
        for (String token : expiredTokens) {
            removePendingToken(properties, token);
        }
        return activePending;
    }

    private void removePendingToken(Properties properties, String token) {
        String safeToken = normalizeId(token);
        properties.remove(pendingKey(safeToken, "email"));
        properties.remove(pendingKey(safeToken, "createdAt"));
        properties.remove(pendingKey(safeToken, "expiresAt"));
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

    private String generateVerificationToken(Properties properties) {
        String token = encode(randomBytes(VERIFICATION_TOKEN_BYTES));
        while (!Objects.toString(properties.getProperty(pendingKey(token, "email")), "").isEmpty()) {
            token = encode(randomBytes(VERIFICATION_TOKEN_BYTES));
        }
        return token;
    }

    private Instant parseInstant(String value) {
        try {
            return Instant.parse(Objects.toString(value, ""));
        } catch (RuntimeException e) {
            return Instant.EPOCH;
        }
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

    private String normalizeEmail(String email) {
        return Objects.toString(email, "").trim().toLowerCase();
    }

    private String normalizeId(String value) {
        return Objects.toString(value, "").trim();
    }

    private String userKey(String username, String suffix) {
        return USER_PREFIX + normalizeEmail(username) + "." + Objects.toString(suffix, "");
    }

    private String pendingKey(String token, String suffix) {
        return PENDING_PREFIX + normalizeId(token) + "." + Objects.toString(suffix, "");
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

    public static final class AuthenticationResult {

        // *** MEMBERS ***
        private final Account account;
        private final boolean authenticated;
        private final boolean locked;
        private final int failedAttempts;

        // *** CONSTRUCTORS ***
        private AuthenticationResult(Account account, boolean authenticated, boolean locked, int failedAttempts) {
            this.account = Objects.requireNonNullElseGet(account, () -> new Account("", "", false));
            this.authenticated = authenticated;
            this.locked = locked;
            this.failedAttempts = Math.max(0, failedAttempts);
        }

        private static AuthenticationResult authenticated(Account account) {
            return new AuthenticationResult(account, true, false, 0);
        }

        private static AuthenticationResult failed() {
            return new AuthenticationResult(new Account("", "", false), false, false, 0);
        }

        private static AuthenticationResult failed(int failedAttempts) {
            return new AuthenticationResult(new Account("", "", false), false, false, failedAttempts);
        }

        private static AuthenticationResult locked(Account account, int failedAttempts) {
            return new AuthenticationResult(account, false, true, Math.max(MAX_FAILED_LOGIN_ATTEMPTS, failedAttempts));
        }

        // *** METHODS ***
        public Account getAccount() {
            return account;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public boolean isLocked() {
            return locked;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public int getRemainingAttempts() {
            return Math.max(0, MAX_FAILED_LOGIN_ATTEMPTS - failedAttempts);
        }
    }

    public static final class VerificationRequest {

        // *** MEMBERS ***
        private final String email;
        private final String token;

        // *** CONSTRUCTORS ***
        private VerificationRequest(String email, String token) {
            this.email = Objects.toString(email, "");
            this.token = Objects.toString(token, "");
        }

        // *** METHODS ***
        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }
    }

    public static final class AccountLookup {

        // *** MEMBERS ***
        private final String email;
        private final boolean exists;
        private final boolean passwordSet;
        private final boolean locked;

        // *** CONSTRUCTORS ***
        private AccountLookup(String email, boolean exists, boolean passwordSet, boolean locked) {
            this.email = Objects.toString(email, "");
            this.exists = exists;
            this.passwordSet = passwordSet;
            this.locked = locked;
        }

        // *** METHODS ***
        public String getEmail() {
            return email;
        }

        public boolean exists() {
            return exists;
        }

        public boolean isPasswordSet() {
            return passwordSet;
        }

        public boolean isLocked() {
            return locked;
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

    public static final class AccountSummary {

        // *** MEMBERS ***
        private final String id;
        private final String email;
        private final boolean passwordSet;
        private final boolean locked;
        private final int failedLoginAttempts;
        private final String createdAt;
        private final String verifiedAt;
        private final String lastLoginAt;
        private final String lastLoginIp;
        private final List<String> draftIds;

        // *** CONSTRUCTORS ***
        private AccountSummary(
                String id,
                String email,
                boolean passwordSet,
                boolean locked,
                int failedLoginAttempts,
                String createdAt,
                String verifiedAt,
                String lastLoginAt,
                String lastLoginIp,
                List<String> draftIds
        ) {
            this.id = Objects.toString(id, "");
            this.email = Objects.toString(email, "");
            this.passwordSet = passwordSet;
            this.locked = locked;
            this.failedLoginAttempts = Math.max(0, failedLoginAttempts);
            this.createdAt = Objects.toString(createdAt, "");
            this.verifiedAt = Objects.toString(verifiedAt, "");
            this.lastLoginAt = Objects.toString(lastLoginAt, "");
            this.lastLoginIp = Objects.toString(lastLoginIp, "");
            this.draftIds = List.copyOf(Objects.requireNonNullElseGet(draftIds, List::of));
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public boolean isPasswordSet() {
            return passwordSet;
        }

        public boolean isLocked() {
            return locked;
        }

        public int getFailedLoginAttempts() {
            return failedLoginAttempts;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getVerifiedAt() {
            return verifiedAt;
        }

        public String getLastLoginAt() {
            return lastLoginAt;
        }

        public String getLastLoginIp() {
            return lastLoginIp;
        }

        public List<String> getDraftIds() {
            return draftIds;
        }
    }
}
