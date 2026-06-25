/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import com.gamemaker.gmrules.GameElement;
import com.gamemaker.gmrules.GameElements.Currency;
import java.util.Objects;

/**
 * Represents a standardized spell component entry with cost and bulk data.
 */
public class SpellComponents extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private int costPerUnit = 0;
    private Currency currencyType = new Currency("");
    private float weightPerUnit = 0.0f;
    private float sizePerUnit = 0.0f;

// *** CONSTRUCTORS ***
    public SpellComponents(String name) {
        super(name);
    }

    public SpellComponents(String name, String description) {
        super(name, description);
    }

// *** METHODS ***
    public int getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(int costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public Currency getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Currency currencyType) {
        this.currencyType = Objects.requireNonNullElse(currencyType, new Currency(""));
    }

    public float getWeightPerUnit() {
        return weightPerUnit;
    }

    public void setWeightPerUnit(float weightPerUnit) {
        this.weightPerUnit = weightPerUnit;
    }

    public float getSizePerUnit() {
        return sizePerUnit;
    }

    public void setSizePerUnit(float sizePerUnit) {
        this.sizePerUnit = sizePerUnit;
    }
}
