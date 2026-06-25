/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.GameElement;

/**
 * Simple armor class method flags for hybrid AC systems.
 */
public class ArmorClassMethod extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private int baseArmorClass = 10;
    private String acAbilityAttributeId = "";
    private boolean gearBased = true;
    private boolean basePlusModifier = true;
    private boolean abilityBased = true;

// *** CONSTRUCTORS ***
    public ArmorClassMethod(String name) {
        super(name);
    }

    public ArmorClassMethod(String name, String description) {
        super(name, description);
    }

// *** METHODS ***
    public int getBaseArmorClass() { return Math.max(0, baseArmorClass); }
    public void setBaseArmorClass(int baseArmorClass) { this.baseArmorClass = Math.max(0, baseArmorClass); }

    public String getAcAbilityAttributeId() { return acAbilityAttributeId; }
    public void setAcAbilityAttributeId(String acAbilityAttributeId) {
        this.acAbilityAttributeId = java.util.Objects.toString(acAbilityAttributeId, "").trim();
    }

    public boolean isGearBased() { return gearBased; }
    public void setGearBased(boolean gearBased) { this.gearBased = gearBased; }

    public boolean isBasePlusModifier() { return basePlusModifier; }
    public void setBasePlusModifier(boolean basePlusModifier) { this.basePlusModifier = basePlusModifier; }

    public boolean isAbilityBased() { return abilityBased; }
    public void setAbilityBased(boolean abilityBased) { this.abilityBased = abilityBased; }
}
