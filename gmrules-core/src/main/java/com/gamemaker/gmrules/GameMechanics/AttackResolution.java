/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.GameElement;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/**
 * Configures source-neutral comparison of generated attack and defense results.
 *
 * <p>Every attack occurrence selects an {@link AttackSourceRoute} by stable route
 * id. If a caller has no more-specific route, it uses the configured default. This
 * makes hybrid dice, card, Attribute, Skill, gear, and other attacks explicit and
 * prevents descendant applications from guessing a source from empty collections.</p>
 *
 * <p>Resolution may compare an attack with a passive defense, compare separately
 * generated attack and defense results, compare a defender-only result with an
 * attack-supplied threat, or resolve contact automatically. Comparison and outcome
 * configuration is independent of how either side generated its values.</p>
 *
 * <p>This class is the ruleset source of truth for turning core-generated attack
 * and defense values into an attack result. Consumers ask {@link AttackMethod}
 * and {@link DefenseMethod} to generate their values, may present or react to
 * those intermediate results, and then pass both {@link GeneratedValue} objects
 * to {@link #getAttackResult(GeneratedValue, GeneratedValue)}. Consumers do not
 * recreate generation formulas, comparisons, or resolution-section selection.</p>
 */
public class AttackResolution extends GameElement {

    // *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    public static final String MODE_ATTACK_VS_PASSIVE = "attack_vs_passive";
    public static final String MODE_ATTACK_VS_DEFENSE_RESULT = "attack_vs_defense_result";
    public static final String MODE_DEFENSE_VS_THREAT = "defense_vs_threat";
    public static final String MODE_AUTOMATIC = "automatic";

    public static final String COMPARISON_MEET_OR_EXCEED = "meet_or_exceed";
    public static final String COMPARISON_EXCEED = "exceed";
    public static final String COMPARISON_LOWER_WINS = "lower_wins";
    public static final String COMPARISON_SUCCESS_COUNT = "success_count";
    public static final String COMPARISON_OUTCOME_BANDS = "outcome_bands";

    public static final String ROLL_DIRECTION_OVER = "over";
    public static final String ROLL_DIRECTION_UNDER = "under";

    public static final String POOL_RESOLUTION_SUCCESS_COUNT = "success_count";
    public static final String POOL_RESOLUTION_HIGHEST_DIE = "highest_die";
    public static final String POOL_RESOLUTION_LOWEST_DIE = "lowest_die";
    public static final String POOL_RESOLUTION_SUM = "sum";

    public static final String OUTCOME_METRIC_ATTACK_RESULT = "attack_result";
    public static final String OUTCOME_METRIC_DEFENSE_RESULT = "defense_result";
    public static final String OUTCOME_METRIC_MARGIN = "margin";

    public static final String SOURCE_ATTACK_METHOD = "attack_method";
    public static final String SOURCE_CARD = "card";
    public static final String SOURCE_ATTRIBUTE = "attribute";
    public static final String SOURCE_SKILL = "skill";
    public static final String SOURCE_GEAR = "gear";
    public static final String SOURCE_OTHER = "other";

    public static final String REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING =
        "OutcomeBand does not classify its outcome as attack success or failure.";
    public static final String REASON_ATTACK_CHART_ENTRY_MISSING =
        "No configured attack-chart entry includes the generated attack value.";
    public static final String REASON_OUTCOME_BAND_MISSING =
        "No configured OutcomeBand includes the calculated metric.";
    public static final String REASON_ATTACK_POOL_THRESHOLD_MISSING =
        "AttackResolution requires a minimum successful roll for attack pools.";

    private String resolutionMode = MODE_AUTOMATIC;
    private String comparisonMethod = COMPARISON_MEET_OR_EXCEED;
    /** Retained only so older serialized rulesets can migrate their tie setting. */
    private String tieResolution = "";
    private boolean attackerWinsTies = true;
    private boolean equalityHandlingConfigured = true;
    private String attackRollDirection = ROLL_DIRECTION_OVER;
    private boolean targetValueIsDefenseValue = true;
    private boolean initialResolutionSelectionConfigured = true;
    private String attackPoolResolutionMethod = POOL_RESOLUTION_SUCCESS_COUNT;
    private int attackPoolSuccessThreshold = 0;
    private String outcomeMetric = OUTCOME_METRIC_MARGIN;
    private String automaticOutcomeKey = "contact";
    private ArrayList<AttackSourceRoute> attackSourceRoutes = new ArrayList<>();
    private String defaultAttackSourceRouteId = "";
    private ArrayList<OutcomeBand> outcomeBands = new ArrayList<>();

    // *** CONSTRUCTORS ***
    public AttackResolution(String name) {
        super(name);
        initializeDefaultAttackSourceRoute();
    }

    public AttackResolution(String name, String description) {
        super(name, description);
        initializeDefaultAttackSourceRoute();
    }

    // *** METHODS ***
    public String getResolutionMode() {
        return resolutionMode;
    }

    public void setResolutionMode(String resolutionMode) {
        this.resolutionMode = normalizeResolutionMode(resolutionMode);
    }

    public boolean comparesAttackToPassiveDefense() {
        return MODE_ATTACK_VS_PASSIVE.equals(resolutionMode);
    }

