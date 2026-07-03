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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Append-only CSV audit store for beta NDA acceptance and account creation.
 */
public final class NdaAuditStore {

    // *** MEMBERS ***
    public static final String CURRENT_NDA_VERSION = "v1";

    private static final Object WRITE_LOCK = new Object();
    private static final Pattern UNSAFE_FILENAME = Pattern.compile("[^a-zA-Z0-9._-]");
    private static final String HEADER = "audit_id,event,timestamp,full_name,email,ip_address,nda_version,nda_scroll_completed_at,nda_accepted_at,user_agent,public_base_url,account_id\n";
    private static final String LEGACY_HEADER = "audit_id,event,timestamp,email,ip_address,nda_version,nda_scroll_completed_at,nda_accepted_at,user_agent,public_base_url,account_id";

    private final Path auditDirectory;
    private final String publicBaseUrl;

    // *** CONSTRUCTORS ***
    public NdaAuditStore(WebConfig config) {
        WebConfig safeConfig = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.auditDirectory = safeConfig.getNdaAuditDirectory();
        this.publicBaseUrl = safeConfig.getPublicBaseUrl();
    }

    // *** METHODS ***
    public void recordNdaAcceptance(
            String fullName,
            String email,
            String ipAddress,
            String userAgent,
            String ndaScrollCompletedAt,
            String ndaAcceptedAt
    ) throws IOException {
        appendAuditRow("nda_accepted", fullName, email, ipAddress, userAgent, ndaScrollCompletedAt, ndaAcceptedAt, "");
    }

    public void recordAccountCreated(String email, String ipAddress, String userAgent, String accountId)
            throws IOException {
        appendAuditRow("account_created", "", email, ipAddress, userAgent, "", "", accountId);
    }

    public String readAuditCsv(String email) throws IOException {
        String safeEmail = normalizeEmail(email);
        Path auditFile = auditDirectory.resolve(filenameForEmail(safeEmail));
        Path legacyAuditFile = auditDirectory.resolve(legacyFilenameForEmail(safeEmail));
        StringBuilder auditCsv = new StringBuilder();
        if (Files.exists(auditFile)) {
            auditCsv.append(Files.readString(auditFile, StandardCharsets.UTF_8));
        }
        if (!auditFile.equals(legacyAuditFile) && Files.exists(legacyAuditFile)) {
            appendLegacyAuditCsv(auditCsv, legacyAuditFile);
        }
        return auditCsv.toString();
    }

    private void appendAuditRow(
            String event,
            String fullName,
            String email,
            String ipAddress,
            String userAgent,
            String ndaScrollCompletedAt,
            String ndaAcceptedAt,
            String accountId
    )
            throws IOException {
        String safeEmail = normalizeEmail(email);
        Path auditFile = auditDirectory.resolve(filenameForEmail(safeEmail));
        String row = csv(UUID.randomUUID().toString())
            + "," + csv(event)
            + "," + csv(Instant.now().toString())
            + "," + csv(fullName)
            + "," + csv(safeEmail)
            + "," + csv(ipAddress)
            + "," + csv(CURRENT_NDA_VERSION)
            + "," + csv(ndaScrollCompletedAt)
            + "," + csv(ndaAcceptedAt)
            + "," + csv(userAgent)
            + "," + csv(publicBaseUrl)
            + "," + csv(accountId)
            + "\n";
        synchronized (WRITE_LOCK) {
            Files.createDirectories(auditDirectory);
            boolean addHeader = !Files.exists(auditFile) || Files.size(auditFile) == 0;
            if (addHeader) {
                Files.writeString(
                    auditFile,
                    HEADER,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
            }
            Files.writeString(
                auditFile,
                row,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
    }

    private String filenameForEmail(String email) {
        String safeEmail = normalizeEmail(email);
        if (safeEmail.isEmpty()) {
            return "email-unknown.csv";
        }
        return "email-" + sha256Hex(safeEmail) + ".csv";
    }

    private String legacyFilenameForEmail(String email) {
        String localPart = email;
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            localPart = email.substring(0, atIndex);
        }
        String filenameBase = UNSAFE_FILENAME.matcher(localPart).replaceAll("_");
        if (filenameBase.isBlank()) {
            filenameBase = "unknown";
        }
        return filenameBase + ".csv";
    }

    private void appendLegacyAuditCsv(StringBuilder auditCsv, Path legacyAuditFile) throws IOException {
        String legacyCsv = Files.readString(legacyAuditFile, StandardCharsets.UTF_8);
        if (legacyCsv.isBlank()) {
            return;
        }
        if (auditCsv.length() == 0) {
            auditCsv.append(legacyCsv);
            return;
        }
        String[] lines = legacyCsv.split("\\R", -1);
        int startIndex = 0;
        boolean legacyHeader = lines.length > 0 && LEGACY_HEADER.equals(lines[0].trim());
        if (lines.length > 0 && (HEADER.trim().equals(lines[0].trim()) || legacyHeader)) {
            startIndex = 1;
        }
        for (int i = startIndex; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                continue;
            }
            if (auditCsv.length() > 0 && auditCsv.charAt(auditCsv.length() - 1) != '\n') {
                auditCsv.append('\n');
            }
            auditCsv.append(legacyHeader ? addBlankFullNameColumn(lines[i]) : lines[i]).append('\n');
        }
    }

    private String addBlankFullNameColumn(String legacyRow) {
        int insertAt = nthCommaIndex(legacyRow, 3);
        if (insertAt < 0) {
            return legacyRow;
        }
        return legacyRow.substring(0, insertAt) + ",\"\"" + legacyRow.substring(insertAt);
    }

    private int nthCommaIndex(String value, int commaCount) {
        int found = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ',') {
                found++;
                if (found == commaCount) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Objects.toString(value, "").getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    private String normalizeEmail(String email) {
        return Objects.toString(email, "").trim().toLowerCase();
    }

    private String csv(String value) {
        String safeValue = Objects.toString(value, "");
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }
}
