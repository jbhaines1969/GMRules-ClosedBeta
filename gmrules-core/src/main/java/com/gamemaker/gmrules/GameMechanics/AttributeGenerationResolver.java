/*
 FILE CONTRACT (Non-Null):
 - This is a package-private implementation detail of AttributeGenerationMethod.
 - Consumers must enter through the configured rule object; do not make this class public.
 - Normalize external absent values immediately and return immutable/copy-safe values.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.Game;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/** Stateless mechanical implementation available only to its owning rule object. */
final class AttributeGenerationResolver {

    private AttributeGenerationResolver() {
    }

    static List<List<Integer>> generateRollSets(
        AttributeGenerationMethod method,
        Collection<Attribute> attributeSource,
        DiceRoller diceRoller
    ) {
        AttributeGenerationMethod rules = requireMethod(method);
        List<Attribute> attributes = normalizeAttributes(attributeSource);
        DiceRoller safeRoller = Objects.requireNonNullElseGet(diceRoller, DiceRoller::random);
        int setCount = Math.max(1, rules.getNumberOfSets());
        ArrayList<List<Integer>> sets = new ArrayList<>();
        for (int setIndex = 0; setIndex < setCount; setIndex++) {
            ArrayList<Integer> values = new ArrayList<>();
            for (int attributeIndex = 0; attributeIndex < attributes.size(); attributeIndex++) {
                values.add(generateRollValue(rules, safeRoller));
            }
            sets.add(List.copyOf(values));
        }
        return List.copyOf(sets);
    }

    static List<Integer> applyRollSubstitution(
        AttributeGenerationMethod method,
        Collection<Attribute> attributeSource,
        Collection<Integer> rolledValues,
        int rollIndex,
        int substitutionsAlreadyUsed
    ) {
        AttributeGenerationMethod rules = requireMethod(method);
        List<Integer> values = new ArrayList<>(Objects.requireNonNullElse(rolledValues, List.of()));
        if (!rules.isAllowDiceSubstitution()) {
            throw new IllegalStateException(
                "This Attribute Generation method does not allow dice substitution."
            );
        }
        if (substitutionsAlreadyUsed < 0
            || substitutionsAlreadyUsed >= rules.getMaxDiceSubstitutions()) {
            throw new IllegalStateException("No Attribute dice substitutions remain.");
        }
        if (values.size() != normalizeAttributes(attributeSource).size()) {
            throw new IllegalArgumentException("Choose a complete generated Attribute roll set.");
        }
        if (rollIndex < 0 || rollIndex >= values.size()) {
            throw new IllegalArgumentException("Choose an Attribute roll to replace.");
        }
        values.set(rollIndex, rules.getDiceSubstitutionValue());
        return List.copyOf(values);
    }

    static RollAdjustmentMethod.Result adjustRolls(
        AttributeGenerationMethod method,
        Collection<Attribute> attributeSource,
        RollAdjustmentMethod.Request request
    ) {
        AttributeGenerationMethod rules = requireMethod(method);
        RollAdjustmentMethod.Request safeRequest = new RollAdjustmentMethod.Request(request);
        List<Attribute> attributes = normalizeAttributes(attributeSource);
        ArrayList<RollAdjustmentMethod.RollValue> values = RollAdjustmentMethod.copyValues(
            safeRequest.getValues()
        );
        validateRollValues(attributes, values);

        LinkedHashMap<String, Integer> uses = RollAdjustmentMethod.copyNonNegativeMap(
            safeRequest.getUsesByMethod()
        );
        LinkedHashMap<String, Integer> resourceSpent = RollAdjustmentMethod.copyNonNegativeMap(
            safeRequest.getResourceSpentByKey()
        );
        LinkedHashMap<String, Integer> deltas = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> resourceCosts = new LinkedHashMap<>();
        String methodId = safeRequest.getMethodId();
        if (methodId.isEmpty()) {
            return rollAdjustmentResult(false, rules, values, uses, resourceSpent, deltas, resourceCosts, "");
        }

        RollAdjustmentMethod selectedMethod = findRollAdjustmentMethod(rules, methodId)
            .orElseThrow(() -> new IllegalArgumentException("Choose an available roll adjustment."));
        RollAdjustmentMethod.Option option = describeRollAdjustment(
            selectedMethod,
            values,
            uses,
            resourceSpent
        );
        if (!option.isAvailable()) {
            throw new IllegalStateException(option.getReason());
        }

        if (selectedMethod.getType().equals(RollAdjustmentMethod.TYPE_FIXED_VALUE)) {
            int targetIndex = requireRollValueIndex(values, safeRequest.getTargetValueId(), "target");
            replaceRollValue(values, targetIndex, selectedMethod.getValue(), deltas);
        } else if (selectedMethod.getType().equals(RollAdjustmentMethod.TYPE_RAISE_HIGHEST)) {
            int targetIndex = highestRollValueIndex(values);
            replaceRollValue(values, targetIndex, selectedMethod.getValue(), deltas);
        } else if (selectedMethod.getType().equals(RollAdjustmentMethod.TYPE_TRANSFER)) {
            int sourceIndex = requireRollValueIndex(values, safeRequest.getSourceValueId(), "source");
            int targetIndex = requireRollValueIndex(values, safeRequest.getTargetValueId(), "target");
            if (sourceIndex == targetIndex) {
                throw new IllegalArgumentException("Choose different source and target rolls.");
            }
            int amount = safeRequest.getAmount();
            int sourceDelta = Math.multiplyExact(amount, selectedMethod.getSourceCostPerUnit());
            int targetDelta = Math.multiplyExact(amount, selectedMethod.getTargetGainPerUnit());
            int sourceValue = values.get(sourceIndex).getValue();
            int targetValue = values.get(targetIndex).getValue();
            if (sourceValue - sourceDelta < selectedMethod.getSourceMinimum()) {
                throw new IllegalArgumentException("The source roll cannot pay that transfer.");
            }
            if (selectedMethod.getTargetMaximum() > 0
                && targetValue + targetDelta > selectedMethod.getTargetMaximum()) {
                throw new IllegalArgumentException("The target roll cannot receive that transfer.");
            }
            replaceRollValue(values, sourceIndex, Math.subtractExact(sourceValue, sourceDelta), deltas);
            replaceRollValue(values, targetIndex, Math.addExact(targetValue, targetDelta), deltas);
        } else if (selectedMethod.getType().equals(RollAdjustmentMethod.TYPE_SPEND_RESOURCE)) {
            int targetIndex = requireRollValueIndex(values, safeRequest.getTargetValueId(), "target");
            int amount = safeRequest.getAmount();
            int cost = Math.multiplyExact(amount, selectedMethod.getResourceCostPerPoint());
            String resourceKey = selectedMethod.getResourceKey();
            int alreadySpent = resourceSpent.getOrDefault(resourceKey, 0);
            if (resourceKey.isEmpty() || alreadySpent + cost > selectedMethod.getResourceBudget()) {
                throw new IllegalArgumentException("The adjustment resource cannot pay that cost.");
            }
            int targetValue = values.get(targetIndex).getValue();
            if (selectedMethod.getTargetMaximum() > 0
                && targetValue + amount > selectedMethod.getTargetMaximum()) {
                throw new IllegalArgumentException("The target roll cannot receive that adjustment.");
            }
            replaceRollValue(values, targetIndex, Math.addExact(targetValue, amount), deltas);
            resourceSpent.put(resourceKey, alreadySpent + cost);
            resourceCosts.put(resourceKey, cost);
        }

        uses.put(methodId, uses.getOrDefault(methodId, 0) + 1);
        return rollAdjustmentResult(true, rules, values, uses, resourceSpent, deltas, resourceCosts, "");
    }

