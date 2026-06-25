/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import com.gamemaker.gmrules.Game;

/**
 * Contract for stages that participate in sidebar navigation.
 */
public interface StageView {

    // *** MEMBERS ***
    // (none)

    // *** CONSTRUCTORS ***
    // (none)

    // *** METHODS ***
    StageId getStageId();

    Game getGame();
}
