/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All records normalize external absent values at construction.
 - OptionalInt represents an absent numeric value.
 */
// NONNULL_CONTRACT

package com.gamemaker.gmrules.audit;

import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import com.gamemaker.gmrules.GameMechanics.DiceRoller;
import com.gamemaker.gmrules.GameMechanics.GeneratedValue;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * A deliberately independent example consumer of the public combat configuration.
 *
 * <p>The class does not fill gaps with hidden game conventions. When the persisted
 * configuration and supplied raw data do not identify one result, it returns an
 * indeterminate derivation or resolution with a human-readable reason.</p>
 */
public final class AttackSequenceConsumer {

    public static final String REASON_ATTACK_POOL_REDUCER_MISSING =
        AttackMethod.REASON_POOL_REDUCER_MISSING;
    public static final String REASON_ATTACK_ROLL_AGGREGATION_MISSING =
        AttackMethod.REASON_ROLL_AGGREGATION_MISSING;
    public static final String REASON_DEFENSE_ROLL_AGGREGATION_MISSING =
        DefenseMethod.REASON_ROLL_AGGREGATION_MISSING;
    public static final String REASON_ROLL_UNDER_RESULT_MISSING =
        DefenseMethod.REASON_ROLL_UNDER_RESULT_MISSING;
    public static final String REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING =
        AttackResolution.REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING;
    @FunctionalInterface
    public interface DieRoller {
        int roll(int dieSides);
    }

    public enum Availability {
        AVAILABLE,
        NOT_APPLICABLE,
        INDETERMINATE
    }

    public enum AttackSuccess {
        SUCCEEDED,
        FAILED,
        INDETERMINATE
    }

    public record ValueDerivation(
        Availability availability,
        OptionalInt value,
        String reason
    ) {
        public ValueDerivation {
            availability = Objects.requireNonNullElse(availability, Availability.INDETERMINATE);
            value = Objects.requireNonNullElseGet(value, OptionalInt::empty);
            reason = Objects.toString(reason, "");
        }

        public static ValueDerivation available(int value) {
            return new ValueDerivation(Availability.AVAILABLE, OptionalInt.of(value), "");
        }

        public static ValueDerivation notApplicable(String reason) {
            return new ValueDerivation(Availability.NOT_APPLICABLE, OptionalInt.empty(), reason);
        }

        public static ValueDerivation indeterminate(String reason) {
            return new ValueDerivation(Availability.INDETERMINATE, OptionalInt.empty(), reason);
        }
    }

    public record RawAttackData(
        String requestedRouteId,
        List<List<Integer>> rolls,
        OptionalInt suppliedAttackValue
    ) {
        public RawAttackData {
            requestedRouteId = Objects.toString(requestedRouteId, "").trim();
            rolls = immutableRolls(rolls);
            suppliedAttackValue = Objects.requireNonNullElseGet(
                suppliedAttackValue,
                OptionalInt::empty
            );
        }

        public static RawAttackData supplied(String requestedRouteId, int attackValue) {
            return new RawAttackData(requestedRouteId, List.of(), OptionalInt.of(attackValue));
        }
    }

    public record RawDefenseData(List<List<Integer>> rolls, OptionalInt suppliedDefenseValue) {
        public RawDefenseData {
            rolls = immutableRolls(rolls);
            suppliedDefenseValue = Objects.requireNonNullElseGet(
                suppliedDefenseValue,
                OptionalInt::empty
            );
        }

        public static RawDefenseData supplied(int defenseValue) {
            return new RawDefenseData(List.of(), OptionalInt.of(defenseValue));
        }
    }

    public record ResolutionInput(
        String requestedRouteId,
        OptionalInt attackValue,
        OptionalInt defenseValue,
        OptionalInt threatValue
    ) {
        public ResolutionInput {
            requestedRouteId = Objects.toString(requestedRouteId, "").trim();
            attackValue = Objects.requireNonNullElseGet(attackValue, OptionalInt::empty);
            defenseValue = Objects.requireNonNullElseGet(defenseValue, OptionalInt::empty);
            threatValue = Objects.requireNonNullElseGet(threatValue, OptionalInt::empty);
        }

        public static ResolutionInput attackVersusPassive(
            String routeId,
            int attackValue,
            int passiveDefenseValue
        ) {
            return new ResolutionInput(
                routeId,
                OptionalInt.of(attackValue),
                OptionalInt.of(passiveDefenseValue),
                OptionalInt.empty()
            );
        }

        public static ResolutionInput attackVersusDefense(
            String routeId,
            int attackValue,
            int defenseValue
        ) {
            return attackVersusPassive(routeId, attackValue, defenseValue);
        }

        public static ResolutionInput defenseVersusThreat(
            String routeId,
            int defenseValue,
            int threatValue
        ) {
            return new ResolutionInput(
                routeId,
                OptionalInt.empty(),
                OptionalInt.of(defenseValue),
                OptionalInt.of(threatValue)
            );
        }

        public static ResolutionInput automatic() {
            return new ResolutionInput(
                "",
                OptionalInt.empty(),
                OptionalInt.empty(),
                OptionalInt.empty()
            );
        }
    }

