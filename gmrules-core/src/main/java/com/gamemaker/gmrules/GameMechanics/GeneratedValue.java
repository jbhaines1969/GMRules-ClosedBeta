/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - OptionalInt represents an absent scalar value.
 - Returned roll collections are immutable and contain no null values.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/** Core-owned runtime output from an Attack or Defense value generator. */
public record GeneratedValue(
    Availability availability,
    OptionalInt value,
    List<List<Integer>> rolls,
    String reason
) {

    public GeneratedValue {
        availability = Objects.requireNonNullElse(availability, Availability.INDETERMINATE);
        value = Objects.requireNonNullElseGet(value, OptionalInt::empty);
        rolls = DiceRoller.immutableRolls(rolls);
        reason = Objects.toString(reason, "");
    }

    public static GeneratedValue available(int value, List<List<Integer>> rolls) {
        return new GeneratedValue(
            Availability.AVAILABLE,
            OptionalInt.of(value),
            rolls,
            ""
        );
    }

    public static GeneratedValue notApplicable(String reason) {
        return new GeneratedValue(
            Availability.NOT_APPLICABLE,
            OptionalInt.empty(),
            List.of(),
            reason
        );
    }

    public static GeneratedValue indeterminate(List<List<Integer>> rolls, String reason) {
        return new GeneratedValue(
            Availability.INDETERMINATE,
            OptionalInt.empty(),
            rolls,
            reason
        );
    }

    public boolean hasValue() {
        return availability == Availability.AVAILABLE && value.isPresent();
    }

    public int requireValue() {
        if (!hasValue()) {
            String message = reason.isBlank()
                ? "The configured generator did not produce one numerical value."
                : reason;
            throw new IllegalStateException(message);
        }
        return value.getAsInt();
    }

    public enum Availability {
        AVAILABLE,
        NOT_APPLICABLE,
        INDETERMINATE
    }
}