    public boolean comparesAttackAndDefenseResults() {
        return MODE_ATTACK_VS_DEFENSE_RESULT.equals(resolutionMode);
    }

    public boolean usesDefenderOnlyResolution() {
        return MODE_DEFENSE_VS_THREAT.equals(resolutionMode);
    }

    public boolean usesAutomaticContact() {
        return MODE_AUTOMATIC.equals(resolutionMode);
    }

    public String getComparisonMethod() {
        return comparisonMethod;
    }

    public void setComparisonMethod(String comparisonMethod) {
        this.comparisonMethod = normalizeComparisonMethod(comparisonMethod);
    }

    public boolean isAttackerWinsTies() {
        return attackerWinsTies;
    }

    /**
     * Defines equality once for every comparison that can produce equal values.
     * When false, equality belongs to the defender.
     */
    public void setAttackerWinsTies(boolean attackerWinsTies) {
        this.attackerWinsTies = attackerWinsTies;
        equalityHandlingConfigured = true;
    }

    public String getAttackRollDirection() {
        return attackRollDirection;
    }

    public void setAttackRollDirection(String attackRollDirection) {
        this.attackRollDirection = normalizeAttackRollDirection(attackRollDirection);
        initialResolutionSelectionConfigured = true;
    }

    public boolean isTargetValueDefenseValue() {
        return targetValueIsDefenseValue;
    }

    /**
     * Selects direct comparison with the generated defense value. When false,
     * the attack result is resolved through the retained attack outcome chart.
     */
    public void setTargetValueIsDefenseValue(boolean targetValueIsDefenseValue) {
        this.targetValueIsDefenseValue = targetValueIsDefenseValue;
        initialResolutionSelectionConfigured = true;
    }

    public String getAttackPoolResolutionMethod() {
        return attackPoolResolutionMethod;
    }

    public void setAttackPoolResolutionMethod(String attackPoolResolutionMethod) {
        this.attackPoolResolutionMethod = normalizeAttackPoolResolutionMethod(
            attackPoolResolutionMethod
        );
    }

    /**
     * Returns the inclusive per-die threshold used to count attack-pool successes.
     * It is a minimum for roll-over attacks and a maximum for roll-under attacks.
     */
    public int getAttackPoolSuccessThreshold() {
        return attackPoolSuccessThreshold;
    }

    /** Zero represents an attack-pool threshold that has not been configured yet. */
    public void setAttackPoolSuccessThreshold(int attackPoolSuccessThreshold) {
        this.attackPoolSuccessThreshold = Math.max(0, attackPoolSuccessThreshold);
    }

    public String getOutcomeMetric() {
        return outcomeMetric;
    }

    public void setOutcomeMetric(String outcomeMetric) {
        this.outcomeMetric = normalizeOutcomeMetric(outcomeMetric);
    }

    public String getAutomaticOutcomeKey() {
        return automaticOutcomeKey;
    }

    public void setAutomaticOutcomeKey(String automaticOutcomeKey) {
        this.automaticOutcomeKey = normalizeKey(automaticOutcomeKey);
    }

    public ArrayList<AttackSourceRoute> getAttackSourceRoutes() {
        return copyAttackSourceRoutes(attackSourceRoutes);
    }

    public void setAttackSourceRoutes(Collection<AttackSourceRoute> attackSourceRoutes) {
        this.attackSourceRoutes = copyAttackSourceRoutes(attackSourceRoutes);
        normalizeDefaultAttackSourceRoute();
    }

    public String addAttackSourceRoute(AttackSourceRoute attackSourceRoute) {
        AttackSourceRoute copy = new AttackSourceRoute(
            Objects.requireNonNullElseGet(attackSourceRoute, AttackSourceRoute::new)
        );
        if (findStoredAttackSourceRoute(copy.getId()).isPresent()) {
            copy.setId("");
        }
        attackSourceRoutes.add(copy);
        if (defaultAttackSourceRouteId.isEmpty()) {
            defaultAttackSourceRouteId = copy.getId();
        }
        return copy.getId();
    }

    public boolean removeAttackSourceRoute(String routeId) {
        String safeRouteId = normalizeId(routeId);
        boolean removed = attackSourceRoutes.removeIf(route -> route.getId().equals(safeRouteId));
        normalizeDefaultAttackSourceRoute();
        return removed;
    }

    public String getDefaultAttackSourceRouteId() {
        return defaultAttackSourceRouteId;
    }

    public void setDefaultAttackSourceRouteId(String defaultAttackSourceRouteId) {
        String safeRouteId = normalizeId(defaultAttackSourceRouteId);
        this.defaultAttackSourceRouteId = findStoredAttackSourceRoute(safeRouteId)
            .map(AttackSourceRoute::getId)
            .orElseGet(this::firstAttackSourceRouteId);
    }

    /**
     * Selects an explicitly requested route, falling back only to the configured default.
     */
    public Optional<AttackSourceRoute> selectAttackSourceRoute(String requestedRouteId) {
        String safeRouteId = normalizeId(requestedRouteId);
        return findStoredAttackSourceRoute(safeRouteId)
            .or(() -> findStoredAttackSourceRoute(defaultAttackSourceRouteId))
            .map(AttackSourceRoute::new);
    }

