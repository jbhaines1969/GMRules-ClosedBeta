/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.GameElements.*;

import com.gamemaker.gmrules.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a difficulty system that can be applied to various game elements.
 * Provides flexible difficulty mechanics for Skills, Spells, Equipment crafting, and other RPG elements.
 */
public class DifficultySystem extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Core Difficulty Properties
    private int difficultyClass = 0;                    // Base difficulty number
    private String difficultyScale = "";                // Easy, Medium, Hard, etc.
    private Map<String, Integer> difficultyChart = new LinkedHashMap<>(); // Situation -> difficulty modifier

// *** CONSTRUCTORS ***
    public DifficultySystem(String name) {
        super(name);
    }

    public DifficultySystem(String name, String description) {
        super(name, description);
    }

    public DifficultySystem(String name, String description, int difficultyClass) {
        super(name, description);
        this.difficultyClass = Math.max(0, difficultyClass);
    }

// *** METHODS ***

    public int getDifficultyClass() { return difficultyClass; }
    public void setDifficultyClass(int difficultyClass) { this.difficultyClass = Math.max(0, difficultyClass); }

    public String getDifficultyScale() { return difficultyScale; }
    public void setDifficultyScale(String difficultyScale) { this.difficultyScale = difficultyScale; }

    public Map<String, Integer> getDifficultyChart() { return difficultyChart; }
    public void setDifficultyChart(Map<String, Integer> difficultyChart) {
        this.difficultyChart = difficultyChart;
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * DifficultySystem does not reference other GameElements, so this method primarily
     * ensures data integrity.
     * @return number of orphaned references removed (always 0 for DifficultySystem)
     */
    public int cleanupOrphanedReferences() {
        // DifficultyChart keys are freeform situation descriptions, not references to other GameElements
        // No cleanup needed
        return 0;
    }
}
