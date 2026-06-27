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
    private static final String HEADER = "audit_id,event,timestamp,email,ip_address,nda_version,nda_scroll_completed_at,nda_accepted_at,user_agent,public_base_url,account_id\n";

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
            String email,
            String ipAddress,
            String userAgent,
            String ndaScrollCompletedAt,
            String ndaAcceptedAt
    ) throws IOException {
        appendAuditRow("nda_accepted", email, ipAddress, userAgent, ndaScrollCompletedAt, ndaAcceptedAt, "");
    }

    public void recordAccountCreated(String email, String ipAddress, String userAgent, String accountId)
            throws IOException {
        appendAuditRow("account_created", email, ipAddress, userAgent, "", "", accountId);
    }

    public String readAuditCsv(String email) throws IOException {
        Path auditFile = auditDirectory.resolve(filenameForEmail(normalizeEmail(email)));
        if (!Files.exists(auditFile)) {
            return "";
        }
        return Files.readString(auditFile, StandardCharsets.UTF_8);
    }

    private void appendAuditRow(
            String event,
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

    private String normalizeEmail(String email) {
        return Objects.toString(email, "").trim().toLowerCase();
    }

    private String csv(String value) {
        String safeValue = Objects.toString(value, "");
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }
}
