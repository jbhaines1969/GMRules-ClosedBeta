/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - Normalize external absent values immediately.
 - Returned roll collections are immutable and contain no null values.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/** Core-owned die source and reusable complete-roll generator. */
@FunctionalInterface
public interface DiceRoller {

    int roll(int dieSides);

    static DiceRoller random() {
        return dieSides -> ThreadLocalRandom.current().nextInt(dieSides) + 1;
    }

    static List<List<Integer>> generate(
        int numberOfRolls,
        int dicePerRoll,
        int dieSides,
        DiceRoller diceRoller
    ) {
        if (numberOfRolls <= 0 || dicePerRoll <= 0 || dieSides <= 0) {
            throw new IllegalArgumentException(
                "Dice generation requires positive rolls, dice, and sides."
            );
        }
        DiceRoller safeRoller = Objects.requireNonNullElseGet(diceRoller, DiceRoller::random);
        List<List<Integer>> rolls = new ArrayList<>();
        for (int rollIndex = 0; rollIndex < numberOfRolls; rollIndex++) {
            List<Integer> dice = new ArrayList<>();
            for (int dieIndex = 0; dieIndex < dicePerRoll; dieIndex++) {
                int value = safeRoller.roll(dieSides);
                if (value < 1 || value > dieSides) {
                    throw new IllegalArgumentException(
                        "DiceRoller returned a value outside the die range."
                    );
                }
                dice.add(value);
            }
            rolls.add(List.copyOf(dice));
        }
        return List.copyOf(rolls);
    }

    static List<List<Integer>> immutableRolls(List<List<Integer>> rolls) {
        List<List<Integer>> safeRolls = Objects.requireNonNullElse(rolls, List.of());
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> roll : safeRolls) {
            copy.add(List.copyOf(Objects.requireNonNullElse(roll, List.of())));
        }
        return List.copyOf(copy);
    }
}