    public boolean hasCompleteAttackSourceRouting() {
        return usesAutomaticContact()
            || (!attackSourceRoutes.isEmpty()
                && findStoredAttackSourceRoute(defaultAttackSourceRouteId).isPresent());
    }

    public ArrayList<OutcomeBand> getOutcomeBands() {
        return copyOutcomeBands(outcomeBands);
    }

    public void setOutcomeBands(Collection<OutcomeBand> outcomeBands) {
        this.outcomeBands = copyOutcomeBands(outcomeBands);
    }

    public void addOutcomeBand(OutcomeBand outcomeBand) {
        outcomeBands.add(new OutcomeBand(Objects.requireNonNullElseGet(outcomeBand, OutcomeBand::new)));
        outcomeBands.sort(Comparator.comparingInt(OutcomeBand::getMinimumValue));
    }

    public boolean removeOutcomeBand(String outcomeKey) {
        String safeOutcomeKey = normalizeKey(outcomeKey);
        return outcomeBands.removeIf(band -> band.getOutcomeKey().equals(safeOutcomeKey));
    }

    public Optional<OutcomeBand> resolveOutcomeBand(int metricValue) {
        return outcomeBands.stream()
            .filter(band -> band.includes(metricValue))
            .findFirst()
            .map(OutcomeBand::new);
    }

    public boolean hasValidOutcomeBands() {
        if (targetValueIsDefenseValue && !COMPARISON_OUTCOME_BANDS.equals(comparisonMethod)) {
            return true;
        }
        if (outcomeBands.isEmpty()) {
            return false;
        }
        Set<String> outcomeKeys = new HashSet<>();
        int previousMaximum = Integer.MIN_VALUE;
        boolean first = true;
        for (OutcomeBand band : outcomeBands) {
            if (band.getOutcomeKey().isEmpty() || !outcomeKeys.add(band.getOutcomeKey())) {
                return false;
            }
            if (!first && band.getMinimumValue() <= previousMaximum) {
                return false;
            }
            previousMaximum = band.getMaximumValue();
            first = false;
        }
        return true;
    }

    // === RUNTIME RESOLUTION ===

    /** Resolves caller-obtained core attack and defense values through the configured section. */
    public AttackResult getAttackResult(
        GeneratedValue attack,
        GeneratedValue defense
    ) {
        return getAttackResult("", attack, defense);
    }

    /**
     * Resolves caller-obtained values for an explicitly selected attack-source route.
     * A blank or unknown route id uses only the configured default fallback.
     */
    public AttackResult getAttackResult(
        String requestedRouteId,
        GeneratedValue attack,
        GeneratedValue defense
    ) {
        GeneratedValue safeAttack = Objects.requireNonNull(attack, "attack");
        GeneratedValue safeDefense = Objects.requireNonNull(defense, "defense");
        if (usesAttackPoolAgainstPassiveDefense(safeAttack)) {
            GeneratedValue reducedAttack = reduceAttackPool(safeAttack);
            int passiveDefense = requireValue(
                safeDefense.value(),
                "passive defense value"
            );
            ResolutionResult resolution = POOL_RESOLUTION_SUCCESS_COUNT.equals(
                attackPoolResolutionMethod
            )
                ? resolveAttackPoolSuccessCountAgainstPassiveDefense(
                    requestedRouteId,
                    reducedAttack.requireValue(),
                    passiveDefense
                )
                : resolveAttackPoolValueAgainstPassiveDefense(
                    requestedRouteId,
                    reducedAttack.requireValue(),
                    passiveDefense
                );
            return new AttackResult(
                reducedAttack,
                safeDefense,
                resolution
            );
        }
        ResolutionInput input = switch (resolutionMode) {
            case MODE_DEFENSE_VS_THREAT -> new ResolutionInput(
                requestedRouteId,
                OptionalInt.empty(),
                safeDefense.value(),
                safeAttack.value()
            );
            case MODE_AUTOMATIC -> ResolutionInput.automatic();
            default -> new ResolutionInput(
                requestedRouteId,
                safeAttack.value(),
                safeDefense.value(),
                OptionalInt.empty()
            );
        };
        return new AttackResult(safeAttack, safeDefense, resolve(input));
    }

    /** Reduces one generated attack pool through the configured core method. */
    public GeneratedValue reduceAttackPool(GeneratedValue attack) {
        return switch (attackPoolResolutionMethod) {
            case POOL_RESOLUTION_HIGHEST_DIE -> reduceAttackPoolToHighestDie(attack);
            case POOL_RESOLUTION_LOWEST_DIE -> reduceAttackPoolToLowestDie(attack);
            case POOL_RESOLUTION_SUM -> reduceAttackPoolToSum(attack);
            default -> reduceAttackPoolToSuccessCount(attack);
        };
    }

