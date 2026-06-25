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
 * Registry for spell schools with hybrid validation and optional strict mode.
 */
public class SpellSchools implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, SpellSchool> schools = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public SpellSchools() {
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

    public List<SpellSchool> getAll() {
        return new ArrayList<>(schools.values());
    }

    public Map<String, SpellSchool> getSchools() {
        return new LinkedHashMap<>(schools);
    }

    public void register(SpellSchool school) {
        String key = normalizeKey(school.getKey());
        if (key.isEmpty()) {
            addWarning("SpellSchool missing key; registration skipped.");
            return;
        }
        school.setKey(key);
        schools.put(key, school);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("SpellSchool missing key; registration skipped.");
            return;
        }
        SpellSchool school = new SpellSchool(normalizedKey, name, description, false);
        schools.put(normalizedKey, school);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return schools.containsKey(normalizedKey);
    }

    public SpellSchool get(String key) {
        String normalizedKey = normalizeKey(key);
        return schools.get(normalizedKey);
    }

    public SpellSchool resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("SpellSchool key is empty; using custom placeholder.");
            return new SpellSchool("", "", "", true);
        }
        SpellSchool existing = schools.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        SpellSchool custom = new SpellSchool(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown SpellSchool '" + normalizedKey + "' treated as custom.");
        if (!strictValidation) {
            schools.put(normalizedKey, custom);
        }
        return custom;
    }

    public boolean remove(String key) {
        String normalizedKey = normalizeKey(key);
        return schools.remove(normalizedKey) != null;
    }

    private String normalizeKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase();
    }

    private void addWarning(String warning) {
        warnings.add(warning);
    }
}
