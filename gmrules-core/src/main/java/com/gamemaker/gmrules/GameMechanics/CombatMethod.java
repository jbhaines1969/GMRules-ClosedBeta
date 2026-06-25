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
 * Minimal combat method selectors for PoC automation.
 * Descendant applications interpret these flags and apply values from Game elements.
 * This class intentionally does not implement rolling or resolution logic.
 */
public class CombatMethod extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Initiative
    private boolean usesInitiative = true;

    // Attacks
    private boolean usesAttackRolls = true;
    private boolean attacksRollUnder = false;
    private boolean usesArmorClassAsDefense = true;

    // Critical hits (very minimal PoC selectors)
    private boolean criticalsEnabled = true;
    private boolean criticalOnNatural20 = true;
    private boolean natural1AutoMiss = true;

    // *** CONSTRUCTORS ***
    public CombatMethod(String name) {
        super(name);
    }

    public CombatMethod(String name, String description) {
        super(name, description);
    }

    // *** METHODS ***
    public boolean isUsesInitiative() {
        return usesInitiative;
    }

    public void setUsesInitiative(boolean usesInitiative) {
        this.usesInitiative = usesInitiative;
    }

    public boolean isUsesAttackRolls() {
        return usesAttackRolls;
    }

    public void setUsesAttackRolls(boolean usesAttackRolls) {
        this.usesAttackRolls = usesAttackRolls;
    }

    public boolean isAttacksRollUnder() {
        return attacksRollUnder;
    }

    public void setAttacksRollUnder(boolean attacksRollUnder) {
        this.attacksRollUnder = attacksRollUnder;
    }

    public boolean isUsesArmorClassAsDefense() {
        return usesArmorClassAsDefense;
    }

    public void setUsesArmorClassAsDefense(boolean usesArmorClassAsDefense) {
        this.usesArmorClassAsDefense = usesArmorClassAsDefense;
    }

    public boolean isCriticalsEnabled() {
        return criticalsEnabled;
    }

    public void setCriticalsEnabled(boolean criticalsEnabled) {
        this.criticalsEnabled = criticalsEnabled;
    }

    public boolean isCriticalOnNatural20() {
        return criticalOnNatural20;
    }

    public void setCriticalOnNatural20(boolean criticalOnNatural20) {
        this.criticalOnNatural20 = criticalOnNatural20;
    }

    public boolean isNatural1AutoMiss() {
        return natural1AutoMiss;
    }

    public void setNatural1AutoMiss(boolean natural1AutoMiss) {
        this.natural1AutoMiss = natural1AutoMiss;
    }
}

