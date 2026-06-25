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
 * Registry for attribute types with hybrid validation and optional strict mode.
 */
public class AttributeTypes implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, AttributeType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public AttributeTypes() {
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

    public List<AttributeType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, AttributeType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(AttributeType attributeType) {
        String key = normalizeKey(attributeType.getKey());
        if (key.isEmpty()) {
            addWarning("AttributeType missing key; registration skipped.");
            return;
        }
        attributeType.setKey(key);
        types.put(key, attributeType);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("AttributeType missing key; registration skipped.");
            return;
        }
        AttributeType attributeType = new AttributeType(normalizedKey, name, description, false);
        types.put(normalizedKey, attributeType);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return types.containsKey(normalizedKey);
    }

    public AttributeType get(String key) {
        String normalizedKey = normalizeKey(key);
        return types.get(normalizedKey);
    }

    public AttributeType resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("AttributeType key is empty; using custom placeholder.");
            return new AttributeType("", "", "", true);
        }
        AttributeType existing = types.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        AttributeType custom = new AttributeType(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown AttributeType '" + normalizedKey + "' treated as custom.");
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
