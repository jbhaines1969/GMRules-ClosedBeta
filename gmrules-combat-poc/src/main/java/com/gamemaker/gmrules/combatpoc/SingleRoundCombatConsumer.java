/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - The supplied Game and its core mechanics are required.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.combatpoc;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import java.util.Objects;

/** The complete rules-facing code required by this consumer to automate one round. */
public final class SingleRoundCombatConsumer {

    public AttackResolution.AttackResult run(Game game) {
        Game ruleset = Objects.requireNonNull(game, "game");

        return ruleset.getAttackResolution().getAttackResult();
    }
}
