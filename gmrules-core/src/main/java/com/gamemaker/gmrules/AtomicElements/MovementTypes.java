/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for movement types with hybrid validation and optional strict mode.
 */
public class MovementTypes implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, MovementType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public MovementTypes() {
    }

// *** METHODS ***
    public boolean isStrictValidation() {
        return strictValidation;
    }

    public void setStrictValidation(boolean strictValidation) {
        this.strictValidation = strictValidation;
    }

    public void clearWarnings() {
        warnings.clear();
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public List<MovementType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, MovementType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(MovementType movementType) {
        String key = normalizeKey(movementType.getKey());
        if (key.isEmpty()) {
            addWarning("MovementType missing key; registration skipped.");
            return;
        }
        movementType.setKey(key);
        types.put(key, movementType);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("MovementType missing key; registration skipped.");
            return;
        }
        MovementType movementType = new MovementType(normalizedKey, name, description, false);
        types.put(normalizedKey, movementType);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return types.containsKey(normalizedKey);
    }

    public MovementType get(String key) {
        String normalizedKey = normalizeKey(key);
        return types.get(normalizedKey);
    }

    public MovementType resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("MovementType key is empty; using custom placeholder.");
            return new MovementType("", "", "", true);
        }
        MovementType existing = types.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        MovementType custom = new MovementType(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown MovementType '" + normalizedKey + "' treated as custom.");
        if (!strictValidation) {
            types.put(normalizedKey, custom);
        }
        return custom;
    }

    public boolean remove(String key) {
        String normalizedKey = normalizeKey(key);
        return types.remove(normalizedKey) != null;
    }

    private String normalizeKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase();
    }

    private void addWarning(String warning) {
        warnings.add(warning);
    }
}
