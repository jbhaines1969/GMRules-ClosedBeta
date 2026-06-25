/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks


package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a currency system in the game. The currency system uses a map
 * where the first entry serves as the base unit (value = 1), and all other
 * currencies are calculated relative to this base unit.
 *
 * Example: If "copper" is first with value 1, then "silver" might be 10, "gold" might be 100
 */
public class Currency extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = -6435335923277850006L;

    private LinkedHashMap<String, Float> denominations = new LinkedHashMap<>();

// *** CONSTRUCTORS ***
    public Currency(String name) {
        super(name);
    }

    public Currency(String name, String description) {
        super(name, description);
    }
// *** METHODS ***

    public Map<String, Float> getDenominations() {
        return denominations;
    }

    public void setDenominations(LinkedHashMap<String, Float> denominations) {
        this.denominations = Objects.requireNonNullElseGet(denominations, LinkedHashMap::new);
    }

    /**
     * Adds a currency denomination to the system
     * @param denominationName the name of the currency (e.g., "copper", "silver", "gold")
     * @param value the value relative to the base unit
     */
    public void addDenomination(String denominationName, Float value) {
        String safeName = Objects.toString(denominationName, "").trim();
        float safeValue = Objects.requireNonNullElse(value, 0f);
        if (safeName.isEmpty() || safeValue <= 0f) {
            return;
        }
        if (denominations.isEmpty()) {
            this.denominations.put(safeName, 1.0f);
            return;
        }
        this.denominations.put(safeName, safeValue);
    }

    /**
 * Gets the base unit currency (first entry in the map)
 * @return the name of the base currency, or "" if no denominations exist
 */

    public String getBaseCurrency() {
        return denominations.isEmpty() ? "" : denominations.keySet().iterator().next();
    }

    /**
     * Converts an amount from one denomination to another
     * @param amount the amount to convert
     * @param fromDenomination the source denomination
     * @param toDenomination the target denomination
     * @return the converted amount, or -1 if conversion is not possible
     */
    public double convertCurrency(double amount, String fromDenomination, String toDenomination) {
        if (!denominations.containsKey(fromDenomination) || !denominations.containsKey(toDenomination)) {
            return -1; // Invalid denominations
        }

        Float fromValue = denominations.get(fromDenomination);
        Float toValue = denominations.get(toDenomination);

        // Convert to base units, then to target denomination
        double baseAmount = amount * fromValue;
        return baseAmount / toValue;
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * Currency does not reference other GameElements, so this method primarily
     * ensures data integrity.
     * @return number of orphaned references removed (always 0 for Currency)
     */
    public int cleanupOrphanedReferences() {
        // Currency denominations are freeform strings, not references to other GameElements
        // No cleanup needed
        return 0;
    }
    
}
