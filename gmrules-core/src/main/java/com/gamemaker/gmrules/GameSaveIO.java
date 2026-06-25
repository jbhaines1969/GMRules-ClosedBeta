/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.Set;

/**
 * Writes serialized Game files to disk.
 */
public class GameSaveIO {

    // *** MEMBERS ***
    private static final String FILE_EXTENSION = ".gmrf";
    private static final String DEFAULT_FILENAME = "Untitled_Game.gmrf";
    private static final String USER_HOME_DIRECTORY = "GameMakerFiles";

    // *** CONSTRUCTORS ***
    public GameSaveIO() {
    }

    // *** METHODS ***
    /**
     * Writes the game to a JSON file in the user's home directory.
     */
    public Path saveToUserHome(Game game) throws IOException {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        String filename = buildFilename(safeGame);
        Path outputPath = resolveUserHomePath(filename);
        return saveToPath(safeGame, outputPath);
    }

    /**
     * Writes the game to a JSON file in the user's home directory with a custom filename.
     */
    public Path saveToUserHome(Game game, String filename) throws IOException {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        String safeFilename = normalizeFilename(filename);
        Path outputPath = resolveUserHomePath(safeFilename);
        return saveToPath(safeGame, outputPath);
    }

    /**
     * Builds the default save path in the user's home directory for the game.
     */
    public Path getUserHomeSavePath(Game game) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        String filename = buildFilename(safeGame);
        return resolveUserHomePath(filename);
    }

    /**
     * Writes the game to the specified path.
     */
    public Path saveToPath(Game game, Path outputPath) throws IOException {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        Path safePath = Objects.requireNonNullElseGet(outputPath, () -> Paths.get(DEFAULT_FILENAME));
        ensureParentDirectory(safePath);
        safeGame.updateLastModified();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(safePath.toFile()))) {
            outputStream.writeObject(safeGame);
        }
        applyOpenPermissionsIfSupported(safePath);
        return safePath;
    }

    /**
     * Builds a safe filename using the game name.
     */
    public String buildFilename(Game game) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        String rawName = Objects.toString(safeGame.getName(), "").trim();
        String baseName = rawName.isEmpty() ? "Untitled_Game" : rawName;
        return normalizeFilename(baseName);
    }

    private String normalizeFilename(String value) {
        String safe = Objects.toString(value, "");
        String cleaned = safe.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (cleaned.isEmpty()) {
            return DEFAULT_FILENAME;
        }
        if (cleaned.toLowerCase().endsWith(FILE_EXTENSION)) {
            return cleaned;
        }
        return cleaned + FILE_EXTENSION;
    }

    private Path resolveUserHomePath(String filename) {
        String userHome = Objects.toString(System.getProperty("user.home"), "").trim();
        if (userHome.isEmpty()) {
            return Paths.get(filename);
        }
        return Paths.get(userHome, USER_HOME_DIRECTORY, filename);
    }

    private void ensureParentDirectory(Path path) throws IOException {
        Path parent = Objects.requireNonNullElseGet(path.getParent(), () -> Paths.get(""));
        if (!parent.toString().isEmpty() && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private void applyOpenPermissionsIfSupported(Path path) {
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
            Files.setPosixFilePermissions(path, perms);
        } catch (IOException | UnsupportedOperationException | SecurityException ignored) {
            // Filesystem does not support POSIX permissions (e.g., Windows).
            path.toFile().setReadable(true, false);
            path.toFile().setWritable(true, false);
        }
    }
}
