/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Configuration holder for the web server.
 */
public final class WebConfig {

    // *** MEMBERS ***
    private static final String HOST_PROPERTY = "gmrules.web.host";
    private static final String PORT_PROPERTY = "gmrules.web.port";
    private static final String THREADS_PROPERTY = "gmrules.web.threads";
    private static final String MAX_UPLOAD_PROPERTY = "gmrules.web.maxUploadBytes";
    private static final String SESSION_MINUTES_PROPERTY = "gmrules.web.sessionMinutes";
    private static final String DRAFTS_DIR_PROPERTY = "gmrules.web.draftsDir";
    private static final String ACCOUNTS_FILE_PROPERTY = "gmrules.web.accountsFile";
    private static final String NDA_AUDIT_DIR_PROPERTY = "gmrules.web.ndaAuditDir";
    private static final String NDA_AUDIT_EMAIL_TO_PROPERTY = "gmrules.ndaAudit.emailTo";
    private static final String ADMIN_EMAILS_PROPERTY = "gmrules.web.adminEmails";
    private static final String BLOCKED_ACCESS_FILE_PROPERTY = "gmrules.web.blockedAccessFile";
    private static final String FEEDBACK_DIR_PROPERTY = "gmrules.web.feedbackDir";
    private static final String PUBLIC_BASE_URL_PROPERTY = "gmrules.web.publicBaseUrl";
    private static final String EMAIL_API_URL_PROPERTY = "gmrules.email.api.url";
    private static final String EMAIL_API_KEY_PROPERTY = "gmrules.email.api.key";
    private static final String EMAIL_FROM_PROPERTY = "gmrules.email.from";
    private static final String DISCORD_FEEDBACK_WEBHOOK_URL_PROPERTY = "gmrules.discord.feedbackWebhookUrl";
    private static final String DISCORD_BUG_WEBHOOK_URL_PROPERTY = "gmrules.discord.bugWebhookUrl";
    private static final String DISCORD_BLOCKER_WEBHOOK_URL_PROPERTY = "gmrules.discord.blockerWebhookUrl";

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_THREADS = 12;
    private static final long DEFAULT_MAX_UPLOAD_BYTES = 512L * 1024 * 1024;
    private static final long DEFAULT_SESSION_MINUTES = 480;
    private static final String DEFAULT_DRAFTS_DIR = "drafts";
    private static final String DEFAULT_ACCOUNTS_FILE = "server-data/accounts.properties";
    private static final String DEFAULT_NDA_AUDIT_DIR = "server-data/nda-audit";
    private static final String DEFAULT_NDA_AUDIT_EMAIL_TO = "";
    private static final String DEFAULT_ADMIN_EMAILS = "";
    private static final String DEFAULT_BLOCKED_ACCESS_FILE = "server-data/blocked-access.properties";
    private static final String DEFAULT_FEEDBACK_DIR = "server-data/feedback";
    private static final String DEFAULT_PUBLIC_BASE_URL = "https://gmrulesbeta.duckdns.org";
    private static final String DEFAULT_EMAIL_API_URL = "https://api.resend.com/emails";
    private static final String DEFAULT_EMAIL_API_KEY = "";
    private static final String DEFAULT_EMAIL_FROM = "";
    private static final String DEFAULT_DISCORD_FEEDBACK_WEBHOOK_URL = "";
    private static final String DEFAULT_DISCORD_BUG_WEBHOOK_URL = "";
    private static final String DEFAULT_DISCORD_BLOCKER_WEBHOOK_URL = "";

    private final String host;
    private final int port;
    private final int threads;
    private final long maxUploadBytes;
    private final long sessionMinutes;
    private final Path draftsDirectory;
    private final Path accountsFile;
    private final Path ndaAuditDirectory;
    private final String ndaAuditEmailTo;
    private final Set<String> adminEmails;
    private final Path blockedAccessFile;
    private final Path feedbackDirectory;
    private final String publicBaseUrl;
    private final String emailApiUrl;
    private final String emailApiKey;
    private final String emailFrom;
    private final String discordFeedbackWebhookUrl;
    private final String discordBugWebhookUrl;
    private final String discordBlockerWebhookUrl;

