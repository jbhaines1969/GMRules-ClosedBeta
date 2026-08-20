/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - An empty reason means the loaded ruleset is supported by this PoC revision.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.combatpoc;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import java.util.Objects;

/** Defines only the narrow rules combination currently demonstrated by the automator. */
public final class CombatRulesSupport {

    private CombatRulesSupport() {
    }

    public static String unsupportedReason(Game game) {
        Game ruleset = Objects.requireNonNull(game, "game");
        AttackMethod attack = ruleset.getAttackMethod();
        DefenseMethod defense = ruleset.getDefenseMethod();
        AttackResolution resolution = ruleset.getAttackResolution();

        if (!attack.isDiceRolled()) {
            return "This revision requires the shared dice-based Attack Method.";
        }
        if (attack.getNumberOfRolls() != 1) {
            return "This revision supports exactly one complete attack roll.";
        }
        if (attack.getNumberOfDiceRolled() <= 0) {
            return "The Attack Method must roll at least one die.";
        }
        if (attack.getDieSides() <= 0) {
            return "The Attack Method must select a die.";
        }
        if (!defense.usesPassiveValue() || !defense.isStandardDefenseValue()) {
            return "This revision requires a final passive Defense value.";
        }
        if (!AttackResolution.MODE_ATTACK_VS_PASSIVE.equals(resolution.getResolutionMode())) {
            return "This revision requires Attack versus passive Defense resolution.";
        }
        if (!resolution.isTargetValueDefenseValue()) {
            return "This revision compares the attack directly with the Defense value.";
        }
        if (attack.getNumberOfDiceRolled() > 1
            && AttackResolution.POOL_RESOLUTION_SUCCESS_COUNT.equals(
                resolution.getAttackPoolResolutionMethod()
            )
            && (resolution.getAttackPoolSuccessThreshold() <= 0
                || resolution.getAttackPoolSuccessThreshold() > attack.getDieSides())) {
            return "Attack Resolution must set a successful-roll threshold within the selected die's range.";
        }
        if (!resolution.hasCompleteAttackSourceRouting()) {
            return "Attack Resolution must have a default attack-source route.";
        }
        return "";
    }
}
