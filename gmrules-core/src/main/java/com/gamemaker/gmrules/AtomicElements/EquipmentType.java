/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import com.gamemaker.gmrules.GameElement;
import java.util.Objects;

/**
 * Represents a single equipment type with automation flags and capacity caps.
 */
public class EquipmentType extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private String key = "";
    private boolean custom = false;
    private boolean hasFittings = false;
    private boolean hasWeaponSlots = false;
    private boolean consumable = false;
    private boolean container = false;
    private boolean tool = false;
    private int powerCap = 0;
    private int massCap = 0;

// *** CONSTRUCTORS ***
    public EquipmentType(String key) {
        super(key);
        this.key = Objects.toString(key, "");
    }

    public EquipmentType(String key, String name, String description) {
        super(name, description);
        this.key = Objects.toString(key, "");
    }

    public EquipmentType(String key, String name, String description, boolean custom) {
        super(name, description);
        this.key = Objects.toString(key, "");
        this.custom = custom;
    }

// *** METHODS ***
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = Objects.toString(key, "");
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean hasFittings() {
        return hasFittings;
    }

    public void setHasFittings(boolean hasFittings) {
        this.hasFittings = hasFittings;
    }

    public boolean hasWeaponSlots() {
        return hasWeaponSlots;
    }

    public void setHasWeaponSlots(boolean hasWeaponSlots) {
        this.hasWeaponSlots = hasWeaponSlots;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public void setConsumable(boolean consumable) {
        this.consumable = consumable;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public boolean isTool() {
        return tool;
    }

    public void setTool(boolean tool) {
        this.tool = tool;
    }

    public int getPowerCap() {
        return powerCap;
    }

    public void setPowerCap(int powerCap) {
        this.powerCap = Math.max(0, powerCap);
    }

    public int getMassCap() {
        return massCap;
    }

    public void setMassCap(int massCap) {
        this.massCap = Math.max(0, massCap);
    }
}
