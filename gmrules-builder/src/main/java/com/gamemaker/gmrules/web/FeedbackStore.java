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
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Append-only private local storage for closed-beta feedback reports.
 */
public final class FeedbackStore {

    // *** MEMBERS ***
    private static final Object WRITE_LOCK = new Object();
    private static final Pattern SAFE_TYPE = Pattern.compile("[^a-zA-Z0-9._-]");

    private final Path feedbackDirectory;
    private final ObjectMapper mapper = new ObjectMapper();

    // *** CONSTRUCTORS ***
    public FeedbackStore(WebConfig config) {
        WebConfig safeConfig = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.feedbackDirectory = safeConfig.getFeedbackDirectory();
    }

    // *** METHODS ***
    public void save(Report report) throws IOException {
        Report safeReport = Objects.requireNonNullElseGet(report, Report::empty);
        Path reportFile = feedbackDirectory.resolve(filenameFor(safeReport));
        String line = mapper.writeValueAsString(safeReport) + "\n";
        synchronized (WRITE_LOCK) {
            Files.createDirectories(feedbackDirectory);
            Files.writeString(
                reportFile,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
    }

    private String filenameFor(Report report) {
        String type = SAFE_TYPE.matcher(report.getType()).replaceAll("_");
        if (type.isBlank()) {
            type = "feedback";
        }
        String date = LocalDate.now(ZoneOffset.UTC).toString();
        return type + "-" + date + ".jsonl";
    }

    public static final class Report {

        // *** MEMBERS ***
        private final String id;
        private final String type;
        private final String severity;
        private final String title;
        private final String message;
        private final String steps;
        private final String accountId;
        private final String accountEmail;
        private final boolean legacyGuest;
        private final String route;
        private final String page;
        private final String stage;
        private final String draftId;
        private final String browserUserAgent;
        private final String requestUserAgent;
        private final String ipAddress;
        private final String clientTimestamp;
        private final String serverTimestamp;

        // *** CONSTRUCTORS ***
        public Report(
                String id,
                String type,
                String severity,
                String title,
                String message,
                String steps,
                String accountId,
                String accountEmail,
                boolean legacyGuest,
                String route,
                String page,
                String stage,
                String draftId,
                String browserUserAgent,
                String requestUserAgent,
                String ipAddress,
                String clientTimestamp,
                String serverTimestamp
        ) {
            this.id = Objects.toString(id, "").trim();
            this.type = Objects.toString(type, "").trim();
            this.severity = Objects.toString(severity, "").trim();
            this.title = Objects.toString(title, "").trim();
            this.message = Objects.toString(message, "");
            this.steps = Objects.toString(steps, "");
            this.accountId = Objects.toString(accountId, "").trim();
            this.accountEmail = Objects.toString(accountEmail, "").trim();
            this.legacyGuest = legacyGuest;
            this.route = Objects.toString(route, "").trim();
            this.page = Objects.toString(page, "").trim();
            this.stage = Objects.toString(stage, "").trim();
            this.draftId = Objects.toString(draftId, "").trim();
            this.browserUserAgent = Objects.toString(browserUserAgent, "").trim();
            this.requestUserAgent = Objects.toString(requestUserAgent, "").trim();
            this.ipAddress = Objects.toString(ipAddress, "").trim();
            this.clientTimestamp = Objects.toString(clientTimestamp, "").trim();
            this.serverTimestamp = Objects.toString(serverTimestamp, "").trim();
        }

        private static Report empty() {
            return new Report("", "feedback", "low", "", "", "", "", "", false, "", "", "", "", "", "", "", "", "");
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getSeverity() {
            return severity;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getSteps() {
            return steps;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getAccountEmail() {
            return accountEmail;
        }

        public boolean isLegacyGuest() {
            return legacyGuest;
        }

        public String getRoute() {
            return route;
        }

        public String getPage() {
            return page;
        }

        public String getStage() {
            return stage;
        }

        public String getDraftId() {
            return draftId;
        }

        public String getBrowserUserAgent() {
            return browserUserAgent;
        }

        public String getRequestUserAgent() {
            return requestUserAgent;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getClientTimestamp() {
            return clientTimestamp;
        }

        public String getServerTimestamp() {
            return serverTimestamp;
        }
    }
}
