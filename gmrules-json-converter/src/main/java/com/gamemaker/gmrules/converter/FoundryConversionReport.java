package com.gamemaker.gmrules.converter;

import com.gamemaker.gmrules.Game;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Result of one offline Foundry conversion. */
public final class FoundryConversionReport {
    private final Game game;
    private final Map<String, Integer> importedCounts;
    private final int excludedOgl;
    private final int excludedUnknownLicense;

    FoundryConversionReport(Game game, Map<String, Integer> importedCounts, int excludedOgl, int excludedUnknownLicense) {
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.importedCounts = Map.copyOf(new LinkedHashMap<>(Objects.requireNonNullElse(importedCounts, Map.of())));
        this.excludedOgl = Math.max(0, excludedOgl);
        this.excludedUnknownLicense = Math.max(0, excludedUnknownLicense);
    }

    public Game getGame() { return game; }
    public Map<String, Integer> getImportedCounts() { return importedCounts; }
    public int getExcludedOgl() { return excludedOgl; }
    public int getExcludedUnknownLicense() { return excludedUnknownLicense; }
}
