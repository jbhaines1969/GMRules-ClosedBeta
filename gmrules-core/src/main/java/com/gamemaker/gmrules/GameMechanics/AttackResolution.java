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
 * <p>This class stores ruleset configuration only. Complete runtime attack,
 * defense, and resolution results remain separate contracts so downstream damage
 * and effects can retain raw rolls, totals, margins, success counts, and outcomes.</p>
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

    public static final String TIE_ATTACKER = "attacker";
    public static final String TIE_DEFENDER = "defender";
    public static final String TIE_OUTCOME = "outcome";

    public static final String OUTCOME_METRIC_ATTACK_RESULT = "attack_result";
    public static final String OUTCOME_METRIC_DEFENSE_RESULT = "defense_result";
    public static final String OUTCOME_METRIC_MARGIN = "margin";

    public static final String SOURCE_ATTACK_METHOD = "attack_method";
    public static final String SOURCE_CARD = "card";
    public static final String SOURCE_ATTRIBUTE = "attribute";
    public static final String SOURCE_SKILL = "skill";
    public static final String SOURCE_GEAR = "gear";
    public static final String SOURCE_OTHER = "other";

    private String resolutionMode = MODE_AUTOMATIC;
    private String comparisonMethod = COMPARISON_MEET_OR_EXCEED;
    private String tieResolution = TIE_DEFENDER;
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

    public String getTieResolution() {
        return tieResolution;
    }

    public void setTieResolution(String tieResolution) {
        this.tieResolution = normalizeTieResolution(tieResolution);
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
        if (!COMPARISON_OUTCOME_BANDS.equals(comparisonMethod)) {
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

    private static String normalizeTieResolution(String value) {
        String safeValue = normalizeKey(value);
        return switch (safeValue) {
            case TIE_ATTACKER, TIE_OUTCOME -> safeValue;
            default -> TIE_DEFENDER;
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
        setResolutionMode(resolutionMode);
        setComparisonMethod(comparisonMethod);
        setTieResolution(tieResolution);
        setOutcomeMetric(outcomeMetric);
        setAutomaticOutcomeKey(automaticOutcomeKey);
        setAttackSourceRoutes(attackSourceRoutes);
        if (attackSourceRoutesWereAbsent) {
            initializeDefaultAttackSourceRoute();
        }
        setDefaultAttackSourceRouteId(defaultAttackSourceRouteId);
        setOutcomeBands(outcomeBands);
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
