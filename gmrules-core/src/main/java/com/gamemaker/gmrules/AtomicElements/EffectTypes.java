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
 * Registry for effect types with optional strict validation.
 */
public class EffectTypes implements AtomicRegistry {

// *** MEMBERS ***
    private static final List<String> DEFAULT_TYPES = List.of(
        "Damage",
        "Resistance",
        "Immunity",
        "Armor",
        "Speed",
        "Movement",
        "Apply Status"
    );

    private Map<String, EffectType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public EffectTypes() {
        registerDefaults();
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

    public List<EffectType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, EffectType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(EffectType effectType) {
        String key = normalizeKey(effectType.getName());
        if (key.isEmpty()) {
            addWarning("EffectType missing name; registration skipped.");
            return;
        }
        types.put(key, effectType);
    }

    public void register(String name, String description) {
        String key = normalizeKey(name);
        if (key.isEmpty()) {
            addWarning("EffectType missing name; registration skipped.");
            return;
        }
        EffectType effectType = new EffectType(name, description);
        types.put(key, effectType);
    }

    public boolean contains(String name) {
        String key = normalizeKey(name);
        return types.containsKey(key);
    }

    public EffectType get(String name) {
        String key = normalizeKey(name);
        return types.get(key);
    }

    public EffectType resolve(String name) {
        String key = normalizeKey(name);
        if (key.isEmpty()) {
            addWarning("EffectType name is empty; using placeholder.");
            return new EffectType("");
        }
        EffectType existing = types.get(key);
        if (existing != null) {
            return existing;
        }
        EffectType custom = new EffectType(name, "");
        addWarning("Unknown EffectType '" + key + "' treated as custom.");
        if (!strictValidation) {
            types.put(key, custom);
        }
        return custom;
    }

    public boolean remove(String name) {
        String key = normalizeKey(name);
        return types.remove(key) != null;
    }

    private void registerDefaults() {
        for (String name : DEFAULT_TYPES) {
            register(name, "");
        }
    }

    private String normalizeKey(String name) {
        return Objects.toString(name, "").trim().toLowerCase();
    }

    private void addWarning(String warning) {
        warnings.add(warning);
    }
}
