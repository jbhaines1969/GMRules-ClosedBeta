/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.CharacterElements;

import com.gamemaker.gmrules.GameElement;
import java.io.Serializable;

/**
 * Represents a character background, history, origin, or prior vocation.
 * Backgrounds are independent of character classes so a game may use either
 * collection by itself or both collections together.
 */
public class Background extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public Background(String name) {
        super(name);
    }

    public Background(String name, String description) {
        super(name, description);
    }
}
