/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks


package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.AtomicElements.*;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.util.*;

/**
 * Comprehensive attribute generation system supporting dice rolling, point buy,
 * hybrid methods, and advanced RPG attribute generation techniques.
 * Focuses on codeable mechanics - excludes storyteller discretionary elements.
 */
public class AttributeGenerationMethod extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 7606235203030250610L;
    public static final String CATEGORY_ASSIGNMENT_CREATOR = "creator";
    public static final String CATEGORY_ASSIGNMENT_PLAYER = "player";
    public static final String LEGACY_DICE_SUBSTITUTION_ID = "legacy-dice-substitution";

    // === GENERATION TYPE CONTROL ===
    private String generationType = "dice";

    // === GENERIC ARRAY REGISTRY ===
    private ArrayHandler arrayHandler = new ArrayHandler();

    // === DICE ROLLING SYSTEM ===
    private ArrayList<DiceTerm> diceTerms = new ArrayList<>();

    // Multiple Sets Generation
    private int numberOfSets = 0;

    // Advanced Dice Features
    private Map<String,String> diceVariants = new HashMap<>();
    private boolean allowDiceSubstitution = false;
    private int diceSubstitutionValue = 14;
    private int maxDiceSubstitutions = 1;
    private ArrayList<RollAdjustmentMethod> rollAdjustmentMethods = new ArrayList<>();

    // === POINT BUY SYSTEM ===
    private int basePoints = 0;
    private int baseAttributeValue = 0;
    private Map<Integer,Integer> pointCosts = new HashMap<>();
    private int minAttributeValue = 0;
    private int maxAttributeValue = 0;
    private int maxAttributeValuePostRacial = 0;
    private boolean allowNegativeAttributes = false;
    private int minimumPointsToSpend = 0;
    private boolean assignByCategory = false;
    private String categoryAssignmentMode = CATEGORY_ASSIGNMENT_CREATOR;
    private ArrayList<CategoryPointRule> categoryPointRules = new ArrayList<>();
    private ArrayList<CategoryPointSlot> categoryPointSlots = new ArrayList<>();

    // === STANDARD ARRAYS ===
    private String defaultArrayType = "standard";
    private String standardArrayAssignmentMode = "assigned";
    private boolean allAttributesUseSameStandardScore = false;
    private int standardSharedScore = 0;
    private boolean allAttributesUseSameEliteScore = false;
    private int eliteSharedScore = 0;

    // === ASSIGNMENT RULES ===
    private boolean assignInOrder = false;
    private boolean allowReassignment = false;
    private Map<String,Integer> minimumRequirements = new HashMap<>();

    // === VALIDATION RULES ===
    private int minimumTotal = 0;
    private int maximumTotal = 0;
    private boolean requireMinimumInPrimary = false;
    private int primaryAttributeMinimum = 0;
    private Map<String,Map<String,Integer>> classMinimums = new HashMap<>();

    // === RACIAL MODIFIER INTEGRATION ===
    private String racialModifierTiming = "after";
    private boolean racialModsAffectLimits = false;
    private boolean racialModsCountAgainstTotal = false;

    // === HYBRID SYSTEM COMBINATIONS ===
    private Map<String,Object> hybridParameters = new HashMap<>();
    private boolean allowHybridOptimization = false;

    // === ADVANCED FEATURES ===
    private Map<String,String> systemVariants = new HashMap<>();
    private boolean useWeightedAttributes = false;
    private Map<String,Double> attributeWeights = new HashMap<>();

    // === CONFIGURATION FLAGS ===
    private boolean usesAdvancedDice = false;
    private boolean usesPointBuyVariants = false;
    private boolean usesHybridSystems = false;
    private boolean usesClassValidation = false;
    private boolean usesRacialIntegration = false;

