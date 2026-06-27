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
    private static final String PUBLIC_BASE_URL_PROPERTY = "gmrules.web.publicBaseUrl";
    private static final String EMAIL_API_URL_PROPERTY = "gmrules.email.api.url";
    private static final String EMAIL_API_KEY_PROPERTY = "gmrules.email.api.key";
    private static final String EMAIL_FROM_PROPERTY = "gmrules.email.from";

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_THREADS = 12;
    private static final long DEFAULT_MAX_UPLOAD_BYTES = 512L * 1024 * 1024;
    private static final long DEFAULT_SESSION_MINUTES = 480;
    private static final String DEFAULT_DRAFTS_DIR = "drafts";
    private static final String DEFAULT_ACCOUNTS_FILE = "server-data/accounts.properties";
    private static final String DEFAULT_PUBLIC_BASE_URL = "https://gmrulesbeta.duckdns.org";
    private static final String DEFAULT_EMAIL_API_URL = "https://api.resend.com/emails";
    private static final String DEFAULT_EMAIL_API_KEY = "";
    private static final String DEFAULT_EMAIL_FROM = "";

    private final String host;
    private final int port;
    private final int threads;
    private final long maxUploadBytes;
    private final long sessionMinutes;
    private final Path draftsDirectory;
    private final Path accountsFile;
    private final String publicBaseUrl;
    private final String emailApiUrl;
    private final String emailApiKey;
    private final String emailFrom;

    // *** CONSTRUCTORS ***
    private WebConfig(
            String host,
            int port,
            int threads,
            long maxUploadBytes,
            long sessionMinutes,
            Path draftsDirectory,
            Path accountsFile,
            String publicBaseUrl,
            String emailApiUrl,
            String emailApiKey,
            String emailFrom
    ) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.maxUploadBytes = maxUploadBytes;
        this.sessionMinutes = sessionMinutes;
        this.draftsDirectory = draftsDirectory;
        this.accountsFile = accountsFile;
        this.publicBaseUrl = publicBaseUrl;
        this.emailApiUrl = emailApiUrl;
        this.emailApiKey = emailApiKey;
        this.emailFrom = emailFrom;
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
        String publicBaseUrl = readString(PUBLIC_BASE_URL_PROPERTY, DEFAULT_PUBLIC_BASE_URL);
        String emailApiUrl = readString(EMAIL_API_URL_PROPERTY, DEFAULT_EMAIL_API_URL);
        String emailApiKey = readString(EMAIL_API_KEY_PROPERTY, DEFAULT_EMAIL_API_KEY);
        String emailFrom = readString(EMAIL_FROM_PROPERTY, DEFAULT_EMAIL_FROM);

        return new WebConfig(
            host,
            port,
            threads,
            maxUploadBytes,
            sessionMinutes,
            draftsDirectory,
            accountsFile,
            publicBaseUrl,
            emailApiUrl,
            emailApiKey,
            emailFrom
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
