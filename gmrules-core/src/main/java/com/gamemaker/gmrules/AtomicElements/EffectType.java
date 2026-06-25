/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.AtomicElements;

import com.gamemaker.gmrules.GameElement;

/**
 * Represents a single effect type used as an automation flag.
 */
public class EffectType extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

// *** CONSTRUCTORS ***
    public EffectType(String name) {
        super(name);
    }

    public EffectType(String name, String description) {
        super(name, description);
    }

// *** METHODS ***
}