    /** Counts dice meeting the configured inclusive over/under threshold. */
    public GeneratedValue reduceAttackPoolToSuccessCount(GeneratedValue attack) {
        GeneratedValue safeAttack = Objects.requireNonNull(attack, "attack");
        if (attackPoolSuccessThreshold <= 0) {
            throw new IllegalStateException(REASON_ATTACK_POOL_THRESHOLD_MISSING);
        }
        if (!usesSingleAttackPool(safeAttack)) {
            throw new IllegalArgumentException(
                "Attack-pool success counting requires one complete roll containing multiple dice."
            );
        }
        int successes = (int) safeAttack.rolls().get(0).stream()
            .filter(this::attackPoolDieSucceeds)
            .count();
        return GeneratedValue.available(successes, safeAttack.rolls());
    }

    /** Uses the highest die in one generated attack pool as its attack value. */
    public GeneratedValue reduceAttackPoolToHighestDie(GeneratedValue attack) {
        GeneratedValue safeAttack = requireSingleAttackPool(attack);
        int highestDie = safeAttack.rolls().get(0).stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElseThrow();
        return GeneratedValue.available(highestDie, safeAttack.rolls());
    }

    /** Uses the lowest die in one generated attack pool as its attack value. */
    public GeneratedValue reduceAttackPoolToLowestDie(GeneratedValue attack) {
        GeneratedValue safeAttack = requireSingleAttackPool(attack);
        int lowestDie = safeAttack.rolls().get(0).stream()
            .mapToInt(Integer::intValue)
            .min()
            .orElseThrow();
        return GeneratedValue.available(lowestDie, safeAttack.rolls());
    }

    /** Sums every die in one generated attack pool into its attack value. */
    public GeneratedValue reduceAttackPoolToSum(GeneratedValue attack) {
        GeneratedValue safeAttack = requireSingleAttackPool(attack);
        int total = safeAttack.rolls().get(0).stream()
            .mapToInt(Integer::intValue)
            .sum();
        return GeneratedValue.available(total, safeAttack.rolls());
    }

    /** Resolves an attack-pool success count against its minimum passive requirement. */
    public ResolutionResult resolveAttackPoolSuccessCountAgainstPassiveDefense(
        String requestedRouteId,
        int attackSuccesses,
        int requiredSuccesses
    ) {
        boolean attackSucceeded = resolveDirectionalComparison(
            attackSuccesses,
            requiredSuccesses,
            ROLL_DIRECTION_OVER,
            attackerWinsTies
        );
        return comparedResult(
            attackSucceeded ? AttackSuccess.SUCCEEDED : AttackSuccess.FAILED,
            "",
            attackSuccesses,
            requiredSuccesses,
            requireAttackSourceRoute(requestedRouteId),
            ""
        );
    }

    /** Resolves a highest-die, lowest-die, or summed pool value against passive Defense. */
    public ResolutionResult resolveAttackPoolValueAgainstPassiveDefense(
        String requestedRouteId,
        int attackValue,
        int passiveDefense
    ) {
        boolean attackSucceeded = resolveDirectionalComparison(
            attackValue,
            passiveDefense,
            attackRollDirection,
            attackerWinsTies
        );
        return comparedResult(
            attackSucceeded ? AttackSuccess.SUCCEEDED : AttackSuccess.FAILED,
            "",
            attackValue,
            passiveDefense,
            requireAttackSourceRoute(requestedRouteId),
            ""
        );
    }

    /** Dispatches the configured resolution section using caller-supplied runtime values. */
    public ResolutionResult resolve(ResolutionInput input) {
        ResolutionInput safeInput = Objects.requireNonNullElseGet(input, ResolutionInput::empty);
        return switch (resolutionMode) {
            case MODE_ATTACK_VS_PASSIVE -> resolveAttackVersusPassive(safeInput);
            case MODE_ATTACK_VS_DEFENSE_RESULT -> resolveAttackVersusDefenseResult(safeInput);
            case MODE_DEFENSE_VS_THREAT -> resolveDefenseVersusThreat(safeInput);
            default -> resolveAutomaticContact();
        };
    }

    // --- Attack value versus passive Defense value ---

    public ResolutionResult resolveAttackVersusPassive(ResolutionInput input) {
        ResolutionInput safeInput = Objects.requireNonNullElseGet(input, ResolutionInput::empty);
        int attackValue = requireValue(safeInput.attackValue(), "attack value");
        if (!targetValueIsDefenseValue) {
            return resolveAttackChart(safeInput.requestedRouteId(), attackValue);
        }
        int defenseValue = requireValue(safeInput.defenseValue(), "passive defense value");
        return resolveInitialDirectComparison(
            safeInput.requestedRouteId(),
            attackValue,
            defenseValue
        );
    }

    // --- Attack value versus generated Defense result ---

    public ResolutionResult resolveAttackVersusDefenseResult(ResolutionInput input) {
        ResolutionInput safeInput = Objects.requireNonNullElseGet(input, ResolutionInput::empty);
        int attackValue = requireValue(safeInput.attackValue(), "attack value");
        int defenseValue = requireValue(safeInput.defenseValue(), "defense result");
        return resolveConfiguredComparison(
            safeInput.requestedRouteId(),
            attackValue,
            defenseValue,
            true
        );
    }

    // --- Defender-only result versus attack-supplied threat ---

