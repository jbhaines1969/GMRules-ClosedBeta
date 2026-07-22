/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.GameElement;
import java.io.Serializable;

/**
 * Reusable damage type definition for attacks, effects, spells, and gear.
 */
public class DamageType extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public DamageType(String name) {
        super(name);
    }

    public DamageType(String name, String description) {
        super(name, description);
    }
}
