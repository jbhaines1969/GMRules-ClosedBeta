/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Append-only JSONL request log for abuse/debug visibility without payload capture.
 */
public final class RequestLogStore {

    // *** MEMBERS ***
    private static final Object WRITE_LOCK = new Object();
    private static final int MAX_FIELD_LENGTH = 512;

    private final Path requestLogDirectory;
    private final ObjectMapper mapper = new ObjectMapper();

    // *** CONSTRUCTORS ***
    public RequestLogStore(WebConfig config) {
        this.requestLogDirectory = Objects.requireNonNullElseGet(config, WebConfig::load).getRequestLogDirectory();
    }

    // *** METHODS ***
    public void save(RequestLogEntry entry) throws IOException {
        RequestLogEntry safeEntry = Objects.requireNonNullElseGet(entry, RequestLogEntry::empty);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("ts", limit(safeEntry.getTimestamp()));
        payload.put("requestId", limit(safeEntry.getRequestId()));
        payload.put("method", limit(safeEntry.getMethod()));
        payload.put("route", limit(safeEntry.getRoute()));
        payload.put("status", safeEntry.getStatus());
        payload.put("durationMs", safeEntry.getDurationMs());
        payload.put("ip", limit(safeEntry.getIpAddress()));
        payload.put("userAgent", limit(safeEntry.getUserAgent()));
        payload.put("accountId", limit(safeEntry.getAccountId()));
        payload.put("accountEmail", limit(safeEntry.getAccountEmail()));
        payload.put("admin", safeEntry.isAdmin());
        payload.put("requestBodyBytes", safeEntry.getRequestBodyBytes());
        payload.put("responseBodyBytes", safeEntry.getResponseBodyBytes());
        payload.put("errorCategory", limit(safeEntry.getErrorCategory()));
        String line = mapper.writeValueAsString(payload) + "\n";
        Path logFile = requestLogDirectory.resolve(LocalDate.now(ZoneOffset.UTC) + ".jsonl");
        synchronized (WRITE_LOCK) {
            Files.createDirectories(requestLogDirectory);
            Files.writeString(
                logFile,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
    }

    private String limit(String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.length() <= MAX_FIELD_LENGTH) {
            return safeValue;
        }
        return safeValue.substring(0, MAX_FIELD_LENGTH);
    }

    public static final class RequestLogEntry {

        // *** MEMBERS ***
        private final String timestamp;
        private final String requestId;
        private final String method;
        private final String route;
        private final int status;
        private final long durationMs;
        private final String ipAddress;
        private final String userAgent;
        private final String accountId;
        private final String accountEmail;
        private final boolean admin;
        private final long requestBodyBytes;
        private final long responseBodyBytes;
        private final String errorCategory;

        // *** CONSTRUCTORS ***
        public RequestLogEntry(
                String timestamp,
                String requestId,
                String method,
                String route,
                int status,
                long durationMs,
                String ipAddress,
                String userAgent,
                String accountId,
                String accountEmail,
                boolean admin,
                long requestBodyBytes,
                long responseBodyBytes,
                String errorCategory
        ) {
            this.timestamp = Objects.toString(timestamp, "");
            this.requestId = Objects.toString(requestId, "");
            this.method = Objects.toString(method, "");
            this.route = Objects.toString(route, "");
            this.status = Math.max(0, status);
            this.durationMs = Math.max(0L, durationMs);
            this.ipAddress = Objects.toString(ipAddress, "");
            this.userAgent = Objects.toString(userAgent, "");
            this.accountId = Objects.toString(accountId, "");
            this.accountEmail = Objects.toString(accountEmail, "");
            this.admin = admin;
            this.requestBodyBytes = Math.max(0L, requestBodyBytes);
            this.responseBodyBytes = Math.max(0L, responseBodyBytes);
            this.errorCategory = Objects.toString(errorCategory, "");
        }

        private static RequestLogEntry empty() {
            return new RequestLogEntry("", "", "", "", 0, 0L, "", "", "", "", false, 0L, 0L, "");
        }

        // *** METHODS ***
        public String getTimestamp() {
            return timestamp;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getMethod() {
            return method;
        }

        public String getRoute() {
            return route;
        }

        public int getStatus() {
            return status;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getAccountEmail() {
            return accountEmail;
        }

        public boolean isAdmin() {
            return admin;
        }

        public long getRequestBodyBytes() {
            return requestBodyBytes;
        }

        public long getResponseBodyBytes() {
            return responseBodyBytes;
        }

        public String getErrorCategory() {
            return errorCategory;
        }
    }
}
