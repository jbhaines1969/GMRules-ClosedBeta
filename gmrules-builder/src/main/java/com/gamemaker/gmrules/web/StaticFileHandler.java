/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Serves static files from the classpath.
 */
public final class StaticFileHandler {

    // *** MEMBERS ***
    private static final String ROOT = "web";
    private final ClassLoader classLoader = StaticFileHandler.class.getClassLoader();

    // *** CONSTRUCTORS ***
    public StaticFileHandler() {
    }

    // *** METHODS ***
    public boolean handle(HttpExchange exchange) throws IOException {
        String path = Objects.toString(exchange.getRequestURI().getPath(), "/");
        if (path.startsWith("/api/")) {
            return false;
        }
        if (path.equals("/")) {
            path = "/index.html";
        }
        if (path.contains("..")) {
            sendText(exchange, 400, "Bad request");
            return true;
        }
        String resourcePath = ROOT + path;
        try (InputStream input = classLoader.getResourceAsStream(resourcePath)) {
            if (input == null) {
                sendText(exchange, 404, "Not found");
                return true;
            }
            byte[] data = input.readAllBytes();
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", resolveContentType(path));
            if (path.endsWith(".html") || path.endsWith(".js") || path.endsWith(".css")) {
                headers.set("Cache-Control", "no-store");
            }
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.close();
            return true;
        }
    }

    private String resolveContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "text/html; charset=utf-8";
    }

    private void sendText(HttpExchange exchange, int status, String message) throws IOException {
        byte[] data = Objects.toString(message, "").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }
}
