/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Request and response helper for routing.
 */
public final class RequestContext {

    // *** MEMBERS ***
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final HttpExchange exchange;
    private final ObjectMapper mapper;
    private final WebConfig config;
    private final SessionStore sessionStore;
    private final AccountStore accountStore;
    private final DraftStore draftStore;
    private final String routeTemplate;
    private final String requestId = UUID.randomUUID().toString();
    private final Map<String, String> pathParams;
    private final Map<String, List<String>> queryParams;
    private byte[] body = new byte[0];
    private boolean bodyRead = false;
    private int responseStatus = 0;
    private long responseBodyBytes = 0L;
    private String logErrorCategory = "";

    // *** CONSTRUCTORS ***
    public RequestContext(
            HttpExchange exchange,
            ObjectMapper mapper,
            WebConfig config,
            SessionStore sessionStore,
            AccountStore accountStore,
            DraftStore draftStore,
            String routeTemplate,
            Map<String, String> pathParams
    ) {
        this.exchange = exchange;
        this.mapper = mapper;
        this.config = config;
        this.sessionStore = sessionStore;
        this.accountStore = accountStore;
        this.draftStore = draftStore;
        this.routeTemplate = Objects.toString(routeTemplate, "").trim();
        this.pathParams = pathParams;
        this.queryParams = parseQuery(exchange.getRequestURI().getRawQuery());
    }

    // *** METHODS ***
    public String method() {
        return exchange.getRequestMethod();
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public String getRouteTemplate() {
        return routeTemplate;
    }

    public String getRequestId() {
        return requestId;
    }

    public WebConfig getConfig() {
        return config;
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public DraftStore getDraftStore() {
        return draftStore;
    }

    public String pathParam(String name) {
        return Objects.toString(pathParams.get(name), "");
    }

    public Map<String, List<String>> queryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public List<String> queryParam(String name) {
        return queryParams.getOrDefault(name, List.of());
    }

    public String header(String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return "";
        }
        List<String> values = exchange.getRequestHeaders().getOrDefault(safeName, List.of());
        if (values.isEmpty()) {
            return "";
        }
        return Objects.toString(values.get(0), "");
    }

    public String clientIp() {
        String forwardedFor = header("X-Forwarded-For").trim();
        if (!forwardedFor.isEmpty()) {
            return forwardedFor.split(",", 2)[0].trim();
        }
        String realIp = header("X-Real-IP").trim();
        if (!realIp.isEmpty()) {
            return realIp;
        }
        return Objects.toString(exchange.getRemoteAddress().getAddress().getHostAddress(), "");
    }

    public String userAgent() {
        return header("User-Agent").trim();
    }

    public long requestBodyBytes() {
        if (bodyRead) {
            return body.length;
        }
        String contentLength = header("Content-Length").trim();
        if (contentLength.isEmpty()) {
            return 0L;
        }
        try {
            return Math.max(0L, Long.parseLong(contentLength));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public int responseStatus() {
        return responseStatus;
    }

    public long responseBodyBytes() {
        return responseBodyBytes;
    }

    public String logErrorCategory() {
        return logErrorCategory;
    }

    public void setLogErrorCategory(String logErrorCategory) {
        this.logErrorCategory = Objects.toString(logErrorCategory, "").trim();
    }

    public byte[] readBody() throws IOException {
        return readBody(config.getMaxUploadBytes());
    }

    public byte[] readBody(long maxBytes) throws IOException {
        if (bodyRead) {
            if (body.length > maxBytes) {
                throw new IOException("Request body too large.");
            }
            return body;
        }
        bodyRead = true;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = exchange.getRequestBody();
        byte[] buffer = new byte[8192];
        int read;
        long total = 0;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > maxBytes) {
                throw new IOException("Request body too large.");
            }
            output.write(buffer, 0, read);
        }
        body = output.toByteArray();
        return body;
    }

    public Map<String, Object> readJsonMap() throws IOException {
        byte[] payload = readBody();
        if (payload.length == 0) {
            return new HashMap<>();
        }
        return mapper.readValue(payload, MAP_TYPE);
    }

    public Map<String, Object> readJsonMap(long maxBytes) throws IOException {
        byte[] payload = readBody(maxBytes);
        if (payload.length == 0) {
            return new HashMap<>();
        }
        return mapper.readValue(payload, MAP_TYPE);
    }

    public void json(int status, Object payload) throws IOException {
        byte[] data = mapper.writeValueAsBytes(payload);
        recordResponse(status, data.length);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

    public void text(int status, String message, String contentType) throws IOException {
        byte[] data = Objects.toString(message, "").getBytes(StandardCharsets.UTF_8);
        recordResponse(status, data.length);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(status, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

    public void redirect(String location) throws IOException {
        String safeLocation = Objects.toString(location, "").trim();
        Headers headers = exchange.getResponseHeaders();
        headers.set("Location", safeLocation.isEmpty() ? "/" : safeLocation);
        headers.set("Cache-Control", "no-store");
        recordResponse(303, 0L);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    public void bytes(int status, byte[] data, String contentType, Map<String, String> extraHeaders)
            throws IOException {
        byte[] payload = Objects.requireNonNullElseGet(data, () -> new byte[0]);
        recordResponse(status, payload.length);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
            headers.set(entry.getKey(), entry.getValue());
        }
        exchange.sendResponseHeaders(status, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    public void setCookie(String name, String value, long maxAgeSeconds) {
        String safeName = Objects.toString(name, "").trim();
        String safeValue = Objects.toString(value, "").trim();
        if (safeName.isEmpty()) {
            return;
        }
        StringBuilder cookie = new StringBuilder();
        cookie.append(safeName).append("=").append(safeValue);
        cookie.append("; Path=/; HttpOnly; SameSite=Strict");
        if (maxAgeSeconds >= 0) {
            cookie.append("; Max-Age=").append(maxAgeSeconds);
        }
        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    public void clearCookie(String name) {
        setCookie(name, "", 0);
    }

    public String getCookie(String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return "";
        }
        List<String> cookies = exchange.getRequestHeaders().getOrDefault("Cookie", List.of());
        for (String header : cookies) {
            String[] parts = header.split(";");
            for (String part : parts) {
                String trimmed = part.trim();
                int idx = trimmed.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                if (safeName.equals(key)) {
                    return value;
                }
            }
        }
        return "";
    }

    public String decodeBase64(String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            return "";
        }
        byte[] decoded = Base64.getDecoder().decode(safeValue);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private Map<String, List<String>> parseQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, List<String>> params = new HashMap<>();
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return params;
    }

    private void recordResponse(int status, long bodyBytes) {
        responseStatus = Math.max(0, status);
        responseBodyBytes = Math.max(0L, bodyBytes);
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(Objects.toString(value, ""), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return Objects.toString(value, "");
        }
    }
}
