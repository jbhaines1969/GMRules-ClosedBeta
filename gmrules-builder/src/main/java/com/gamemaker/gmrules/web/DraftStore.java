/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Stores draft Game objects for the web flow.
 */
public final class DraftStore {

    // *** MEMBERS ***
    private static final int DRAFT_ID_BYTES = 18;

    private final Map<String, Draft> drafts = new ConcurrentHashMap<>();
    private final Path draftsDirectory;
    private final GameIO gameIO = new GameIO();

    // *** CONSTRUCTORS ***
    public DraftStore(WebConfig config) {
        this.draftsDirectory = Objects.requireNonNullElseGet(config, WebConfig::load).getDraftsDirectory();
        ensureDraftDirectory();
    }

    // *** METHODS ***
    public Draft createDraft() throws IOException {
        Game game = new Game("");
        String draftId = generateDraftId();
        Draft draft = new Draft(draftId, game, buildDraftPath(draftId));
        drafts.put(draft.getId(), draft);
        saveDraft(draft);
        return draft;
    }

    public Draft createDraft(Game game) throws IOException {
        Game safeGame = Objects.requireNonNullElse(game, new Game(""));
        String draftId = generateDraftId();
        Draft draft = new Draft(draftId, safeGame, buildDraftPath(draftId));
        drafts.put(draft.getId(), draft);
        saveDraft(draft);
        return draft;
    }

    public Draft importDraft(byte[] payload) throws IOException, ClassNotFoundException {
        byte[] safePayload = Objects.requireNonNullElseGet(payload, () -> new byte[0]);
        Path tempFile = Files.createTempFile("gmrules-upload-", ".gmrf");
        Files.write(tempFile, safePayload);
        Game loaded = gameIO.readGame(tempFile);
        Files.deleteIfExists(tempFile);
        return createDraft(loaded);
    }

    public Draft openDraft(String draftId) throws IOException {
        return getDraft(draftId);
    }

    public <T> T readDraft(String draftId, Function<Game, T> reader) throws IOException {
        Draft draft = getDraft(draftId);
        return draft.withLock(() -> reader.apply(draft.getGame()));
    }

    public void updateDraft(String draftId, DraftUpdater updater) throws IOException {
        Draft draft = getDraft(draftId);
        draft.withLock(() -> {
            updater.update(draft.getGame());
            draft.setLastSaved(Instant.now());
            return null;
        });
        saveDraft(draft);
    }

    public byte[] exportDraft(String draftId) throws IOException {
        Draft draft = getDraft(draftId);
        return draft.withLock(() -> {
            Game exportGame = deserializeGame(serializeGame(draft.getGame()));
            exportGame.getHpMethod().setHpModifierAttributeId("");
            exportGame.getHpMethod().setAllowNegativeAttributeModifier(false);
            return serializeGame(exportGame);
        });
    }

    public void deleteDraft(String draftId) throws IOException {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        Draft draft = drafts.remove(safeId);
        Path path = draft == null ? buildDraftPath(safeId) : draft.getPath();
        Files.deleteIfExists(path);
    }

    public Instant getLastSaved(String draftId) throws IOException {
        Draft draft = getDraft(draftId);
        return draft.withLock(draft::getLastSaved);
    }

    private Draft getDraft(String draftId) throws IOException {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            throw new IOException("Missing draft id.");
        }
        Draft draft = drafts.get(safeId);
        if (draft == null) {
            draft = loadDraft(safeId);
            drafts.put(safeId, draft);
        }
        return draft;
    }

    private Draft loadDraft(String draftId) throws IOException {
        Path path = buildDraftPath(draftId);
        if (!Files.exists(path)) {
            throw new IOException("Draft not found.");
        }
        try {
            Game game = gameIO.readGame(path);
            Draft draft = new Draft(draftId, game, path);
            FileTime modified = Files.getLastModifiedTime(path);
            draft.setLastSaved(modified.toInstant());
            return draft;
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid draft file.", e);
        }
    }

    private void saveDraft(Draft draft) throws IOException {
        Path path = draft.getPath();
        ensureDraftDirectory();
        draft.withLock(() -> {
            byte[] data = serializeGame(draft.getGame());
            Path tempFile = path.resolveSibling(path.getFileName() + ".tmp");
            Files.write(tempFile, data);
            Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return null;
        });
    }

    private byte[] serializeGame(Game game) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(buffer)) {
            outputStream.writeObject(game);
        }
        return buffer.toByteArray();
    }

    private Game deserializeGame(byte[] data) throws IOException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object value = inputStream.readObject();
            if (value instanceof Game game) {
                return game;
            }
            throw new IOException("Invalid draft data.");
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid draft data.", e);
        }
    }

    private void ensureDraftDirectory() {
        Path safeDirectory = Objects.requireNonNullElseGet(draftsDirectory, () -> Paths.get("drafts"));
        if (!Files.exists(safeDirectory)) {
            try {
                Files.createDirectories(safeDirectory);
            } catch (IOException ignored) {
                // Directory creation failure handled by save operations.
            }
        }
    }

    private String generateDraftId() {
        byte[] buffer = new byte[DRAFT_ID_BYTES];
        new SecureRandom().nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    private Path buildDraftPath(String draftId) {
        String safeId = Objects.toString(draftId, "").trim();
        if (safeId.isEmpty()) {
            safeId = generateDraftId();
        }
        return draftsDirectory.resolve(safeId + ".gmrf");
    }

    public interface DraftUpdater {
        void update(Game game);
    }

    public static final class Draft {

        // *** MEMBERS ***
        private final String id;
        private final Game game;
        private final Path path;
        private final ReentrantLock lock = new ReentrantLock();
        private Instant lastSaved = Instant.now();

        // *** CONSTRUCTORS ***
        private Draft(String id, Game game, Path path) {
            this.id = id;
            this.game = game;
            this.path = path;
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public Game getGame() {
            return game;
        }

        public Path getPath() {
            return path;
        }

        public Instant getLastSaved() {
            return lastSaved;
        }

        public void setLastSaved(Instant lastSaved) {
            this.lastSaved = Objects.requireNonNullElseGet(lastSaved, Instant::now);
        }

        private <T> T withLock(java.util.concurrent.Callable<T> action) throws IOException {
            lock.lock();
            try {
                return action.call();
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException("Draft operation failed.", e);
            } finally {
                lock.unlock();
            }
        }

        private void withLock(Runnable action) throws IOException {
            lock.lock();
            try {
                action.run();
            } catch (RuntimeException e) {
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }
}
