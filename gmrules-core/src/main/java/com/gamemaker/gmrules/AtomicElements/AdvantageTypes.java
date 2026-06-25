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
 * Registry for advantage types with hybrid validation and optional strict mode.
 */
public class AdvantageTypes implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, AdvantageType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public AdvantageTypes() {
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

    public List<AdvantageType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, AdvantageType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(AdvantageType type) {
        String key = normalizeKey(type.getKey());
        if (key.isEmpty()) {
            addWarning("AdvantageType missing key; registration skipped.");
            return;
        }
        type.setKey(key);
        types.put(key, type);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("AdvantageType missing key; registration skipped.");
            return;
        }
        AdvantageType type = new AdvantageType(normalizedKey, name, description, false);
        types.put(normalizedKey, type);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return types.containsKey(normalizedKey);
    }

    public AdvantageType get(String key) {
        String normalizedKey = normalizeKey(key);
        return types.get(normalizedKey);
    }

    public AdvantageType resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("AdvantageType key is empty; using custom placeholder.");
            return new AdvantageType("", "", "", true);
        }
        AdvantageType existing = types.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        AdvantageType custom = new AdvantageType(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown AdvantageType '" + normalizedKey + "' treated as custom.");
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
