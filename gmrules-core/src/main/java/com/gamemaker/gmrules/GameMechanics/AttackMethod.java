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
 * Base configuration shared by attack methods that use dice.
 * Descendant applications interpret these values when resolving an attack.
 */
public class AttackMethod extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private boolean diceRolled = false;
    private boolean standardNumberOfDice = false;
    private int numberOfRolls = 0;
    private int dieSides = 0;
    private int numberOfDiceRolled = 0;

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
}
