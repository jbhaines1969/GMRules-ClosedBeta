/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * File-backed deny list for closed-beta signup and login abuse controls.
 */
public final class BlockedAccessStore {

    // *** MEMBERS ***
    private static final String EMAIL_PREFIX = "email.";
    private static final String IP_PREFIX = "ip.";

    private final Path blockedAccessFile;
    private final Object lock = new Object();

    // *** CONSTRUCTORS ***
    public BlockedAccessStore(WebConfig config) {
        this.blockedAccessFile = Objects.requireNonNullElseGet(config, WebConfig::load).getBlockedAccessFile();
        ensureBlockedAccessDirectory();
    }

    // *** METHODS ***
    public boolean isEmailBlocked(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            return !Objects.toString(loadProperties().getProperty(keyFor(EMAIL_PREFIX, safeEmail, "value")), "").isEmpty();
        }
    }

    public boolean isIpBlocked(String ipAddress) throws IOException {
        String safeIpAddress = normalizeIp(ipAddress);
        if (safeIpAddress.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            return !Objects.toString(loadProperties().getProperty(keyFor(IP_PREFIX, safeIpAddress, "value")), "").isEmpty();
        }
    }

    public void blockEmail(String email, String reason, String createdBy, String sourceAccountId) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        block(EMAIL_PREFIX, safeEmail, reason, createdBy, sourceAccountId);
    }

    public void blockIp(String ipAddress, String reason, String createdBy, String sourceAccountId) throws IOException {
        String safeIpAddress = normalizeIp(ipAddress);
        if (safeIpAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address is required.");
        }
        block(IP_PREFIX, safeIpAddress, reason, createdBy, sourceAccountId);
    }

    public void unblockEmail(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        unblock(EMAIL_PREFIX, safeEmail);
    }

    public void unblockIp(String ipAddress) throws IOException {
        String safeIpAddress = normalizeIp(ipAddress);
        if (safeIpAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address is required.");
        }
        unblock(IP_PREFIX, safeIpAddress);
    }

    public List<BlockEntry> listBlocks() throws IOException {
        synchronized (lock) {
            Properties properties = loadProperties();
            ArrayList<BlockEntry> blocks = new ArrayList<>();
            readBlocks(properties, EMAIL_PREFIX, "email", blocks);
            readBlocks(properties, IP_PREFIX, "ip", blocks);
            blocks.sort((left, right) -> {
                int typeCompare = left.getType().compareTo(right.getType());
                if (typeCompare != 0) {
                    return typeCompare;
                }
                return left.getValue().compareToIgnoreCase(right.getValue());
            });
            return List.copyOf(blocks);
        }
    }

    private void block(String prefix, String value, String reason, String createdBy, String sourceAccountId)
            throws IOException {
        synchronized (lock) {
            Properties properties = loadProperties();
            String id = encode(value);
            Instant now = Instant.now();
            properties.setProperty(key(prefix, id, "value"), value);
            properties.setProperty(key(prefix, id, "reason"), Objects.toString(reason, "").trim());
            properties.setProperty(key(prefix, id, "createdBy"), Objects.toString(createdBy, "").trim());
            properties.setProperty(key(prefix, id, "sourceAccountId"), Objects.toString(sourceAccountId, "").trim());
            properties.setProperty(key(prefix, id, "createdAt"), now.toString());
            saveProperties(properties);
        }
    }

    private void unblock(String prefix, String value) throws IOException {
        synchronized (lock) {
            Properties properties = loadProperties();
            String id = encode(value);
            properties.remove(key(prefix, id, "value"));
            properties.remove(key(prefix, id, "reason"));
            properties.remove(key(prefix, id, "createdBy"));
            properties.remove(key(prefix, id, "sourceAccountId"));
            properties.remove(key(prefix, id, "createdAt"));
            saveProperties(properties);
        }
    }

    private void readBlocks(Properties properties, String prefix, String type, List<BlockEntry> target) {
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith(prefix) || !key.endsWith(".value")) {
                continue;
            }
            String id = key.substring(prefix.length(), key.length() - ".value".length());
            String value = Objects.toString(properties.getProperty(key), "");
            if (value.isEmpty()) {
                continue;
            }
            target.add(new BlockEntry(
                type,
                value,
                Objects.toString(properties.getProperty(key(prefix, id, "reason")), ""),
                Objects.toString(properties.getProperty(key(prefix, id, "createdBy")), ""),
                Objects.toString(properties.getProperty(key(prefix, id, "sourceAccountId")), ""),
                Objects.toString(properties.getProperty(key(prefix, id, "createdAt")), "")
            ));
        }
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        if (!Files.exists(blockedAccessFile)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(blockedAccessFile)) {
            properties.load(input);
        }
        return properties;
    }

    private void saveProperties(Properties properties) throws IOException {
        ensureBlockedAccessDirectory();
        Path tempFile = blockedAccessFile.resolveSibling(blockedAccessFile.getFileName() + ".tmp");
        try (OutputStream output = Files.newOutputStream(tempFile)) {
            properties.store(output, "GMRules blocked access");
        }
        Files.move(tempFile, blockedAccessFile, StandardCopyOption.REPLACE_EXISTING);
    }

    private void ensureBlockedAccessDirectory() {
        Path parent = Objects.requireNonNullElseGet(blockedAccessFile.getParent(), () -> Path.of(""));
        if (!parent.toString().isEmpty() && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ignored) {
                // Directory creation failure is surfaced by save operations.
            }
        }
    }

    private String keyFor(String prefix, String value, String suffix) {
        return key(prefix, encode(value), suffix);
    }

    private String key(String prefix, String id, String suffix) {
        return prefix + Objects.toString(id, "").trim() + "." + Objects.toString(suffix, "").trim();
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(Objects.toString(value, "").getBytes(StandardCharsets.UTF_8));
    }

    private String normalizeEmail(String email) {
        return Objects.toString(email, "").trim().toLowerCase();
    }

    private String normalizeIp(String ipAddress) {
        return Objects.toString(ipAddress, "").trim();
    }

    public static final class BlockEntry {

        // *** MEMBERS ***
        private final String type;
        private final String value;
        private final String reason;
        private final String createdBy;
        private final String sourceAccountId;
        private final String createdAt;

        // *** CONSTRUCTORS ***
        private BlockEntry(
                String type,
                String value,
                String reason,
                String createdBy,
                String sourceAccountId,
                String createdAt
        ) {
            this.type = Objects.toString(type, "");
            this.value = Objects.toString(value, "");
            this.reason = Objects.toString(reason, "");
            this.createdBy = Objects.toString(createdBy, "");
            this.sourceAccountId = Objects.toString(sourceAccountId, "");
            this.createdAt = Objects.toString(createdAt, "");
        }

        // *** METHODS ***
        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getReason() {
            return reason;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public String getSourceAccountId() {
            return sourceAccountId;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
