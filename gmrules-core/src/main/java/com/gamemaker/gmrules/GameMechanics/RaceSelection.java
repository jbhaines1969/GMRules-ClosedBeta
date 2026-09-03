/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized at declaration and remain non-null.
 - Normalize absent external values at the boundary.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.GameElements.Species;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Core-owned Race eligibility and application results for Character Generation. */
public final class RaceSelection {

    public static final String REASON_NONE = "";
    public static final String REASON_NOT_FOUND = "raceNotFound";
    public static final String REASON_NOT_PLAYABLE = "notPlayable";
    public static final String REASON_ATTRIBUTE_MINIMUM = "attributeMinimum";
    public static final String REASON_ATTRIBUTE_MAXIMUM = "attributeMaximum";

    private RaceSelection() {
    }

    public static List<Result> evaluate(
        Collection<Race> raceSource,
        Map<String, Integer> attributeScores
    ) {
        Collection<Race> safeRaces = Objects.requireNonNullElse(raceSource, List.of());
        Map<String, Integer> safeScores = copyIntegerMap(attributeScores);
        ArrayList<Result> results = new ArrayList<>();
        for (Race race : safeRaces) {
            results.add(evaluate(Objects.requireNonNull(race, "race"), safeScores));
        }
        return List.copyOf(results);
    }

    public static Result select(
        Collection<Race> raceSource,
        Map<String, Integer> attributeScores,
        String raceId
    ) {
        String safeRaceId = Objects.toString(raceId, "").trim();
        for (Result result : evaluate(raceSource, attributeScores)) {
            if (result.getRaceId().equals(safeRaceId)) {
                return result;
            }
        }
        return Result.unavailable(safeRaceId, REASON_NOT_FOUND, "", 0, 0);
    }

    private static Result evaluate(Race race, Map<String, Integer> attributeScores) {
        if (!race.isPlayable()) {
            return Result.fromRace(race, false, REASON_NOT_PLAYABLE, "", 0, 0);
        }
        for (Species.AttributeScoreLimit limit : race.getAttributeScoreLimits()) {
            String attributeId = Objects.toString(limit.getAttributeId(), "").trim();
            if (attributeId.isEmpty()) {
                continue;
            }
            int actualScore = attributeScores.getOrDefault(attributeId, 0);
            if (limit.getMin() > 0 && actualScore < limit.getMin()) {
                return Result.fromRace(
                    race,
                    false,
                    REASON_ATTRIBUTE_MINIMUM,
                    attributeId,
                    limit.getMin(),
                    actualScore
                );
            }
            if (limit.getMax() > 0 && actualScore > limit.getMax()) {
                return Result.fromRace(
                    race,
                    false,
                    REASON_ATTRIBUTE_MAXIMUM,
                    attributeId,
                    limit.getMax(),
                    actualScore
                );
            }
        }
        return Result.fromRace(race, true, REASON_NONE, "", 0, 0);
    }

    private static LinkedHashMap<String, Integer> copyIntegerMap(Map<String, Integer> source) {
        Map<String, Integer> safeSource = Objects.requireNonNullElse(source, Map.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeSource.entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Objects.requireNonNullElse(entry.getValue(), 0));
            }
        }
        return copy;
    }

    private static ArrayList<String> copyStringList(Collection<String> source) {
        Collection<String> safeSource = Objects.requireNonNullElse(source, List.of());
        ArrayList<String> copy = new ArrayList<>();
        for (String value : safeSource) {
            String safeValue = Objects.toString(value, "").trim();
            if (!safeValue.isEmpty() && !copy.contains(safeValue)) {
                copy.add(safeValue);
            }
        }
        return copy;
    }

    /** One authoritative Race option, including the data applied by a successful selection. */
    public static final class Result {
        private String raceId = "";
        private String name = "";
        private String description = "";
        private boolean eligible = false;
        private String reason = "";
        private String attributeId = "";
        private int requiredScore = 0;
        private int actualScore = 0;
        private Map<String, Integer> attributeModifiers = new LinkedHashMap<>();
        private List<String> racialSkillIds = new ArrayList<>();
        private List<String> racialTraitIds = new ArrayList<>();

        private Result() {
        }

        private static Result fromRace(
            Race race,
            boolean eligible,
            String reason,
            String attributeId,
            int requiredScore,
            int actualScore
        ) {
            Result result = new Result();
            result.raceId = Objects.toString(race.getId(), "").trim();
            result.name = Objects.toString(race.getName(), "");
            result.description = Objects.toString(race.getDescription(), "");
            result.eligible = eligible;
            result.reason = Objects.toString(reason, "");
            result.attributeId = Objects.toString(attributeId, "").trim();
            result.requiredScore = requiredScore;
            result.actualScore = actualScore;
            result.attributeModifiers = copyIntegerMap(race.getAbilityModifiers());
            result.racialSkillIds = copyStringList(race.<String>getArray("racialSkills"));
            result.racialTraitIds = copyStringList(race.<String>getArray("racialTraitNames"));
            return result;
        }

        private static Result unavailable(
            String raceId,
            String reason,
            String attributeId,
            int requiredScore,
            int actualScore
        ) {
            Result result = new Result();
            result.raceId = Objects.toString(raceId, "").trim();
            result.reason = Objects.toString(reason, "");
            result.attributeId = Objects.toString(attributeId, "").trim();
            result.requiredScore = requiredScore;
            result.actualScore = actualScore;
            return result;
        }

        public String getRaceId() { return raceId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isEligible() { return eligible; }
        public String getReason() { return reason; }
        public String getAttributeId() { return attributeId; }
        public int getRequiredScore() { return requiredScore; }
        public int getActualScore() { return actualScore; }
        public Map<String, Integer> getAttributeModifiers() {
            return copyIntegerMap(attributeModifiers);
        }
        public List<String> getRacialSkillIds() { return List.copyOf(racialSkillIds); }
        public List<String> getRacialTraitIds() { return List.copyOf(racialTraitIds); }
    }
}
