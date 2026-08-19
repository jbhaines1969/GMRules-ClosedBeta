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
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Configures and executes initial attack-value generation.
 * Attack Resolution owns any later reduction or comparison rule.
 */
public class AttackMethod extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    public static final String REASON_POOL_REDUCER_MISSING =
        "AttackMethod does not define how multiple dice become one attack value.";
    public static final String REASON_ROLL_AGGREGATION_MISSING =
        "AttackMethod does not define how multiple rolls become one attack value.";
    public static final String REASON_EXTERNAL_SOURCE_REQUIRED =
        "AttackMethod delegates attack generation to another configured source.";

    private boolean diceRolled = false;
    private boolean standardNumberOfDice = false;
    private int numberOfRolls = 0;
    private int dieSides = 0;
    private int numberOfDiceRolled = 0;
    private int singleRollModifier = 0;

    // *** CONSTRUCTORS ***
    public AttackMethod(String name) {
        super(name);
    }

    public AttackMethod(String name, String description) {
        super(name, description);
    }

    // *** METHODS ***
    public boolean isDiceRolled() {
        return diceRolled;
    }

    public void setDiceRolled(boolean diceRolled) {
        this.diceRolled = diceRolled;
    }

    public boolean isStandardNumberOfDice() {
        return standardNumberOfDice;
    }

    public void setStandardNumberOfDice(boolean standardNumberOfDice) {
        this.standardNumberOfDice = standardNumberOfDice;
        setNumberOfDiceRolled(numberOfDiceRolled);
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
        if (standardNumberOfDice) {
            this.numberOfDiceRolled = Math.max(1, numberOfDiceRolled);
        } else {
            this.numberOfDiceRolled = Math.max(0, numberOfDiceRolled);
        }
    }

    /** Returns the signed value added to one attack die rolled once. */
    public int getSingleRollModifier() {
        return singleRollModifier;
    }

    public void setSingleRollModifier(int singleRollModifier) {
        this.singleRollModifier = singleRollModifier;
    }

    public void addSingleRollModifier(int amount) {
        singleRollModifier += amount;
    }

    public void subtractSingleRollModifier(int amount) {
        singleRollModifier -= amount;
    }

    // === RUNTIME ATTACK VALUE GENERATION ===

    /** Generates and evaluates the configured attack using the core random die source. */
    public GeneratedValue generateAttackValue() {
        return generateAttackValue(DiceRoller.random());
    }

    /** Generates and evaluates the configured attack using a caller-supplied die source. */
    public GeneratedValue generateAttackValue(DiceRoller diceRoller) {
        if (!diceRolled) {
            return GeneratedValue.notApplicable(REASON_EXTERNAL_SOURCE_REQUIRED);
        }
        List<List<Integer>> rolls = DiceRoller.generate(
            numberOfRolls,
            numberOfDiceRolled,
            dieSides,
            diceRoller
        );
        return evaluateAttackValue(rolls, OptionalInt.empty());
    }

    /**
     * Evaluates already generated attack data. A supplied scalar from a card,
     * Attribute, Skill, gear, or another source takes precedence over dice.
     */
    public GeneratedValue evaluateAttackValue(
        List<List<Integer>> rolls,
        OptionalInt suppliedAttackValue
    ) {
        List<List<Integer>> safeRolls = DiceRoller.immutableRolls(rolls);
        OptionalInt safeSuppliedValue = Objects.requireNonNullElseGet(
            suppliedAttackValue,
            OptionalInt::empty
        );
        if (safeSuppliedValue.isPresent()) {
            return GeneratedValue.available(safeSuppliedValue.getAsInt(), safeRolls);
        }
        if (safeRolls.size() != 1) {
            return GeneratedValue.indeterminate(safeRolls, REASON_ROLL_AGGREGATION_MISSING);
        }
        List<Integer> roll = safeRolls.get(0);
        if (roll.size() != 1) {
            return GeneratedValue.indeterminate(safeRolls, REASON_POOL_REDUCER_MISSING);
        }
        return GeneratedValue.available(roll.get(0) + singleRollModifier, safeRolls);
    }

    public GeneratedValue useSuppliedAttackValue(int attackValue) {
        return evaluateAttackValue(List.of(), OptionalInt.of(attackValue));
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setNumberOfRolls(numberOfRolls);
        setDieSides(dieSides);
        setNumberOfDiceRolled(numberOfDiceRolled);
    }
}
