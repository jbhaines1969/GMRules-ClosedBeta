package com.gamemaker.gmrules.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gamemaker.gmrules.GameMechanics.AttackMethod;
import com.gamemaker.gmrules.GameMechanics.AttackResolution;
import com.gamemaker.gmrules.GameMechanics.DefenseMethod;
import com.gamemaker.gmrules.GameMechanics.GeneratedValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.List;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AttackSequenceConsumerTest {

    private final AttackSequenceConsumer consumer = new AttackSequenceConsumer();

    @Test
    void coverageGuardListsEveryCurrentAttackResolutionPossibility() {
        assertEquals(
            Set.of(
                AttackResolution.MODE_ATTACK_VS_PASSIVE,
                AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
                AttackResolution.MODE_DEFENSE_VS_THREAT,
                AttackResolution.MODE_AUTOMATIC
            ),
            stringConstantsWithPrefix("MODE_")
        );
        assertEquals(
            Set.of(
                AttackResolution.COMPARISON_MEET_OR_EXCEED,
                AttackResolution.COMPARISON_EXCEED,
                AttackResolution.COMPARISON_LOWER_WINS,
                AttackResolution.COMPARISON_SUCCESS_COUNT,
                AttackResolution.COMPARISON_OUTCOME_BANDS
            ),
            stringConstantsWithPrefix("COMPARISON_")
        );
        assertEquals(
            Set.of(
                AttackResolution.ROLL_DIRECTION_OVER,
                AttackResolution.ROLL_DIRECTION_UNDER
            ),
            stringConstantsWithPrefix("ROLL_DIRECTION_")
        );
        assertEquals(
            Set.of(
                AttackResolution.POOL_RESOLUTION_SUCCESS_COUNT,
                AttackResolution.POOL_RESOLUTION_HIGHEST_DIE,
                AttackResolution.POOL_RESOLUTION_LOWEST_DIE,
                AttackResolution.POOL_RESOLUTION_SUM
            ),
            stringConstantsWithPrefix("POOL_RESOLUTION_")
        );
        assertEquals(
            Set.of(
                AttackResolution.OUTCOME_METRIC_ATTACK_RESULT,
                AttackResolution.OUTCOME_METRIC_DEFENSE_RESULT,
                AttackResolution.OUTCOME_METRIC_MARGIN
            ),
            stringConstantsWithPrefix("OUTCOME_METRIC_")
        );
        assertEquals(
            Set.of(
                AttackResolution.SOURCE_ATTACK_METHOD,
                AttackResolution.SOURCE_CARD,
                AttackResolution.SOURCE_ATTRIBUTE,
                AttackResolution.SOURCE_SKILL,
                AttackResolution.SOURCE_GEAR,
                AttackResolution.SOURCE_OTHER
            ),
            stringConstantsWithPrefix("SOURCE_")
        );
    }

    @Test
    void singleDieAttackProducesRawDataAndAnUnambiguousAttackValue() {
        AttackMethod attack = attackDice(1, 1, 20);

        AttackSequenceConsumer.RawAttackData raw = consumer.generateAttackData(
            attack,
            "route",
            roller(13)
        );
        AttackSequenceConsumer.ValueDerivation value = consumer.deriveAttackValue(attack, raw);

        assertEquals(List.of(List.of(13)), raw.rolls());
        assertEquals(AttackSequenceConsumer.Availability.AVAILABLE, value.availability());
        assertEquals(13, value.value().orElseThrow());
    }

    @Test
    void singleDieAttackAppliesOneSignedDirectModifier() {
        AttackMethod attack = attackDice(1, 1, 20);
        attack.setSingleRollModifier(3);

        GeneratedValue bonusResult = attack.generateAttackValue(roller(10)::roll);
        assertEquals(3, attack.getSingleRollModifier());
        assertEquals(13, bonusResult.requireValue());

        attack.addSingleRollModifier(-5);
        GeneratedValue penaltyResult = attack.generateAttackValue(roller(10)::roll);
        assertEquals(-2, attack.getSingleRollModifier());
        assertEquals(8, penaltyResult.requireValue());

        attack.subtractSingleRollModifier(4);
        GeneratedValue largerPenaltyResult = attack.generateAttackValue(roller(10)::roll);

        assertEquals(-6, attack.getSingleRollModifier());
        assertEquals(4, largerPenaltyResult.requireValue());
    }

    @Test
    void consumerCanGenerateAndResolveScalarValuesEntirelyThroughCore() {
        AttackMethod attack = attackDice(1, 1, 20);
        DefenseMethod defense = new DefenseMethod("Armor Class");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(12);
        AttackResolution resolution = new AttackResolution("Attack Resolution");
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        resolution.setTargetValueIsDefenseValue(true);
        resolution.setAttackerWinsTies(false);
        AttackSequenceConsumer.DieRoller deterministicRoller = roller(13);

        GeneratedValue attackValue = attack.generateAttackValue(deterministicRoller::roll);
        GeneratedValue defenseValue = defense.generateDefenseValue();
        AttackResolution.ResolutionResult result = resolution.resolve(
            AttackResolution.ResolutionInput.attackVersusPassive(
                "route",
                attackValue.requireValue(),
                defenseValue.requireValue()
            )
        );

        assertEquals(List.of(List.of(13)), attackValue.rolls());
        assertEquals(13, result.attackValue().orElseThrow());
        assertEquals(12, result.defenseValue().orElseThrow());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void dicePoolProducesEveryRawDieButCannotSelectOneAttackValue() {
        AttackMethod attack = attackDice(1, 3, 6);

        AttackSequenceConsumer.RawAttackData raw = consumer.generateAttackData(
            attack,
            "route",
            roller(2, 4, 6)
        );
        AttackSequenceConsumer.ValueDerivation value = consumer.deriveAttackValue(attack, raw);

        assertEquals(List.of(List.of(2, 4, 6)), raw.rolls());
        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, value.availability());
        assertEquals(AttackSequenceConsumer.REASON_ATTACK_POOL_REDUCER_MISSING, value.reason());
    }

    @Test
    void attackResolutionCountsPoolDiceMeetingTheMinimumAgainstPassiveDefense() {
        AttackMethod attack = attackDice(1, 4, 10);
        attack.setSingleRollModifier(99);
        GeneratedValue rawAttack = attack.generateAttackValue(roller(6, 7, 8, 10)::roll);
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(3);
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );
        resolution.setAttackPoolSuccessThreshold(7);

        AttackResolution.AttackResult result = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );

        assertEquals(List.of(List.of(6, 7, 8, 10)), result.attack().rolls());
        assertEquals(3, result.attack().requireValue());
        assertEquals(3, result.defense().requireValue());
        assertEquals(0, result.resolution().margin().orElseThrow());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void attackPoolFailsWhenItsSuccessCountIsBelowPassiveDefense() {
        AttackMethod attack = attackDice(1, 3, 10);
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(2);
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );
        resolution.setAttackPoolSuccessThreshold(7);

        AttackResolution.AttackResult result = resolution.getAttackResult(
            attack.generateAttackValue(roller(6, 7, 3)::roll),
            defense.generateDefenseValue()
        );

        assertEquals(1, result.attack().requireValue());
        assertEquals(-1, result.resolution().margin().orElseThrow());
        assertEquals(AttackResolution.AttackSuccess.FAILED, result.attackSuccess());
    }

    @Test
    void highestDiePoolUsesAttackerWinsTiesByDefault() {
        AttackMethod attack = attackDice(1, 4, 10);
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(8);
        AttackResolution resolution = new AttackResolution("Resolution");
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackPoolResolutionMethod(AttackResolution.POOL_RESOLUTION_HIGHEST_DIE);

        AttackResolution.AttackResult result = resolution.getAttackResult(
            attack.generateAttackValue(roller(2, 8, 5, 7)::roll),
            defense.generateDefenseValue()
        );

        assertTrue(resolution.isAttackerWinsTies());
        assertEquals(8, result.attack().requireValue());
        assertEquals(List.of(List.of(2, 8, 5, 7)), result.attack().rolls());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void summedPoolUsesAttackerWinsTiesByDefault() {
        AttackMethod attack = attackDice(1, 3, 6);
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(9);
        AttackResolution resolution = new AttackResolution("Resolution");
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackPoolResolutionMethod(AttackResolution.POOL_RESOLUTION_SUM);

        AttackResolution.AttackResult result = resolution.getAttackResult(
            attack.generateAttackValue(roller(2, 3, 4)::roll),
            defense.generateDefenseValue()
        );

        assertTrue(resolution.isAttackerWinsTies());
        assertEquals(9, result.attack().requireValue());
        assertEquals(List.of(List.of(2, 3, 4)), result.attack().rolls());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @ParameterizedTest
    @MethodSource("scalarPoolMethods")
    void scalarPoolReducersHonorDirectionAndEquality(
        String poolMethod,
        List<Integer> rolls,
        int expectedAttackValue
    ) {
        AttackMethod attack = attackDice(1, rolls.size(), 20);
        GeneratedValue rawAttack = attack.evaluateAttackValue(
            List.of(rolls),
            OptionalInt.empty()
        );
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        AttackResolution resolution = new AttackResolution("Resolution");
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackPoolResolutionMethod(poolMethod);

        defense.setPassiveDefenseValue(expectedAttackValue);
        for (String direction : List.of(
            AttackResolution.ROLL_DIRECTION_OVER,
            AttackResolution.ROLL_DIRECTION_UNDER
        )) {
            resolution.setAttackRollDirection(direction);
            resolution.setAttackerWinsTies(true);
            assertEquals(
                AttackResolution.AttackSuccess.SUCCEEDED,
                resolution.getAttackResult(rawAttack, defense.generateDefenseValue()).attackSuccess()
            );

            resolution.setAttackerWinsTies(false);
            assertEquals(
                AttackResolution.AttackSuccess.FAILED,
                resolution.getAttackResult(rawAttack, defense.generateDefenseValue()).attackSuccess()
            );
        }

        resolution.setAttackerWinsTies(false);
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        defense.setPassiveDefenseValue(expectedAttackValue - 1);
        AttackResolution.AttackResult strictOver = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);
        defense.setPassiveDefenseValue(expectedAttackValue + 1);
        AttackResolution.AttackResult strictUnder = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );

        assertEquals(expectedAttackValue, strictOver.attack().requireValue());
        assertEquals(List.of(rolls), strictOver.attack().rolls());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, strictOver.attackSuccess());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, strictUnder.attackSuccess());
    }

    @Test
    void successCountPoolUsesInclusiveThresholdAndConfiguredEqualityInBothDirections() {
        AttackMethod attack = attackDice(1, 4, 10);
        GeneratedValue rawAttack = attack.generateAttackValue(roller(2, 4, 6, 8)::roll);
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        AttackResolution resolution = new AttackResolution("Resolution");
        resolution.setResolutionMode(AttackResolution.MODE_ATTACK_VS_PASSIVE);
        resolution.setAttackPoolResolutionMethod(AttackResolution.POOL_RESOLUTION_SUCCESS_COUNT);
        resolution.setAttackPoolSuccessThreshold(4);

        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);
        defense.setPassiveDefenseValue(2);
        resolution.setAttackerWinsTies(true);
        AttackResolution.AttackResult inclusiveUnder = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );
        resolution.setAttackerWinsTies(false);
        AttackResolution.AttackResult strictUnder = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );

        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        defense.setPassiveDefenseValue(3);
        resolution.setAttackerWinsTies(true);
        AttackResolution.AttackResult inclusiveOver = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );
        resolution.setAttackerWinsTies(false);
        AttackResolution.AttackResult strictOver = resolution.getAttackResult(
            rawAttack,
            defense.generateDefenseValue()
        );

        assertEquals(2, inclusiveUnder.attack().requireValue());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, inclusiveUnder.attackSuccess());
        assertEquals(AttackResolution.AttackSuccess.FAILED, strictUnder.attackSuccess());
        assertEquals(3, inclusiveOver.attack().requireValue());
        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, inclusiveOver.attackSuccess());
        assertEquals(AttackResolution.AttackSuccess.FAILED, strictOver.attackSuccess());
    }

    @Test
    void attackDiceMinimumDependsOnWhetherTheCountIsFixedOrAnAdjustablePool() throws Exception {
        AttackMethod attack = new AttackMethod("Attack");
        attack.setStandardNumberOfDice(false);
        attack.setNumberOfDiceRolled(0);
        attack.setSingleRollModifier(-3);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(attack);
        }
        AttackMethod restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (AttackMethod) input.readObject();
        }

        assertFalse(restored.isStandardNumberOfDice());
        assertEquals(0, restored.getNumberOfDiceRolled());
        assertEquals(-3, restored.getSingleRollModifier());

        restored.setStandardNumberOfDice(true);
        assertEquals(1, restored.getNumberOfDiceRolled());
        restored.setNumberOfDiceRolled(0);
        assertEquals(1, restored.getNumberOfDiceRolled());
    }

    @Test
    void multipleAttackRollsRemainSeparateAndHaveNoAggregationRule() {
        AttackMethod attack = attackDice(2, 1, 20);

        AttackSequenceConsumer.RawAttackData raw = consumer.generateAttackData(
            attack,
            "route",
            roller(15, 7)
        );
        AttackSequenceConsumer.ValueDerivation value = consumer.deriveAttackValue(attack, raw);

        assertEquals(List.of(List.of(15), List.of(7)), raw.rolls());
        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, value.availability());
        assertEquals(AttackSequenceConsumer.REASON_ATTACK_ROLL_AGGREGATION_MISSING, value.reason());
    }

    @Test
    void externalAttackSourceCanSupplyAnAlreadyGeneratedAttackValue() {
        AttackMethod attack = new AttackMethod("Other Source");
        AttackSequenceConsumer.RawAttackData raw = AttackSequenceConsumer.RawAttackData.supplied(
            "card-route",
            9
        );

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveAttackValue(attack, raw);

        assertEquals(AttackSequenceConsumer.Availability.AVAILABLE, value.availability());
        assertEquals(9, value.value().orElseThrow());
    }

    @Test
    void invalidDieOutputIsRejectedInsteadOfBecomingRuntimeData() {
        AttackMethod attack = attackDice(1, 1, 6);

        assertThrows(
            IllegalArgumentException.class,
            () -> consumer.generateAttackData(attack, "route", roller(7))
        );
    }

    @Test
    void finalPassiveDefenseProducesTheConfiguredDefenseValue() {
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(true);
        defense.setPassiveDefenseValue(12);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            new AttackSequenceConsumer.RawDefenseData(List.of(), OptionalInt.empty())
        );

        assertEquals(AttackSequenceConsumer.Availability.AVAILABLE, value.availability());
        assertEquals(12, value.value().orElseThrow());
    }

    @Test
    void adjustablePassiveDefenseNeedsItsFinalAdjustedValueFromRuntimeData() {
        DefenseMethod defense = new DefenseMethod("Passive Defense");
        defense.setDefenseMode(DefenseMethod.MODE_PASSIVE_VALUE);
        defense.setStandardDefenseValue(false);
        defense.setPassiveDefenseValue(10);

        AttackSequenceConsumer.ValueDerivation incomplete = consumer.deriveDefenseValue(
            defense,
            new AttackSequenceConsumer.RawDefenseData(List.of(), OptionalInt.empty())
        );
        AttackSequenceConsumer.ValueDerivation supplied = consumer.deriveDefenseValue(
            defense,
            AttackSequenceConsumer.RawDefenseData.supplied(14)
        );

        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, incomplete.availability());
        assertEquals(14, supplied.value().orElseThrow());
    }

    @Test
    void additiveActiveDefenseProducesOneDefenseTotal() {
        DefenseMethod defense = activeDefense(DefenseMethod.ACTIVE_ROLL_ADDITIVE_TOTAL, 1, 2, 6);
        defense.setBaseRollModifier(3);

        AttackSequenceConsumer.RawDefenseData raw = consumer.generateDefenseData(
            defense,
            roller(4, 5)
        );
        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(defense, raw);

        assertEquals(List.of(List.of(4, 5)), raw.rolls());
        assertEquals(12, value.value().orElseThrow());
    }

    @Test
    void successCountDefenseCountsDiceMeetingTheInclusiveThreshold() {
        DefenseMethod defense = activeDefense(DefenseMethod.ACTIVE_ROLL_SUCCESS_COUNT, 1, 4, 10);
        defense.setSuccessThreshold(7);

        AttackSequenceConsumer.RawDefenseData raw = consumer.generateDefenseData(
            defense,
            roller(6, 7, 9, 2)
        );
        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(defense, raw);

        assertEquals(2, value.value().orElseThrow());
    }

    @Test
    void multipleCompleteDefenseRollsHaveNoConfiguredAggregationRule() {
        DefenseMethod defense = activeDefense(DefenseMethod.ACTIVE_ROLL_ADDITIVE_TOTAL, 2, 1, 20);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            consumer.generateDefenseData(defense, roller(16, 8))
        );

        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, value.availability());
        assertEquals(AttackSequenceConsumer.REASON_DEFENSE_ROLL_AGGREGATION_MISSING, value.reason());
    }

    @Test
    void successCountBaseModifierHasNoConfiguredMeaning() {
        DefenseMethod defense = activeDefense(DefenseMethod.ACTIVE_ROLL_SUCCESS_COUNT, 1, 3, 10);
        defense.setSuccessThreshold(7);
        defense.setBaseRollModifier(2);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            consumer.generateDefenseData(defense, roller(6, 8, 9))
        );

        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, value.availability());
        assertTrue(value.reason().contains("Base Roll Modifier"));
    }

    @Test
    void rollUnderDefenseCannotIdentifyItsResolutionValue() {
        DefenseMethod defense = activeDefense(DefenseMethod.ACTIVE_ROLL_ROLL_UNDER, 1, 1, 20);
        defense.setRollTargetNumber(12);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            consumer.generateDefenseData(defense, roller(8))
        );

        assertEquals(AttackSequenceConsumer.Availability.INDETERMINATE, value.availability());
        assertEquals(AttackSequenceConsumer.REASON_ROLL_UNDER_RESULT_MISSING, value.reason());
    }

    @Test
    void attackModifierDefenseExplicitlyHasNoStandaloneDefenseValue() {
        DefenseMethod defense = new DefenseMethod("Attack Adjustment");
        defense.setDefenseMode(DefenseMethod.MODE_ATTACK_MODIFIER);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            new AttackSequenceConsumer.RawDefenseData(List.of(), OptionalInt.empty())
        );

        assertEquals(AttackSequenceConsumer.Availability.NOT_APPLICABLE, value.availability());
    }

    @Test
    void noAccuracyDefenseExplicitlyHasNoStandaloneDefenseValue() {
        DefenseMethod defense = new DefenseMethod("No Defense");
        defense.setDefenseMode(DefenseMethod.MODE_NONE);

        AttackSequenceConsumer.ValueDerivation value = consumer.deriveDefenseValue(
            defense,
            new AttackSequenceConsumer.RawDefenseData(List.of(), OptionalInt.empty())
        );

        assertEquals(AttackSequenceConsumer.Availability.NOT_APPLICABLE, value.availability());
    }

    @Test
    void resolvesAttackVersusPassiveDefense() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 15, 12)
        );

        assertEquals(15, result.attackValue().orElseThrow());
        assertEquals(12, result.defenseValue().orElseThrow());
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void resolvesAttackVersusGeneratedDefenseResult() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_EXCEED,
            false
        );

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 11, 13)
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.FAILED, result.attackSuccess());
    }

    @Test
    void resolvesDefenseVersusThreatAndInvertsAWinningDefense() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_DEFENSE_VS_THREAT,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            false
        );

        AttackSequenceConsumer.ResolutionResult blocked = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.defenseVersusThreat("", 14, 12)
        );
        AttackSequenceConsumer.ResolutionResult penetrated = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.defenseVersusThreat("", 9, 12)
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.FAILED, blocked.attackSuccess());
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, penetrated.attackSuccess());
    }

    @Test
    void resolvesAutomaticContactWithoutAttackOrDefenseValues() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_AUTOMATIC,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            false
        );
        resolution.setAutomaticOutcomeKey("contact");

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.automatic()
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
        assertEquals("contact", result.outcomeKey());
        assertTrue(result.attackValue().isEmpty());
        assertTrue(result.defenseValue().isEmpty());
    }

    @Test
    void resolvesMeetOrExceedComparison() {
        assertTrue(AttackResolution.resolveMeetOrExceedComparison(10, 10));
        assertFalse(AttackResolution.resolveMeetOrExceedComparison(9, 10));
    }

    @Test
    void resolvesStrictExceedComparison() {
        assertTrue(AttackResolution.resolveStrictExceedComparison(11, 10));
        assertFalse(AttackResolution.resolveStrictExceedComparison(10, 10));
    }

    @Test
    void attackerWinsTiesIncludesEqualityForOpposedResults() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_EXCEED,
            true
        );

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 10, 10)
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void initialRollDirectionUsesTheReusableEqualityBoolean() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            false
        );

        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        AttackSequenceConsumer.ResolutionResult strictOver = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 10, 10)
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);
        AttackSequenceConsumer.ResolutionResult strictUnder = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 10, 10)
        );
        resolution.setAttackerWinsTies(true);
        AttackSequenceConsumer.ResolutionResult inclusiveUnder = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 10, 10)
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_OVER);
        AttackSequenceConsumer.ResolutionResult inclusiveOver = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 10, 10)
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.FAILED, strictOver.attackSuccess());
        assertEquals(AttackSequenceConsumer.AttackSuccess.FAILED, strictUnder.attackSuccess());
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, inclusiveUnder.attackSuccess());
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, inclusiveOver.attackSuccess());
    }

    @ParameterizedTest
    @MethodSource("directCoreComparisonCases")
    void coreOwnsEveryDirectAttackVersusDefenseComparator(
        String direction,
        boolean attackerWinsTies,
        int successfulAttackValue,
        int expectedMargin,
        AttackResolution.AttackSuccess equalityResult
    ) {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            attackerWinsTies
        );
        resolution.setAttackRollDirection(direction);

        AttackResolution.ResolutionResult successful = resolution.resolve(
            AttackResolution.ResolutionInput.attackVersusPassive(
                "",
                successfulAttackValue,
                10
            )
        );
        AttackResolution.ResolutionResult equal = resolution.resolve(
            AttackResolution.ResolutionInput.attackVersusPassive("", 10, 10)
        );

        assertEquals(AttackResolution.AttackSuccess.SUCCEEDED, successful.attackSuccess());
        assertEquals(successfulAttackValue, successful.attackValue().orElseThrow());
        assertEquals(10, successful.defenseValue().orElseThrow());
        assertEquals(expectedMargin, successful.margin().orElseThrow());
        assertEquals(equalityResult, equal.attackSuccess());
    }

    @Test
    void resolvesLowerWinsComparison() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_LOWER_WINS,
            false
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 4, 7)
        );

        assertTrue(AttackResolution.resolveLowerWinsComparison(4, 7));
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void passiveDefenseResolutionUsesThePersistedRollDirection() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            false
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusPassive("", 6, 10)
        );

        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void uncheckedDefenseTargetResolvesTheAttackValueThroughTheRetainedChart() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            false
        );
        resolution.setTargetValueIsDefenseValue(false);
        resolution.setOutcomeBands(List.of(
            new AttackResolution.OutcomeBand(1, 6, "miss", "Miss", ""),
            new AttackResolution.OutcomeBand(7, 20, "contact", "Contact", "")
        ));

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            new AttackSequenceConsumer.ResolutionInput(
                "",
                OptionalInt.of(14),
                OptionalInt.empty(),
                OptionalInt.empty()
            )
        );

        assertEquals("contact", result.outcomeKey());
        assertTrue(result.defenseValue().isEmpty());
        assertEquals(AttackSequenceConsumer.AttackSuccess.INDETERMINATE, result.attackSuccess());
    }

    @Test
    void targetCheckboxAndRollDirectionSerializeWithoutDiscardingAttackChart() throws Exception {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );
        resolution.setAttackRollDirection(AttackResolution.ROLL_DIRECTION_UNDER);
        resolution.setTargetValueIsDefenseValue(false);
        resolution.setOutcomeBands(List.of(
            new AttackResolution.OutcomeBand(1, 20, "result", "Result", "")
        ));
        resolution.setTargetValueIsDefenseValue(true);
        resolution.setAttackPoolResolutionMethod(AttackResolution.POOL_RESOLUTION_HIGHEST_DIE);
        resolution.setAttackPoolSuccessThreshold(7);

        AttackResolution restored;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(resolution);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (AttackResolution) input.readObject();
        }

        assertEquals(AttackResolution.ROLL_DIRECTION_UNDER, restored.getAttackRollDirection());
        assertTrue(restored.isAttackerWinsTies());
        assertTrue(restored.isTargetValueDefenseValue());
        assertEquals(
            AttackResolution.POOL_RESOLUTION_HIGHEST_DIE,
            restored.getAttackPoolResolutionMethod()
        );
        assertEquals(7, restored.getAttackPoolSuccessThreshold());
        assertEquals("result", restored.getOutcomeBands().get(0).getOutcomeKey());
    }

    @ParameterizedTest
    @MethodSource("legacyInitialResolutionSelections")
    void olderResolutionDataInfersTheNewInitialSelection(
        String comparisonMethod,
        String expectedDirection,
        boolean expectedDefenseTarget
    ) throws Exception {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            comparisonMethod,
            false
        );
        Field configured = AttackResolution.class.getDeclaredField(
            "initialResolutionSelectionConfigured"
        );
        configured.setAccessible(true);
        configured.setBoolean(resolution, false);

        AttackResolution restored;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(resolution);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (AttackResolution) input.readObject();
        }

        assertEquals(expectedDirection, restored.getAttackRollDirection());
        assertEquals(expectedDefenseTarget, restored.isTargetValueDefenseValue());
    }

    @Test
    void olderTieSettingsMigrateToTheEqualityBoolean() throws Exception {
        for (String legacyTie : List.of("attacker", "defender", "outcome")) {
            AttackResolution resolution = resolution(
                AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
                AttackResolution.COMPARISON_MEET_OR_EXCEED,
                false
            );
            Field configured = AttackResolution.class.getDeclaredField("equalityHandlingConfigured");
            configured.setAccessible(true);
            configured.setBoolean(resolution, false);
            Field storedTie = AttackResolution.class.getDeclaredField("tieResolution");
            storedTie.setAccessible(true);
            storedTie.set(resolution, legacyTie);

            AttackResolution restored;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(resolution);
            }
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                restored = (AttackResolution) input.readObject();
            }

            assertEquals("attacker".equals(legacyTie), restored.isAttackerWinsTies());
        }
    }

    @Test
    void resolvesSuccessCountComparison() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_SUCCESS_COUNT,
            true
        );

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 3, 2)
        );

        assertTrue(AttackResolution.resolveSuccessCountComparison(3, 2));
        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @ParameterizedTest
    @MethodSource("outcomeMetrics")
    void resolvesEveryOutcomeBandMetric(
        String outcomeMetric,
        int minimum,
        int maximum,
        String expectedOutcome
    ) {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_OUTCOME_BANDS,
            false
        );
        resolution.setOutcomeMetric(outcomeMetric);
        resolution.setOutcomeBands(List.of(
            new AttackResolution.OutcomeBand(minimum, maximum, expectedOutcome, expectedOutcome, "")
        ));

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 12, 8)
        );

        assertEquals(expectedOutcome, result.outcomeKey());
        assertEquals(AttackSequenceConsumer.AttackSuccess.INDETERMINATE, result.attackSuccess());
        assertEquals(
            AttackSequenceConsumer.REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING,
            result.reason()
        );
    }

    @Test
    void outcomeBandsResolveEqualValuesThroughTheirRangesInsteadOfEqualityHandling() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_OUTCOME_BANDS,
            false
        );
        resolution.setOutcomeMetric(AttackResolution.OUTCOME_METRIC_ATTACK_RESULT);
        resolution.setOutcomeBands(List.of(
            new AttackResolution.OutcomeBand(10, 10, "equal-value", "Equal Value", "")
        ));

        AttackSequenceConsumer.ResolutionResult result = consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 10, 10)
        );

        assertEquals("equal-value", result.outcomeKey());
        assertEquals(AttackSequenceConsumer.AttackSuccess.INDETERMINATE, result.attackSuccess());
    }

    @Test
    void attackerWinsEqualityWhenConfigured() {
        AttackSequenceConsumer.ResolutionResult result = resolveEquality(true);

        assertEquals(AttackSequenceConsumer.AttackSuccess.SUCCEEDED, result.attackSuccess());
    }

    @Test
    void defenderWinsEqualityWhenAttackerDoesNot() {
        AttackSequenceConsumer.ResolutionResult result = resolveEquality(false);

        assertEquals(AttackSequenceConsumer.AttackSuccess.FAILED, result.attackSuccess());
    }

    @ParameterizedTest
    @MethodSource("sourceKinds")
    void selectsEveryAttackSourceKind(String sourceKind) {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );
        AttackResolution.AttackSourceRoute route = new AttackResolution.AttackSourceRoute(
            sourceKind + " route",
            sourceKind,
            sourceKind + "s",
            sourceKind + "-id",
            ""
        );
        String routeId = resolution.addAttackSourceRoute(route);

        AttackResolution.AttackSourceRoute selected = resolution
            .selectAttackSourceRoute(routeId)
            .orElseThrow();

        assertEquals(sourceKind, selected.getSourceKind());
    }

    @Test
    void missingRequestedRouteUsesOnlyTheConfiguredDefault() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );
        String cardRouteId = resolution.addAttackSourceRoute(
            new AttackResolution.AttackSourceRoute(
                "Card",
                AttackResolution.SOURCE_CARD,
                "cards",
                "card-id",
                ""
            )
        );
        resolution.setDefaultAttackSourceRouteId(cardRouteId);

        AttackResolution.AttackSourceRoute selected = resolution
            .selectAttackSourceRoute("missing")
            .orElseThrow();

        assertEquals(cardRouteId, selected.getId());
    }

    @Test
    void comparedModeRejectsMissingRequiredRuntimeValue() {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_PASSIVE,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            true
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> consumer.resolve(resolution, AttackSequenceConsumer.ResolutionInput.automatic())
        );
    }

    private AttackSequenceConsumer.ResolutionResult resolveEquality(boolean attackerWinsTies) {
        AttackResolution resolution = resolution(
            AttackResolution.MODE_ATTACK_VS_DEFENSE_RESULT,
            AttackResolution.COMPARISON_MEET_OR_EXCEED,
            attackerWinsTies
        );
        return consumer.resolve(
            resolution,
            AttackSequenceConsumer.ResolutionInput.attackVersusDefense("", 10, 10)
        );
    }

    private static AttackMethod attackDice(int numberOfRolls, int dicePerRoll, int dieSides) {
        AttackMethod attack = new AttackMethod("Attack");
        attack.setDiceRolled(true);
        attack.setNumberOfRolls(numberOfRolls);
        attack.setNumberOfDiceRolled(dicePerRoll);
        attack.setDieSides(dieSides);
        return attack;
    }

    private static DefenseMethod activeDefense(
        String evaluation,
        int numberOfRolls,
        int dicePerRoll,
        int dieSides
    ) {
        DefenseMethod defense = new DefenseMethod("Active Defense");
        defense.setDefenseMode(DefenseMethod.MODE_ACTIVE_ROLL);
        defense.setActiveRollEvaluation(evaluation);
        defense.setNumberOfRolls(numberOfRolls);
        defense.setNumberOfDiceRolled(dicePerRoll);
        defense.setDieSides(dieSides);
        return defense;
    }

    private static AttackResolution resolution(String mode, String comparison, boolean attackerWinsTies) {
        AttackResolution resolution = new AttackResolution("Resolution");
        resolution.setResolutionMode(mode);
        resolution.setComparisonMethod(comparison);
        resolution.setAttackerWinsTies(attackerWinsTies);
        return resolution;
    }

    private static AttackSequenceConsumer.DieRoller roller(int... values) {
        Queue<Integer> remaining = new ArrayDeque<>();
        for (int value : values) {
            remaining.add(value);
        }
        return dieSides -> {
            if (remaining.isEmpty()) {
                throw new IllegalStateException("No test die value remains.");
            }
            return remaining.remove();
        };
    }

    private static Stream<Arguments> scalarPoolMethods() {
        return Stream.of(
            Arguments.of(
                AttackResolution.POOL_RESOLUTION_HIGHEST_DIE,
                List.of(2, 8, 5),
                8
            ),
            Arguments.of(
                AttackResolution.POOL_RESOLUTION_LOWEST_DIE,
                List.of(2, 8, 5),
                2
            ),
            Arguments.of(
                AttackResolution.POOL_RESOLUTION_SUM,
                List.of(2, 3, 4),
                9
            )
        );
    }

    private static Stream<Arguments> outcomeMetrics() {
        return Stream.of(
            Arguments.of(AttackResolution.OUTCOME_METRIC_ATTACK_RESULT, 12, 12, "attack-band"),
            Arguments.of(AttackResolution.OUTCOME_METRIC_DEFENSE_RESULT, 8, 8, "defense-band"),
            Arguments.of(AttackResolution.OUTCOME_METRIC_MARGIN, 4, 4, "margin-band")
        );
    }

    private static Stream<Arguments> directCoreComparisonCases() {
        return Stream.of(
            Arguments.of(
                AttackResolution.ROLL_DIRECTION_OVER,
                false,
                11,
                1,
                AttackResolution.AttackSuccess.FAILED
            ),
            Arguments.of(
                AttackResolution.ROLL_DIRECTION_OVER,
                true,
                11,
                1,
                AttackResolution.AttackSuccess.SUCCEEDED
            ),
            Arguments.of(
                AttackResolution.ROLL_DIRECTION_UNDER,
                false,
                9,
                -1,
                AttackResolution.AttackSuccess.FAILED
            ),
            Arguments.of(
                AttackResolution.ROLL_DIRECTION_UNDER,
                true,
                9,
                -1,
                AttackResolution.AttackSuccess.SUCCEEDED
            )
        );
    }

    private static Stream<String> sourceKinds() {
        return Stream.of(
            AttackResolution.SOURCE_ATTACK_METHOD,
            AttackResolution.SOURCE_CARD,
            AttackResolution.SOURCE_ATTRIBUTE,
            AttackResolution.SOURCE_SKILL,
            AttackResolution.SOURCE_GEAR,
            AttackResolution.SOURCE_OTHER
        );
    }

    private static Stream<Arguments> legacyInitialResolutionSelections() {
        return Stream.of(
            Arguments.of(
                AttackResolution.COMPARISON_MEET_OR_EXCEED,
                AttackResolution.ROLL_DIRECTION_OVER,
                true
            ),
            Arguments.of(
                AttackResolution.COMPARISON_LOWER_WINS,
                AttackResolution.ROLL_DIRECTION_UNDER,
                true
            ),
            Arguments.of(
                AttackResolution.COMPARISON_OUTCOME_BANDS,
                AttackResolution.ROLL_DIRECTION_OVER,
                false
            )
        );
    }

    private static Set<String> stringConstantsWithPrefix(String prefix) {
        return Stream.of(AttackResolution.class.getFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> field.getType().equals(String.class))
            .filter(field -> field.getName().startsWith(prefix))
            .map(AttackSequenceConsumerTest::readStringConstant)
            .collect(Collectors.toUnmodifiableSet());
    }

    private static String readStringConstant(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException exception) {
            throw new AssertionError("Could not read " + field.getName(), exception);
        }
    }
}
