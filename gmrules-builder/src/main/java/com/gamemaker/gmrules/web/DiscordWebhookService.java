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
 * Sends closed-beta feedback reports to Discord webhooks when configured.
 */
public final class DiscordWebhookService {

    // *** MEMBERS ***
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(8);
    private static final int MAX_CONTENT_LENGTH = 1900;
    private static final int MAX_DETAIL_LENGTH = 700;
    private static final int MAX_STEPS_LENGTH = 450;

    private final WebConfig config;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    // *** CONSTRUCTORS ***
    public DiscordWebhookService(WebConfig config) {
        this.config = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    }

    // *** METHODS ***
    public boolean deliver(FeedbackStore.Report report) throws IOException {
        FeedbackStore.Report safeReport = Objects.requireNonNull(report, "report");
        String webhookUrl = webhookUrlFor(safeReport.getType());
        if (webhookUrl.isEmpty()) {
            return false;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("content", buildContent(safeReport));
        HttpRequest request = HttpRequest.newBuilder(parseWebhookUri(webhookUrl))
            .timeout(REQUEST_TIMEOUT)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                mapper.writeValueAsString(payload),
                StandardCharsets.UTF_8
            ))
            .build();

        HttpResponse<String> response = sendRequest(request);
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Discord webhook returned HTTP " + status + ".");
        }
        return true;
    }

    private String webhookUrlFor(String reportType) {
        String safeType = Objects.toString(reportType, "").trim().toLowerCase();
        if ("bug".equals(safeType)) {
            return config.getDiscordBugWebhookUrl();
        }
        if ("blocker".equals(safeType)) {
            return config.getDiscordBlockerWebhookUrl();
        }
        return config.getDiscordFeedbackWebhookUrl();
    }

    private URI parseWebhookUri(String webhookUrl) throws IOException {
        try {
            return URI.create(webhookUrl);
        } catch (IllegalArgumentException e) {
            throw new IOException("Discord webhook URL is invalid.", e);
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Discord webhook request was interrupted.", e);
        } catch (IOException e) {
            throw new IOException("Discord webhook request failed.", e);
        }
    }

    private String buildContent(FeedbackStore.Report report) {
        StringBuilder content = new StringBuilder();
        content.append("**GMRules beta report**\n");
        appendLine(content, "Report ID", report.getId());
        appendLine(content, "Type", report.getType());
        appendLine(content, "Severity", report.getSeverity());
        appendLine(content, "Title", report.getTitle());
        appendLine(content, "Account", report.getAccountEmail());
        appendLine(content, "Account ID", report.getAccountId());
        appendLine(content, "Route", report.getRoute());
        appendLine(content, "Page", report.getPage());
        appendLine(content, "Stage", report.getStage());
        appendLine(content, "Draft", report.getDraftId());
        appendLine(content, "Client time", report.getClientTimestamp());
        appendLine(content, "Server time", report.getServerTimestamp());
        appendLine(content, "Browser", limit(report.getBrowserUserAgent(), 240));
        content.append("\n**Message**\n");
        content.append(limit(report.getMessage(), MAX_DETAIL_LENGTH)).append("\n");
        if (!report.getSteps().isBlank()) {
            content.append("\n**Steps**\n");
            content.append(limit(report.getSteps(), MAX_STEPS_LENGTH)).append("\n");
        }
        return limit(content.toString(), MAX_CONTENT_LENGTH);
    }

    private void appendLine(StringBuilder builder, String label, String value) {
        String safeValue = limit(Objects.toString(value, "").trim(), 300);
        if (safeValue.isEmpty()) {
            return;
        }
        builder.append("**").append(label).append(":** ").append(safeValue).append("\n");
    }

    private String limit(String value, int maxLength) {
        String safeValue = neutralizeMentions(Objects.toString(value, ""));
        int safeLimit = Math.max(0, maxLength);
        if (safeValue.length() <= safeLimit) {
            return safeValue;
        }
        if (safeLimit <= 3) {
            return safeValue.substring(0, safeLimit);
        }
        return safeValue.substring(0, safeLimit - 3) + "...";
    }

    private String neutralizeMentions(String value) {
        return Objects.toString(value, "")
            .replace("@everyone", "@ everyone")
            .replace("@here", "@ here");
    }
}