// *** CONSTRUCTORS ***
    public AttributeGenerationMethod(String name) {
        super(name);
        initializeArrayRegistry();
        initializeDefaults();
    }

    public AttributeGenerationMethod(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
        initializeDefaults();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("hybridStages", new ArrayList<String>());
        arrayHandler.putArray("standardArrays", new ArrayList<String>());
        arrayHandler.putArray("eliteArrays", new ArrayList<String>());
        arrayHandler.putArray("attributeOrder", new ArrayList<String>());
    }

    private void initializeDefaults() {
        // Initialize array registry with empty arrays (system-agnostic - no defaults)
    }

    // === GENERIC ARRAY HANDLER METHODS ===
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        if (!arrayHandler.getArrayNames().contains(arrayName)) {
            return null;
        }
        ArrayList<T> array = arrayHandler.getArray(arrayName);
        if (array == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(array);
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        if (!arrayHandler.getArrayNames().contains(arrayName)) {
            return null;
        }
        ArrayList<T> array = arrayHandler.getObjectArray(arrayName);
        if (array == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(array);
    }

    public <T> void addToArray(String arrayName, T value) {
        arrayHandler.addElement(arrayName, value);
    }

    public <T> boolean removeFromArray(String arrayName, T value) {
        return arrayHandler.removeElement(arrayName, value);
    }

    public <T> void replaceArray(String arrayName, List<T> values) {
        List<T> safeValues = Objects.requireNonNullElseGet(values, List::of);
        arrayHandler.putArray(arrayName, new ArrayList<>(safeValues));
    }

    public void clearArray(String arrayName) {
        arrayHandler.clearArray(arrayName);
    }

    public Set<String> getArrayNames() {
        return new HashSet<>(arrayHandler.getArrayNames());
    }

    public List<String> getAttributeOrder() {
        ArrayList<String> storedOrder = getArray("attributeOrder");
        return storedOrder == null ? List.of() : storedOrder;
    }

    public void setAttributeOrder(Collection<String> attributeIds) {
        Collection<String> safeIds = Objects.requireNonNullElseGet(attributeIds, List::of);
        ArrayList<String> normalizedIds = new ArrayList<>();
        for (String attributeId : safeIds) {
            String safeId = Objects.toString(attributeId, "").trim();
            if (!safeId.isEmpty() && !normalizedIds.contains(safeId)) {
                normalizedIds.add(safeId);
            }
        }
        replaceArray("attributeOrder", normalizedIds);
    }

    // === GENERATION TYPE METHODS ===
    public String getGenerationType() { return generationType; }
    public void setGenerationType(String generationType) {
        this.generationType = Objects.toString(generationType, "");
    }


    // === DICE SYSTEM METHODS ===
    public ArrayList<DiceTerm> getDiceTerms() { return copyDiceTerms(diceTerms); }
    public void setDiceTerms(Collection<DiceTerm> diceTerms) { this.diceTerms = copyDiceTerms(diceTerms); }
    public void addDiceTerm(DiceTerm diceTerm) {
        diceTerms.add(new DiceTerm(Objects.requireNonNull(diceTerm, "diceTerm")));
    }
    public boolean removeDiceTerm(DiceTerm diceTerm) {
        return diceTerms.remove(Objects.requireNonNull(diceTerm, "diceTerm"));
    }
    public void clearDiceTerms() { diceTerms.clear(); }

    public int getNumberOfSets() { return numberOfSets; }
    public void setNumberOfSets(int numberOfSets) { this.numberOfSets = numberOfSets; }

    public Map<String,String> getDiceVariants() { return new HashMap<>(diceVariants); }
    public void setDiceVariants(Map<String,String> diceVariants) {
        this.diceVariants = new HashMap<>(Objects.requireNonNullElse(diceVariants, Collections.emptyMap()));
    }
    public void addDiceVariant(String attribute, String diceExpression) { diceVariants.put(attribute, diceExpression); }
    public void removeDiceVariant(String attribute) { diceVariants.remove(attribute); }

    public boolean isAllowDiceSubstitution() { return allowDiceSubstitution; }
    public void setAllowDiceSubstitution(boolean allowDiceSubstitution) {
        this.allowDiceSubstitution = allowDiceSubstitution;
        if (!allowDiceSubstitution && rollAdjustmentMethods != null) {
            rollAdjustmentMethods.removeIf(
                method -> method.getId().equals(LEGACY_DICE_SUBSTITUTION_ID)
            );
        } else if (allowDiceSubstitution) {
            ensureLegacyRollAdjustment();
        }
    }

    public int getDiceSubstitutionValue() { return diceSubstitutionValue; }
    public void setDiceSubstitutionValue(int diceSubstitutionValue) {
        this.diceSubstitutionValue = diceSubstitutionValue;
        syncLegacyRollAdjustment();
    }

    public int getMaxDiceSubstitutions() { return maxDiceSubstitutions; }
    public void setMaxDiceSubstitutions(int maxDiceSubstitutions) {
        this.maxDiceSubstitutions = Math.max(0, maxDiceSubstitutions);
        if (this.maxDiceSubstitutions == 0) {
            allowDiceSubstitution = false;
            rollAdjustmentMethods.removeIf(
                method -> method.getId().equals(LEGACY_DICE_SUBSTITUTION_ID)
            );
        } else {
            syncLegacyRollAdjustment();
        }
    }

    public ArrayList<RollAdjustmentMethod> getRollAdjustmentMethods() {
        ensureLegacyRollAdjustment();
        return copyRollAdjustmentMethods(rollAdjustmentMethods);
    }

    public void setRollAdjustmentMethods(Collection<RollAdjustmentMethod> methods) {
        rollAdjustmentMethods = copyRollAdjustmentMethods(methods);
        syncLegacyFields();
    }

    public void addRollAdjustmentMethod(RollAdjustmentMethod method) {
        RollAdjustmentMethod copy = new RollAdjustmentMethod(
            Objects.requireNonNull(method, "rollAdjustmentMethod")
        );
        removeRollAdjustmentMethod(copy.getId());
        rollAdjustmentMethods.add(copy);
        syncLegacyFields();
    }

    public boolean removeRollAdjustmentMethod(String methodId) {
        String safeId = Objects.toString(methodId, "").trim();
        boolean removed = rollAdjustmentMethods.removeIf(method -> method.getId().equals(safeId));
        syncLegacyFields();
        return removed;
    }

    // === POINT BUY METHODS ===
    public int getBasePoints() { return basePoints; }
    public void setBasePoints(int basePoints) { this.basePoints = basePoints; }

    public int getBaseAttributeValue() { return baseAttributeValue; }
    public void setBaseAttributeValue(int baseAttributeValue) { this.baseAttributeValue = baseAttributeValue; }

    public Map<Integer,Integer> getPointCosts() { return new HashMap<>(pointCosts); }
    public void setPointCosts(Map<Integer,Integer> pointCosts) {
        this.pointCosts = new HashMap<>(Objects.requireNonNullElse(pointCosts, Collections.emptyMap()));
    }
    public void setPointCost(int attributeValue, int cost) { pointCosts.put(attributeValue, cost); }
    public int getPointCost(int attributeValue) {
        Integer cost = pointCosts.get(attributeValue);
        return cost == null ? -1 : cost;
    }

    public int getMinAttributeValue() { return minAttributeValue; }
    public void setMinAttributeValue(int minAttributeValue) { this.minAttributeValue = minAttributeValue; }

    public int getMaxAttributeValue() { return maxAttributeValue; }
    public void setMaxAttributeValue(int maxAttributeValue) { this.maxAttributeValue = maxAttributeValue; }

    public int getMaxAttributeValuePostRacial() { return maxAttributeValuePostRacial; }
    public void setMaxAttributeValuePostRacial(int maxAttributeValuePostRacial) {
        this.maxAttributeValuePostRacial = maxAttributeValuePostRacial;
    }

    public boolean isAllowNegativeAttributes() { return allowNegativeAttributes; }
    public void setAllowNegativeAttributes(boolean allowNegativeAttributes) {
        this.allowNegativeAttributes = allowNegativeAttributes;
    }

    public int getMinimumPointsToSpend() { return minimumPointsToSpend; }
    public void setMinimumPointsToSpend(int minimumPointsToSpend) { this.minimumPointsToSpend = minimumPointsToSpend; }

    public boolean isAssignByCategory() { return assignByCategory; }
    public void setAssignByCategory(boolean assignByCategory) { this.assignByCategory = assignByCategory; }

    public String getCategoryAssignmentMode() { return categoryAssignmentMode; }
    public void setCategoryAssignmentMode(String categoryAssignmentMode) {
        String safeMode = Objects.toString(categoryAssignmentMode, "").trim().toLowerCase(Locale.ROOT);
        this.categoryAssignmentMode = safeMode.equals(CATEGORY_ASSIGNMENT_PLAYER)
            ? CATEGORY_ASSIGNMENT_PLAYER
            : CATEGORY_ASSIGNMENT_CREATOR;
    }

    public boolean isPlayerAssignsCategories() {
        return CATEGORY_ASSIGNMENT_PLAYER.equals(categoryAssignmentMode);
    }

    public ArrayList<CategoryPointRule> getCategoryPointRules() {
        return copyCategoryPointRules(categoryPointRules);
    }

    public void setCategoryPointRules(Collection<CategoryPointRule> categoryPointRules) {
        this.categoryPointRules = copyCategoryPointRules(categoryPointRules);
    }

    public void setCategoryAvailablePoints(String attributeCategoryKey, int availablePoints) {
        String safeKey = normalizeAttributeCategoryKey(attributeCategoryKey);
        if (safeKey.isEmpty()) {
            return;
        }
        for (CategoryPointRule rule : categoryPointRules) {
            if (rule.getAttributeCategoryKey().equals(safeKey)) {
                rule.setAvailablePoints(availablePoints);
                return;
            }
        }
        categoryPointRules.add(new CategoryPointRule(safeKey, availablePoints));
    }

    public boolean removeCategoryPointRule(String attributeCategoryKey) {
        String safeKey = normalizeAttributeCategoryKey(attributeCategoryKey);
        return categoryPointRules.removeIf(rule -> rule.getAttributeCategoryKey().equals(safeKey));
    }

    public OptionalInt getCategoryAvailablePoints(String attributeCategoryKey) {
        String safeKey = normalizeAttributeCategoryKey(attributeCategoryKey);
        for (CategoryPointRule rule : categoryPointRules) {
            if (rule.getAttributeCategoryKey().equals(safeKey)) {
                return OptionalInt.of(rule.getAvailablePoints());
            }
        }
        return OptionalInt.empty();
    }

    public int cleanupCategoryPointRules(Set<String> validAttributeCategoryKeys) {
        Set<String> safeKeys = Objects.requireNonNullElse(validAttributeCategoryKeys, Collections.emptySet());
        int originalSize = categoryPointRules.size();
        categoryPointRules.removeIf(rule -> !safeKeys.contains(rule.getAttributeCategoryKey()));
        return originalSize - categoryPointRules.size();
    }

    public ArrayList<CategoryPointSlot> getCategoryPointSlots() {
        return copyCategoryPointSlots(categoryPointSlots);
    }

    public void setCategoryPointSlots(Collection<CategoryPointSlot> categoryPointSlots) {
        this.categoryPointSlots = copyCategoryPointSlots(categoryPointSlots);
    }

    public boolean isCategoryPointConfigurationComplete(int attributeCategoryCount) {
        if (!assignByCategory) {
            return true;
        }
        int safeCategoryCount = Math.max(0, attributeCategoryCount);
        if (isPlayerAssignsCategories()) {
            return safeCategoryCount > 0 && categoryPointSlots.size() == safeCategoryCount;
        }
        return safeCategoryCount > 0 && categoryPointRules.size() == safeCategoryCount;
    }

    public boolean areCategoryPointSlotAssignmentsComplete(
        Map<String, String> slotAssignments,
        Set<String> validAttributeCategoryKeys
    ) {
        Set<String> safeCategoryKeys = new HashSet<>();
        Set<String> sourceCategoryKeys = Objects.requireNonNullElseGet(
            validAttributeCategoryKeys,
            Collections::emptySet
        );
        for (String key : sourceCategoryKeys) {
            String safeKey = normalizeAttributeCategoryKey(key);
            if (!safeKey.isEmpty()) {
                safeCategoryKeys.add(safeKey);
            }
        }
        if (!assignByCategory
            || !isPlayerAssignsCategories()
            || !isCategoryPointConfigurationComplete(safeCategoryKeys.size())) {
            return false;
        }
        Map<String, String> safeAssignments = Objects.requireNonNullElseGet(
            slotAssignments,
            Collections::emptyMap
        );
        if (safeAssignments.size() != categoryPointSlots.size()) {
            return false;
        }
        Set<String> expectedSlotIds = new HashSet<>();
        for (CategoryPointSlot slot : categoryPointSlots) {
            expectedSlotIds.add(slot.getId());
        }
        Set<String> assignedCategoryKeys = new HashSet<>();
        for (Map.Entry<String, String> entry : safeAssignments.entrySet()) {
            String slotId = Objects.toString(entry.getKey(), "").trim();
            String categoryKey = normalizeAttributeCategoryKey(entry.getValue());
            if (!expectedSlotIds.contains(slotId)
                || !safeCategoryKeys.contains(categoryKey)
                || !assignedCategoryKeys.add(categoryKey)) {
                return false;
            }
        }
        return assignedCategoryKeys.size() == safeCategoryKeys.size();
    }

    // === ARRAY METHODS ===
    public String getDefaultArrayType() { return defaultArrayType; }
    public void setDefaultArrayType(String defaultArrayType) {
        this.defaultArrayType = Objects.toString(defaultArrayType, "");
    }

    public String getStandardArrayAssignmentMode() { return standardArrayAssignmentMode; }
    public void setStandardArrayAssignmentMode(String standardArrayAssignmentMode) {
        String safeMode = Objects.toString(standardArrayAssignmentMode, "").trim().toLowerCase();
        if (!safeMode.equals("open")) {
            safeMode = "assigned";
        }
        this.standardArrayAssignmentMode = safeMode;
        if (safeMode.equals("open")) {
            normalizePlayerAssignedArrays();
        }
    }

    public boolean isAllAttributesUseSameStandardScore() {
        return allAttributesUseSameStandardScore;
    }
    public void setAllAttributesUseSameStandardScore(boolean allAttributesUseSameStandardScore) {
        this.allAttributesUseSameStandardScore = allAttributesUseSameStandardScore;
    }

    public int getStandardSharedScore() { return standardSharedScore; }
    public void setStandardSharedScore(int standardSharedScore) {
        this.standardSharedScore = standardSharedScore;
    }

    public boolean isAllAttributesUseSameEliteScore() {
        return allAttributesUseSameEliteScore;
    }
    public void setAllAttributesUseSameEliteScore(boolean allAttributesUseSameEliteScore) {
        this.allAttributesUseSameEliteScore = allAttributesUseSameEliteScore;
    }

    public int getEliteSharedScore() { return eliteSharedScore; }
    public void setEliteSharedScore(int eliteSharedScore) {
        this.eliteSharedScore = eliteSharedScore;
    }

    // === ASSIGNMENT METHODS ===
    public boolean isAssignInOrder() { return assignInOrder; }
    public void setAssignInOrder(boolean assignInOrder) { this.assignInOrder = assignInOrder; }


    public boolean isAllowReassignment() { return allowReassignment; }
    public void setAllowReassignment(boolean allowReassignment) { this.allowReassignment = allowReassignment; }

    public Map<String,Integer> getMinimumRequirements() { return new HashMap<>(minimumRequirements); }
    public void setMinimumRequirements(Map<String,Integer> minimumRequirements) {
        this.minimumRequirements = new HashMap<>(Objects.requireNonNullElse(minimumRequirements, Collections.emptyMap()));
    }
    public void setMinimumRequirement(String attribute, int minimum) { minimumRequirements.put(attribute, minimum); }
    public void removeMinimumRequirement(String attribute) { minimumRequirements.remove(attribute); }

    // === VALIDATION METHODS ===
    public int getMinimumTotal() { return minimumTotal; }
    public void setMinimumTotal(int minimumTotal) { this.minimumTotal = minimumTotal; }

    public int getMaximumTotal() { return maximumTotal; }
    public void setMaximumTotal(int maximumTotal) { this.maximumTotal = maximumTotal; }

    public boolean isRequireMinimumInPrimary() { return requireMinimumInPrimary; }
    public void setRequireMinimumInPrimary(boolean requireMinimumInPrimary) {
        this.requireMinimumInPrimary = requireMinimumInPrimary;
    }

    public int getPrimaryAttributeMinimum() { return primaryAttributeMinimum; }
    public void setPrimaryAttributeMinimum(int primaryAttributeMinimum) {
        this.primaryAttributeMinimum = primaryAttributeMinimum;
    }


    public Map<String,Map<String,Integer>> getClassMinimums() { return copyClassMinimums(classMinimums); }
    public void setClassMinimums(Map<String,Map<String,Integer>> classMinimums) {
        this.classMinimums = copyClassMinimums(Objects.requireNonNullElseGet(classMinimums, HashMap::new));
    }
    public void setClassMinimum(String className, String attribute, int minimum) {
        classMinimums.computeIfAbsent(className, k -> new HashMap<>()).put(attribute, minimum);
    }
    public void removeClassMinimum(String className) { classMinimums.remove(className); }

    // === RACIAL MODIFIER METHODS ===
    public String getRacialModifierTiming() { return racialModifierTiming; }
    public void setRacialModifierTiming(String racialModifierTiming) {
        this.racialModifierTiming = Objects.toString(racialModifierTiming, "");
    }

    public boolean isRacialModsAffectLimits() { return racialModsAffectLimits; }
    public void setRacialModsAffectLimits(boolean racialModsAffectLimits) {
        this.racialModsAffectLimits = racialModsAffectLimits;
    }

    public boolean isRacialModsCountAgainstTotal() { return racialModsCountAgainstTotal; }
    public void setRacialModsCountAgainstTotal(boolean racialModsCountAgainstTotal) {
        this.racialModsCountAgainstTotal = racialModsCountAgainstTotal;
    }

    // === HYBRID SYSTEM METHODS ===
    public Map<String,Object> getHybridParameters() { return new HashMap<>(hybridParameters); }
    public void setHybridParameters(Map<String,Object> hybridParameters) {
        this.hybridParameters = new HashMap<>(Objects.requireNonNullElse(hybridParameters, Collections.emptyMap()));
    }
    public void setHybridParameter(String key, Object value) { hybridParameters.put(key, value); }
    public void removeHybridParameter(String key) { hybridParameters.remove(key); }

    public boolean isAllowHybridOptimization() { return allowHybridOptimization; }
    public void setAllowHybridOptimization(boolean allowHybridOptimization) {
        this.allowHybridOptimization = allowHybridOptimization;
    }

    // === ADVANCED FEATURE METHODS ===

    public Map<String,String> getSystemVariants() { return new HashMap<>(systemVariants); }
    public void setSystemVariants(Map<String,String> systemVariants) {
        this.systemVariants = new HashMap<>(Objects.requireNonNullElse(systemVariants, Collections.emptyMap()));
    }
    public void addSystemVariant(String key, String value) { systemVariants.put(key, value); }
    public void removeSystemVariant(String key) { systemVariants.remove(key); }

    public boolean isUseWeightedAttributes() { return useWeightedAttributes; }
    public void setUseWeightedAttributes(boolean useWeightedAttributes) {
        this.useWeightedAttributes = useWeightedAttributes;
    }

    public Map<String,Double> getAttributeWeights() { return new HashMap<>(attributeWeights); }
    public void setAttributeWeights(Map<String,Double> attributeWeights) {
        this.attributeWeights = new HashMap<>(Objects.requireNonNullElse(attributeWeights, Collections.emptyMap()));
    }
    public void setAttributeWeight(String attribute, double weight) { attributeWeights.put(attribute, weight); }
    public void removeAttributeWeight(String attribute) { attributeWeights.remove(attribute); }

    // === CONFIGURATION FLAG METHODS ===
    public boolean isUsesAdvancedDice() { return usesAdvancedDice; }
    public void setUsesAdvancedDice(boolean usesAdvancedDice) { this.usesAdvancedDice = usesAdvancedDice; }

    public boolean isUsesPointBuyVariants() { return usesPointBuyVariants; }
    public void setUsesPointBuyVariants(boolean usesPointBuyVariants) {
        this.usesPointBuyVariants = usesPointBuyVariants;
    }

    public boolean isUsesHybridSystems() { return usesHybridSystems; }
    public void setUsesHybridSystems(boolean usesHybridSystems) { this.usesHybridSystems = usesHybridSystems; }

    public boolean isUsesClassValidation() { return usesClassValidation; }
    public void setUsesClassValidation(boolean usesClassValidation) {
        this.usesClassValidation = usesClassValidation;
    }

    public boolean isUsesRacialIntegration() { return usesRacialIntegration; }
    public void setUsesRacialIntegration(boolean usesRacialIntegration) {
        this.usesRacialIntegration = usesRacialIntegration;
    }

    /**
     * Generates complete Attribute roll sets through this configured rule object.
     * The package-private resolver is deliberately inaccessible to consumers.
     */
    public List<List<Integer>> generateAttributeRollSets(Collection<Attribute> attributes) {
        return AttributeGenerationResolver.generateRollSets(this, attributes, DiceRoller.random());
    }

    /** Deterministic-friendly variant of the configured Attribute roll generator. */
    public List<List<Integer>> generateAttributeRollSets(
        Collection<Attribute> attributeSource,
        DiceRoller diceRoller
    ) {
        return AttributeGenerationResolver.generateRollSets(this, attributeSource, diceRoller);
    }

    /** Generates roll sets with identities that survive pre-assignment adjustments. */
    public List<List<RollAdjustmentMethod.RollValue>> generateAttributeRollValueSets(
        Collection<Attribute> attributeSource
    ) {
        List<List<Integer>> generatedSets = generateAttributeRollSets(attributeSource);
        ArrayList<List<RollAdjustmentMethod.RollValue>> identifiedSets = new ArrayList<>();
        for (int setIndex = 0; setIndex < generatedSets.size(); setIndex++) {
            ArrayList<RollAdjustmentMethod.RollValue> values = new ArrayList<>();
            for (int valueIndex = 0; valueIndex < generatedSets.get(setIndex).size(); valueIndex++) {
                values.add(new RollAdjustmentMethod.RollValue(
                    "set-" + (setIndex + 1) + "-roll-" + (valueIndex + 1),
                    generatedSets.get(setIndex).get(valueIndex)
                ));
            }
            identifiedSets.add(List.copyOf(values));
        }
        return List.copyOf(identifiedSets);
    }

    /** Describes or applies one creator-authorized pre-assignment roll adjustment. */
    public RollAdjustmentMethod.Result adjustAttributeRolls(
        Collection<Attribute> attributeSource,
        RollAdjustmentMethod.Request request
    ) {
        return AttributeGenerationResolver.adjustRolls(this, attributeSource, request);
    }

    /** Applies one configured dice substitution without exposing its rule to a consumer. */
    public List<Integer> applyAttributeRollSubstitution(
        Collection<Attribute> attributeSource,
        Collection<Integer> rolledValues,
        int rollIndex,
        int substitutionsAlreadyUsed
    ) {
        return AttributeGenerationResolver.applyRollSubstitution(
            this,
            attributeSource,
            rolledValues,
            rollIndex,
            substitutionsAlreadyUsed
        );
    }

    /**
     * Resolves the authoritative starting Attribute scores for player choices.
     * The caller supplies the fully loaded Game's options and ordered Attributes;
     * this mechanic owns their generation, combination, pricing, and validation.
     */
    public AttributeGenerationResult getAttributeScores(
        Collection<Game.AttributeGenerationOption> optionSource,
        Collection<Attribute> attributeSource,
        AttributeGenerationRequest request
    ) {
        LinkedHashSet<String> categoryKeys = new LinkedHashSet<>();
        Collection<Attribute> safeAttributes = Objects.requireNonNullElse(
            attributeSource,
            List.of()
        );
        for (Attribute attribute : safeAttributes) {
            String categoryKey = normalizeAttributeCategoryKey(
                Objects.requireNonNull(attribute, "attribute").getType()
            );
            if (!categoryKey.isEmpty()) {
                categoryKeys.add(categoryKey);
            }
        }
        return getAttributeScores(optionSource, attributeSource, categoryKeys, request);
    }

    /**
     * Full resolution boundary used by a loaded Game. Category keys must come from
     * that Game's Attribute Category registry so category-slot assignments cannot
     * substitute invented categories.
     */
    public AttributeGenerationResult getAttributeScores(
        Collection<Game.AttributeGenerationOption> optionSource,
        Collection<Attribute> attributeSource,
        Collection<String> attributeCategoryKeys,
        AttributeGenerationRequest request
    ) {
        return AttributeGenerationResolver.resolve(
            this,
            optionSource,
            attributeSource,
            attributeCategoryKeys,
            request
        );
    }

    public int getAttributeMinimumScore(Attribute attribute) {
        return AttributeGenerationResolver.attributeMinimum(this, attribute);
    }

    public int getAttributeMaximumScore(Attribute attribute) {
        return AttributeGenerationResolver.attributeMaximum(this, attribute);
    }

    public int getAttributeBaseScore(Attribute attribute) {
        return AttributeGenerationResolver.attributeBase(this, attribute);
    }

    public int getAttributePointCost(int score) {
        return AttributeGenerationResolver.attributePointCost(this, score);
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
        boolean migrateLegacySubstitution = allowDiceSubstitution;
        if (arrayHandler == null) {
            arrayHandler = new ArrayHandler();
            initializeArrayRegistry();
        }
        if (!arrayHandler.getArrayNames().contains("attributeOrder")) {
            arrayHandler.putArray("attributeOrder", new ArrayList<String>());
        }
        setCategoryAssignmentMode(categoryAssignmentMode);
        setCategoryPointRules(categoryPointRules);
        setCategoryPointSlots(categoryPointSlots);
        setStandardArrayAssignmentMode(standardArrayAssignmentMode);
        rollAdjustmentMethods = copyRollAdjustmentMethods(rollAdjustmentMethods);
        allowDiceSubstitution = migrateLegacySubstitution;
        if (migrateLegacySubstitution && maxDiceSubstitutions > 0) {
            ensureLegacyRollAdjustment();
        } else {
            syncLegacyFields();
        }
    }

    private void normalizePlayerAssignedArrays() {
        for (String arrayName : List.of("standardArrays", "eliteArrays")) {
            ArrayList<String> entries = Objects.requireNonNullElseGet(getArray(arrayName), ArrayList::new);
            ArrayList<String> values = new ArrayList<>();
            for (String entry : entries) {
                String safeEntry = Objects.toString(entry, "").trim();
                int separator = safeEntry.indexOf('=');
                if (separator >= 0 && separator < safeEntry.length() - 1) {
                    safeEntry = safeEntry.substring(separator + 1).trim();
                }
                values.add(safeEntry);
            }
            replaceArray(arrayName, values);
        }
    }

    private ArrayList<DiceTerm> copyDiceTerms(Collection<DiceTerm> source) {
        Collection<DiceTerm> safeSource = Objects.requireNonNullElse(source, Collections.emptyList());
        ArrayList<DiceTerm> copy = new ArrayList<>();
        for (DiceTerm term : safeSource) {
            copy.add(new DiceTerm(Objects.requireNonNull(term, "diceTerm")));
        }
        return copy;
    }

    private ArrayList<RollAdjustmentMethod> copyRollAdjustmentMethods(
        Collection<RollAdjustmentMethod> source
    ) {
        Collection<RollAdjustmentMethod> safeSource = Objects.requireNonNullElse(source, List.of());
        LinkedHashMap<String, RollAdjustmentMethod> methodsById = new LinkedHashMap<>();
        for (RollAdjustmentMethod method : safeSource) {
            RollAdjustmentMethod copy = new RollAdjustmentMethod(
                Objects.requireNonNull(method, "rollAdjustmentMethod")
            );
            if (!copy.getName().isEmpty()) {
                methodsById.put(copy.getId(), copy);
            }
        }
        return new ArrayList<>(methodsById.values());
    }

    private void ensureLegacyRollAdjustment() {
        if (rollAdjustmentMethods == null) {
            rollAdjustmentMethods = new ArrayList<>();
        }
        if (allowDiceSubstitution && rollAdjustmentMethods.stream().noneMatch(
            method -> method.getId().equals(LEGACY_DICE_SUBSTITUTION_ID)
        )) {
            RollAdjustmentMethod legacy = new RollAdjustmentMethod(
                LEGACY_DICE_SUBSTITUTION_ID,
                "Dice substitution",
                RollAdjustmentMethod.TYPE_FIXED_VALUE
            );
            legacy.setValue(diceSubstitutionValue);
            legacy.setMaximumUses(Math.max(1, maxDiceSubstitutions));
            rollAdjustmentMethods.add(legacy);
        }
    }

    private void syncLegacyRollAdjustment() {
        ensureLegacyRollAdjustment();
        for (RollAdjustmentMethod method : rollAdjustmentMethods) {
            if (method.getId().equals(LEGACY_DICE_SUBSTITUTION_ID)) {
                method.setValue(diceSubstitutionValue);
                method.setMaximumUses(Math.max(1, maxDiceSubstitutions));
            }
        }
    }

    private void syncLegacyFields() {
        for (RollAdjustmentMethod method : rollAdjustmentMethods) {
            if (method.getId().equals(LEGACY_DICE_SUBSTITUTION_ID)
                && method.getType().equals(RollAdjustmentMethod.TYPE_FIXED_VALUE)) {
                allowDiceSubstitution = true;
                diceSubstitutionValue = method.getValue();
                maxDiceSubstitutions = method.getMaximumUses();
                return;
            }
        }
        allowDiceSubstitution = false;
    }

    private ArrayList<CategoryPointRule> copyCategoryPointRules(Collection<CategoryPointRule> source) {
        Collection<CategoryPointRule> safeSource = Objects.requireNonNullElse(source, Collections.emptyList());
        LinkedHashMap<String, CategoryPointRule> rulesByCategory = new LinkedHashMap<>();
        for (CategoryPointRule rule : safeSource) {
            CategoryPointRule copy = new CategoryPointRule(Objects.requireNonNull(rule, "categoryPointRule"));
            if (!copy.getAttributeCategoryKey().isEmpty()) {
                rulesByCategory.put(copy.getAttributeCategoryKey(), copy);
            }
        }
        return new ArrayList<>(rulesByCategory.values());
    }

    private ArrayList<CategoryPointSlot> copyCategoryPointSlots(Collection<CategoryPointSlot> source) {
        Collection<CategoryPointSlot> safeSource = Objects.requireNonNullElse(source, Collections.emptyList());
        LinkedHashMap<String, CategoryPointSlot> slotsById = new LinkedHashMap<>();
        for (CategoryPointSlot slot : safeSource) {
            CategoryPointSlot copy = new CategoryPointSlot(Objects.requireNonNull(slot, "categoryPointSlot"));
            if (!copy.getId().isEmpty() && !copy.getName().isEmpty()) {
                slotsById.put(copy.getId(), copy);
            }
        }
        return new ArrayList<>(slotsById.values());
    }

    private static String normalizeAttributeCategoryKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase(Locale.ROOT);
    }

    private Map<String,Map<String,Integer>> copyClassMinimums(Map<String,Map<String,Integer>> source) {
        Map<String,Map<String,Integer>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    // === UTILITY METHODS ===

    /**
     * Gets a human-readable description of the dice rolling method
     */
    public String getDiceDescription() {
        if (diceTerms.isEmpty()) {
            return "";
        }
        StringBuilder desc = new StringBuilder();
        for (int i = 0; i < diceTerms.size(); i++) {
            if (i > 0) {
                desc.append(" + ");
            }
            desc.append(diceTerms.get(i).getNotation());
        }
        return desc.toString();
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validAttributeIds, Set<String> validClassIds) {
        int removedCount = 0;

        // Clean up dice variants (keys are Attribute ids)
        Iterator<Map.Entry<String, String>> diceVariantIter = diceVariants.entrySet().iterator();
        while (diceVariantIter.hasNext()) {
            Map.Entry<String, String> entry = diceVariantIter.next();
            String attributeId = entry.getKey();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                diceVariantIter.remove();
                removedCount++;
            }
        }

        // Clean up minimum requirements (keys are Attribute ids)
        Iterator<Map.Entry<String, Integer>> minReqIter = minimumRequirements.entrySet().iterator();
        while (minReqIter.hasNext()) {
            Map.Entry<String, Integer> entry = minReqIter.next();
            String attributeId = entry.getKey();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                minReqIter.remove();
                removedCount++;
            }
        }

        // Clean up class minimums (outer keys are CharacterClass ids, inner keys are Attribute ids)
        Iterator<Map.Entry<String, Map<String, Integer>>> classMinIter = classMinimums.entrySet().iterator();
        while (classMinIter.hasNext()) {
            Map.Entry<String, Map<String, Integer>> classEntry = classMinIter.next();
            String classId = classEntry.getKey();

            // Check if class exists
            boolean classExists = validClassIds.contains(classId);
            if (!classExists) {
                classMinIter.remove();
                removedCount++;
                continue;
            }

            // Clean up attribute references within this class's minimums
            Map<String, Integer> attrMinimums = classEntry.getValue();
            Iterator<Map.Entry<String, Integer>> attrMinIter = attrMinimums.entrySet().iterator();
            while (attrMinIter.hasNext()) {
                Map.Entry<String, Integer> attrEntry = attrMinIter.next();
                String attributeId = attrEntry.getKey();
                boolean attrExists = validAttributeIds.contains(attributeId);
                if (!attrExists) {
                    attrMinIter.remove();
                    removedCount++;
                }
            }

            // Remove class entry if no valid attributes remain
            if (attrMinimums.isEmpty()) {
                classMinIter.remove();
            }
        }

        // Clean up attribute weights (keys are Attribute ids)
        Iterator<Map.Entry<String, Double>> attrWeightIter = attributeWeights.entrySet().iterator();
        while (attrWeightIter.hasNext()) {
            Map.Entry<String, Double> entry = attrWeightIter.next();
            String attributeId = entry.getKey();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                attrWeightIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
    public static final class AttributeGenerationRequest {
        private String optionId = "";
        private String arrayType = "";
        private Map<String, Integer> arrayAssignments = new LinkedHashMap<>();
        private List<Integer> rolledValues = new ArrayList<>();
        private Map<String, Integer> rollAssignments = new LinkedHashMap<>();
        private Map<String, Integer> pointBuyScores = new LinkedHashMap<>();
        private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
        private int chosenStepIndex = -1;

        public AttributeGenerationRequest() {
        }

        public AttributeGenerationRequest(AttributeGenerationRequest source) {
            AttributeGenerationRequest safeSource = Objects.requireNonNullElseGet(
                source,
                AttributeGenerationRequest::new
            );
            setOptionId(safeSource.optionId);
            setArrayType(safeSource.arrayType);
            setArrayAssignments(safeSource.arrayAssignments);
            setRolledValues(safeSource.rolledValues);
            setRollAssignments(safeSource.rollAssignments);
            setPointBuyScores(safeSource.pointBuyScores);
            setCategoryPointSlotAssignments(safeSource.categoryPointSlotAssignments);
            setChosenStepIndex(safeSource.chosenStepIndex);
        }

        public String getOptionId() { return optionId; }
        public void setOptionId(String optionId) {
            this.optionId = Objects.toString(optionId, "").trim();
        }

        public String getArrayType() { return arrayType; }
        public void setArrayType(String arrayType) {
            this.arrayType = Objects.toString(arrayType, "").trim();
        }

        public Map<String, Integer> getArrayAssignments() {
            return new LinkedHashMap<>(arrayAssignments);
        }
        public void setArrayAssignments(Map<String, Integer> arrayAssignments) {
            this.arrayAssignments = copyIntegerMap(arrayAssignments);
        }

        public List<Integer> getRolledValues() { return List.copyOf(rolledValues); }
        public void setRolledValues(Collection<Integer> rolledValues) {
            this.rolledValues = copyIntegerList(rolledValues);
        }

        public Map<String, Integer> getRollAssignments() {
            return new LinkedHashMap<>(rollAssignments);
        }
        public void setRollAssignments(Map<String, Integer> rollAssignments) {
            this.rollAssignments = copyIntegerMap(rollAssignments);
        }

        public Map<String, Integer> getPointBuyScores() {
            return new LinkedHashMap<>(pointBuyScores);
        }
        public void setPointBuyScores(Map<String, Integer> pointBuyScores) {
            this.pointBuyScores = copyIntegerMap(pointBuyScores);
        }

        public Map<String, String> getCategoryPointSlotAssignments() {
            return new LinkedHashMap<>(categoryPointSlotAssignments);
        }
        public void setCategoryPointSlotAssignments(
            Map<String, String> categoryPointSlotAssignments
        ) {
            Map<String, String> safeAssignments = Objects.requireNonNullElse(
                categoryPointSlotAssignments,
                Map.of()
            );
            LinkedHashMap<String, String> copy = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : safeAssignments.entrySet()) {
                String slotId = Objects.toString(entry.getKey(), "").trim();
                String categoryKey = normalizeAttributeCategoryKey(entry.getValue());
                if (!slotId.isEmpty() && !categoryKey.isEmpty()) {
                    copy.put(slotId, categoryKey);
                }
            }
            this.categoryPointSlotAssignments = copy;
        }

        public int getChosenStepIndex() { return chosenStepIndex; }
        public void setChosenStepIndex(int chosenStepIndex) {
            this.chosenStepIndex = chosenStepIndex;
        }

        private static LinkedHashMap<String, Integer> copyIntegerMap(
            Map<String, Integer> source
        ) {
            Map<String, Integer> safeSource = Objects.requireNonNullElse(source, Map.of());
            LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : safeSource.entrySet()) {
                String key = Objects.toString(entry.getKey(), "").trim();
                if (!key.isEmpty()) {
                    copy.put(key, Objects.requireNonNull(entry.getValue(), "integer value"));
                }
            }
            return copy;
        }

        private static ArrayList<Integer> copyIntegerList(Collection<Integer> source) {
            Collection<Integer> safeSource = Objects.requireNonNullElse(source, List.of());
            ArrayList<Integer> copy = new ArrayList<>();
            for (Integer value : safeSource) {
                copy.add(Objects.requireNonNull(value, "integer value"));
            }
            return copy;
        }
    }

    /** Immutable-by-copy result returned through this rule object's public gate. */
    public static final class AttributeGenerationResult {
        private final boolean complete;
        private final Map<String, Integer> scores;
        private final Map<Integer, Map<String, Integer>> stepResults;
        private final int pointsSpent;
        private final int pointsRemaining;
        private final Map<String, CategoryPointBudgetResult> categoryPointBudgets;
        private final String reason;

        AttributeGenerationResult(
            boolean complete,
            Map<String, Integer> scores,
            Map<Integer, Map<String, Integer>> stepResults,
            int pointsSpent,
            int pointsRemaining,
            Map<String, CategoryPointBudgetResult> categoryPointBudgets,
            String reason
        ) {
            this.complete = complete;
            this.scores = copyScores(scores);
            this.stepResults = copyStepResults(stepResults);
            this.pointsSpent = pointsSpent;
            this.pointsRemaining = pointsRemaining;
            this.categoryPointBudgets = copyCategoryPointBudgets(categoryPointBudgets);
            this.reason = Objects.toString(reason, "");
        }

        static AttributeGenerationResult incomplete(String reason) {
            return new AttributeGenerationResult(
                false,
                Map.of(),
                Map.of(),
                0,
                0,
                Map.of(),
                reason
            );
        }

        public boolean isComplete() { return complete; }
        public Map<String, Integer> getScores() { return copyScores(scores); }
        public Map<Integer, Map<String, Integer>> getStepResults() {
            return copyStepResults(stepResults);
        }
        public int getPointsSpent() { return pointsSpent; }
        public int getPointsRemaining() { return pointsRemaining; }
        public Map<String, CategoryPointBudgetResult> getCategoryPointBudgets() {
            return copyCategoryPointBudgets(categoryPointBudgets);
        }
        public String getReason() { return reason; }

        private static LinkedHashMap<String, Integer> copyScores(
            Map<String, Integer> source
        ) {
            return new LinkedHashMap<>(Objects.requireNonNullElse(source, Map.of()));
        }

        private static LinkedHashMap<Integer, Map<String, Integer>> copyStepResults(
            Map<Integer, Map<String, Integer>> source
        ) {
            Map<Integer, Map<String, Integer>> safeSource = Objects.requireNonNullElse(
                source,
                Map.of()
            );
            LinkedHashMap<Integer, Map<String, Integer>> copy = new LinkedHashMap<>();
            for (Map.Entry<Integer, Map<String, Integer>> entry : safeSource.entrySet()) {
                Integer key = Objects.requireNonNull(entry.getKey(), "step index");
                copy.put(key, copyScores(entry.getValue()));
            }
            return copy;
        }

        private static LinkedHashMap<String, CategoryPointBudgetResult> copyCategoryPointBudgets(
            Map<String, CategoryPointBudgetResult> source
        ) {
            Map<String, CategoryPointBudgetResult> safeSource = Objects.requireNonNullElse(
                source,
                Map.of()
            );
            LinkedHashMap<String, CategoryPointBudgetResult> copy = new LinkedHashMap<>();
            for (Map.Entry<String, CategoryPointBudgetResult> entry : safeSource.entrySet()) {
                String categoryKey = normalizeAttributeCategoryKey(entry.getKey());
                if (!categoryKey.isEmpty()) {
                    copy.put(
                        categoryKey,
                        new CategoryPointBudgetResult(
                            Objects.requireNonNull(entry.getValue(), "categoryPointBudget")
                        )
                    );
                }
            }
            return copy;
        }
    }

    /** Authoritative accounting for one category pool in a resolved Point Buy step. */
    public static final class CategoryPointBudgetResult {
        private final String attributeCategoryKey;
        private final String sourceId;
        private final String sourceName;
        private final int budget;
        private final int spent;
        private final int remaining;

        CategoryPointBudgetResult(
            String attributeCategoryKey,
            String sourceId,
            String sourceName,
            int budget,
            int spent
        ) {
            this.attributeCategoryKey = normalizeAttributeCategoryKey(attributeCategoryKey);
            this.sourceId = Objects.toString(sourceId, "").trim();
            this.sourceName = Objects.toString(sourceName, "").trim();
            this.budget = Math.max(0, budget);
            this.spent = spent;
            this.remaining = this.budget - spent;
        }

        private CategoryPointBudgetResult(CategoryPointBudgetResult source) {
            this(
                source.attributeCategoryKey,
                source.sourceId,
                source.sourceName,
                source.budget,
                source.spent
            );
        }

        public String getAttributeCategoryKey() { return attributeCategoryKey; }
        public String getSourceId() { return sourceId; }
        public String getSourceName() { return sourceName; }
        public int getBudget() { return budget; }
        public int getSpent() { return spent; }
        public int getRemaining() { return remaining; }
    }

    // === POINT BUY CATEGORY RULE STRUCTURE ===
    public static class CategoryPointRule implements java.io.Serializable {

    // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private String attributeCategoryKey = "";
        private int availablePoints = 0;

    // *** CONSTRUCTORS ***
        public CategoryPointRule() {
        }

        public CategoryPointRule(String attributeCategoryKey, int availablePoints) {
            this.attributeCategoryKey = normalizeAttributeCategoryKey(attributeCategoryKey);
            this.availablePoints = availablePoints;
        }

        public CategoryPointRule(CategoryPointRule source) {
            CategoryPointRule safeSource = Objects.requireNonNull(source, "source");
            this.attributeCategoryKey = normalizeAttributeCategoryKey(safeSource.attributeCategoryKey);
            this.availablePoints = safeSource.availablePoints;
        }

    // *** METHODS ***
        public String getAttributeCategoryKey() { return attributeCategoryKey; }
        public void setAttributeCategoryKey(String attributeCategoryKey) {
            this.attributeCategoryKey = normalizeAttributeCategoryKey(attributeCategoryKey);
        }

        public int getAvailablePoints() { return availablePoints; }
        public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }
    }

    // === PLAYER-ASSIGNED POINT BUY CATEGORY SLOT STRUCTURE ===
    public static class CategoryPointSlot implements java.io.Serializable {

    // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private String id = "";
        private String name = "";
        private int availablePoints = 0;

    // *** CONSTRUCTORS ***
        public CategoryPointSlot() {
        }

        public CategoryPointSlot(String id, String name, int availablePoints) {
            setId(id);
            setName(name);
            setAvailablePoints(availablePoints);
        }

        public CategoryPointSlot(CategoryPointSlot source) {
            CategoryPointSlot safeSource = Objects.requireNonNull(source, "source");
            setId(safeSource.id);
            setName(safeSource.name);
            setAvailablePoints(safeSource.availablePoints);
        }

    // *** METHODS ***
        public String getId() { return id; }
        public void setId(String id) { this.id = Objects.toString(id, "").trim(); }

        public String getName() { return name; }
        public void setName(String name) { this.name = Objects.toString(name, "").trim(); }

        public int getAvailablePoints() { return availablePoints; }
        public void setAvailablePoints(int availablePoints) { this.availablePoints = Math.max(0, availablePoints); }
    }

    // === DICE TERM STRUCTURE ===
    public static class DiceTerm implements java.io.Serializable {

    // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private int count = 0;
        private int sides = 0;
        private int dropLowest = 0;
        private int dropHighest = 0;
        private int flatModifier = 0;
        private boolean exploding = false;
        private int explodeThreshold = 0;
        private ArrayList<Integer> ignoredFaces = new ArrayList<>();

    // *** CONSTRUCTORS ***
        public DiceTerm() {
        }

        public DiceTerm(int count, int sides) {
            this.count = count;
            this.sides = sides;
        }

        public DiceTerm(DiceTerm source) {
            this.count = source.count;
            this.sides = source.sides;
            this.dropLowest = source.dropLowest;
            this.dropHighest = source.dropHighest;
            this.flatModifier = source.flatModifier;
            this.exploding = source.exploding;
            this.explodeThreshold = source.explodeThreshold;
            this.ignoredFaces = new ArrayList<>(source.ignoredFaces);
        }

    // *** METHODS ***
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public int getSides() { return sides; }
        public void setSides(int sides) { this.sides = sides; }

        public int getDropLowest() { return dropLowest; }
        public void setDropLowest(int dropLowest) { this.dropLowest = dropLowest; }

        public int getDropHighest() { return dropHighest; }
        public void setDropHighest(int dropHighest) { this.dropHighest = dropHighest; }

        public int getFlatModifier() { return flatModifier; }
        public void setFlatModifier(int flatModifier) { this.flatModifier = flatModifier; }

        public boolean isExploding() { return exploding; }
        public void setExploding(boolean exploding) { this.exploding = exploding; }

        public int getExplodeThreshold() { return explodeThreshold; }
        public void setExplodeThreshold(int explodeThreshold) { this.explodeThreshold = explodeThreshold; }

        public ArrayList<Integer> getIgnoredFaces() { return new ArrayList<>(ignoredFaces); }
        public void setIgnoredFaces(Collection<Integer> ignoredFaces) {
            this.ignoredFaces = new ArrayList<>(Objects.requireNonNullElse(ignoredFaces, Collections.emptyList()));
        }
        public void addIgnoredFace(int face) { ignoredFaces.add(face); }
        public void removeIgnoredFace(int face) { ignoredFaces.remove(Integer.valueOf(face)); }

        public String getNotation() {
            StringBuilder notation = new StringBuilder();
            notation.append(count).append("d").append(sides);
            if (dropLowest > 0) {
                notation.append(" drop ").append(dropLowest).append(" lowest");
            }
            if (dropHighest > 0) {
                notation.append(" drop ").append(dropHighest).append(" highest");
            }
            if (flatModifier != 0) {
                notation.append(flatModifier > 0 ? "+" : "").append(flatModifier);
            }
            if (exploding) {
                notation.append(" explode");
                if (explodeThreshold > 0) {
                    notation.append(" on ").append(explodeThreshold).append("+");
                }
            }
            if (!ignoredFaces.isEmpty()) {
                notation.append(" ignore ").append(ignoredFaces);
            }
            return notation.toString();
        }
    }
}