    public ResolutionResult resolveDefenseVersusThreat(ResolutionInput input) {
        ResolutionInput safeInput = Objects.requireNonNullElseGet(input, ResolutionInput::empty);
        int defenseValue = requireValue(safeInput.defenseValue(), "defense result");
        int threatValue = requireValue(safeInput.threatValue(), "threat value");
        return resolveConfiguredComparison(
            safeInput.requestedRouteId(),
            threatValue,
            defenseValue,
            false
        );
    }

    // --- Automatic contact ---

    public ResolutionResult resolveAutomaticContact() {
        return new ResolutionResult(
            AttackSuccess.SUCCEEDED,
            automaticOutcomeKey,
            OptionalInt.empty(),
            OptionalInt.empty(),
            OptionalInt.empty(),
            "",
            "",
            ""
        );
    }

    // === REUSABLE COMPARISON RULES ===

    public static boolean resolveDirectionalComparison(
        int actingValue,
        int opposingValue,
        String direction,
        boolean actingSideWinsTies
    ) {
        boolean rollUnder = ROLL_DIRECTION_UNDER.equals(normalizeAttackRollDirection(direction));
        return rollUnder
            ? actingSideWinsTies
                ? actingValue <= opposingValue
                : actingValue < opposingValue
            : actingSideWinsTies
                ? actingValue >= opposingValue
                : actingValue > opposingValue;
    }

    public static boolean resolveMeetOrExceedComparison(int actingValue, int opposingValue) {
        return resolveDirectionalComparison(
            actingValue,
            opposingValue,
            ROLL_DIRECTION_OVER,
            true
        );
    }

    public static boolean resolveStrictExceedComparison(int actingValue, int opposingValue) {
        return resolveDirectionalComparison(
            actingValue,
            opposingValue,
            ROLL_DIRECTION_OVER,
            false
        );
    }

    public static boolean resolveLowerWinsComparison(int actingValue, int opposingValue) {
        return resolveDirectionalComparison(
            actingValue,
            opposingValue,
            ROLL_DIRECTION_UNDER,
            false
        );
    }

    public static boolean resolveSuccessCountComparison(
        int actingSuccesses,
        int opposingSuccesses
    ) {
        return resolveDirectionalComparison(
            actingSuccesses,
            opposingSuccesses,
            ROLL_DIRECTION_OVER,
            true
        );
    }

    public static int attackResultMetric(int attackValue) {
        return attackValue;
    }

    public static int defenseResultMetric(int defenseValue) {
        return defenseValue;
    }

    /** Margin is defined once as the attack value minus the defense value. */
    public static int marginMetric(int attackValue, int defenseValue) {
        return attackValue - defenseValue;
    }

    private ResolutionResult resolveInitialDirectComparison(
        String requestedRouteId,
        int attackValue,
        int defenseValue
    ) {
        boolean attackSucceeded = resolveDirectionalComparison(
            attackValue,
            defenseValue,
            attackRollDirection,
            attackerWinsTies
        );
        return comparedResult(
            attackSucceeded ? AttackSuccess.SUCCEEDED : AttackSuccess.FAILED,
            "",
            attackValue,
            defenseValue,
            requireAttackSourceRoute(requestedRouteId),
            ""
        );
    }

    private ResolutionResult resolveConfiguredComparison(
        String requestedRouteId,
        int attackValue,
        int defenseValue,
        boolean actingSideIsAttacker
    ) {
        AttackSourceRoute route = requireAttackSourceRoute(requestedRouteId);
        int margin = marginMetric(attackValue, defenseValue);
        if (COMPARISON_OUTCOME_BANDS.equals(comparisonMethod)) {
            return resolveOutcomeBandComparison(attackValue, defenseValue, margin, route);
        }

        int actingValue = actingSideIsAttacker ? attackValue : defenseValue;
        int opposingValue = actingSideIsAttacker ? defenseValue : attackValue;
        if (actingValue == opposingValue) {
            return comparedResult(
                attackerWinsTies ? AttackSuccess.SUCCEEDED : AttackSuccess.FAILED,
                "",
                attackValue,
                defenseValue,
                route,
                ""
            );
        }

        boolean actingSideWon = switch (comparisonMethod) {
            case COMPARISON_LOWER_WINS ->
                resolveLowerWinsComparison(actingValue, opposingValue);
            case COMPARISON_SUCCESS_COUNT ->
                resolveSuccessCountComparison(actingValue, opposingValue);
            case COMPARISON_EXCEED ->
                resolveStrictExceedComparison(actingValue, opposingValue);
            default -> resolveMeetOrExceedComparison(actingValue, opposingValue);
        };
        boolean attackSucceeded = actingSideIsAttacker ? actingSideWon : !actingSideWon;
        return comparedResult(
            attackSucceeded ? AttackSuccess.SUCCEEDED : AttackSuccess.FAILED,
            "",
            attackValue,
            defenseValue,
            route,
            ""
        );
    }