    // *** CONSTRUCTORS ***
    private WebConfig(
            String host,
            int port,
            int threads,
            long maxUploadBytes,
            long sessionMinutes,
            Path draftsDirectory,
            Path accountsFile,
            Path ndaAuditDirectory,
            String ndaAuditEmailTo,
            Set<String> adminEmails,
            Path blockedAccessFile,
            Path feedbackDirectory,
            String publicBaseUrl,
            String emailApiUrl,
            String emailApiKey,
            String emailFrom,
            String discordFeedbackWebhookUrl,
            String discordBugWebhookUrl,
            String discordBlockerWebhookUrl
    ) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.maxUploadBytes = maxUploadBytes;
        this.sessionMinutes = sessionMinutes;
        this.draftsDirectory = draftsDirectory;
        this.accountsFile = accountsFile;
        this.ndaAuditDirectory = ndaAuditDirectory;
        this.ndaAuditEmailTo = ndaAuditEmailTo;
        this.adminEmails = Set.copyOf(Objects.requireNonNullElseGet(adminEmails, Set::of));
        this.blockedAccessFile = blockedAccessFile;
        this.feedbackDirectory = feedbackDirectory;
        this.publicBaseUrl = publicBaseUrl;
        this.emailApiUrl = emailApiUrl;
        this.emailApiKey = emailApiKey;
        this.emailFrom = emailFrom;
        this.discordFeedbackWebhookUrl = discordFeedbackWebhookUrl;
        this.discordBugWebhookUrl = discordBugWebhookUrl;
        this.discordBlockerWebhookUrl = discordBlockerWebhookUrl;
    }

    // *** METHODS ***
    public static WebConfig load() {
        String host = readString(HOST_PROPERTY, DEFAULT_HOST);
        int port = readInt(PORT_PROPERTY, DEFAULT_PORT);
        int threads = Math.max(2, readInt(THREADS_PROPERTY, DEFAULT_THREADS));
        long maxUploadBytes = readLong(MAX_UPLOAD_PROPERTY, DEFAULT_MAX_UPLOAD_BYTES);
        long sessionMinutes = Math.max(15, readLong(SESSION_MINUTES_PROPERTY, DEFAULT_SESSION_MINUTES));
        Path draftsDirectory = Paths.get(readString(DRAFTS_DIR_PROPERTY, DEFAULT_DRAFTS_DIR));
        Path accountsFile = Paths.get(readString(ACCOUNTS_FILE_PROPERTY, DEFAULT_ACCOUNTS_FILE));
        Path ndaAuditDirectory = Paths.get(readString(NDA_AUDIT_DIR_PROPERTY, DEFAULT_NDA_AUDIT_DIR));
        String ndaAuditEmailTo = readString(NDA_AUDIT_EMAIL_TO_PROPERTY, DEFAULT_NDA_AUDIT_EMAIL_TO);
        Set<String> adminEmails = readEmailSet(ADMIN_EMAILS_PROPERTY, DEFAULT_ADMIN_EMAILS);
        Path blockedAccessFile = Paths.get(readString(BLOCKED_ACCESS_FILE_PROPERTY, DEFAULT_BLOCKED_ACCESS_FILE));
        Path feedbackDirectory = Paths.get(readString(FEEDBACK_DIR_PROPERTY, DEFAULT_FEEDBACK_DIR));
        String publicBaseUrl = readString(PUBLIC_BASE_URL_PROPERTY, DEFAULT_PUBLIC_BASE_URL);
        String emailApiUrl = readString(EMAIL_API_URL_PROPERTY, DEFAULT_EMAIL_API_URL);
        String emailApiKey = readString(EMAIL_API_KEY_PROPERTY, DEFAULT_EMAIL_API_KEY);
        String emailFrom = readString(EMAIL_FROM_PROPERTY, DEFAULT_EMAIL_FROM);
        String discordFeedbackWebhookUrl = readString(
            DISCORD_FEEDBACK_WEBHOOK_URL_PROPERTY,
            DEFAULT_DISCORD_FEEDBACK_WEBHOOK_URL
        );
        String discordBugWebhookUrl = readString(DISCORD_BUG_WEBHOOK_URL_PROPERTY, DEFAULT_DISCORD_BUG_WEBHOOK_URL);
        String discordBlockerWebhookUrl = readString(
            DISCORD_BLOCKER_WEBHOOK_URL_PROPERTY,
            DEFAULT_DISCORD_BLOCKER_WEBHOOK_URL
        );

        return new WebConfig(
            host,
            port,
            threads,
            maxUploadBytes,
            sessionMinutes,
            draftsDirectory,
            accountsFile,
            ndaAuditDirectory,
            ndaAuditEmailTo,
            adminEmails,
            blockedAccessFile,
            feedbackDirectory,
            publicBaseUrl,
            emailApiUrl,
            emailApiKey,
            emailFrom,
            discordFeedbackWebhookUrl,
            discordBugWebhookUrl,
            discordBlockerWebhookUrl
        );
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getThreads() {
        return threads;
    }

    public long getMaxUploadBytes() {
        return maxUploadBytes;
    }

    public long getSessionMinutes() {
        return sessionMinutes;
    }

    public Path getDraftsDirectory() {
        return draftsDirectory;
    }

    public Path getAccountsFile() {
        return accountsFile;
    }

    public Path getNdaAuditDirectory() {
        return ndaAuditDirectory;
    }

    public String getNdaAuditEmailTo() {
        return ndaAuditEmailTo;
    }

    public Set<String> getAdminEmails() {
        return adminEmails;
    }

    public boolean isAdminEmail(String email) {
        return adminEmails.contains(Objects.toString(email, "").trim().toLowerCase());
    }

    public Path getBlockedAccessFile() {
        return blockedAccessFile;
    }

    public Path getFeedbackDirectory() {
        return feedbackDirectory;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public String getEmailApiUrl() {
        return emailApiUrl;
    }

    public String getEmailApiKey() {
        return emailApiKey;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public String getDiscordFeedbackWebhookUrl() {
        return discordFeedbackWebhookUrl;
    }

    public String getDiscordBugWebhookUrl() {
        return discordBugWebhookUrl;
    }

    public String getDiscordBlockerWebhookUrl() {
        return discordBlockerWebhookUrl;
    }

    private static String readString(String property, String fallback) {
        String value = Objects.toString(System.getProperty(property), "").trim();
        if (!value.isEmpty()) {
            return value;
        }
        String env = Objects.toString(System.getenv(propertyToEnv(property)), "").trim();
        if (!env.isEmpty()) {
            return env;
        }
        return fallback;
    }

    private static Set<String> readEmailSet(String property, String fallback) {
        String raw = readString(property, fallback);
        LinkedHashSet<String> values = new LinkedHashSet<>();
        for (String part : raw.split(",")) {
            String safePart = Objects.toString(part, "").trim().toLowerCase();
            if (!safePart.isEmpty()) {
                values.add(safePart);
            }
        }
        return values;
    }

    private static int readInt(String property, int fallback) {
        String raw = Objects.toString(System.getProperty(property), "").trim();
        if (raw.isEmpty()) {
            raw = Objects.toString(System.getenv(propertyToEnv(property)), "").trim();
        }
        if (raw.isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static long readLong(String property, long fallback) {
        String raw = Objects.toString(System.getProperty(property), "").trim();
        if (raw.isEmpty()) {
            raw = Objects.toString(System.getenv(propertyToEnv(property)), "").trim();
        }
        if (raw.isEmpty()) {
            return fallback;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String propertyToEnv(String property) {
        return property.toUpperCase().replace('.', '_');
    }
}
