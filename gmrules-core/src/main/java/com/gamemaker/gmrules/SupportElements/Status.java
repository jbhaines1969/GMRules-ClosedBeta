/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.SupportElements;

import com.gamemaker.gmrules.ArrayHandler;
import com.gamemaker.gmrules.GameElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a reusable status/condition definition.
 */
public class Status extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Status(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Status(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("effectTypeKeys", new ArrayList<String>());
    }

    public ArrayList<String> getEffectTypeKeys() {
        return ensureEffectTypeKeys();
    }

    public void setEffectTypeKeys(java.util.Collection<String> effectTypeKeys) {
        ensureEffectTypeKeys();
        arrayHandler.replaceArray("effectTypeKeys", normalizeEffectTypeKeys(effectTypeKeys));
    }

    public void addEffectTypeKey(String effectTypeKey) {
        ArrayList<String> keys = ensureEffectTypeKeys();
        String safeKey = Objects.toString(effectTypeKey, "").trim();
        if (safeKey.isEmpty()) {
            return;
        }
        if (!keys.contains(safeKey)) {
            keys.add(safeKey);
        }
    }

    public boolean removeEffectTypeKey(String effectTypeKey) {
        ArrayList<String> keys = ensureEffectTypeKeys();
        String safeKey = Objects.toString(effectTypeKey, "").trim();
        if (safeKey.isEmpty()) {
            return false;
        }
        return keys.remove(safeKey);
    }

    /**
     * Cleans up orphaned references that no longer exist in the game.
     * @param validEffectTypeKeys Set of valid EffectType keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validEffectTypeKeys) {
        int removedCount = 0;

        ArrayList<String> effectTypeKeys = ensureEffectTypeKeys();
        Iterator<String> typeIter = effectTypeKeys.iterator();
        while (typeIter.hasNext()) {
            String key = normalizeEffectTypeKey(typeIter.next());
            boolean exists = validEffectTypeKeys.contains(key);
            if (!exists) {
                typeIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    private ArrayList<String> ensureEffectTypeKeys() {
        ArrayList<String> keys = arrayHandler.getObjectArray("effectTypeKeys");
        if (keys == null) {
            keys = new ArrayList<>();
            arrayHandler.putArray("effectTypeKeys", keys);
        }
        return keys;
    }

    private ArrayList<String> normalizeEffectTypeKeys(java.util.Collection<String> keys) {
        java.util.LinkedHashSet<String> deduped = new java.util.LinkedHashSet<>();
        java.util.Collection<String> safeKeys = Objects.requireNonNullElse(keys, java.util.List.of());
        for (String key : safeKeys) {
            String safeKey = Objects.toString(key, "").trim();
            if (!safeKey.isEmpty()) {
                deduped.add(safeKey);
            }
        }
        return new ArrayList<>(deduped);
    }

    private String normalizeEffectTypeKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase();
    }
}