    private static RollAdjustmentMethod.Result rollAdjustmentResult(
        boolean applied,
        AttributeGenerationMethod rules,
        Collection<RollAdjustmentMethod.RollValue> values,
        Map<String, Integer> uses,
        Map<String, Integer> resourceSpent,
        Map<String, Integer> deltas,
        Map<String, Integer> resourceCosts,
        String reason
    ) {
        ArrayList<RollAdjustmentMethod.Option> options = new ArrayList<>();
        for (RollAdjustmentMethod adjustmentMethod : rules.getRollAdjustmentMethods()) {
            options.add(describeRollAdjustment(adjustmentMethod, values, uses, resourceSpent));
        }
        return new RollAdjustmentMethod.Result(
            applied,
            values,
            options,
            uses,
            resourceSpent,
            deltas,
            resourceCosts,
            reason
        );
    }

    private static RollAdjustmentMethod.Option describeRollAdjustment(
        RollAdjustmentMethod method,
        Collection<RollAdjustmentMethod.RollValue> valueSource,
        Map<String, Integer> uses,
        Map<String, Integer> resourceSpent
    ) {
        List<RollAdjustmentMethod.RollValue> values = RollAdjustmentMethod.copyValues(valueSource);
        int usesRemaining = Math.max(0, method.getMaximumUses() - uses.getOrDefault(method.getId(), 0));
        boolean targetRequired = method.getType().equals(RollAdjustmentMethod.TYPE_FIXED_VALUE)
            || method.getType().equals(RollAdjustmentMethod.TYPE_TRANSFER)
            || method.getType().equals(RollAdjustmentMethod.TYPE_SPEND_RESOURCE);
        boolean sourceRequired = method.getType().equals(RollAdjustmentMethod.TYPE_TRANSFER);
        boolean amountRequired = sourceRequired
            || method.getType().equals(RollAdjustmentMethod.TYPE_SPEND_RESOURCE);
        ArrayList<String> targets = new ArrayList<>();
        ArrayList<String> sources = new ArrayList<>();
        int maximumAmount = amountRequired ? 0 : 1;
        int resourceRemaining = 0;
        String reason = "";

        if (method.getType().equals(RollAdjustmentMethod.TYPE_FIXED_VALUE)) {
            for (RollAdjustmentMethod.RollValue value : values) {
                if (value.getValue() != method.getValue()) {
                    targets.add(value.getId());
                }
            }
            if (targets.isEmpty()) {
                reason = "Every roll already has the fixed value.";
            }
        } else if (method.getType().equals(RollAdjustmentMethod.TYPE_RAISE_HIGHEST)) {
            if (values.isEmpty() || values.get(highestRollValueIndex(values)).getValue() >= method.getValue()) {
                reason = "The highest roll already meets this floor.";
            }
        } else if (method.getType().equals(RollAdjustmentMethod.TYPE_TRANSFER)) {
            for (RollAdjustmentMethod.RollValue source : values) {
                if (source.getValue() - selectedTransferSourceCost(method) >= method.getSourceMinimum()) {
                    sources.add(source.getId());
                }
            }
            for (RollAdjustmentMethod.RollValue target : values) {
                if (method.getTargetMaximum() == 0
                    || target.getValue() + method.getTargetGainPerUnit() <= method.getTargetMaximum()) {
                    targets.add(target.getId());
                }
            }
            for (RollAdjustmentMethod.RollValue source : values) {
                int sourceUnits = Math.max(
                    0,
                    (source.getValue() - method.getSourceMinimum()) / method.getSourceCostPerUnit()
                );
                for (RollAdjustmentMethod.RollValue target : values) {
                    if (source.getId().equals(target.getId())) {
                        continue;
                    }
                    int targetUnits = method.getTargetMaximum() == 0
                        ? sourceUnits
                        : Math.max(
                            0,
                            (method.getTargetMaximum() - target.getValue()) / method.getTargetGainPerUnit()
                        );
                    maximumAmount = Math.max(maximumAmount, Math.min(sourceUnits, targetUnits));
                }
            }
            if (maximumAmount == 0) {
                reason = "No legal transfer remains.";
            }
        } else if (method.getType().equals(RollAdjustmentMethod.TYPE_SPEND_RESOURCE)) {
            resourceRemaining = Math.max(
                0,
                method.getResourceBudget() - resourceSpent.getOrDefault(method.getResourceKey(), 0)
            );
            int affordableAmount = resourceRemaining / method.getResourceCostPerPoint();
            for (RollAdjustmentMethod.RollValue target : values) {
                int targetCapacity = method.getTargetMaximum() == 0
                    ? affordableAmount
                    : Math.max(0, method.getTargetMaximum() - target.getValue());
                if (targetCapacity > 0) {
                    targets.add(target.getId());
                    maximumAmount = Math.max(maximumAmount, Math.min(affordableAmount, targetCapacity));
                }
            }
            if (method.getResourceKey().isEmpty() || method.getResourceBudget() == 0) {
                reason = "This adjustment has no configured resource budget.";
            } else if (maximumAmount == 0) {
                reason = "No legal resource-funded adjustment remains.";
            }
        }

        if (usesRemaining == 0) {
            reason = "No uses of this adjustment remain.";
        }
        boolean available = usesRemaining > 0 && reason.isEmpty();
        return new RollAdjustmentMethod.Option(
            method,
            available,
            reason,
            usesRemaining,
            targetRequired,
            sourceRequired,
            amountRequired,
            targets,
            sources,
            maximumAmount,
            resourceRemaining
        );
    }

