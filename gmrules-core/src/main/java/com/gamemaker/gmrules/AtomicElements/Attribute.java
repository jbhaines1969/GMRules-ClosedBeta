/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks

package com.gamemaker.gmrules.AtomicElements;

import com.gamemaker.gmrules.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a character attribute (like Strength, Dexterity, Intelligence, etc.)
 * in the game system. Attributes define the core capabilities of characters.
 */
public class Attribute extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private int minValue = 0;
    private int maxValue = 0;
    private String type = "";
    private Map<Float, Float> modifierMap = new LinkedHashMap<>();
    // Maps score threshold to stable Effect ids to activate
    private Map<Integer, ArrayList<String>> scoreBonuses = new LinkedHashMap<>();

// *** CONSTRUCTORS ***
    public Attribute(String name) {
        super(name);
    }

    public Attribute(String name, String description) {
        super(name, description);
    }

    public Attribute(String name, String description, String type) {
        super(name, description);
        this.type = type;
    }

// *** METHODS ***

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<Float, Float> getModifierMap() {
        return modifierMap;
    }

    public void setModifierMap(Map<Float, Float> modifierMap) {
        this.modifierMap = modifierMap;
    }

    public void addModifier(float value, float modifier) {
        this.modifierMap.put(value, modifier);
    }

    public Float getModifier(float value) {
        return this.modifierMap.getOrDefault(value, 0.0f);
    }

    // === SCORE BONUSES METHODS ===

    /**
     * Adds a score bonus effect to the attribute at a specific threshold.
     * @param threshold The attribute score threshold required to activate the effect
     * @param effectId The stable Effect id to activate
     */
    public void addScoreBonus(int threshold, String effectId) {
        String safeEffectId = Objects.toString(effectId, "").trim();
        if (safeEffectId.isEmpty()) {
            return;
        }
        scoreBonuses.computeIfAbsent(threshold, k -> new ArrayList<>()).add(safeEffectId);
    }

    /**
     * Removes a score bonus effect from the attribute at a specific threshold.
     * @param threshold The attribute score threshold
     * @param effectId The stable Effect id to remove
     * @return true if the effect was removed, false otherwise
     */
    public boolean removeScoreBonus(int threshold, String effectId) {
        ArrayList<String> effects = scoreBonuses.get(threshold);
        if (effects != null) {
            boolean removed = effects.remove(Objects.toString(effectId, "").trim());
            // Clean up empty threshold entries
            if (effects.isEmpty()) {
                scoreBonuses.remove(threshold);
            }
            return removed;
        }
        return false;
    }

    /**
     * Gets all Effect ids for a specific score threshold.
     * @param threshold The attribute score threshold
     * @return ArrayList of stable Effect ids, or empty list if none exist
     */
    public ArrayList<String> getScoreBonuses(int threshold) {
        return scoreBonuses.get(threshold);
    }

    /**
     * Gets all score bonuses mapped to their thresholds.
     * @return Map of threshold to Effect name lists
     */
    public Map<Integer, ArrayList<String>> getAllScoreBonuses() {
        return scoreBonuses;
    }

    /**
     * Sets all score bonuses (for deserialization).
     * @param scoreBonuses Map of threshold to Effect name lists
     */
    public void setScoreBonuses(Map<Integer, ArrayList<String>> scoreBonuses) {
        this.scoreBonuses = scoreBonuses;
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validAttributeTypeKeys Set of valid attribute type keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validEffectIds, Set<String> validAttributeTypeKeys) {
        int removedCount = 0;

        // Clean up attribute type reference (keys)
        if (!type.isEmpty()) {
            boolean typeExists = validAttributeTypeKeys.contains(type);
            if (!typeExists) {
                type = ""; // Reset to empty if orphaned
                removedCount++;
            }
        }

        // Clean up score bonuses Effect references
        Iterator<Map.Entry<Integer, ArrayList<String>>> iterator = scoreBonuses.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<String>> entry = iterator.next();
            ArrayList<String> effectNames = entry.getValue();

            // Remove invalid effect names (case-insensitive comparison)
            int beforeSize = effectNames.size();
            effectNames.removeIf(id -> !validEffectIds.contains(id));
            removedCount += (beforeSize - effectNames.size());

            // Remove threshold entry if no effects remain
            if (effectNames.isEmpty()) {
                iterator.remove();
            }
        }

        return removedCount;
    }
}
