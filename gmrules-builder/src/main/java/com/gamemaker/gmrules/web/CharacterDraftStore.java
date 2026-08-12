/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Text-backed storage for lightweight web .gmcf character drafts.
 */
public final class CharacterDraftStore {

    // *** MEMBERS ***
    private static final int CHARACTER_DRAFT_ID_BYTES = 18;
    private static final long MAX_CHARACTER_DRAFT_BYTES = 64L * 1024;

    private final Path characterDraftsDirectory;
    private final SecureRandom random = new SecureRandom();

    // *** CONSTRUCTORS ***
    public CharacterDraftStore(WebConfig config) {
        WebConfig safeConfig = Objects.requireNonNullElseGet(config, WebConfig::load);
        this.characterDraftsDirectory = safeConfig.getDraftsDirectory().resolve("characters");
        ensureCharacterDraftDirectory();
    }

    // *** METHODS ***
    public CharacterDraft saveCharacterDraft(String characterDraftId, String text) throws IOException {
        String safeId = normalizeId(characterDraftId);
        if (safeId.isEmpty()) {
            safeId = generateCharacterDraftId();
        }
        String safeText = Objects.toString(text, "");
        if (safeText.getBytes(StandardCharsets.UTF_8).length > MAX_CHARACTER_DRAFT_BYTES) {
            throw new IllegalArgumentException("Character draft is too large.");
        }
        ensureCharacterDraftDirectory();
        Path path = buildCharacterDraftPath(safeId);
        Path tempFile = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(tempFile, safeText, StandardCharsets.UTF_8);
        Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
        return readCharacterDraft(safeId);
    }

    public CharacterDraft readCharacterDraft(String characterDraftId) throws IOException {
        String safeId = normalizeId(characterDraftId);
        if (safeId.isEmpty()) {
            throw new IOException("Missing character draft id.");
        }
        Path path = buildCharacterDraftPath(safeId);
        if (!Files.exists(path)) {
            throw new IOException("Character draft not found.");
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        FileTime modified = Files.getLastModifiedTime(path);
        return new CharacterDraft(safeId, text, modified.toInstant());
    }

    public void deleteCharacterDraft(String characterDraftId) throws IOException {
        String safeId = normalizeId(characterDraftId);
        if (safeId.isEmpty()) {
            return;
        }
        Files.deleteIfExists(buildCharacterDraftPath(safeId));
    }

    public CharacterDraftSummary summarize(String characterDraftId) throws IOException {
        CharacterDraft draft = readCharacterDraft(characterDraftId);
        Map<String, String> fields = parseFields(draft.getText());
        return new CharacterDraftSummary(
            draft.getId(),
            Objects.toString(fields.get("gameDraftId"), ""),
            Objects.toString(fields.get("gameId"), ""),
            Objects.toString(fields.get("gameHash"), ""),
            Objects.toString(fields.get("characterName"), ""),
            Objects.toString(fields.get("gameName"), ""),
            Objects.toString(fields.get("raceId"), ""),
            Objects.toString(fields.get("backgroundId"), ""),
            Objects.toString(fields.get("classId"), ""),
            draft.getLastSaved()
        );
    }

    private Map<String, String> parseFields(String text) {
        Map<String, String> fields = new LinkedHashMap<>();
        String[] lines = Objects.toString(text, "").split("\\R");
        for (int i = 1; i < lines.length; i++) {
            String line = Objects.toString(lines[i], "").trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            fields.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
        }
        return fields;
    }

    private void ensureCharacterDraftDirectory() {
        if (!Files.exists(characterDraftsDirectory)) {
            try {
                Files.createDirectories(characterDraftsDirectory);
            } catch (IOException ignored) {
                // Directory creation failure is surfaced by save operations.
            }
        }
    }

    private String generateCharacterDraftId() {
        String id = encode(randomBytes(CHARACTER_DRAFT_ID_BYTES));
        while (Files.exists(buildCharacterDraftPath(id))) {
            id = encode(randomBytes(CHARACTER_DRAFT_ID_BYTES));
        }
        return id;
    }

    private Path buildCharacterDraftPath(String characterDraftId) {
        String safeId = normalizeId(characterDraftId);
        if (safeId.isEmpty()) {
            safeId = generateCharacterDraftId();
        }
        return characterDraftsDirectory.resolve(safeId + ".gmcf");
    }

    private byte[] randomBytes(int length) {
        byte[] bytes = new byte[Math.max(1, length)];
        random.nextBytes(bytes);
        return bytes;
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Objects.requireNonNullElseGet(value, () -> new byte[0]));
    }

    private String normalizeId(String value) {
        return Objects.toString(value, "").trim();
    }

    public static final class CharacterDraft {

        // *** MEMBERS ***
        private final String id;
        private final String text;
        private final Instant lastSaved;

        // *** CONSTRUCTORS ***
        private CharacterDraft(String id, String text, Instant lastSaved) {
            this.id = Objects.toString(id, "");
            this.text = Objects.toString(text, "");
            this.lastSaved = Objects.requireNonNullElseGet(lastSaved, Instant::now);
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public Instant getLastSaved() {
            return lastSaved;
        }
    }

    public static final class CharacterDraftSummary {

        // *** MEMBERS ***
        private final String id;
        private final String gameDraftId;
        private final String gameId;
        private final String gameHash;
        private final String characterName;
        private final String gameName;
        private final String raceId;
        private final String backgroundId;
        private final String classId;
        private final Instant lastSaved;

        // *** CONSTRUCTORS ***
        private CharacterDraftSummary(
                String id,
                String gameDraftId,
                String gameId,
                String gameHash,
                String characterName,
                String gameName,
                String raceId,
                String backgroundId,
                String classId,
                Instant lastSaved
        ) {
            this.id = Objects.toString(id, "");
            this.gameDraftId = Objects.toString(gameDraftId, "");
            this.gameId = Objects.toString(gameId, "");
            this.gameHash = Objects.toString(gameHash, "");
            this.characterName = Objects.toString(characterName, "");
            this.gameName = Objects.toString(gameName, "");
            this.raceId = Objects.toString(raceId, "");
            this.backgroundId = Objects.toString(backgroundId, "");
            this.classId = Objects.toString(classId, "");
            this.lastSaved = Objects.requireNonNullElseGet(lastSaved, Instant::now);
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public String getGameDraftId() {
            return gameDraftId;
        }

        public String getGameId() {
            return gameId;
        }

        public String getGameHash() {
            return gameHash;
        }

        public String getCharacterName() {
            return characterName;
        }

        public String getGameName() {
            return gameName;
        }

        public String getRaceId() {
            return raceId;
        }

        public String getBackgroundId() {
            return backgroundId;
        }

        public String getClassId() {
            return classId;
        }

        public Instant getLastSaved() {
            return lastSaved;
        }
    }
}
