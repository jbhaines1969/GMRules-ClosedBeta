/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory session store for the web UI.
 */
public final class SessionStore {

    // *** MEMBERS ***
    private static final int SESSION_ID_BYTES = 24;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Duration sessionTtl;

    // *** CONSTRUCTORS ***
    public SessionStore(WebConfig config) {
        long minutes = Objects.requireNonNullElseGet(config, WebConfig::load).getSessionMinutes();
        this.sessionTtl = Duration.ofMinutes(minutes);
    }

    // *** METHODS ***
    public Session createSession(String userId, String username, boolean legacyGuest) {
        String id = generateSessionId();
        Session session = new Session(id, userId, username, legacyGuest);
        sessions.put(id, session);
        return session;
    }

    public Session getSession(String id) {
        String safeId = Objects.toString(id, "").trim();
        if (safeId.isEmpty()) {
            return null;
        }
        Session session = sessions.get(safeId);
        if (session == null) {
            return null;
        }
        if (session.isExpired(sessionTtl)) {
            sessions.remove(safeId);
            return null;
        }
        session.touch();
        return session;
    }

    public void invalidate(String id) {
        String safeId = Objects.toString(id, "").trim();
        if (!safeId.isEmpty()) {
            sessions.remove(safeId);
        }
    }

    public void invalidateUser(String userId) {
        String safeUserId = Objects.toString(userId, "").trim();
        if (safeUserId.isEmpty()) {
            return;
        }
        sessions.entrySet().removeIf(entry -> safeUserId.equals(entry.getValue().getUserId()));
    }

    private String generateSessionId() {
        byte[] buffer = new byte[SESSION_ID_BYTES];
        new SecureRandom().nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    public static final class Session {

        // *** MEMBERS ***
        private final String id;
        private final String userId;
        private final String username;
        private final boolean legacyGuest;
        private Instant createdAt = Instant.now();
        private Instant lastAccessAt = Instant.now();
        private String draftId = "";

        // *** CONSTRUCTORS ***
        private Session(String id, String userId, String username, boolean legacyGuest) {
            this.id = id;
            this.userId = Objects.toString(userId, "");
            this.username = Objects.toString(username, "");
            this.legacyGuest = legacyGuest;
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getLastAccessAt() {
            return lastAccessAt;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public boolean isLegacyGuest() {
            return legacyGuest;
        }

        public String getDraftId() {
            return draftId;
        }

        public void setDraftId(String draftId) {
            this.draftId = Objects.toString(draftId, "");
        }

        private void touch() {
            lastAccessAt = Instant.now();
        }

        private boolean isExpired(Duration ttl) {
            Instant expiration = lastAccessAt.plus(ttl);
            return Instant.now().isAfter(expiration);
        }
    }
}