    private ResolutionResult resolveAttackChart(String requestedRouteId, int attackValue) {
        AttackSourceRoute route = requireAttackSourceRoute(requestedRouteId);
        String outcomeKey = resolveOutcomeBand(attackValue)
            .map(OutcomeBand::getOutcomeKey)
            .orElse("");
        String reason = outcomeKey.isEmpty()
            ? REASON_ATTACK_CHART_ENTRY_MISSING
            : REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING;
        return new ResolutionResult(
            AttackSuccess.INDETERMINATE,
            outcomeKey,
            OptionalInt.of(attackValue),
            OptionalInt.empty(),
            OptionalInt.empty(),
            route.getId(),
            route.getSourceKind(),
            reason
        );
    }

    private ResolutionResult resolveOutcomeBandComparison(
        int attackValue,
        int defenseValue,
        int margin,
        AttackSourceRoute route
    ) {
        int metric = switch (outcomeMetric) {
            case OUTCOME_METRIC_ATTACK_RESULT -> attackResultMetric(attackValue);
            case OUTCOME_METRIC_DEFENSE_RESULT -> defenseResultMetric(defenseValue);
            default -> margin;
        };
        String outcomeKey = resolveOutcomeBand(metric)
            .map(OutcomeBand::getOutcomeKey)
            .orElse("");
        String reason = outcomeKey.isEmpty()
            ? REASON_OUTCOME_BAND_MISSING
            : REASON_OUTCOME_SUCCESS_CLASSIFICATION_MISSING;
        return comparedResult(
            AttackSuccess.INDETERMINATE,
            outcomeKey,
            attackValue,
            defenseValue,
            route,
            reason
        );
    }

    private ResolutionResult comparedResult(
        AttackSuccess attackSuccess,
        String outcomeKey,
        int attackValue,
        int defenseValue,
        AttackSourceRoute route,
        String reason
    ) {
        return new ResolutionResult(
            attackSuccess,
            outcomeKey,
            OptionalInt.of(attackValue),
            OptionalInt.of(defenseValue),
            OptionalInt.of(marginMetric(attackValue, defenseValue)),
            route.getId(),
            route.getSourceKind(),
            reason
        );
    }

    private AttackSourceRoute requireAttackSourceRoute(String requestedRouteId) {
        return selectAttackSourceRoute(requestedRouteId)
            .orElseThrow(() -> new IllegalArgumentException("No attack source route is available."));
    }

    private static int requireValue(OptionalInt value, String name) {
        OptionalInt safeValue = Objects.requireNonNullElseGet(value, OptionalInt::empty);
        if (safeValue.isEmpty()) {
            throw new IllegalArgumentException("Resolution requires " + name + ".");
        }
        return safeValue.getAsInt();
    }

    private boolean usesAttackPoolAgainstPassiveDefense(GeneratedValue attack) {
        return MODE_ATTACK_VS_PASSIVE.equals(resolutionMode)
            && attack.value().isEmpty()
            && usesSingleAttackPool(attack);
    }

    private static boolean usesSingleAttackPool(GeneratedValue attack) {
        return attack.rolls().size() == 1 && attack.rolls().get(0).size() > 1;
    }

    private static GeneratedValue requireSingleAttackPool(GeneratedValue attack) {
        GeneratedValue safeAttack = Objects.requireNonNull(attack, "attack");
        if (!usesSingleAttackPool(safeAttack)) {
            throw new IllegalArgumentException(
                "Attack-pool reduction requires one complete roll containing multiple dice."
            );
        }
        return safeAttack;
    }

    private boolean attackPoolDieSucceeds(int roll) {
        return ROLL_DIRECTION_UNDER.equals(attackRollDirection)
            ? roll <= attackPoolSuccessThreshold
            : roll >= attackPoolSuccessThreshold;
    }

    private void initializeDefaultAttackSourceRoute() {
        AttackSourceRoute defaultRoute = new AttackSourceRoute(
            "Attack Method",
            SOURCE_ATTACK_METHOD,
            "",
            "",
            "Use the ruleset's configured dice-based Attack Method."
        );
        attackSourceRoutes.add(defaultRoute);
        defaultAttackSourceRouteId = defaultRoute.getId();
    }

    private void normalizeDefaultAttackSourceRoute() {
        defaultAttackSourceRouteId = findStoredAttackSourceRoute(defaultAttackSourceRouteId)
            .map(AttackSourceRoute::getId)
            .orElseGet(this::firstAttackSourceRouteId);
    }

    private String firstAttackSourceRouteId() {
        return attackSourceRoutes.stream()
            .findFirst()
            .map(AttackSourceRoute::getId)
            .orElse("");
    }

    private Optional<AttackSourceRoute> findStoredAttackSourceRoute(String routeId) {
        String safeRouteId = normalizeId(routeId);
        return attackSourceRoutes.stream()
            .filter(route -> route.getId().equals(safeRouteId))
            .findFirst();
    }

    private static ArrayList<AttackSourceRoute> copyAttackSourceRoutes(
        Collection<AttackSourceRoute> routes
    ) {
        Collection<AttackSourceRoute> safeRoutes = Objects.requireNonNullElse(routes, List.of());
        ArrayList<AttackSourceRoute> copy = new ArrayList<>();
        Set<String> routeIds = new HashSet<>();
        safeRoutes.stream()
            .filter(Objects::nonNull)
            .map(AttackSourceRoute::new)
            .filter(route -> routeIds.add(route.getId()))
            .forEach(copy::add);
        return copy;
    }