    public record ResolutionResult(
        AttackSuccess attackSuccess,
        String outcomeKey,
        OptionalInt attackValue,
        OptionalInt defenseValue,
        OptionalInt margin,
        String selectedRouteId,
        String selectedSourceKind,
        String reason
    ) {
        public ResolutionResult {
            attackSuccess = Objects.requireNonNullElse(
                attackSuccess,
                AttackSuccess.INDETERMINATE
            );
            outcomeKey = Objects.toString(outcomeKey, "");
            attackValue = Objects.requireNonNullElseGet(attackValue, OptionalInt::empty);
            defenseValue = Objects.requireNonNullElseGet(defenseValue, OptionalInt::empty);
            margin = Objects.requireNonNullElseGet(margin, OptionalInt::empty);
            selectedRouteId = Objects.toString(selectedRouteId, "");
            selectedSourceKind = Objects.toString(selectedSourceKind, "");
            reason = Objects.toString(reason, "");
        }
    }

    /** Generates every individual die result described by AttackMethod. */
    public RawAttackData generateAttackData(
        AttackMethod method,
        String requestedRouteId,
        DieRoller dieRoller
    ) {
        AttackMethod safeMethod = Objects.requireNonNull(method, "method");
        DieRoller safeRoller = Objects.requireNonNull(dieRoller, "dieRoller");
        GeneratedValue generated = safeMethod.generateAttackValue(safeRoller::roll);
        return new RawAttackData(
            requestedRouteId,
            generated.rolls(),
            OptionalInt.empty()
        );
    }

    /** Generates every individual die result described by an active DefenseMethod. */
    public RawDefenseData generateDefenseData(DefenseMethod method, DieRoller dieRoller) {
        DefenseMethod safeMethod = Objects.requireNonNull(method, "method");
        DieRoller safeRoller = Objects.requireNonNull(dieRoller, "dieRoller");
        GeneratedValue generated = safeMethod.generateDefenseValue(safeRoller::roll);
        return new RawDefenseData(
            generated.rolls(),
            OptionalInt.empty()
        );
    }

    /**
     * Attempts to obtain the one numeric attack value expected by ordinary comparisons.
     */
    public ValueDerivation deriveAttackValue(AttackMethod method, RawAttackData rawData) {
        AttackMethod safeMethod = Objects.requireNonNull(method, "method");
        RawAttackData safeData = Objects.requireNonNull(rawData, "rawData");
        return fromCoreValue(
            safeMethod.evaluateAttackValue(
                safeData.rolls(),
                safeData.suppliedAttackValue()
            )
        );
    }

    /**
     * Attempts to obtain the one numeric defense value expected by ordinary comparisons.
     */
    public ValueDerivation deriveDefenseValue(DefenseMethod method, RawDefenseData rawData) {
        DefenseMethod safeMethod = Objects.requireNonNull(method, "method");
        RawDefenseData safeData = Objects.requireNonNull(rawData, "rawData");
        return fromCoreValue(
            safeMethod.evaluateDefenseValue(
                safeData.rolls(),
                safeData.suppliedDefenseValue()
            )
        );
    }

    /** Delegates all rules calculations to the core AttackResolution. */
    public ResolutionResult resolve(AttackResolution resolution, ResolutionInput input) {
        AttackResolution safeResolution = Objects.requireNonNull(resolution, "resolution");
        ResolutionInput safeInput = Objects.requireNonNull(input, "input");
        return fromCoreResult(safeResolution.resolve(toCoreInput(safeInput)));
    }

    public ResolutionResult resolveAttackVersusPassive(
        AttackResolution resolution,
        ResolutionInput input
    ) {
        AttackResolution safeResolution = Objects.requireNonNull(resolution, "resolution");
        ResolutionInput safeInput = Objects.requireNonNull(input, "input");
        return fromCoreResult(safeResolution.resolveAttackVersusPassive(toCoreInput(safeInput)));
    }

    public ResolutionResult resolveAttackVersusDefenseResult(
        AttackResolution resolution,
        ResolutionInput input
    ) {
        AttackResolution safeResolution = Objects.requireNonNull(resolution, "resolution");
        ResolutionInput safeInput = Objects.requireNonNull(input, "input");
        return fromCoreResult(
            safeResolution.resolveAttackVersusDefenseResult(toCoreInput(safeInput))
        );
    }

    public ResolutionResult resolveDefenseVersusThreat(
        AttackResolution resolution,
        ResolutionInput input
    ) {
        AttackResolution safeResolution = Objects.requireNonNull(resolution, "resolution");
        ResolutionInput safeInput = Objects.requireNonNull(input, "input");
        return fromCoreResult(safeResolution.resolveDefenseVersusThreat(toCoreInput(safeInput)));
    }

    public ResolutionResult resolveAutomaticContact(AttackResolution resolution) {
        AttackResolution safeResolution = Objects.requireNonNull(resolution, "resolution");
        return fromCoreResult(safeResolution.resolveAutomaticContact());
    }

    private static AttackResolution.ResolutionInput toCoreInput(ResolutionInput input) {
        return new AttackResolution.ResolutionInput(
            input.requestedRouteId(),
            input.attackValue(),
            input.defenseValue(),
            input.threatValue()
        );
    }

    private static ResolutionResult fromCoreResult(AttackResolution.ResolutionResult result) {
        return new ResolutionResult(
            AttackSuccess.valueOf(result.attackSuccess().name()),
            result.outcomeKey(),
            result.attackValue(),
            result.defenseValue(),
            result.margin(),
            result.selectedRouteId(),
            result.selectedSourceKind(),
            result.reason()
        );
    }

    private static ValueDerivation fromCoreValue(GeneratedValue generatedValue) {
        Availability availability = Availability.valueOf(generatedValue.availability().name());
        return new ValueDerivation(
            availability,
            generatedValue.value(),
            generatedValue.reason()
        );
    }

    private static List<List<Integer>> immutableRolls(List<List<Integer>> rolls) {
        return DiceRoller.immutableRolls(rolls);
    }
}
