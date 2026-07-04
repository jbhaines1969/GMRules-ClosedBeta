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
    private static final String CLOSED_BETA_SUBJECT = "GMRules Closed Beta";
    private static final String CLOSED_BETA_CONTENT = "You requested access to the GMRules closed beta and agreed to the NDA. Click the button below to verify your email and create your account.";
    private static final String PASSWORD_RESET_SUBJECT = "GMRules Closed Beta Password Reset";
    private static final String PASSWORD_RESET_CONTENT = "A password reset was requested for your GMRules Closed Beta account. Click the button below to verify the request and choose a new password.";
    private static final String UNREQUESTED_ACCESS_TEXT = "If you did not request access to the GMRules Closed Beta, you can ignore this email.";
    private static final String NDA_AUDIT_SUBJECT_PREFIX = "GMRules NDA Audit - ";
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
    public boolean sendClosedBetaEmail(String recipient, String verificationUrl) throws IOException {
        String safeUrl = Objects.toString(verificationUrl, "").trim();
        String text = CLOSED_BETA_CONTENT + "\n\n" + UNREQUESTED_ACCESS_TEXT + "\n\n" + safeUrl;
        String html = "<p>" + CLOSED_BETA_CONTENT + "</p>"
            + "<p>" + UNREQUESTED_ACCESS_TEXT + "</p>"
            + "<p><a href=\"" + escapeHtml(safeUrl) + "\" "
            + "style=\"display:inline-block;padding:12px 16px;background:#2563eb;color:#ffffff;"
            + "text-decoration:none;border-radius:6px;\">Verify Email and Create Account</a></p>"
            + "<p>If the button does not work, copy and paste this link:</p>"
            + "<p>" + escapeHtml(safeUrl) + "</p>";
        return sendEmail(recipient, CLOSED_BETA_SUBJECT, text, html);
    }

    public boolean sendPasswordResetEmail(String recipient, String resetUrl) throws IOException {
        String safeUrl = Objects.toString(resetUrl, "").trim();
        String text = PASSWORD_RESET_CONTENT + "\n\n" + UNREQUESTED_ACCESS_TEXT + "\n\n" + safeUrl;
        String html = "<p>" + PASSWORD_RESET_CONTENT + "</p>"
            + "<p>" + UNREQUESTED_ACCESS_TEXT + "</p>"
            + "<p><a href=\"" + escapeHtml(safeUrl) + "\" "
            + "style=\"display:inline-block;padding:12px 16px;background:#2563eb;color:#ffffff;"
            + "text-decoration:none;border-radius:6px;\">Verify Password Reset</a></p>"
            + "<p>If the button does not work, copy and paste this link:</p>"
            + "<p>" + escapeHtml(safeUrl) + "</p>";
        return sendEmail(recipient, PASSWORD_RESET_SUBJECT, text, html);
    }

    public boolean sendNdaAuditEmail(String participantEmail, String auditCsv) throws IOException {
        String recipient = config.getNdaAuditEmailTo();
        if (recipient.isEmpty()) {
            return false;
        }
        String safeParticipant = Objects.toString(participantEmail, "").trim();
        String safeCsv = Objects.toString(auditCsv, "");
        String subject = NDA_AUDIT_SUBJECT_PREFIX + safeParticipant;
        String text = "A GMRules beta participant finalized NDA verification.\n\n"
            + "Participant: " + safeParticipant + "\n\n"
            + "Current audit CSV:\n\n"
            + safeCsv;
        String html = "<p>A GMRules beta participant finalized NDA verification.</p>"
            + "<p><strong>Participant:</strong> " + escapeHtml(safeParticipant) + "</p>"
            + "<p><strong>Current audit CSV:</strong></p>"
            + "<pre style=\"white-space:pre-wrap;word-break:break-word;\">"
            + escapeHtml(safeCsv)
            + "</pre>";
        return sendEmail(recipient, subject, text, html);
    }

    private boolean sendEmail(String recipient, String subject, String content, String html) throws IOException {
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
        String safeHtml = Objects.toString(html, "");
        if (safeRecipient.isEmpty()) {
            throw new IOException("Email send failed: recipient is empty.");
        }
        URI apiUri = parseApiUri(apiUrl);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("from", safeSender);
        payload.put("to", safeRecipient);
        payload.put("subject", safeSubject);
        payload.put("text", safeContent);
        payload.put("html", safeHtml);
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

    private String escapeHtml(String value) {
        return Objects.toString(value, "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