    private static ArrayList<OutcomeBand> copyOutcomeBands(Collection<OutcomeBand> bands) {
        Collection<OutcomeBand> safeBands = Objects.requireNonNullElse(bands, List.of());
        ArrayList<OutcomeBand> copy = new ArrayList<>();
        safeBands.stream()
            .filter(Objects::nonNull)
            .map(OutcomeBand::new)
            .forEach(copy::add);
        copy.sort(Comparator.comparingInt(OutcomeBand::getMinimumValue));
        return copy;
    }

    private static String normalizeResolutionMode(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case MODE_ATTACK_VS_PASSIVE,
                 MODE_ATTACK_VS_DEFENSE_RESULT,
                 MODE_DEFENSE_VS_THREAT -> safeValue;
            default -> MODE_AUTOMATIC;
        };
    }

    private static String normalizeComparisonMethod(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case COMPARISON_EXCEED,
                 COMPARISON_LOWER_WINS,
                 COMPARISON_SUCCESS_COUNT,
                 COMPARISON_OUTCOME_BANDS -> safeValue;
            default -> COMPARISON_MEET_OR_EXCEED;
        };
    }

    private static String normalizeAttackRollDirection(String value) {
        return ROLL_DIRECTION_UNDER.equals(normalizeKey(value))
            ? ROLL_DIRECTION_UNDER
            : ROLL_DIRECTION_OVER;
    }

    private static String normalizeAttackPoolResolutionMethod(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case POOL_RESOLUTION_HIGHEST_DIE,
                 POOL_RESOLUTION_LOWEST_DIE,
                 POOL_RESOLUTION_SUM -> safeValue;
            default -> POOL_RESOLUTION_SUCCESS_COUNT;
        };
    }

    private static String normalizeOutcomeMetric(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case OUTCOME_METRIC_ATTACK_RESULT, OUTCOME_METRIC_DEFENSE_RESULT -> safeValue;
            default -> OUTCOME_METRIC_MARGIN;
        };
    }

    private static String normalizeSourceKind(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case SOURCE_ATTACK_METHOD,
                 SOURCE_CARD,
                 SOURCE_ATTRIBUTE,
                 SOURCE_SKILL,
                 SOURCE_GEAR -> safeValue;
            default -> SOURCE_OTHER;
        };
    }

    private static String normalizeKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeText(String value) {
        return Objects.toString(value, "").trim();
    }

    private static String normalizeId(String value) {
        return normalizeText(value);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        boolean attackSourceRoutesWereAbsent = Objects.isNull(attackSourceRoutes);
        boolean initialResolutionSelectionWasAbsent = !initialResolutionSelectionConfigured;
        boolean equalityHandlingWasAbsent = !equalityHandlingConfigured;
        setResolutionMode(resolutionMode);
        setComparisonMethod(comparisonMethod);
        if (equalityHandlingWasAbsent) {
            attackerWinsTies = "attacker".equals(normalizeKey(tieResolution));
        }
        equalityHandlingConfigured = true;
        tieResolution = "";
        if (initialResolutionSelectionWasAbsent) {
            attackRollDirection = COMPARISON_LOWER_WINS.equals(comparisonMethod)
                ? ROLL_DIRECTION_UNDER
                : ROLL_DIRECTION_OVER;
            targetValueIsDefenseValue = !COMPARISON_OUTCOME_BANDS.equals(comparisonMethod);
        } else {
            setAttackRollDirection(attackRollDirection);
        }
        initialResolutionSelectionConfigured = true;
        setAttackPoolResolutionMethod(attackPoolResolutionMethod);
        setAttackPoolSuccessThreshold(attackPoolSuccessThreshold);
        setOutcomeMetric(outcomeMetric);
        setAutomaticOutcomeKey(automaticOutcomeKey);
        setAttackSourceRoutes(attackSourceRoutes);
        if (attackSourceRoutesWereAbsent) {
            initializeDefaultAttackSourceRoute();
        }
        setDefaultAttackSourceRouteId(defaultAttackSourceRouteId);
        setOutcomeBands(outcomeBands);
    }

    /** Core-owned success state returned to every descendant application. */
    public enum AttackSuccess {
        SUCCEEDED,
        FAILED,
        INDETERMINATE
    }

    /** Complete core-owned output from generating and resolving one attack. */
    public record AttackResult(
        GeneratedValue attack,
        GeneratedValue defense,
        ResolutionResult resolution
    ) {
        public AttackResult {
            attack = Objects.requireNonNull(attack, "attack");
            defense = Objects.requireNonNull(defense, "defense");
            resolution = Objects.requireNonNull(resolution, "resolution");
        }

        public AttackSuccess attackSuccess() {
            return resolution.attackSuccess();
        }
    }

    /**
     * Runtime values supplied by a consumer. Only the values required by the
     * configured resolution section need to be present.
     */
    public record ResolutionInput(
        String requestedRouteId,
        OptionalInt attackValue,
        OptionalInt defenseValue,
        OptionalInt threatValue
    ) {
        public ResolutionInput {
            requestedRouteId = normalizeId(requestedRouteId);
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
            return empty();
        }

        private static ResolutionInput empty() {
            return new ResolutionInput(
                "",
                OptionalInt.empty(),
                OptionalInt.empty(),
                OptionalInt.empty()
            );
        }
    }

    /**
     * Complete initial-contact result returned by the core. Runtime values are
     * retained so later damage and effect stages do not need to reconstruct them.
     */
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
            outcomeKey = normalizeKey(outcomeKey);
            attackValue = Objects.requireNonNullElseGet(attackValue, OptionalInt::empty);
            defenseValue = Objects.requireNonNullElseGet(defenseValue, OptionalInt::empty);
            margin = Objects.requireNonNullElseGet(margin, OptionalInt::empty);
            selectedRouteId = normalizeId(selectedRouteId);
            String safeSourceKind = normalizeKey(selectedSourceKind);
            selectedSourceKind = safeSourceKind.isEmpty()
                ? ""
                : normalizeSourceKind(safeSourceKind);
            reason = Objects.toString(reason, "");
        }
    }

    /**
     * One explicitly selectable way to obtain the attack-side input.
     */
    public static final class AttackSourceRoute implements Serializable {

        // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private String id = UUID.randomUUID().toString();
        private String name = "";
        private String sourceKind = SOURCE_OTHER;
        private String sourceCollectionKey = "";
        private String sourceReferenceId = "";
        private String description = "";

        // *** CONSTRUCTORS ***
        public AttackSourceRoute() {
        }

        public AttackSourceRoute(
            String name,
            String sourceKind,
            String sourceCollectionKey,
            String sourceReferenceId,
            String description
        ) {
            setName(name);
            setSourceKind(sourceKind);
            setSourceCollectionKey(sourceCollectionKey);
            setSourceReferenceId(sourceReferenceId);
            setDescription(description);
        }

        public AttackSourceRoute(AttackSourceRoute source) {
            AttackSourceRoute safeSource = Objects.requireNonNullElseGet(
                source,
                AttackSourceRoute::new
            );
            setId(safeSource.id);
            setName(safeSource.name);
            setSourceKind(safeSource.sourceKind);
            setSourceCollectionKey(safeSource.sourceCollectionKey);
            setSourceReferenceId(safeSource.sourceReferenceId);
            setDescription(safeSource.description);
        }

        // *** METHODS ***
        public String getId() {
            return id;
        }

        public void setId(String id) {
            String safeId = normalizeId(id);
            this.id = safeId.isEmpty() ? UUID.randomUUID().toString() : safeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = normalizeText(name);
        }

        public String getSourceKind() {
            return sourceKind;
        }

        public void setSourceKind(String sourceKind) {
            this.sourceKind = normalizeSourceKind(sourceKind);
        }

        public String getSourceCollectionKey() {
            return sourceCollectionKey;
        }

        public void setSourceCollectionKey(String sourceCollectionKey) {
            this.sourceCollectionKey = normalizeKey(sourceCollectionKey);
        }

        public String getSourceReferenceId() {
            return sourceReferenceId;
        }

        public void setSourceReferenceId(String sourceReferenceId) {
            this.sourceReferenceId = normalizeId(sourceReferenceId);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = Objects.toString(description, "");
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            setId(id);
            setName(name);
            setSourceKind(sourceKind);
            setSourceCollectionKey(sourceCollectionKey);
            setSourceReferenceId(sourceReferenceId);
            setDescription(description);
        }
    }

    /**
     * Inclusive range mapping one comparison metric to a creator-defined outcome.
     */
    public static final class OutcomeBand implements Serializable {

        // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private int minimumValue = 0;
        private int maximumValue = 0;
        private String outcomeKey = "";
        private String name = "";
        private String description = "";

        // *** CONSTRUCTORS ***
        public OutcomeBand() {
        }

        public OutcomeBand(
            int minimumValue,
            int maximumValue,
            String outcomeKey,
            String name,
            String description
        ) {
            setRange(minimumValue, maximumValue);
            setOutcomeKey(outcomeKey);
            setName(name);
            setDescription(description);
        }

        public OutcomeBand(OutcomeBand source) {
            OutcomeBand safeSource = Objects.requireNonNullElseGet(source, OutcomeBand::new);
            setRange(safeSource.minimumValue, safeSource.maximumValue);
            setOutcomeKey(safeSource.outcomeKey);
            setName(safeSource.name);
            setDescription(safeSource.description);
        }

        // *** METHODS ***
        public int getMinimumValue() {
            return minimumValue;
        }

        public int getMaximumValue() {
            return maximumValue;
        }

        public void setRange(int minimumValue, int maximumValue) {
            this.minimumValue = Math.min(minimumValue, maximumValue);
            this.maximumValue = Math.max(minimumValue, maximumValue);
        }

        public String getOutcomeKey() {
            return outcomeKey;
        }

        public void setOutcomeKey(String outcomeKey) {
            this.outcomeKey = normalizeKey(outcomeKey);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = normalizeText(name);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = Objects.toString(description, "");
        }

        public boolean includes(int value) {
            return value >= minimumValue && value <= maximumValue;
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            setRange(minimumValue, maximumValue);
            setOutcomeKey(outcomeKey);
            setName(name);
            setDescription(description);
        }
    }
}