    private static int selectedTransferSourceCost(RollAdjustmentMethod method) {
        return Math.max(1, method.getSourceCostPerUnit());
    }

    private static Optional<RollAdjustmentMethod> findRollAdjustmentMethod(
        AttributeGenerationMethod rules,
        String methodId
    ) {
        for (RollAdjustmentMethod method : rules.getRollAdjustmentMethods()) {
            if (method.getId().equals(methodId)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private static void validateRollValues(
        List<Attribute> attributes,
        List<RollAdjustmentMethod.RollValue> values
    ) {
        if (values.size() != attributes.size()) {
            throw new IllegalArgumentException("Choose a complete generated Attribute roll set.");
        }
        HashSet<String> ids = new HashSet<>();
        for (RollAdjustmentMethod.RollValue value : values) {
            if (value.getId().isEmpty() || !ids.add(value.getId())) {
                throw new IllegalArgumentException("Generated Attribute roll identities must be unique.");
            }
        }
    }

    private static int requireRollValueIndex(
        List<RollAdjustmentMethod.RollValue> values,
        String valueId,
        String role
    ) {
        for (int index = 0; index < values.size(); index++) {
            if (values.get(index).getId().equals(valueId)) {
                return index;
            }
        }
        throw new IllegalArgumentException("Choose a legal " + role + " roll.");
    }

    private static int highestRollValueIndex(List<RollAdjustmentMethod.RollValue> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Choose a generated Attribute roll set.");
        }
        int highestIndex = 0;
        for (int index = 1; index < values.size(); index++) {
            if (values.get(index).getValue() > values.get(highestIndex).getValue()) {
                highestIndex = index;
            }
        }
        return highestIndex;
    }

    private static void replaceRollValue(
        List<RollAdjustmentMethod.RollValue> values,
        int index,
        int replacement,
        Map<String, Integer> deltas
    ) {
        RollAdjustmentMethod.RollValue current = values.get(index);
        values.set(index, new RollAdjustmentMethod.RollValue(current.getId(), replacement));
        deltas.merge(current.getId(), replacement - current.getValue(), Integer::sum);
    }

    static AttributeGenerationMethod.AttributeGenerationResult resolve(
        AttributeGenerationMethod method,
        Collection<Game.AttributeGenerationOption> optionSource,
        Collection<Attribute> attributeSource,
        Collection<String> categoryKeySource,
        AttributeGenerationMethod.AttributeGenerationRequest request
    ) {
        AttributeGenerationMethod rules = requireMethod(method);
        AttributeGenerationMethod.AttributeGenerationRequest safeRequest =
            new AttributeGenerationMethod.AttributeGenerationRequest(request);
        List<Attribute> attributes = normalizeAttributes(attributeSource);
        LinkedHashSet<String> categoryKeys = normalizeCategoryKeys(categoryKeySource);
        if (attributes.isEmpty()) {
            return AttributeGenerationMethod.AttributeGenerationResult.incomplete(
                "The loaded ruleset has no Attributes."
            );
        }

        Optional<Game.AttributeGenerationOption> resolvedOption = resolveOption(
            optionSource,
            safeRequest.getOptionId()
        );
        if (resolvedOption.isEmpty()) {
            return AttributeGenerationMethod.AttributeGenerationResult.incomplete(
                "Choose one of the loaded Attribute Generation options."
            );
        }

        LinkedHashMap<String, Integer> currentScores = new LinkedHashMap<>();
        LinkedHashMap<Integer, Map<String, Integer>> stepResults = new LinkedHashMap<>();
        LinkedHashMap<String, AttributeGenerationMethod.CategoryPointBudgetResult>
            categoryBudgets = new LinkedHashMap<>();
        int pointsSpent = 0;
        int pointsRemaining = Math.max(0, rules.getBasePoints());
        List<Game.AttributeGenerationStep> steps = resolvedOption.get().getSteps();

        for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
            Game.AttributeGenerationStep step = steps.get(stepIndex);
            String methodType = Game.AttributeGenerationStep.normalizeMethodType(step.getMethodType());
            String applicationMode = Game.AttributeGenerationStep.normalizeApplicationMode(
                step.getApplicationMode()
            );
            ScoreResolution resolution;
            if (methodType.equals(Game.AttributeGenerationStep.METHOD_STANDARD_ARRAY)) {
                resolution = resolveStandardArray(rules, safeRequest, attributes);
            } else if (methodType.equals(Game.AttributeGenerationStep.METHOD_DICE)) {
                resolution = resolveDice(rules, safeRequest, attributes, applicationMode);
            } else if (methodType.equals(Game.AttributeGenerationStep.METHOD_POINT_BUY)) {
                resolution = resolvePointBuy(
                    rules,
                    safeRequest,
                    attributes,
                    categoryKeys,
                    currentScores,
                    applicationMode
                );
            } else {
                return AttributeGenerationMethod.AttributeGenerationResult.incomplete(
                    "The selected Attribute Generation option contains an unsupported step."
                );
            }

            if (methodType.equals(Game.AttributeGenerationStep.METHOD_POINT_BUY)) {
                pointsSpent = resolution.pointsSpent();
                pointsRemaining = resolution.pointsRemaining();
                categoryBudgets = new LinkedHashMap<>(resolution.categoryBudgets());
            }
            if (!resolution.complete()) {
                if (!resolution.scores().isEmpty()) {
                    stepResults.put(stepIndex, Map.copyOf(resolution.scores()));
                }
                return result(
                    false,
                    currentScores,
                    stepResults,
                    pointsSpent,
                    pointsRemaining,
                    categoryBudgets,
                    resolution.reason()
                );
            }

            LinkedHashMap<String, Integer> stepScores = new LinkedHashMap<>(resolution.scores());
            if (applicationMode.equals(Game.AttributeGenerationStep.APPLICATION_ADD)
                && !methodType.equals(Game.AttributeGenerationStep.METHOD_POINT_BUY)) {
                stepScores = addScores(rules, currentScores, stepScores, attributes);
            }

            stepResults.put(stepIndex, Map.copyOf(stepScores));
            if (applicationMode.equals(Game.AttributeGenerationStep.APPLICATION_CHOOSE)) {
                int chosenStep = safeRequest.getChosenStepIndex();
                if (chosenStep != stepIndex && chosenStep != stepIndex - 1) {
                    return result(
                        false,
                        currentScores,
                        stepResults,
                        pointsSpent,
                        pointsRemaining,
                        categoryBudgets,
                        "Choose one of the completed Attribute score results."
                    );
                }
                currentScores = new LinkedHashMap<>(stepResults.get(chosenStep));
            } else {
                currentScores = stepScores;
            }
        }

        String validationError = validateScores(rules, currentScores, attributes);
        return result(
            validationError.isEmpty(),
            currentScores,
            stepResults,
            pointsSpent,
            pointsRemaining,
            categoryBudgets,
            validationError
        );
    }

    static int attributeMinimum(AttributeGenerationMethod method, Attribute attribute) {
        AttributeGenerationMethod rules = requireMethod(method);
        Attribute safeAttribute = Objects.requireNonNull(attribute, "attribute");
        if (safeAttribute.getMinValue() > 0) {
            return safeAttribute.getMinValue();
        }
        return rules.getMinAttributeValue() > 0 ? rules.getMinAttributeValue() : 0;
    }

    static int attributeMaximum(AttributeGenerationMethod method, Attribute attribute) {
        AttributeGenerationMethod rules = requireMethod(method);
        Attribute safeAttribute = Objects.requireNonNull(attribute, "attribute");
        if (safeAttribute.getMaxValue() > 0) {
            return safeAttribute.getMaxValue();
        }
        return rules.getMaxAttributeValue() > 0 ? rules.getMaxAttributeValue() : 100;
    }

    static int attributeBase(AttributeGenerationMethod method, Attribute attribute) {
        AttributeGenerationMethod rules = requireMethod(method);
        return clamp(rules, Math.max(0, rules.getBaseAttributeValue()), attribute);
    }

    static int attributePointCost(AttributeGenerationMethod method, int score) {
        AttributeGenerationMethod rules = requireMethod(method);
        Map<Integer, Integer> pointCosts = rules.getPointCosts();
        if (pointCosts.containsKey(score)) {
            return Math.max(0, Objects.requireNonNullElse(pointCosts.get(score), 0));
        }
        int delta = score - rules.getBaseAttributeValue();
        if (delta < 0 && !rules.isAllowNegativeAttributes()) {
            return 0;
        }
        return delta;
    }

    private static int generateRollValue(AttributeGenerationMethod rules, DiceRoller diceRoller) {
        int total = 0;
        for (AttributeGenerationMethod.DiceTerm term : rules.getDiceTerms()) {
            total += generateDiceTerm(term, diceRoller);
        }
        return total;
    }

    private static int generateDiceTerm(
        AttributeGenerationMethod.DiceTerm term,
        DiceRoller diceRoller
    ) {
        int count = Math.max(0, term.getCount());
        int sides = Math.max(0, term.getSides());
        if (count == 0 || sides == 0) {
            return 0;
        }
        ArrayList<Integer> rolls = new ArrayList<>();
        for (int dieIndex = 0; dieIndex < count; dieIndex++) {
            rolls.add(generateDie(term, sides, diceRoller));
        }
        rolls.sort(Integer::compareTo);
        int dropLowest = Math.min(Math.max(0, term.getDropLowest()), rolls.size());
        int dropHighest = Math.min(
            Math.max(0, term.getDropHighest()),
            rolls.size() - dropLowest
        );
        int total = term.getFlatModifier();
        for (int rollIndex = dropLowest; rollIndex < rolls.size() - dropHighest; rollIndex++) {
            total += rolls.get(rollIndex);
        }
        return total;
    }

    private static int generateDie(
        AttributeGenerationMethod.DiceTerm term,
        int sides,
        DiceRoller diceRoller
    ) {
        int threshold = term.getExplodeThreshold() > 0 ? term.getExplodeThreshold() : sides;
        int value = generateDieFace(term, sides, diceRoller);
        if (!term.isExploding()) {
            return value;
        }
        int total = value;
        int current = value;
        int explosions = 0;
        while (current >= threshold && threshold > 0) {
            if (explosions++ >= 1000) {
                throw new IllegalStateException("Attribute die explosion did not terminate.");
            }
            current = generateDieFace(term, sides, diceRoller);
            if (current == 0) {
                break;
            }
            total += current;
        }
        return total;
    }

    private static int generateDieFace(
        AttributeGenerationMethod.DiceTerm term,
        int sides,
        DiceRoller diceRoller
    ) {
        List<Integer> ignoredFaces = term.getIgnoredFaces();
        for (int attempt = 0; attempt < sides * 2; attempt++) {
            int value = diceRoller.roll(sides);
            if (value < 1 || value > sides) {
                throw new IllegalArgumentException(
                    "DiceRoller returned a value outside the die range."
                );
            }
            if (!ignoredFaces.contains(value)) {
                return value;
            }
        }
        return 0;
    }

    private static Optional<Game.AttributeGenerationOption> resolveOption(
        Collection<Game.AttributeGenerationOption> optionSource,
        String requestedId
    ) {
        List<Game.AttributeGenerationOption> options = new ArrayList<>(
            Objects.requireNonNullElse(optionSource, List.of())
        );
        String safeId = Objects.toString(requestedId, "").trim();
        if (!safeId.isEmpty()) {
            for (Game.AttributeGenerationOption option : options) {
                if (option.getId().equals(safeId)) {
                    return Optional.of(option);
                }
            }
            return Optional.empty();
        }
        return options.size() == 1 ? Optional.of(options.get(0)) : Optional.empty();
    }

    private static ScoreResolution resolveStandardArray(
        AttributeGenerationMethod rules,
        AttributeGenerationMethod.AttributeGenerationRequest request,
        List<Attribute> attributes
    ) {
        String arrayType = Objects.toString(request.getArrayType(), "")
            .trim().toLowerCase(Locale.ROOT);
        if (!arrayType.equals("standard") && !arrayType.equals("elite")) {
            arrayType = Objects.toString(rules.getDefaultArrayType(), "")
                .trim().toLowerCase(Locale.ROOT);
        }
        boolean elite = arrayType.equals("elite");
        if (!elite && !arrayType.equals("standard")) {
            return ScoreResolution.incomplete("Choose a Standard or Elite Attribute array.");
        }

        boolean shared = elite
            ? rules.isAllAttributesUseSameEliteScore()
            : rules.isAllAttributesUseSameStandardScore();
        int sharedScore = elite ? rules.getEliteSharedScore() : rules.getStandardSharedScore();
        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        if (shared) {
            for (Attribute attribute : attributes) {
                scores.put(attribute.getId(), clamp(rules, sharedScore, attribute));
            }
            return ScoreResolution.complete(scores);
        }

        String arrayName = elite ? "eliteArrays" : "standardArrays";
        List<String> entries = Objects.requireNonNullElse(rules.getArray(arrayName), List.of());
        if (entries.isEmpty()) {
            return ScoreResolution.incomplete("The selected Attribute array is empty.");
        }

        if (rules.getStandardArrayAssignmentMode().equals("open")) {
            ArrayList<Integer> values = parseOpenArray(entries);
            Map<String, Integer> assignments = request.getArrayAssignments();
            HashSet<Integer> usedIndexes = new HashSet<>();
            for (Attribute attribute : attributes) {
                if (!assignments.containsKey(attribute.getId())) {
                    return ScoreResolution.incomplete(
                        "Assign every Attribute array value exactly once."
                    );
                }
                int valueIndex = assignments.get(attribute.getId());
                if (valueIndex < 0 || valueIndex >= values.size() || !usedIndexes.add(valueIndex)) {
                    return ScoreResolution.incomplete(
                        "Assign every Attribute array value exactly once."
                    );
                }
                scores.put(attribute.getId(), clamp(rules, values.get(valueIndex), attribute));
            }
            if (usedIndexes.size() != values.size() || values.size() != attributes.size()) {
                return ScoreResolution.incomplete(
                    "The Attribute array must contain one value for every Attribute."
                );
            }
            return ScoreResolution.complete(scores);
        }

        Map<String, Integer> valuesByKey = parseAssignedArray(entries);
        for (Attribute attribute : attributes) {
            OptionalInt value = findAssignedValue(valuesByKey, attribute);
            if (value.isEmpty()) {
                return ScoreResolution.incomplete(
                    "The selected Attribute array does not define every Attribute."
                );
            }
            scores.put(attribute.getId(), clamp(rules, value.getAsInt(), attribute));
        }
        return ScoreResolution.complete(scores);
    }

    private static ScoreResolution resolveDice(
        AttributeGenerationMethod rules,
        AttributeGenerationMethod.AttributeGenerationRequest request,
        List<Attribute> attributes,
        String applicationMode
    ) {
        List<Integer> rolledValues = request.getRolledValues();
        if (rolledValues.size() != attributes.size()) {
            return ScoreResolution.incomplete("Choose a complete generated Attribute roll set.");
        }

        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        if (rules.isAssignInOrder()) {
            for (int index = 0; index < attributes.size(); index++) {
                Attribute attribute = attributes.get(index);
                scores.put(
                    attribute.getId(),
                    resolveDiceScore(rules, rolledValues.get(index), attribute, applicationMode)
                );
            }
            return ScoreResolution.complete(scores);
        }

        Map<String, Integer> assignments = request.getRollAssignments();
        HashSet<Integer> usedIndexes = new HashSet<>();
        for (Attribute attribute : attributes) {
            if (!assignments.containsKey(attribute.getId())) {
                return ScoreResolution.incomplete("Assign every rolled Attribute value exactly once.");
            }
            int rollIndex = assignments.get(attribute.getId());
            if (rollIndex < 0
                || rollIndex >= rolledValues.size()
                || !usedIndexes.add(rollIndex)) {
                return ScoreResolution.incomplete("Assign every rolled Attribute value exactly once.");
            }
            scores.put(
                attribute.getId(),
                resolveDiceScore(rules, rolledValues.get(rollIndex), attribute, applicationMode)
            );
        }
        return ScoreResolution.complete(scores);
    }

    private static int resolveDiceScore(
        AttributeGenerationMethod rules,
        int rolledValue,
        Attribute attribute,
        String applicationMode
    ) {
        return applicationMode.equals(Game.AttributeGenerationStep.APPLICATION_ADD)
            ? rolledValue
            : clamp(rules, rolledValue, attribute);
    }

    private static ScoreResolution resolvePointBuy(
        AttributeGenerationMethod rules,
        AttributeGenerationMethod.AttributeGenerationRequest request,
        List<Attribute> attributes,
        Set<String> validCategoryKeys,
        Map<String, Integer> baselineScores,
        String applicationMode
    ) {
        Map<String, Integer> requestedScores = request.getPointBuyScores();
        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> costsByAttribute = new LinkedHashMap<>();
        for (Attribute attribute : attributes) {
            String attributeId = Objects.toString(attribute.getId(), "");
            if (!requestedScores.containsKey(attributeId)) {
                return ScoreResolution.incomplete("Choose a Point Buy score for every Attribute.");
            }
            int requestedScore = requestedScores.get(attributeId);
            int minimum = attributeMinimum(rules, attribute);
            int maximum = attributeMaximum(rules, attribute);
            if (requestedScore < minimum || requestedScore > maximum) {
                return ScoreResolution.incomplete(
                    "A Point Buy score is outside the configured Attribute limits."
                );
            }
            int baseline = baselineScores.getOrDefault(attributeId, attributeBase(rules, attribute));
            int cost;
            if (applicationMode.equals(Game.AttributeGenerationStep.APPLICATION_ADD)) {
                int addedValue = requestedScore - baseline;
                cost = attributePointCost(rules, addedValue) - attributePointCost(rules, 0);
            } else if (applicationMode.equals(Game.AttributeGenerationStep.APPLICATION_SPEND)) {
                cost = attributePointCost(rules, requestedScore)
                    - attributePointCost(rules, baseline);
            } else {
                cost = attributePointCost(rules, requestedScore);
            }
            scores.put(attributeId, requestedScore);
            costsByAttribute.put(attributeId, cost);
        }

        if (!rules.isAssignByCategory()) {
            int pointsSpent = costsByAttribute.values().stream().mapToInt(Integer::intValue).sum();
            return validatePointTotals(
                rules,
                scores,
                pointsSpent,
                Math.max(0, rules.getBasePoints()) - pointsSpent,
                Map.of()
            );
        }

        CategoryConfiguration configuration = resolveCategoryConfiguration(
            rules,
            attributes,
            validCategoryKeys,
            request.getCategoryPointSlotAssignments()
        );
        if (!configuration.complete()) {
            return ScoreResolution.incomplete(configuration.reason());
        }

        LinkedHashMap<String, Integer> spentByCategory = new LinkedHashMap<>();
        configuration.budgets().keySet().forEach(category -> spentByCategory.put(category, 0));
        for (Attribute attribute : attributes) {
            String categoryKey = normalizeCategoryKey(attribute.getType());
            if (!spentByCategory.containsKey(categoryKey)) {
                return ScoreResolution.incomplete(
                    "Every Attribute must belong to a configured Point Buy category."
                );
            }
            String attributeId = Objects.toString(attribute.getId(), "");
            spentByCategory.merge(categoryKey, costsByAttribute.get(attributeId), Integer::sum);
        }

        LinkedHashMap<String, AttributeGenerationMethod.CategoryPointBudgetResult> summaries =
            new LinkedHashMap<>();
        int totalBudget = 0;
        int totalSpent = 0;
        boolean overBudget = false;
        for (Map.Entry<String, BudgetDefinition> entry : configuration.budgets().entrySet()) {
            String categoryKey = entry.getKey();
            BudgetDefinition definition = entry.getValue();
            int spent = spentByCategory.getOrDefault(categoryKey, 0);
            AttributeGenerationMethod.CategoryPointBudgetResult summary =
                new AttributeGenerationMethod.CategoryPointBudgetResult(
                    categoryKey,
                    definition.sourceId(),
                    definition.sourceName(),
                    definition.budget(),
                    spent
                );
            summaries.put(categoryKey, summary);
            totalBudget += summary.getBudget();
            totalSpent += spent;
            overBudget = overBudget || summary.getRemaining() < 0;
        }
        int totalRemaining = totalBudget - totalSpent;
        if (overBudget) {
            return ScoreResolution.incomplete(
                scores,
                totalSpent,
                totalRemaining,
                summaries,
                "The selected Attribute scores exceed at least one category's Point Buy budget."
            );
        }
        return validatePointTotals(rules, scores, totalSpent, totalRemaining, summaries);
    }

    private static ScoreResolution validatePointTotals(
        AttributeGenerationMethod rules,
        Map<String, Integer> scores,
        int pointsSpent,
        int pointsRemaining,
        Map<String, AttributeGenerationMethod.CategoryPointBudgetResult> categoryBudgets
    ) {
        if (pointsRemaining < 0) {
            return ScoreResolution.incomplete(
                scores,
                pointsSpent,
                pointsRemaining,
                categoryBudgets,
                "The selected Attribute scores exceed the Point Buy budget."
            );
        }
        if (pointsSpent < Math.max(0, rules.getMinimumPointsToSpend())) {
            return ScoreResolution.incomplete(
                scores,
                pointsSpent,
                pointsRemaining,
                categoryBudgets,
                "The selected Attribute scores do not meet the minimum Point Buy spend."
            );
        }
        return ScoreResolution.complete(scores, pointsSpent, pointsRemaining, categoryBudgets);
    }

    private static CategoryConfiguration resolveCategoryConfiguration(
        AttributeGenerationMethod rules,
        List<Attribute> attributes,
        Set<String> validCategoryKeys,
        Map<String, String> slotAssignments
    ) {
        LinkedHashSet<String> expectedKeys = new LinkedHashSet<>(validCategoryKeys);
        if (expectedKeys.isEmpty()) {
            return CategoryConfiguration.incomplete(
                "Category Point Buy requires at least one Attribute Category."
            );
        }
        for (Attribute attribute : attributes) {
            String categoryKey = normalizeCategoryKey(attribute.getType());
            if (categoryKey.isEmpty() || !expectedKeys.contains(categoryKey)) {
                return CategoryConfiguration.incomplete(
                    "Every Attribute must reference a valid Attribute Category."
                );
            }
        }

        LinkedHashMap<String, BudgetDefinition> budgets = new LinkedHashMap<>();
        if (!rules.isPlayerAssignsCategories()) {
            LinkedHashMap<String, AttributeGenerationMethod.CategoryPointRule> rulesByCategory =
                new LinkedHashMap<>();
            for (AttributeGenerationMethod.CategoryPointRule rule : rules.getCategoryPointRules()) {
                rulesByCategory.put(normalizeCategoryKey(rule.getAttributeCategoryKey()), rule);
            }
            if (rulesByCategory.size() != expectedKeys.size()
                || !rulesByCategory.keySet().containsAll(expectedKeys)) {
                return CategoryConfiguration.incomplete(
                    "The loaded category Point Buy rules are incomplete or stale."
                );
            }
            for (String categoryKey : expectedKeys) {
                AttributeGenerationMethod.CategoryPointRule rule = rulesByCategory.get(categoryKey);
                budgets.put(
                    categoryKey,
                    new BudgetDefinition(
                        categoryKey,
                        "",
                        Math.max(0, rule.getAvailablePoints())
                    )
                );
            }
            return CategoryConfiguration.complete(budgets);
        }

        if (!rules.areCategoryPointSlotAssignmentsComplete(slotAssignments, expectedKeys)) {
            return CategoryConfiguration.incomplete(
                "Assign every Point Buy category slot to a different Attribute Category."
            );
        }
        LinkedHashMap<String, AttributeGenerationMethod.CategoryPointSlot> slotsById =
            new LinkedHashMap<>();
        for (AttributeGenerationMethod.CategoryPointSlot slot : rules.getCategoryPointSlots()) {
            slotsById.put(slot.getId(), slot);
        }
        for (Map.Entry<String, String> assignment : slotAssignments.entrySet()) {
            String slotId = Objects.toString(assignment.getKey(), "").trim();
            String categoryKey = normalizeCategoryKey(assignment.getValue());
            if (slotsById.containsKey(slotId)) {
                AttributeGenerationMethod.CategoryPointSlot slot = Objects.requireNonNull(
                    slotsById.get(slotId),
                    "categoryPointSlot"
                );
                budgets.put(
                    categoryKey,
                    new BudgetDefinition(slot.getId(), slot.getName(), slot.getAvailablePoints())
                );
            }
        }
        LinkedHashMap<String, BudgetDefinition> orderedBudgets = new LinkedHashMap<>();
        for (String categoryKey : expectedKeys) {
            if (budgets.containsKey(categoryKey)) {
                orderedBudgets.put(categoryKey, budgets.get(categoryKey));
            }
        }
        if (orderedBudgets.size() != expectedKeys.size()) {
            return CategoryConfiguration.incomplete(
                "Assign every Point Buy category slot to a different Attribute Category."
            );
        }
        return CategoryConfiguration.complete(orderedBudgets);
    }

    private static LinkedHashMap<String, Integer> addScores(
        AttributeGenerationMethod rules,
        Map<String, Integer> baseline,
        Map<String, Integer> addition,
        List<Attribute> attributes
    ) {
        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        for (Attribute attribute : attributes) {
            String attributeId = Objects.toString(attribute.getId(), "");
            int baseValue = baseline.getOrDefault(attributeId, attributeBase(rules, attribute));
            scores.put(
                attributeId,
                clamp(rules, baseValue + addition.getOrDefault(attributeId, 0), attribute)
            );
        }
        return scores;
    }

    private static String validateScores(
        AttributeGenerationMethod rules,
        Map<String, Integer> scores,
        List<Attribute> attributes
    ) {
        int total = 0;
        Map<String, Integer> requirements = rules.getMinimumRequirements();
        for (Attribute attribute : attributes) {
            String attributeId = Objects.toString(attribute.getId(), "");
            if (!scores.containsKey(attributeId)) {
                return "Attribute Generation did not produce every Attribute score.";
            }
            int score = scores.get(attributeId);
            if (score < attributeMinimum(rules, attribute)
                || score > attributeMaximum(rules, attribute)) {
                return "An Attribute score is outside the Game's configured limits.";
            }
            if (requirements.containsKey(attributeId) && score < requirements.get(attributeId)) {
                return "An Attribute score does not meet the Game's minimum requirement.";
            }
            total += score;
        }
        if (rules.getMinimumTotal() > 0 && total < rules.getMinimumTotal()) {
            return "The Attribute score total does not meet the Game's minimum.";
        }
        if (rules.getMaximumTotal() > 0 && total > rules.getMaximumTotal()) {
            return "The Attribute score total exceeds the Game's maximum.";
        }
        return "";
    }

    private static int clamp(
        AttributeGenerationMethod rules,
        int score,
        Attribute attribute
    ) {
        return Math.max(
            attributeMinimum(rules, attribute),
            Math.min(attributeMaximum(rules, attribute), score)
        );
    }

    private static ArrayList<Integer> parseOpenArray(List<String> entries) {
        ArrayList<Integer> values = new ArrayList<>();
        for (String entry : entries) {
            String safeEntry = Objects.toString(entry, "").trim();
            int separator = safeEntry.indexOf('=');
            String valueText = separator >= 0
                ? safeEntry.substring(separator + 1).trim()
                : safeEntry;
            try {
                values.add(Integer.parseInt(valueText));
            } catch (NumberFormatException ignored) {
                // Invalid persisted entries make the cardinality check incomplete.
            }
        }
        return values;
    }

    private static Map<String, Integer> parseAssignedArray(List<String> entries) {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        for (String entry : entries) {
            String safeEntry = Objects.toString(entry, "").trim();
            int separator = safeEntry.indexOf('=');
            if (separator <= 0 || separator >= safeEntry.length() - 1) {
                continue;
            }
            String key = safeEntry.substring(0, separator).trim().toLowerCase(Locale.ROOT);
            try {
                values.put(key, Integer.parseInt(safeEntry.substring(separator + 1).trim()));
            } catch (NumberFormatException ignored) {
                // Invalid persisted entries remain unavailable to resolution.
            }
        }
        return values;
    }

    private static OptionalInt findAssignedValue(
        Map<String, Integer> values,
        Attribute attribute
    ) {
        for (String key : List.of(
            Objects.toString(attribute.getName(), ""),
            Objects.toString(attribute.getDisplayName(), ""),
            Objects.toString(attribute.getId(), "")
        )) {
            String normalizedKey = key.trim().toLowerCase(Locale.ROOT);
            if (!normalizedKey.isEmpty() && values.containsKey(normalizedKey)) {
                return OptionalInt.of(values.get(normalizedKey));
            }
        }
        return OptionalInt.empty();
    }

    private static AttributeGenerationMethod.AttributeGenerationResult result(
        boolean complete,
        Map<String, Integer> scores,
        Map<Integer, Map<String, Integer>> stepResults,
        int pointsSpent,
        int pointsRemaining,
        Map<String, AttributeGenerationMethod.CategoryPointBudgetResult> categoryBudgets,
        String reason
    ) {
        return new AttributeGenerationMethod.AttributeGenerationResult(
            complete,
            scores,
            stepResults,
            pointsSpent,
            pointsRemaining,
            categoryBudgets,
            reason
        );
    }

    private static AttributeGenerationMethod requireMethod(AttributeGenerationMethod method) {
        return Objects.requireNonNull(method, "attributeGenerationMethod");
    }

    private static List<Attribute> normalizeAttributes(Collection<Attribute> source) {
        Collection<Attribute> safeSource = Objects.requireNonNullElse(source, List.of());
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (Attribute attribute : safeSource) {
            attributes.add(Objects.requireNonNull(attribute, "attribute"));
        }
        return List.copyOf(attributes);
    }

    private static LinkedHashSet<String> normalizeCategoryKeys(Collection<String> source) {
        Collection<String> safeSource = Objects.requireNonNullElse(source, List.of());
        LinkedHashSet<String> categoryKeys = new LinkedHashSet<>();
        for (String key : safeSource) {
            String safeKey = normalizeCategoryKey(key);
            if (!safeKey.isEmpty()) {
                categoryKeys.add(safeKey);
            }
        }
        return categoryKeys;
    }

    private static String normalizeCategoryKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private record BudgetDefinition(String sourceId, String sourceName, int budget) {
        private BudgetDefinition {
            sourceId = Objects.toString(sourceId, "").trim();
            sourceName = Objects.toString(sourceName, "").trim();
            budget = Math.max(0, budget);
        }
    }

    private record CategoryConfiguration(
        boolean complete,
        Map<String, BudgetDefinition> budgets,
        String reason
    ) {
        private CategoryConfiguration {
            budgets = new LinkedHashMap<>(Objects.requireNonNullElse(budgets, Map.of()));
            reason = Objects.toString(reason, "");
        }

        private static CategoryConfiguration complete(Map<String, BudgetDefinition> budgets) {
            return new CategoryConfiguration(true, budgets, "");
        }

        private static CategoryConfiguration incomplete(String reason) {
            return new CategoryConfiguration(false, Map.of(), reason);
        }
    }

    private record ScoreResolution(
        boolean complete,
        Map<String, Integer> scores,
        int pointsSpent,
        int pointsRemaining,
        Map<String, AttributeGenerationMethod.CategoryPointBudgetResult> categoryBudgets,
        String reason
    ) {
        private ScoreResolution {
            scores = new LinkedHashMap<>(Objects.requireNonNullElse(scores, Map.of()));
            categoryBudgets = new LinkedHashMap<>(
                Objects.requireNonNullElse(categoryBudgets, Map.of())
            );
            reason = Objects.toString(reason, "");
        }

        private static ScoreResolution complete(Map<String, Integer> scores) {
            return complete(scores, 0, 0, Map.of());
        }

        private static ScoreResolution complete(
            Map<String, Integer> scores,
            int pointsSpent,
            int pointsRemaining,
            Map<String, AttributeGenerationMethod.CategoryPointBudgetResult> categoryBudgets
        ) {
            return new ScoreResolution(
                true,
                scores,
                pointsSpent,
                pointsRemaining,
                categoryBudgets,
                ""
            );
        }

        private static ScoreResolution incomplete(String reason) {
            return incomplete(Map.of(), 0, 0, Map.of(), reason);
        }

        private static ScoreResolution incomplete(
            Map<String, Integer> scores,
            int pointsSpent,
            int pointsRemaining,
            Map<String, AttributeGenerationMethod.CategoryPointBudgetResult> categoryBudgets,
            String reason
        ) {
            return new ScoreResolution(
                false,
                scores,
                pointsSpent,
                pointsRemaining,
                categoryBudgets,
                reason
            );
        }
    }
}
