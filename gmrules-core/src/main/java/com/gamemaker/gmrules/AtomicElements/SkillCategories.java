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
 * Registry for skill categories with hybrid validation and optional strict mode.
 */
public class SkillCategories implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, SkillCategory> categories = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public SkillCategories() {
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

    public List<SkillCategory> getAll() {
        return new ArrayList<>(categories.values());
    }

    public Map<String, SkillCategory> getCategories() {
        return new LinkedHashMap<>(categories);
    }

    public void register(SkillCategory category) {
        String key = normalizeKey(category.getKey());
        if (key.isEmpty()) {
            addWarning("SkillCategory missing key; registration skipped.");
            return;
        }
        category.setKey(key);
        categories.put(key, category);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("SkillCategory missing key; registration skipped.");
            return;
        }
        SkillCategory category = new SkillCategory(normalizedKey, name, description, false);
        categories.put(normalizedKey, category);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return categories.containsKey(normalizedKey);
    }

    public SkillCategory get(String key) {
        String normalizedKey = normalizeKey(key);
        return categories.get(normalizedKey);
    }

    public SkillCategory resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("SkillCategory key is empty; using custom placeholder.");
            return new SkillCategory("", "", "", true);
        }
        SkillCategory existing = categories.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        SkillCategory custom = new SkillCategory(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown SkillCategory '" + normalizedKey + "' treated as custom.");
        if (!strictValidation) {
            categories.put(normalizedKey, custom);
        }
        return custom;
    }

    public boolean remove(String key) {
        String normalizedKey = normalizeKey(key);
        return categories.remove(normalizedKey) != null;
    }

    private String normalizeKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase();
    }

    private void addWarning(String warning) {
        warnings.add(warning);
    }
}
