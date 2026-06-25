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
 * Registry for flaw types with hybrid validation and optional strict mode.
 */
public class FlawTypes implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, FlawType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public FlawTypes() {
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

    public List<FlawType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, FlawType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(FlawType type) {
        String key = normalizeKey(type.getKey());
        if (key.isEmpty()) {
            addWarning("FlawType missing key; registration skipped.");
            return;
        }
        type.setKey(key);
        types.put(key, type);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("FlawType missing key; registration skipped.");
            return;
        }
        FlawType type = new FlawType(normalizedKey, name, description, false);
        types.put(normalizedKey, type);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return types.containsKey(normalizedKey);
    }

    public FlawType get(String key) {
        String normalizedKey = normalizeKey(key);
        return types.get(normalizedKey);
    }

    public FlawType resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("FlawType key is empty; using custom placeholder.");
            return new FlawType("", "", "", true);
        }
        FlawType existing = types.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        FlawType custom = new FlawType(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown FlawType '" + normalizedKey + "' treated as custom.");
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
