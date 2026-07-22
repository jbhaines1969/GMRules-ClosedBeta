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
 * Base armor class configuration with an optional ability modifier.
 */
public class ArmorClassMethod extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;
    private int baseArmorClass = 10;
    private String acAbilityAttributeId = "";

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
}
