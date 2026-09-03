/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized at declaration and remain non-null.
 - Normalize absent external values at the boundary.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.CharacterElements.Background;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Core-owned Background eligibility and one-time creation package results. */
public final class BackgroundSelection {

    public static final String REASON_NONE = "";
    public static final String REASON_NOT_FOUND = "backgroundNotFound";
    public static final String REASON_RACE_RESTRICTION = "raceRestriction";
    public static final String REASON_ATTRIBUTE_MINIMUM = "attributeMinimum";

    private BackgroundSelection() {
    }

    /** Returns only Backgrounds legal for the supplied character state. */
    public static List<Result> eligibleOptions(
        Collection<Background> backgroundSource,
        String raceId,
        Map<String, Integer> attributeScores
    ) {
        ArrayList<Result> options = new ArrayList<>();
        for (Result result : evaluate(backgroundSource, raceId, attributeScores)) {
            if (result.isEligible()) {
                options.add(result);
            }
        }
        return List.copyOf(options);
    }

    /** Revalidates and resolves one stable Background id. */
    public static Result select(
        Collection<Background> backgroundSource,
        String raceId,
        Map<String, Integer> attributeScores,
        String backgroundId
    ) {
        String safeBackgroundId = Objects.toString(backgroundId, "").trim();
        for (Result result : evaluate(backgroundSource, raceId, attributeScores)) {
            if (result.getBackgroundId().equals(safeBackgroundId)) {
                return result;
            }
        }
        return Result.unavailable(safeBackgroundId, REASON_NOT_FOUND, "", 0, 0);
    }

    private static List<Result> evaluate(
        Collection<Background> backgroundSource,
        String raceId,
        Map<String, Integer> attributeScores
    ) {
        Collection<Background> safeBackgrounds = Objects.requireNonNullElse(backgroundSource, List.of());
        String safeRaceId = Objects.toString(raceId, "").trim();
        Map<String, Integer> safeScores = copyIntegerMap(attributeScores);
        ArrayList<Result> results = new ArrayList<>();
        for (Background background : safeBackgrounds) {
            results.add(evaluate(Objects.requireNonNull(background, "background"), safeRaceId, safeScores));
        }
        return results;
    }

    private static Result evaluate(
        Background background,
        String raceId,
        Map<String, Integer> attributeScores
    ) {
        List<String> limitedToRaceIds = copyStringList(background.<String>getArray("limitedToRaces"));
        if (!raceId.isEmpty() && !limitedToRaceIds.isEmpty() && !limitedToRaceIds.contains(raceId)) {
            return Result.fromBackground(background, false, REASON_RACE_RESTRICTION, "", 0, 0);
        }
        for (Map.Entry<String, Integer> requirement : background.getRequiredAttributeScores().entrySet()) {
            String attributeId = Objects.toString(requirement.getKey(), "").trim();
            int requiredScore = Objects.requireNonNullElse(requirement.getValue(), 0);
            int actualScore = attributeScores.getOrDefault(attributeId, 0);
            if (!attributeId.isEmpty() && requiredScore > 0 && actualScore < requiredScore) {
                return Result.fromBackground(
                    background,
                    false,
                    REASON_ATTRIBUTE_MINIMUM,
                    attributeId,
                    requiredScore,
                    actualScore
                );
            }
        }
        return Result.fromBackground(background, true, REASON_NONE, "", 0, 0);
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

    /** One legal or rejected Background choice and its authoritative creation package. */
    public static final class Result {
        private String backgroundId = "";
        private String name = "";
        private String description = "";
        private boolean eligible = false;
        private String reason = "";
        private String attributeId = "";
        private int requiredScore = 0;
        private int actualScore = 0;
        private int startingSkillPoints = 0;
        private int startingMoney = 0;
        private List<String> backgroundSkillIds = new ArrayList<>();

        private Result() {
        }

        private static Result fromBackground(
            Background background,
            boolean eligible,
            String reason,
            String attributeId,
            int requiredScore,
            int actualScore
        ) {
            Result result = new Result();
            result.backgroundId = Objects.toString(background.getId(), "").trim();
            result.name = Objects.toString(background.getName(), "");
            result.description = Objects.toString(background.getDescription(), "");
            result.eligible = eligible;
            result.reason = Objects.toString(reason, "");
            result.attributeId = Objects.toString(attributeId, "").trim();
            result.requiredScore = requiredScore;
            result.actualScore = actualScore;
            result.startingSkillPoints = background.getStartingSkillPoints();
            result.startingMoney = background.getStartingMoney();
            result.backgroundSkillIds = copyStringList(background.getBackgroundSkillIds());
            return result;
        }

        private static Result unavailable(
            String backgroundId,
            String reason,
            String attributeId,
            int requiredScore,
            int actualScore
        ) {
            Result result = new Result();
            result.backgroundId = Objects.toString(backgroundId, "").trim();
            result.reason = Objects.toString(reason, "");
            result.attributeId = Objects.toString(attributeId, "").trim();
            result.requiredScore = requiredScore;
            result.actualScore = actualScore;
            return result;
        }

        public String getBackgroundId() { return backgroundId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isEligible() { return eligible; }
        public String getReason() { return reason; }
        public String getAttributeId() { return attributeId; }
        public int getRequiredScore() { return requiredScore; }
        public int getActualScore() { return actualScore; }
        public int getStartingSkillPoints() { return startingSkillPoints; }
        public int getStartingMoney() { return startingMoney; }
        public List<String> getBackgroundSkillIds() { return List.copyOf(backgroundSkillIds); }
    }
}
