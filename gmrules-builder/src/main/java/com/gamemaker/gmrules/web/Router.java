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
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Simple route matcher for the web API.
 */
public final class Router {

    // *** MEMBERS ***
    private final List<Route> routes = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final WebConfig config;
    private final SessionStore sessionStore;
    private final AccountStore accountStore;
    private final DraftStore draftStore;

    // *** CONSTRUCTORS ***
    public Router(WebConfig config, SessionStore sessionStore, AccountStore accountStore, DraftStore draftStore) {
        this.config = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.sessionStore = Objects.requireNonNullElseGet(sessionStore, () -> new SessionStore(this.config));
        this.accountStore = Objects.requireNonNullElseGet(accountStore, () -> new AccountStore(this.config));
        this.draftStore = Objects.requireNonNullElseGet(draftStore, () -> new DraftStore(this.config));
    }

    // *** METHODS ***
    public void add(String method, String template, RouteHandler handler) {
        routes.add(new Route(method, template, handler));
    }

    public boolean handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = normalizePath(exchange.getRequestURI().getPath());
        for (Route route : routes) {
            if (!route.matchesMethod(method)) {
                continue;
            }
            Map<String, String> params = route.match(path);
            if (params == null) {
                continue;
            }
            RequestContext context = new RequestContext(exchange, mapper, config, sessionStore, accountStore, draftStore, params);
            try {
                if (!authorizeDraftRoute(route, context, params)) {
                    return true;
                }
                route.handle(context);
            } catch (IOException e) {
                context.json(500, Map.of("error", "Server error"));
            }
            return true;
        }
        RequestContext context = new RequestContext(exchange, mapper, config, sessionStore, accountStore, draftStore, new HashMap<>());
        context.json(404, Map.of("error", "Not found"));
        return true;
    }

    private String normalizePath(String raw) {
        String safe = Objects.toString(raw, "/");
        if (safe.length() > 1 && safe.endsWith("/")) {
            return safe.substring(0, safe.length() - 1);
        }
        return safe;
    }

    private boolean authorizeDraftRoute(Route route, RequestContext context, Map<String, String> params) throws IOException {
        if (!route.isDraftScoped()) {
            return true;
        }
        String draftId = Objects.toString(params.get("id"), "").trim();
        if (draftId.isEmpty()) {
            context.json(400, Map.of("error", "Missing draft id"));
            return false;
        }
        SessionStore.Session session = sessionStore.getSession(resolveToken(context));
        if (session == null) {
            context.json(401, Map.of("error", "Unauthorized"));
            return false;
        }
        if (session.isLegacyGuest()) {
            if (draftId.equals(session.getDraftId())) {
                return true;
            }
            context.json(403, Map.of("error", "Draft access denied"));
            return false;
        }
        if (!accountStore.userOwnsDraft(session.getUserId(), draftId)) {
            context.json(403, Map.of("error", "Draft access denied"));
            return false;
        }
        return true;
    }

    private String resolveToken(RequestContext context) {
        String auth = context.header("Authorization");
        if (auth.isEmpty()) {
            return "";
        }
        String trimmed = auth.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    public interface RouteHandler {
        void handle(RequestContext context) throws IOException;
    }

    private static final class Route {

        // *** MEMBERS ***
        private final String method;
        private final String template;
        private final RouteHandler handler;
        private final String[] segments;
        private final boolean draftScoped;

        // *** CONSTRUCTORS ***
        private Route(String method, String template, RouteHandler handler) {
            this.method = Objects.toString(method, "").trim().toUpperCase();
            this.template = normalizeTemplate(template);
            this.handler = handler;
            this.segments = splitSegments(this.template);
            this.draftScoped = this.template.startsWith("/api/drafts/{id}");
        }

        // *** METHODS ***
        private boolean matchesMethod(String method) {
            return this.method.equalsIgnoreCase(Objects.toString(method, ""));
        }

        private Map<String, String> match(String path) {
            String[] pathSegments = splitSegments(path);
            if (pathSegments.length != segments.length) {
                return null;
            }
            Map<String, String> params = new HashMap<>();
            for (int i = 0; i < segments.length; i++) {
                String templatePart = segments[i];
                String pathPart = pathSegments[i];
                if (isParam(templatePart)) {
                    params.put(paramName(templatePart), pathPart);
                    continue;
                }
                if (!templatePart.equals(pathPart)) {
                    return null;
                }
            }
            return params;
        }

        private void handle(RequestContext context) throws IOException {
            handler.handle(context);
        }

        private boolean isDraftScoped() {
            return draftScoped;
        }

        private static boolean isParam(String segment) {
            return segment.startsWith("{") && segment.endsWith("}") && segment.length() > 2;
        }

        private static String paramName(String segment) {
            return segment.substring(1, segment.length() - 1);
        }

        private static String normalizeTemplate(String template) {
            String safe = Objects.toString(template, "/");
            if (!safe.startsWith("/")) {
                safe = "/" + safe;
            }
            if (safe.length() > 1 && safe.endsWith("/")) {
                safe = safe.substring(0, safe.length() - 1);
            }
            return safe;
        }

        private static String[] splitSegments(String path) {
            String safe = Objects.toString(path, "/");
            if (safe.startsWith("/")) {
                safe = safe.substring(1);
            }
            if (safe.isEmpty()) {
                return new String[0];
            }
            return safe.split("/");
        }
    }
}
