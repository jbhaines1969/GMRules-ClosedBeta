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
 * Registry for equipment types with hybrid validation and optional strict mode.
 */
public class EquipmentTypes implements AtomicRegistry {

// *** MEMBERS ***
    private Map<String, EquipmentType> types = new LinkedHashMap<>();
    private List<String> warnings = new ArrayList<>();
    private boolean strictValidation = false;

// *** CONSTRUCTORS ***
    public EquipmentTypes() {
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

    public List<EquipmentType> getAll() {
        return new ArrayList<>(types.values());
    }

    public Map<String, EquipmentType> getTypes() {
        return new LinkedHashMap<>(types);
    }

    public void register(EquipmentType equipmentType) {
        String key = normalizeKey(equipmentType.getKey());
        if (key.isEmpty()) {
            addWarning("EquipmentType missing key; registration skipped.");
            return;
        }
        equipmentType.setKey(key);
        types.put(key, equipmentType);
    }

    public void register(String key, String name, String description) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("EquipmentType missing key; registration skipped.");
            return;
        }
        EquipmentType equipmentType = new EquipmentType(normalizedKey, name, description, false);
        types.put(normalizedKey, equipmentType);
    }

    public void register(String key, String name, String description, boolean hasFittings, boolean hasWeaponSlots,
                         int powerCap, int massCap) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("EquipmentType missing key; registration skipped.");
            return;
        }
        EquipmentType equipmentType = new EquipmentType(normalizedKey, name, description, false);
        equipmentType.setHasFittings(hasFittings);
        equipmentType.setHasWeaponSlots(hasWeaponSlots);
        equipmentType.setPowerCap(powerCap);
        equipmentType.setMassCap(massCap);
        types.put(normalizedKey, equipmentType);
    }

    public boolean contains(String key) {
        String normalizedKey = normalizeKey(key);
        return types.containsKey(normalizedKey);
    }

    public EquipmentType get(String key) {
        String normalizedKey = normalizeKey(key);
        return types.get(normalizedKey);
    }

    public EquipmentType resolve(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isEmpty()) {
            addWarning("EquipmentType key is empty; using custom placeholder.");
            return new EquipmentType("", "", "", true);
        }
        EquipmentType existing = types.get(normalizedKey);
        if (existing != null) {
            return existing;
        }
        EquipmentType custom = new EquipmentType(normalizedKey, normalizedKey, "", true);
        addWarning("Unknown EquipmentType '" + normalizedKey + "' treated as custom.");
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
