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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.Objects;

/**
 * Configures generation of the defense-side input used during attack resolution.
 *
 * <p>A defense is one of four forms: a passive value, an active roll, a modifier
 * applied while generating the attack, or no accuracy defense. Active rolls may
 * produce additive totals, roll-under results, or success counts. Attack modifiers
 * cover flat adjustments, difficulty dice, removed attack dice, disadvantage, and
 * adjusted thresholds without pretending that they are standalone defense rolls.</p>
 *
 * <p>This class stops at initial defense generation. Opposed-versus-defender-only
 * comparison, ties, margins, and outcome bands belong to {@link AttackResolution}.
 * Parries, reaction costs, soak, armor reduction, and other changes applied after
 * the initial result belong to their later reaction, damage, or mitigation stages.</p>
 */
public class DefenseMethod extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    public static final String MODE_PASSIVE_VALUE = "passive_value";
    public static final String MODE_ACTIVE_ROLL = "active_roll";
    public static final String MODE_ATTACK_MODIFIER = "attack_modifier";
    public static final String MODE_NONE = "none";

    public static final String ACTIVE_ROLL_ADDITIVE_TOTAL = "additive_total";
    public static final String ACTIVE_ROLL_ROLL_UNDER = "roll_under";
    public static final String ACTIVE_ROLL_SUCCESS_COUNT = "success_count";

    public static final String ATTACK_MODIFIER_FLAT = "flat_modifier";
    public static final String ATTACK_MODIFIER_DIFFICULTY_DICE = "difficulty_dice";
    public static final String ATTACK_MODIFIER_REMOVE_DICE = "remove_attack_dice";
    public static final String ATTACK_MODIFIER_DISADVANTAGE = "disadvantage";
    public static final String ATTACK_MODIFIER_THRESHOLD = "adjust_threshold";

    private String defenseMode = MODE_NONE;

    // Passive value
    private boolean standardDefenseValue = false;
    private int passiveDefenseValue = 0;

    // Active roll
    private String activeRollEvaluation = ACTIVE_ROLL_ADDITIVE_TOTAL;
    private boolean standardNumberOfDice = false;
    private int numberOfRolls = 0;
    private int dieSides = 0;
    private int numberOfDiceRolled = 0;
    private int baseRollModifier = 0;
    private int rollTargetNumber = 0;
    private int successThreshold = 0;

    // Modifier applied during attack generation
    private String attackModifierMethod = ATTACK_MODIFIER_FLAT;
    private boolean standardAttackModifier = false;
    private int attackModifierValue = 0;

    // *** CONSTRUCTORS ***
    public DefenseMethod(String name) {
        super(name);
    }

    public DefenseMethod(String name, String description) {
        super(name, description);
    }

    // *** METHODS ***
    public String getDefenseMode() {
        return defenseMode;
    }

    public void setDefenseMode(String defenseMode) {
        this.defenseMode = normalizeDefenseMode(defenseMode);
    }

    public boolean usesPassiveValue() {
        return MODE_PASSIVE_VALUE.equals(defenseMode);
    }

    public boolean usesActiveRoll() {
        return MODE_ACTIVE_ROLL.equals(defenseMode);
    }

    public boolean modifiesAttackGeneration() {
        return MODE_ATTACK_MODIFIER.equals(defenseMode);
    }

    public boolean hasNoAccuracyDefense() {
        return MODE_NONE.equals(defenseMode);
    }

    public boolean isStandardDefenseValue() {
        return standardDefenseValue;
    }

    /**
     * Marks the passive value as final rather than a base that other systems may adjust.
     */
    public void setStandardDefenseValue(boolean standardDefenseValue) {
        this.standardDefenseValue = standardDefenseValue;
    }

    public int getPassiveDefenseValue() {
        return passiveDefenseValue;
    }

    public void setPassiveDefenseValue(int passiveDefenseValue) {
        this.passiveDefenseValue = passiveDefenseValue;
    }

    public String getActiveRollEvaluation() {
        return activeRollEvaluation;
    }

    public void setActiveRollEvaluation(String activeRollEvaluation) {
        this.activeRollEvaluation = normalizeActiveRollEvaluation(activeRollEvaluation);
    }

    public boolean isStandardNumberOfDice() {
        return standardNumberOfDice;
    }

    /**
     * Marks the configured dice count as final rather than an adjustable base pool.
     */
    public void setStandardNumberOfDice(boolean standardNumberOfDice) {
        this.standardNumberOfDice = standardNumberOfDice;
    }

    public int getNumberOfRolls() {
        return numberOfRolls;
    }

    public void setNumberOfRolls(int numberOfRolls) {
        this.numberOfRolls = Math.max(0, numberOfRolls);
    }

    public int getDieSides() {
        return dieSides;
    }

    public void setDieSides(int dieSides) {
        this.dieSides = Math.max(0, dieSides);
    }

    public int getNumberOfDiceRolled() {
        return numberOfDiceRolled;
    }

    public void setNumberOfDiceRolled(int numberOfDiceRolled) {
        this.numberOfDiceRolled = Math.max(0, numberOfDiceRolled);
    }

    public int getBaseRollModifier() {
        return baseRollModifier;
    }

    public void setBaseRollModifier(int baseRollModifier) {
        this.baseRollModifier = baseRollModifier;
    }

    /**
     * Returns the target used by a roll-under defense. Zero represents not configured.
     */
    public int getRollTargetNumber() {
        return rollTargetNumber;
    }

    public void setRollTargetNumber(int rollTargetNumber) {
        this.rollTargetNumber = rollTargetNumber;
    }

    /**
     * Returns the inclusive per-die threshold used when counting successes.
     */
    public int getSuccessThreshold() {
        return successThreshold;
    }

    public void setSuccessThreshold(int successThreshold) {
        this.successThreshold = Math.max(0, successThreshold);
    }

    public String getAttackModifierMethod() {
        return attackModifierMethod;
    }

    public void setAttackModifierMethod(String attackModifierMethod) {
        this.attackModifierMethod = normalizeAttackModifierMethod(attackModifierMethod);
    }

    public boolean isStandardAttackModifier() {
        return standardAttackModifier;
    }

    /**
     * Marks the attack adjustment as final rather than a base adjustment.
     */
    public void setStandardAttackModifier(boolean standardAttackModifier) {
        this.standardAttackModifier = standardAttackModifier;
    }

    public int getAttackModifierValue() {
        return attackModifierValue;
    }

    public void setAttackModifierValue(int attackModifierValue) {
        this.attackModifierValue = attackModifierValue;
    }

    private static String normalizeDefenseMode(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case MODE_PASSIVE_VALUE, MODE_ACTIVE_ROLL, MODE_ATTACK_MODIFIER -> safeValue;
            default -> MODE_NONE;
        };
    }

    private static String normalizeActiveRollEvaluation(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case ACTIVE_ROLL_ROLL_UNDER, ACTIVE_ROLL_SUCCESS_COUNT -> safeValue;
            default -> ACTIVE_ROLL_ADDITIVE_TOTAL;
        };
    }

    private static String normalizeAttackModifierMethod(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case ATTACK_MODIFIER_DIFFICULTY_DICE,
                 ATTACK_MODIFIER_REMOVE_DICE,
                 ATTACK_MODIFIER_DISADVANTAGE,
                 ATTACK_MODIFIER_THRESHOLD -> safeValue;
            default -> ATTACK_MODIFIER_FLAT;
        };
    }

    private static String normalizeKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setDefenseMode(defenseMode);
        setActiveRollEvaluation(activeRollEvaluation);
        setNumberOfRolls(numberOfRolls);
        setDieSides(dieSides);
        setNumberOfDiceRolled(numberOfDiceRolled);
        setSuccessThreshold(successThreshold);
        setAttackModifierMethod(attackModifierMethod);
    }
}
