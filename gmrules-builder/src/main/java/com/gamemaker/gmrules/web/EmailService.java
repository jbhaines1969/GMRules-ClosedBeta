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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configurable outbound email hook for beta application notifications.
 */
public final class EmailService {

    // *** MEMBERS ***
    private static final String CLOSED_BETA_SUBJECT = "GMRules Open Beta";
    private static final String CLOSED_BETA_CONTENT = "GMRules closed beta email test.";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final int MAX_ERROR_BODY_LENGTH = 240;

    private final WebConfig config;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    // *** CONSTRUCTORS ***
    public EmailService(WebConfig config) {
        this.config = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    }

    // *** METHODS ***
    public boolean sendClosedBetaEmail(String recipient) throws IOException {
        return sendEmail(recipient, CLOSED_BETA_SUBJECT, CLOSED_BETA_CONTENT);
    }

    private boolean sendEmail(String recipient, String subject, String content) throws IOException {
        String apiUrl = config.getEmailApiUrl();
        if (apiUrl.isEmpty() || config.getEmailApiKey().isEmpty()) {
            return false;
        }
        String safeSender = config.getEmailFrom();
        if (safeSender.isEmpty()) {
            throw new IOException("Email send failed: sender is not configured.");
        }
        String safeRecipient = Objects.toString(recipient, "").trim();
        String safeSubject = Objects.toString(subject, "").trim();
        String safeContent = Objects.toString(content, "");
        if (safeRecipient.isEmpty()) {
            throw new IOException("Email send failed: recipient is empty.");
        }
        URI apiUri = parseApiUri(apiUrl);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("from", safeSender);
        payload.put("to", safeRecipient);
        payload.put("subject", safeSubject);
        payload.put("text", safeContent);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(apiUri)
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                mapper.writeValueAsString(payload),
                StandardCharsets.UTF_8
            ));
        if (!config.getEmailApiKey().isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + config.getEmailApiKey());
        }
        HttpResponse<String> response = sendRequest(requestBuilder.build());
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            String responseBody = safeResponseBody(response.body());
            String message = "Email send failed: Resend returned HTTP " + status + ".";
            if (!responseBody.isEmpty()) {
                message += " " + responseBody;
            }
            System.err.println(message);
            throw new IOException(message);
        }
        return true;
    }

    private URI parseApiUri(String apiUrl) throws IOException {
        try {
            return URI.create(apiUrl);
        } catch (IllegalArgumentException e) {
            throw new IOException("Email send failed: email API URL is invalid.", e);
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Email send failed: email API request was interrupted.", e);
        } catch (IOException e) {
            throw new IOException("Email send failed: could not connect to email API.", e);
        }
    }

    private String safeResponseBody(String body) {
        String safeBody = Objects.toString(body, "").replaceAll("\\s+", " ").trim();
        if (safeBody.length() <= MAX_ERROR_BODY_LENGTH) {
            return safeBody;
        }
        return safeBody.substring(0, MAX_ERROR_BODY_LENGTH) + "...";
    }
}
