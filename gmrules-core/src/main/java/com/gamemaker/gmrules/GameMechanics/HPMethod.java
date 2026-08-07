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

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.util.*;

/**
 * Comprehensive hit point system supporting various RPG HP mechanics.
 * Handles hit dice, constitution modifiers, level advancement, multiclass rules,
 * temporary HP, hit die recovery, alternative systems, and conditional modifiers.
 * Designed for mechanical implementation by descendant applications.
 */
public class HPMethod extends GameElement {

// *** MEMBERS ***
    private static final long serialVersionUID = 8335684502958490484L;

    // === HIT DIE SYSTEM ===
    private String hitDieType = "d8";
    private int hitDieSides = 8;
    private int hitDieCount = 1;
    private int hitDieModifier = 0;
    private String hitDieProgression = "perLevel";
    private Map<Integer,String> customHitDiceByLevel = new HashMap<>();
    private Map<String,String> classToDieType = new HashMap<>();
    private boolean allowMultipleDiceTypes = false;

    // HD pool mechanics
    private boolean usesHitDicePool = false;
    private int maxHitDiceInPool = 20;
    private String poolResetFrequency = "longRest";
    private boolean poolIncludesAllClasses = true;

    // === ATTRIBUTE MODIFIER INTEGRATION ===
    // Legacy field names are retained for Java serialization compatibility.
    private boolean appliesConstitutionModifier = false;
    private String hpModifierAttributeId = "";
    private String conModifierTiming = "perLevel";
    private boolean conModifierRetroactive = true;
    private int minimumHPPerLevel = 1;
    private int minimumTotalHP = 1;
    private boolean allowNegativeConModifier = false;

    // CON modifier variants
    private boolean doublesConModifierAtFirst = false;
    private Map<String,Integer> conModifierMultipliers = new HashMap<>();
    private boolean usesConstitutionScore = false;

    // === LEVEL ADVANCEMENT METHODS ===
    private String hpGainMethod = "rolled";
    private boolean allowPlayerChoice = false;
    private boolean alwaysMaxAtFirstLevel = true;
    private int fixedHPPerLevel = 5;
    private boolean allowMultipleFixedGains = false;

    // Attribute-derived HP replaces both starting HP and level-based gain.
    private String attributeDerivationMode = "direct";
    private String attributeDerivedDirectAttributeId = "";
    private List<AttributeHPTerm> attributeDerivedTerms = new ArrayList<>();
    private double attributeDerivedBaseValue = 0.0;
    private double attributeDerivedDivisor = 1.0;
    private String attributeDerivedRoundingMethod = "nearest";

    // Rolling mechanics
    private boolean allowRerollOnes = false;
    private boolean allowRerollBelowAverage = false;
    private int minimumRollValue = 0;
    private boolean explodingHitDice = false;
    private int explodeThreshold = 0;

    // Average mechanics
    private String averageRoundingMethod = "up";
    private boolean averageIncludesConModifier = true;
    private Map<String,Integer> customAverageValues = new HashMap<>();

    // Hybrid systems
    private Map<String,Object> hybridParameters = new HashMap<>();

    // === FIRST LEVEL SPECIAL RULES ===
    private boolean firstLevelMaxHP = true;
    private int firstLevelBonusHP = 0;
    private boolean firstLevelDoubleHD = false;
    private String firstLevelMethod = "maximum";
    private int firstLevelFixedValue = 0;

    // === MULTICLASS MECHANICS ===
    private String multiclassHPMethod = "byClass";
    private boolean stacksHitDice = true;
    private boolean averagesMulticlassHP = false;

    // Favored class bonuses
    private boolean usesFavoredClass = false;
    private String favoredClassBonus = "hp";
    private int favoredClassHPBonus = 1;
    private Map<String,Integer> racialFavoredClassBonuses = new HashMap<>();

    // === TEMPORARY & MAXIMUM HP ===
    private boolean supportsTemporaryHP = false;
    private boolean tempHPStacks = false;
    private String tempHPStackingMethod = "highest";
    private int maxTemporaryHP = 999;

    // Maximum HP modifications
    private boolean allowsMaxHPReduction = false;
    private boolean allowsMaxHPIncrease = true;
    private Map<String,Integer> maxHPModifierLimits = new HashMap<>();

    // === HIT DIE RECOVERY SYSTEM ===
    private boolean usesHitDieRecovery = false;
    private String hitDieRecoveryTiming = "longRest";
    private String recoveryCalculation = "half";
    private int fixedRecoveryAmount = 1;
    private String recoveryFormula = "level/2";
    private int minimumRecovery = 1;
    private int maximumRecovery = 20;

    // Recovery spending
    private boolean canSpendHDDuringRest = true;
    private String hdSpendingTiming = "shortRest";
    private int maxHDSpendablePerRest = 999;
    private boolean recoversHPOnLongRest = true;
    private String longRestHPRecovery = "full";

    // === ALTERNATIVE HP SYSTEMS ===
    private String alternativeSystem = "none";

    // Vitality/Wound system
    private boolean usesVitalityWound = false;
    private String vitalityCalculation = "conScore";
    private String woundCalculation = "hitDice";
    private boolean woundDamageLethal = true;

    // Threshold/Bloodied
    private boolean usesBloodiedState = false;
    private int bloodiedThreshold = 50;
    private Map<String,Object> bloodiedEffects = new HashMap<>();

    // Stamina variants
    private boolean usesStamina = false;
    private String staminaCalculation = "conLevel";
    private boolean staminaRegeneratesInCombat = false;
    private int staminaRegenRate = 0;

    // Massive damage threshold
    private boolean usesMassiveDamageThreshold = false;
    private String massiveDamageCalculation = "50";
    private String massiveDamageEffect = "save";

    // === CONDITIONAL MODIFIERS ===
    private boolean usesConditionalModifiers = false;

    // Feat bonuses
    private Map<String,Integer> featHPBonuses = new HashMap<>();
    private Map<String,String> featHPFormulas = new HashMap<>();

    // Racial modifiers
    private Map<String,Integer> racialHPBonuses = new HashMap<>();
    private Map<String,String> racialHPFormulas = new HashMap<>();
    private boolean racialBonusesRetroactive = true;

    // Class feature modifiers
    private Map<String,Integer> classFeatureHPBonuses = new HashMap<>();
    private Map<String,String> classFeatureConditions = new HashMap<>();

    // Size modifiers
    private boolean usesSizeModifiers = false;
    private Map<String,Integer> sizeHPModifiers = new HashMap<>();

    // Environmental/situational
    private Map<String,Integer> situationalModifiers = new HashMap<>();

    // === DAMAGE & DEATH MECHANICS ===
    private boolean usesNegativeHP = true;
    private int negativeHPThreshold = -10;
    private String negativeHPCalculation = "con";

    // Death and dying
    private boolean usesDeathSaves = false;
    private int deathSaveSuccessThreshold = 3;
    private int deathSaveFailureThreshold = 3;
    private int deathSaveDC = 10;
    private boolean massiveDamageInstantDeath = false;

    // Stabilization
    private boolean allowsStabilization = true;
    private int stabilizationDC = 10;
    private boolean autoStabilizeAtZero = false;

    // Unconsciousness
    private int unconsciousThreshold = 0;
    private boolean unconsciousAtZero = true;

    // === HEALING MECHANICS ===
    private boolean hasHealingLimits = false;
    private int maxHealingPerDay = 999;
    private boolean naturalHealingPerDay = true;
    private String naturalHealingRate = "conMod";
    private int naturalHealingAmount = 1;

    // Rest healing
    private String shortRestHealing = "hdRoll";
    private String longRestHealing = "full";
    private boolean requiresFoodAndWater = false;

    // Magical healing
    private boolean magicalHealingModified = false;
    private Map<String,Double> healingModifiers = new HashMap<>();
    private int maxHealingPerSource = 999;

    // === LEVEL ZERO & COMMONER HP ===
    private boolean allowsLevelZero = false;
    private String levelZeroHPMethod = "fixed";
    private int levelZeroFixedHP = 4;
    private String levelZeroHitDie = "d4";

    // NPC/Commoner rules
    private boolean usesNPCHPRules = false;
    private String npcHPMethod = "average";
    private Map<String,Integer> npcHPByType = new HashMap<>();

    // === ADVANCEMENT & LEVELING ===
    private boolean allowsHPReroll = false;
    private String rerollTiming = "never";
    private boolean rerollAffectsTotal = true;
    private boolean canLoseHPOnReroll = false;

    // Level loss
    private boolean allowsLevelLoss = false;
    private String levelLossHPMethod = "subtract";
    private boolean permanentHPLoss = false;

    // === VARIANT RULES ===
    private Map<String,Object> variantRules = new HashMap<>();

    // Gritty realism
    private boolean usesGrittyRealism = false;
    private String grittyRestDuration = "week";

    // Epic levels
    private boolean supportsEpicLevels = false;
    private String epicHPProgression = "half";
    private int epicFixedHP = 3;

    // === DISPLAY & TRACKING ===
    private boolean displayCurrentHP = true;
    private boolean displayMaxHP = true;
    private boolean displayTempHP = true;
    private boolean displayHitDiceRemaining = true;
    private String hpDisplayFormat = "current/max";

    // === CONFIGURATION FLAGS ===
    private boolean usesHitDice = true;
    private boolean usesConModifier = true;
    private boolean usesMulticlassHP = false;
    private boolean usesFavoredClassSystem = false;
    private boolean usesHPRecovery = false;
    private boolean usesAlternativeSystem = false;
    private boolean usesAdvancedRolling = false;
    private boolean usesDeathAndDying = false;
    private boolean usesHealingRules = false;
    private boolean usesVariantRules = false;

    // === ARRAY REGISTRY ===
    private ArrayHandler arrayHandler = new ArrayHandler();

    public static final class AttributeHPTerm implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private String attributeId = "";
        private double multiplier = 1.0;

        public AttributeHPTerm() {
        }

        public AttributeHPTerm(String attributeId, double multiplier) {
            setAttributeId(attributeId);
            setMultiplier(multiplier);
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = Objects.toString(attributeId, "").trim();
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = Double.isFinite(multiplier) ? multiplier : 1.0;
        }
    }

// *** CONSTRUCTORS ***
    public HPMethod(String name) {
        super(name);
        initializeArrayRegistry();
        initializeDefaults();
    }

    public HPMethod(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
        initializeDefaults();
    }

// *** METHODS ***

    /**
     * Initialize array registry for generic array management
     */
    private void initializeArrayRegistry() {
        arrayHandler.putArray("hybridLevelRanges", new ArrayList<String>());
        arrayHandler.putArray("tempHPSources", new ArrayList<String>());
        arrayHandler.putArray("maxHPModifierTypes", new ArrayList<String>());
        arrayHandler.putArray("hpModifierTypes", new ArrayList<String>());
        arrayHandler.putArray("enabledVariants", new ArrayList<String>());
    }

    private void initializeDefaults() {
        // Default hit dice by class (D&D 5e style)
        classToDieType.put("Barbarian", "d12");
        classToDieType.put("Fighter", "d10");
        classToDieType.put("Paladin", "d10");
        classToDieType.put("Ranger", "d10");
        classToDieType.put("Cleric", "d8");
        classToDieType.put("Druid", "d8");
        classToDieType.put("Monk", "d8");
        classToDieType.put("Rogue", "d8");
        classToDieType.put("Bard", "d8");
        classToDieType.put("Warlock", "d8");
        classToDieType.put("Sorcerer", "d6");
        classToDieType.put("Wizard", "d6");

        // Default average values for each die type
        customAverageValues.put("d4", 3);
        customAverageValues.put("d6", 4);
        customAverageValues.put("d8", 5);
        customAverageValues.put("d10", 6);
        customAverageValues.put("d12", 7);
        customAverageValues.put("d20", 11);

        // Default temp HP sources
        ArrayList<String> tempHPSources = getArray("tempHPSources");
        tempHPSources.add("spell");
        tempHPSources.add("classAbility");
        tempHPSources.add("item");
        tempHPSources.add("feat");

        // Default max HP modifier types
        ArrayList<String> maxHPModifierTypes = getArray("maxHPModifierTypes");
        maxHPModifierTypes.add("drain");
        maxHPModifierTypes.add("boost");
        maxHPModifierTypes.add("curse");
        maxHPModifierTypes.add("blessing");

        // Default HP modifier types
        ArrayList<String> hpModifierTypes = getArray("hpModifierTypes");
        hpModifierTypes.add("feat");
        hpModifierTypes.add("racial");
        hpModifierTypes.add("class");
        hpModifierTypes.add("item");
        hpModifierTypes.add("temporary");

        // Default size modifiers (Pathfinder style)
        sizeHPModifiers.put("Fine", -4);
        sizeHPModifiers.put("Diminutive", -3);
        sizeHPModifiers.put("Tiny", -2);
        sizeHPModifiers.put("Small", -1);
        sizeHPModifiers.put("Medium", 0);
        sizeHPModifiers.put("Large", 1);
        sizeHPModifiers.put("Huge", 2);
        sizeHPModifiers.put("Gargantuan", 3);
        sizeHPModifiers.put("Colossal", 4);
    }

    // ===== GENERIC ARRAY MANAGEMENT METHODS =====

    /**
     * Get array by name
     * @param arrayName the name of the array to retrieve
     * @return the array or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        return arrayHandler.getArray(arrayName);
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        return arrayHandler.getObjectArray(arrayName);
    }

    /**
     * Add value to specified array
     * @param arrayName the name of the array
     * @param value the value to add
     */
    @SuppressWarnings("unchecked")
    public <T> void addToArray(String arrayName, T value) {
        arrayHandler.addElement(arrayName, value);
    }

    /**
     * Remove value from specified array
     * @param arrayName the name of the array
     * @param value the value to remove
     * @return true if removed, false if not found
     */
    @SuppressWarnings("unchecked")
    public <T> boolean removeFromArray(String arrayName, T value) {
        return arrayHandler.removeElement(arrayName, value);
    }

    /**
     * Clear all elements from specified array
     * @param arrayName the name of the array to clear
     */
    public void clearArray(String arrayName) {
        arrayHandler.clearArray(arrayName);
    }

    /**
     * Get all array names
     * @return set of array names
     */
    public Set<String> getArrayNames() {
        return arrayHandler.getArrayNames();
    }

    // === HIT DIE SYSTEM METHODS ===
    public String getHitDieType() {
        int sides = getHitDieSides();
        if (sides > 0) {
            return "d" + sides;
        }
        return hitDieType;
    }

    public void setHitDieType(String hitDieType) {
        String safe = Objects.toString(hitDieType, "").trim().toLowerCase();
        this.hitDieType = safe.isEmpty() ? "d8" : safe;
        this.hitDieSides = parseDieSides(this.hitDieType);
    }

    public int getHitDieSides() {
        if (hitDieSides > 0) {
            return hitDieSides;
        }
        return parseDieSides(hitDieType);
    }

    public void setHitDieSides(int hitDieSides) {
        this.hitDieSides = Math.max(0, hitDieSides);
        if (this.hitDieSides > 0) {
            this.hitDieType = "d" + this.hitDieSides;
        }
    }

    public int getHitDieCount() { return hitDieCount; }
    public void setHitDieCount(int hitDieCount) { this.hitDieCount = Math.max(1, hitDieCount); }

    public int getHitDieModifier() { return hitDieModifier; }
    public void setHitDieModifier(int hitDieModifier) { this.hitDieModifier = hitDieModifier; }

    public String getHitDieProgression() { return hitDieProgression; }
    public void setHitDieProgression(String hitDieProgression) { this.hitDieProgression = hitDieProgression; }

    public Map<Integer,String> getCustomHitDiceByLevel() { return customHitDiceByLevel; }
    public void setCustomHitDiceByLevel(Map<Integer,String> customHitDiceByLevel) { this.customHitDiceByLevel = customHitDiceByLevel; }
    public void setHitDieForLevel(int level, String dieType) { customHitDiceByLevel.put(level, dieType); }
    public void removeHitDieForLevel(int level) { customHitDiceByLevel.remove(level); }

    public Map<String,String> getClassToDieType() { return classToDieType; }
    public void setClassToDieType(Map<String,String> classToDieType) { this.classToDieType = classToDieType; }
    public void setClassDieType(String className, String dieType) { classToDieType.put(className, dieType); }
    public void removeClassDieType(String className) { classToDieType.remove(className); }

    public boolean isAllowMultipleDiceTypes() { return allowMultipleDiceTypes; }
    public void setAllowMultipleDiceTypes(boolean allowMultipleDiceTypes) {
        this.allowMultipleDiceTypes = allowMultipleDiceTypes;
    }

    public boolean isUsesHitDicePool() { return usesHitDicePool; }
    public void setUsesHitDicePool(boolean usesHitDicePool) { this.usesHitDicePool = usesHitDicePool; }

    public int getMaxHitDiceInPool() { return maxHitDiceInPool; }
    public void setMaxHitDiceInPool(int maxHitDiceInPool) { this.maxHitDiceInPool = maxHitDiceInPool; }

    public String getPoolResetFrequency() { return poolResetFrequency; }
    public void setPoolResetFrequency(String poolResetFrequency) { this.poolResetFrequency = poolResetFrequency; }

    public boolean isPoolIncludesAllClasses() { return poolIncludesAllClasses; }
    public void setPoolIncludesAllClasses(boolean poolIncludesAllClasses) {
        this.poolIncludesAllClasses = poolIncludesAllClasses;
    }

    // === CONSTITUTION MODIFIER METHODS ===
    public boolean isAppliesConstitutionModifier() { return appliesConstitutionModifier; }
    public void setAppliesConstitutionModifier(boolean appliesConstitutionModifier) {
        this.appliesConstitutionModifier = appliesConstitutionModifier;
        if (!appliesConstitutionModifier) {
            this.hpModifierAttributeId = "";
        }
    }

    public boolean isAppliesAttributeModifier() { return appliesConstitutionModifier; }
    public void setAppliesAttributeModifier(boolean appliesAttributeModifier) {
        this.appliesConstitutionModifier = appliesAttributeModifier;
        if (!appliesAttributeModifier) {
            this.hpModifierAttributeId = "";
        }
    }

    public String getHpModifierAttributeId() { return hpModifierAttributeId; }
    public void setHpModifierAttributeId(String hpModifierAttributeId) {
        this.hpModifierAttributeId = Objects.toString(hpModifierAttributeId, "").trim();
        this.appliesConstitutionModifier = !this.hpModifierAttributeId.isEmpty();
    }

    public String getConModifierTiming() { return conModifierTiming; }
    public void setConModifierTiming(String conModifierTiming) { this.conModifierTiming = conModifierTiming; }

    public boolean isConModifierRetroactive() { return conModifierRetroactive; }
    public void setConModifierRetroactive(boolean conModifierRetroactive) {
        this.conModifierRetroactive = conModifierRetroactive;
    }

    public int getMinimumHPPerLevel() { return minimumHPPerLevel; }
    public void setMinimumHPPerLevel(int minimumHPPerLevel) { this.minimumHPPerLevel = minimumHPPerLevel; }

    public int getMinimumTotalHP() { return minimumTotalHP; }
    public void setMinimumTotalHP(int minimumTotalHP) { this.minimumTotalHP = minimumTotalHP; }

    public boolean isAllowNegativeConModifier() { return allowNegativeConModifier; }
    public void setAllowNegativeConModifier(boolean allowNegativeConModifier) {
        this.allowNegativeConModifier = allowNegativeConModifier;
    }

    public boolean isAllowNegativeAttributeModifier() { return allowNegativeConModifier; }
    public void setAllowNegativeAttributeModifier(boolean allowNegativeAttributeModifier) {
        this.allowNegativeConModifier = allowNegativeAttributeModifier;
    }

    public boolean isDoublesConModifierAtFirst() { return doublesConModifierAtFirst; }
    public void setDoublesConModifierAtFirst(boolean doublesConModifierAtFirst) {
        this.doublesConModifierAtFirst = doublesConModifierAtFirst;
    }

    public Map<String,Integer> getConModifierMultipliers() { return conModifierMultipliers; }
    public void setConModifierMultipliers(Map<String,Integer> conModifierMultipliers) { this.conModifierMultipliers = conModifierMultipliers; }
    public void setConModifierMultiplier(String className, int multiplier) {
        conModifierMultipliers.put(className, multiplier);
    }
    public void removeConModifierMultiplier(String className) { conModifierMultipliers.remove(className); }

    public boolean isUsesConstitutionScore() { return usesConstitutionScore; }
    public void setUsesConstitutionScore(boolean usesConstitutionScore) {
        this.usesConstitutionScore = usesConstitutionScore;
    }

    // === LEVEL ADVANCEMENT METHODS ===
    public String getHpGainMethod() { return normalizeHpGainMethod(hpGainMethod); }
    public void setHpGainMethod(String hpGainMethod) {
        String safe = Objects.toString(hpGainMethod, "").trim().toLowerCase();
        if ("average".equals(safe)) {
            fixedHPPerLevel = calculateLegacyAverageGain();
        }
        this.hpGainMethod = normalizeHpGainMethod(safe);
    }

    public boolean isAttributeDerived() {
        return "attribute_derived".equals(getHpGainMethod());
    }

    public String getAttributeDerivationMode() {
        return normalizeAttributeDerivationMode(attributeDerivationMode);
    }

    public void setAttributeDerivationMode(String attributeDerivationMode) {
        this.attributeDerivationMode = normalizeAttributeDerivationMode(attributeDerivationMode);
    }

    public String getAttributeDerivedDirectAttributeId() {
        return Objects.toString(attributeDerivedDirectAttributeId, "").trim();
    }

    public void setAttributeDerivedDirectAttributeId(String attributeDerivedDirectAttributeId) {
        this.attributeDerivedDirectAttributeId = Objects.toString(attributeDerivedDirectAttributeId, "").trim();
    }

    public List<AttributeHPTerm> getAttributeDerivedTerms() {
        return Collections.unmodifiableList(attributeDerivedTerms);
    }

    public void setAttributeDerivedTerms(List<AttributeHPTerm> terms) {
        ArrayList<AttributeHPTerm> normalized = new ArrayList<>();
        if (terms != null) {
            for (AttributeHPTerm term : terms) {
                if (term == null) {
                    continue;
                }
                String attributeId = Objects.toString(term.getAttributeId(), "").trim();
                if (!attributeId.isEmpty()) {
                    normalized.add(new AttributeHPTerm(attributeId, term.getMultiplier()));
                }
            }
        }
        attributeDerivedTerms = normalized;
    }

    public double getAttributeDerivedBaseValue() {
        return attributeDerivedBaseValue;
    }

    public void setAttributeDerivedBaseValue(double attributeDerivedBaseValue) {
        this.attributeDerivedBaseValue = Double.isFinite(attributeDerivedBaseValue)
            ? attributeDerivedBaseValue
            : 0.0;
    }

    public double getAttributeDerivedDivisor() {
        return attributeDerivedDivisor;
    }

    public void setAttributeDerivedDivisor(double attributeDerivedDivisor) {
        this.attributeDerivedDivisor = Double.isFinite(attributeDerivedDivisor)
            && attributeDerivedDivisor != 0.0
            ? attributeDerivedDivisor
            : 1.0;
    }

    public String getAttributeDerivedRoundingMethod() {
        return normalizeRoundingMethod(attributeDerivedRoundingMethod);
    }

    public void setAttributeDerivedRoundingMethod(String attributeDerivedRoundingMethod) {
        this.attributeDerivedRoundingMethod = normalizeRoundingMethod(attributeDerivedRoundingMethod);
    }

    public int calculateAttributeDerivedHP(Map<String, ? extends Number> attributeScores) {
        Map<String, ? extends Number> safeScores = attributeScores == null ? Map.of() : attributeScores;
        if ("direct".equals(getAttributeDerivationMode())) {
            Number score = safeScores.get(getAttributeDerivedDirectAttributeId());
            return score == null ? 0 : roundAttributeDerivedValue(score.doubleValue());
        }

        double total = attributeDerivedBaseValue;
        int termLimit = "single_formula".equals(getAttributeDerivationMode()) ? 1 : attributeDerivedTerms.size();
        for (int index = 0; index < termLimit && index < attributeDerivedTerms.size(); index++) {
            AttributeHPTerm term = attributeDerivedTerms.get(index);
            Number score = safeScores.get(term.getAttributeId());
            if (score != null) {
                total += score.doubleValue() * term.getMultiplier();
            }
        }
        return roundAttributeDerivedValue(total / getAttributeDerivedDivisor());
    }

    public boolean isAllowPlayerChoice() { return allowPlayerChoice; }
    public void setAllowPlayerChoice(boolean allowPlayerChoice) { this.allowPlayerChoice = allowPlayerChoice; }

    public boolean isAlwaysMaxAtFirstLevel() { return alwaysMaxAtFirstLevel; }
    public void setAlwaysMaxAtFirstLevel(boolean alwaysMaxAtFirstLevel) {
        this.alwaysMaxAtFirstLevel = alwaysMaxAtFirstLevel;
    }

    public int getFixedHPPerLevel() { return fixedHPPerLevel; }
    public void setFixedHPPerLevel(int fixedHPPerLevel) { this.fixedHPPerLevel = fixedHPPerLevel; }

    public boolean isAllowMultipleFixedGains() { return allowMultipleFixedGains; }
    public void setAllowMultipleFixedGains(boolean allowMultipleFixedGains) {
        this.allowMultipleFixedGains = allowMultipleFixedGains;
    }

    public boolean isAllowRerollOnes() { return allowRerollOnes; }
    public void setAllowRerollOnes(boolean allowRerollOnes) { this.allowRerollOnes = allowRerollOnes; }

    public boolean isAllowRerollBelowAverage() { return allowRerollBelowAverage; }
    public void setAllowRerollBelowAverage(boolean allowRerollBelowAverage) {
        this.allowRerollBelowAverage = allowRerollBelowAverage;
    }

    public int getMinimumRollValue() { return minimumRollValue; }
    public void setMinimumRollValue(int minimumRollValue) { this.minimumRollValue = minimumRollValue; }

    public boolean isExplodingHitDice() { return explodingHitDice; }
    public void setExplodingHitDice(boolean explodingHitDice) { this.explodingHitDice = explodingHitDice; }

    public int getExplodeThreshold() { return explodeThreshold; }
    public void setExplodeThreshold(int explodeThreshold) { this.explodeThreshold = explodeThreshold; }

    public String getAverageRoundingMethod() { return averageRoundingMethod; }
    public void setAverageRoundingMethod(String averageRoundingMethod) {
        this.averageRoundingMethod = averageRoundingMethod;
    }

    public boolean isAverageIncludesConModifier() { return averageIncludesConModifier; }
    public void setAverageIncludesConModifier(boolean averageIncludesConModifier) {
        this.averageIncludesConModifier = averageIncludesConModifier;
    }

    public Map<String,Integer> getCustomAverageValues() { return customAverageValues; }
    public void setCustomAverageValues(Map<String,Integer> customAverageValues) { this.customAverageValues = customAverageValues; }
    public void setCustomAverageValue(String dieType, int average) { customAverageValues.put(dieType, average); }
    public void removeCustomAverageValue(String dieType) { customAverageValues.remove(dieType); }


    public Map<String,Object> getHybridParameters() { return hybridParameters; }
    public void setHybridParameters(Map<String,Object> hybridParameters) { this.hybridParameters = hybridParameters; }
    public void setHybridParameter(String key, Object value) { hybridParameters.put(key, value); }
    public void removeHybridParameter(String key) { hybridParameters.remove(key); }

    // === FIRST LEVEL METHODS ===
    public boolean isFirstLevelMaxHP() { return firstLevelMaxHP; }
    public void setFirstLevelMaxHP(boolean firstLevelMaxHP) { this.firstLevelMaxHP = firstLevelMaxHP; }

    public int getFirstLevelBonusHP() { return firstLevelBonusHP; }
    public void setFirstLevelBonusHP(int firstLevelBonusHP) { this.firstLevelBonusHP = firstLevelBonusHP; }

    public boolean isFirstLevelDoubleHD() { return firstLevelDoubleHD; }
    public void setFirstLevelDoubleHD(boolean firstLevelDoubleHD) { this.firstLevelDoubleHD = firstLevelDoubleHD; }

    public String getFirstLevelMethod() { return firstLevelMethod; }
    public void setFirstLevelMethod(String firstLevelMethod) { this.firstLevelMethod = firstLevelMethod; }

    public int getFirstLevelFixedValue() { return firstLevelFixedValue; }
    public void setFirstLevelFixedValue(int firstLevelFixedValue) { this.firstLevelFixedValue = firstLevelFixedValue; }

    // === MULTICLASS METHODS ===
    public String getMulticlassHPMethod() { return multiclassHPMethod; }
    public void setMulticlassHPMethod(String multiclassHPMethod) { this.multiclassHPMethod = multiclassHPMethod; }

    public boolean isStacksHitDice() { return stacksHitDice; }
    public void setStacksHitDice(boolean stacksHitDice) { this.stacksHitDice = stacksHitDice; }

    public boolean isAveragesMulticlassHP() { return averagesMulticlassHP; }
    public void setAveragesMulticlassHP(boolean averagesMulticlassHP) { this.averagesMulticlassHP = averagesMulticlassHP; }

    public boolean isUsesFavoredClass() { return usesFavoredClass; }
    public void setUsesFavoredClass(boolean usesFavoredClass) { this.usesFavoredClass = usesFavoredClass; }

    public String getFavoredClassBonus() { return favoredClassBonus; }
    public void setFavoredClassBonus(String favoredClassBonus) { this.favoredClassBonus = favoredClassBonus; }

    public int getFavoredClassHPBonus() { return favoredClassHPBonus; }
    public void setFavoredClassHPBonus(int favoredClassHPBonus) { this.favoredClassHPBonus = favoredClassHPBonus; }

    public Map<String,Integer> getRacialFavoredClassBonuses() { return racialFavoredClassBonuses; }
    public void setRacialFavoredClassBonuses(Map<String,Integer> racialFavoredClassBonuses) { this.racialFavoredClassBonuses = racialFavoredClassBonuses; }
    public void setRacialFavoredClassBonus(String race, int bonus) { racialFavoredClassBonuses.put(race, bonus); }
    public void removeRacialFavoredClassBonus(String race) { racialFavoredClassBonuses.remove(race); }

    // === TEMPORARY & MAXIMUM HP METHODS ===
    public boolean isSupportsTemporaryHP() { return supportsTemporaryHP; }
    public void setSupportsTemporaryHP(boolean supportsTemporaryHP) { this.supportsTemporaryHP = supportsTemporaryHP; }

    public boolean isTempHPStacks() { return tempHPStacks; }
    public void setTempHPStacks(boolean tempHPStacks) { this.tempHPStacks = tempHPStacks; }

    public String getTempHPStackingMethod() { return tempHPStackingMethod; }
    public void setTempHPStackingMethod(String tempHPStackingMethod) {
        this.tempHPStackingMethod = tempHPStackingMethod;
    }

    public int getMaxTemporaryHP() { return maxTemporaryHP; }
    public void setMaxTemporaryHP(int maxTemporaryHP) { this.maxTemporaryHP = maxTemporaryHP; }


    public boolean isAllowsMaxHPReduction() { return allowsMaxHPReduction; }
    public void setAllowsMaxHPReduction(boolean allowsMaxHPReduction) {
        this.allowsMaxHPReduction = allowsMaxHPReduction;
    }

    public boolean isAllowsMaxHPIncrease() { return allowsMaxHPIncrease; }
    public void setAllowsMaxHPIncrease(boolean allowsMaxHPIncrease) { this.allowsMaxHPIncrease = allowsMaxHPIncrease; }


    public Map<String,Integer> getMaxHPModifierLimits() { return maxHPModifierLimits; }
    public void setMaxHPModifierLimits(Map<String,Integer> maxHPModifierLimits) { this.maxHPModifierLimits = maxHPModifierLimits; }
    public void setMaxHPModifierLimit(String type, int limit) { maxHPModifierLimits.put(type, limit); }
    public void removeMaxHPModifierLimit(String type) { maxHPModifierLimits.remove(type); }

    // === HIT DIE RECOVERY METHODS ===
    public boolean isUsesHitDieRecovery() { return usesHitDieRecovery; }
    public void setUsesHitDieRecovery(boolean usesHitDieRecovery) { this.usesHitDieRecovery = usesHitDieRecovery; }

    public String getHitDieRecoveryTiming() { return hitDieRecoveryTiming; }
    public void setHitDieRecoveryTiming(String hitDieRecoveryTiming) {
        this.hitDieRecoveryTiming = hitDieRecoveryTiming;
    }

    public String getRecoveryCalculation() { return recoveryCalculation; }
    public void setRecoveryCalculation(String recoveryCalculation) { this.recoveryCalculation = recoveryCalculation; }

    public int getFixedRecoveryAmount() { return fixedRecoveryAmount; }
    public void setFixedRecoveryAmount(int fixedRecoveryAmount) { this.fixedRecoveryAmount = fixedRecoveryAmount; }

    public String getRecoveryFormula() { return recoveryFormula; }
    public void setRecoveryFormula(String recoveryFormula) { this.recoveryFormula = recoveryFormula; }

    public int getMinimumRecovery() { return minimumRecovery; }
    public void setMinimumRecovery(int minimumRecovery) { this.minimumRecovery = minimumRecovery; }

    public int getMaximumRecovery() { return maximumRecovery; }
    public void setMaximumRecovery(int maximumRecovery) { this.maximumRecovery = maximumRecovery; }

    public boolean isCanSpendHDDuringRest() { return canSpendHDDuringRest; }
    public void setCanSpendHDDuringRest(boolean canSpendHDDuringRest) {
        this.canSpendHDDuringRest = canSpendHDDuringRest;
    }

    public String getHdSpendingTiming() { return hdSpendingTiming; }
    public void setHdSpendingTiming(String hdSpendingTiming) { this.hdSpendingTiming = hdSpendingTiming; }

    public int getMaxHDSpendablePerRest() { return maxHDSpendablePerRest; }
    public void setMaxHDSpendablePerRest(int maxHDSpendablePerRest) {
        this.maxHDSpendablePerRest = maxHDSpendablePerRest;
    }

    public boolean isRecoversHPOnLongRest() { return recoversHPOnLongRest; }
    public void setRecoversHPOnLongRest(boolean recoversHPOnLongRest) {
        this.recoversHPOnLongRest = recoversHPOnLongRest;
    }

    public String getLongRestHPRecovery() { return longRestHPRecovery; }
    public void setLongRestHPRecovery(String longRestHPRecovery) { this.longRestHPRecovery = longRestHPRecovery; }

    // === ALTERNATIVE SYSTEM METHODS ===
    public String getAlternativeSystem() { return alternativeSystem; }
    public void setAlternativeSystem(String alternativeSystem) { this.alternativeSystem = alternativeSystem; }

    public boolean isUsesVitalityWound() { return usesVitalityWound; }
    public void setUsesVitalityWound(boolean usesVitalityWound) { this.usesVitalityWound = usesVitalityWound; }

    public String getVitalityCalculation() { return vitalityCalculation; }
    public void setVitalityCalculation(String vitalityCalculation) { this.vitalityCalculation = vitalityCalculation; }

    public String getWoundCalculation() { return woundCalculation; }
    public void setWoundCalculation(String woundCalculation) { this.woundCalculation = woundCalculation; }

    public boolean isWoundDamageLethal() { return woundDamageLethal; }
    public void setWoundDamageLethal(boolean woundDamageLethal) { this.woundDamageLethal = woundDamageLethal; }

    public boolean isUsesBloodiedState() { return usesBloodiedState; }
    public void setUsesBloodiedState(boolean usesBloodiedState) { this.usesBloodiedState = usesBloodiedState; }

    public int getBloodiedThreshold() { return bloodiedThreshold; }
    public void setBloodiedThreshold(int bloodiedThreshold) { this.bloodiedThreshold = bloodiedThreshold; }

    public Map<String,Object> getBloodiedEffects() { return bloodiedEffects; }
    public void setBloodiedEffects(Map<String,Object> bloodiedEffects) { this.bloodiedEffects = bloodiedEffects; }
    public void setBloodiedEffect(String key, Object value) { bloodiedEffects.put(key, value); }
    public void removeBloodiedEffect(String key) { bloodiedEffects.remove(key); }

    public boolean isUsesStamina() { return usesStamina; }
    public void setUsesStamina(boolean usesStamina) { this.usesStamina = usesStamina; }

    public String getStaminaCalculation() { return staminaCalculation; }
    public void setStaminaCalculation(String staminaCalculation) { this.staminaCalculation = staminaCalculation; }

    public boolean isStaminaRegeneratesInCombat() { return staminaRegeneratesInCombat; }
    public void setStaminaRegeneratesInCombat(boolean staminaRegeneratesInCombat) {
        this.staminaRegeneratesInCombat = staminaRegeneratesInCombat;
    }

    public int getStaminaRegenRate() { return staminaRegenRate; }
    public void setStaminaRegenRate(int staminaRegenRate) { this.staminaRegenRate = staminaRegenRate; }

    public boolean isUsesMassiveDamageThreshold() { return usesMassiveDamageThreshold; }
    public void setUsesMassiveDamageThreshold(boolean usesMassiveDamageThreshold) {
        this.usesMassiveDamageThreshold = usesMassiveDamageThreshold;
    }

    public String getMassiveDamageCalculation() { return massiveDamageCalculation; }
    public void setMassiveDamageCalculation(String massiveDamageCalculation) {
        this.massiveDamageCalculation = massiveDamageCalculation;
    }

    public String getMassiveDamageEffect() { return massiveDamageEffect; }
    public void setMassiveDamageEffect(String massiveDamageEffect) { this.massiveDamageEffect = massiveDamageEffect; }

    // === CONDITIONAL MODIFIER METHODS ===
    public boolean isUsesConditionalModifiers() { return usesConditionalModifiers; }
    public void setUsesConditionalModifiers(boolean usesConditionalModifiers) {
        this.usesConditionalModifiers = usesConditionalModifiers;
    }

    public Map<String,Integer> getFeatHPBonuses() { return featHPBonuses; }
    public void setFeatHPBonuses(Map<String,Integer> featHPBonuses) { this.featHPBonuses = featHPBonuses; }
    public void setFeatHPBonus(String feat, int bonus) { featHPBonuses.put(feat, bonus); }
    public void removeFeatHPBonus(String feat) { featHPBonuses.remove(feat); }

    public Map<String,String> getFeatHPFormulas() { return featHPFormulas; }
    public void setFeatHPFormulas(Map<String,String> featHPFormulas) { this.featHPFormulas = featHPFormulas; }
    public void setFeatHPFormula(String feat, String formula) { featHPFormulas.put(feat, formula); }
    public void removeFeatHPFormula(String feat) { featHPFormulas.remove(feat); }

    public Map<String,Integer> getRacialHPBonuses() { return racialHPBonuses; }
    public void setRacialHPBonuses(Map<String,Integer> racialHPBonuses) { this.racialHPBonuses = racialHPBonuses; }
    public void setRacialHPBonus(String race, int bonus) { racialHPBonuses.put(race, bonus); }
    public void removeRacialHPBonus(String race) { racialHPBonuses.remove(race); }

    public Map<String,String> getRacialHPFormulas() { return racialHPFormulas; }
    public void setRacialHPFormulas(Map<String,String> racialHPFormulas) { this.racialHPFormulas = racialHPFormulas; }
    public void setRacialHPFormula(String race, String formula) { racialHPFormulas.put(race, formula); }
    public void removeRacialHPFormula(String race) { racialHPFormulas.remove(race); }

    public boolean isRacialBonusesRetroactive() { return racialBonusesRetroactive; }
    public void setRacialBonusesRetroactive(boolean racialBonusesRetroactive) {
        this.racialBonusesRetroactive = racialBonusesRetroactive;
    }

    public Map<String,Integer> getClassFeatureHPBonuses() { return classFeatureHPBonuses; }
    public void setClassFeatureHPBonuses(Map<String,Integer> classFeatureHPBonuses) { this.classFeatureHPBonuses = classFeatureHPBonuses; }
    public void setClassFeatureHPBonus(String feature, int bonus) { classFeatureHPBonuses.put(feature, bonus); }
    public void removeClassFeatureHPBonus(String feature) { classFeatureHPBonuses.remove(feature); }

    public Map<String,String> getClassFeatureConditions() { return classFeatureConditions; }
    public void setClassFeatureConditions(Map<String,String> classFeatureConditions) { this.classFeatureConditions = classFeatureConditions; }
    public void setClassFeatureCondition(String feature, String condition) {
        classFeatureConditions.put(feature, condition);
    }
    public void removeClassFeatureCondition(String feature) { classFeatureConditions.remove(feature); }

    public boolean isUsesSizeModifiers() { return usesSizeModifiers; }
    public void setUsesSizeModifiers(boolean usesSizeModifiers) { this.usesSizeModifiers = usesSizeModifiers; }

    public Map<String,Integer> getSizeHPModifiers() { return sizeHPModifiers; }
    public void setSizeHPModifiers(Map<String,Integer> sizeHPModifiers) { this.sizeHPModifiers = sizeHPModifiers; }
    public void setSizeHPModifier(String size, int modifier) { sizeHPModifiers.put(size, modifier); }
    public void removeSizeHPModifier(String size) { sizeHPModifiers.remove(size); }

    public Map<String,Integer> getSituationalModifiers() { return situationalModifiers; }
    public void setSituationalModifiers(Map<String,Integer> situationalModifiers) { this.situationalModifiers = situationalModifiers; }
    public void setSituationalModifier(String condition, int modifier) { situationalModifiers.put(condition, modifier); }
    public void removeSituationalModifier(String condition) { situationalModifiers.remove(condition); }


    // === DAMAGE & DEATH METHODS ===
    public boolean isUsesNegativeHP() { return usesNegativeHP; }
    public void setUsesNegativeHP(boolean usesNegativeHP) { this.usesNegativeHP = usesNegativeHP; }

    public int getNegativeHPThreshold() { return negativeHPThreshold; }
    public void setNegativeHPThreshold(int negativeHPThreshold) { this.negativeHPThreshold = negativeHPThreshold; }

    public String getNegativeHPCalculation() { return negativeHPCalculation; }
    public void setNegativeHPCalculation(String negativeHPCalculation) {
        this.negativeHPCalculation = negativeHPCalculation;
    }

    public boolean isUsesDeathSaves() { return usesDeathSaves; }
    public void setUsesDeathSaves(boolean usesDeathSaves) { this.usesDeathSaves = usesDeathSaves; }

    public int getDeathSaveSuccessThreshold() { return deathSaveSuccessThreshold; }
    public void setDeathSaveSuccessThreshold(int deathSaveSuccessThreshold) {
        this.deathSaveSuccessThreshold = deathSaveSuccessThreshold;
    }

    public int getDeathSaveFailureThreshold() { return deathSaveFailureThreshold; }
    public void setDeathSaveFailureThreshold(int deathSaveFailureThreshold) {
        this.deathSaveFailureThreshold = deathSaveFailureThreshold;
    }

    public int getDeathSaveDC() { return deathSaveDC; }
    public void setDeathSaveDC(int deathSaveDC) { this.deathSaveDC = deathSaveDC; }

    public boolean isMassiveDamageInstantDeath() { return massiveDamageInstantDeath; }
    public void setMassiveDamageInstantDeath(boolean massiveDamageInstantDeath) {
        this.massiveDamageInstantDeath = massiveDamageInstantDeath;
    }

    public boolean isAllowsStabilization() { return allowsStabilization; }
    public void setAllowsStabilization(boolean allowsStabilization) { this.allowsStabilization = allowsStabilization; }

    public int getStabilizationDC() { return stabilizationDC; }
    public void setStabilizationDC(int stabilizationDC) { this.stabilizationDC = stabilizationDC; }

    public boolean isAutoStabilizeAtZero() { return autoStabilizeAtZero; }
    public void setAutoStabilizeAtZero(boolean autoStabilizeAtZero) { this.autoStabilizeAtZero = autoStabilizeAtZero; }

    public int getUnconsciousThreshold() { return unconsciousThreshold; }
    public void setUnconsciousThreshold(int unconsciousThreshold) { this.unconsciousThreshold = unconsciousThreshold; }

    public boolean isUnconsciousAtZero() { return unconsciousAtZero; }
    public void setUnconsciousAtZero(boolean unconsciousAtZero) { this.unconsciousAtZero = unconsciousAtZero; }

    // === HEALING METHODS ===
    public boolean isHasHealingLimits() { return hasHealingLimits; }
    public void setHasHealingLimits(boolean hasHealingLimits) { this.hasHealingLimits = hasHealingLimits; }

    public int getMaxHealingPerDay() { return maxHealingPerDay; }
    public void setMaxHealingPerDay(int maxHealingPerDay) { this.maxHealingPerDay = maxHealingPerDay; }

    public boolean isNaturalHealingPerDay() { return naturalHealingPerDay; }
    public void setNaturalHealingPerDay(boolean naturalHealingPerDay) {
        this.naturalHealingPerDay = naturalHealingPerDay;
    }

    public String getNaturalHealingRate() { return naturalHealingRate; }
    public void setNaturalHealingRate(String naturalHealingRate) { this.naturalHealingRate = naturalHealingRate; }

    public int getNaturalHealingAmount() { return naturalHealingAmount; }
    public void setNaturalHealingAmount(int naturalHealingAmount) { this.naturalHealingAmount = naturalHealingAmount; }

    public String getShortRestHealing() { return shortRestHealing; }
    public void setShortRestHealing(String shortRestHealing) { this.shortRestHealing = shortRestHealing; }

    public String getLongRestHealing() { return longRestHealing; }
    public void setLongRestHealing(String longRestHealing) { this.longRestHealing = longRestHealing; }

    public boolean isRequiresFoodAndWater() { return requiresFoodAndWater; }
    public void setRequiresFoodAndWater(boolean requiresFoodAndWater) {
        this.requiresFoodAndWater = requiresFoodAndWater;
    }

    public boolean isMagicalHealingModified() { return magicalHealingModified; }
    public void setMagicalHealingModified(boolean magicalHealingModified) {
        this.magicalHealingModified = magicalHealingModified;
    }

    public Map<String,Double> getHealingModifiers() { return healingModifiers; }
    public void setHealingModifiers(Map<String,Double> healingModifiers) { this.healingModifiers = healingModifiers; }
    public void setHealingModifier(String source, double multiplier) { healingModifiers.put(source, multiplier); }
    public void removeHealingModifier(String source) { healingModifiers.remove(source); }

    public int getMaxHealingPerSource() { return maxHealingPerSource; }
    public void setMaxHealingPerSource(int maxHealingPerSource) { this.maxHealingPerSource = maxHealingPerSource; }

    // === LEVEL ZERO & NPC METHODS ===
    public boolean isAllowsLevelZero() { return allowsLevelZero; }
    public void setAllowsLevelZero(boolean allowsLevelZero) { this.allowsLevelZero = allowsLevelZero; }

    public String getLevelZeroHPMethod() { return levelZeroHPMethod; }
    public void setLevelZeroHPMethod(String levelZeroHPMethod) { this.levelZeroHPMethod = levelZeroHPMethod; }

    public int getLevelZeroFixedHP() { return levelZeroFixedHP; }
    public void setLevelZeroFixedHP(int levelZeroFixedHP) { this.levelZeroFixedHP = levelZeroFixedHP; }

    public String getLevelZeroHitDie() { return levelZeroHitDie; }
    public void setLevelZeroHitDie(String levelZeroHitDie) { this.levelZeroHitDie = levelZeroHitDie; }

    public boolean isUsesNPCHPRules() { return usesNPCHPRules; }
    public void setUsesNPCHPRules(boolean usesNPCHPRules) { this.usesNPCHPRules = usesNPCHPRules; }

    public String getNpcHPMethod() { return npcHPMethod; }
    public void setNpcHPMethod(String npcHPMethod) { this.npcHPMethod = npcHPMethod; }

    public Map<String,Integer> getNpcHPByType() { return npcHPByType; }
    public void setNpcHPByType(Map<String,Integer> npcHPByType) { this.npcHPByType = npcHPByType; }
    public void setNpcHPForType(String type, int hp) { npcHPByType.put(type, hp); }
    public void removeNpcHPForType(String type) { npcHPByType.remove(type); }

    // === ADVANCEMENT & LEVELING METHODS ===
    public boolean isAllowsHPReroll() { return allowsHPReroll; }
    public void setAllowsHPReroll(boolean allowsHPReroll) { this.allowsHPReroll = allowsHPReroll; }

    public String getRerollTiming() { return rerollTiming; }
    public void setRerollTiming(String rerollTiming) { this.rerollTiming = rerollTiming; }

    public boolean isRerollAffectsTotal() { return rerollAffectsTotal; }
    public void setRerollAffectsTotal(boolean rerollAffectsTotal) { this.rerollAffectsTotal = rerollAffectsTotal; }

    public boolean isCanLoseHPOnReroll() { return canLoseHPOnReroll; }
    public void setCanLoseHPOnReroll(boolean canLoseHPOnReroll) { this.canLoseHPOnReroll = canLoseHPOnReroll; }

    public boolean isAllowsLevelLoss() { return allowsLevelLoss; }
    public void setAllowsLevelLoss(boolean allowsLevelLoss) { this.allowsLevelLoss = allowsLevelLoss; }

    public String getLevelLossHPMethod() { return levelLossHPMethod; }
    public void setLevelLossHPMethod(String levelLossHPMethod) { this.levelLossHPMethod = levelLossHPMethod; }

    public boolean isPermanentHPLoss() { return permanentHPLoss; }
    public void setPermanentHPLoss(boolean permanentHPLoss) { this.permanentHPLoss = permanentHPLoss; }

    // === VARIANT RULES METHODS ===
    public Map<String,Object> getVariantRules() { return variantRules; }
    public void setVariantRules(Map<String,Object> variantRules) { this.variantRules = variantRules; }
    public void setVariantRule(String rule, Object value) { variantRules.put(rule, value); }
    public void removeVariantRule(String rule) { variantRules.remove(rule); }


    public boolean isUsesGrittyRealism() { return usesGrittyRealism; }
    public void setUsesGrittyRealism(boolean usesGrittyRealism) { this.usesGrittyRealism = usesGrittyRealism; }

    public String getGrittyRestDuration() { return grittyRestDuration; }
    public void setGrittyRestDuration(String grittyRestDuration) { this.grittyRestDuration = grittyRestDuration; }

    public boolean isSupportsEpicLevels() { return supportsEpicLevels; }
    public void setSupportsEpicLevels(boolean supportsEpicLevels) { this.supportsEpicLevels = supportsEpicLevels; }

    public String getEpicHPProgression() { return epicHPProgression; }
    public void setEpicHPProgression(String epicHPProgression) { this.epicHPProgression = epicHPProgression; }

    public int getEpicFixedHP() { return epicFixedHP; }
    public void setEpicFixedHP(int epicFixedHP) { this.epicFixedHP = epicFixedHP; }

    // === DISPLAY METHODS ===
    public boolean isDisplayCurrentHP() { return displayCurrentHP; }
    public void setDisplayCurrentHP(boolean displayCurrentHP) { this.displayCurrentHP = displayCurrentHP; }

    public boolean isDisplayMaxHP() { return displayMaxHP; }
    public void setDisplayMaxHP(boolean displayMaxHP) { this.displayMaxHP = displayMaxHP; }

    public boolean isDisplayTempHP() { return displayTempHP; }
    public void setDisplayTempHP(boolean displayTempHP) { this.displayTempHP = displayTempHP; }

    public boolean isDisplayHitDiceRemaining() { return displayHitDiceRemaining; }
    public void setDisplayHitDiceRemaining(boolean displayHitDiceRemaining) {
        this.displayHitDiceRemaining = displayHitDiceRemaining;
    }

    public String getHpDisplayFormat() { return hpDisplayFormat; }
    public void setHpDisplayFormat(String hpDisplayFormat) { this.hpDisplayFormat = hpDisplayFormat; }

    // === CONFIGURATION FLAG METHODS ===
    public boolean isUsesHitDice() { return usesHitDice; }
    public void setUsesHitDice(boolean usesHitDice) { this.usesHitDice = usesHitDice; }

    public boolean isUsesConModifier() { return usesConModifier; }
    public void setUsesConModifier(boolean usesConModifier) { this.usesConModifier = usesConModifier; }

    public boolean isUsesMulticlassHP() { return usesMulticlassHP; }
    public void setUsesMulticlassHP(boolean usesMulticlassHP) { this.usesMulticlassHP = usesMulticlassHP; }

    public boolean isUsesFavoredClassSystem() { return usesFavoredClassSystem; }
    public void setUsesFavoredClassSystem(boolean usesFavoredClassSystem) {
        this.usesFavoredClassSystem = usesFavoredClassSystem;
    }

    public boolean isUsesHPRecovery() { return usesHPRecovery; }
    public void setUsesHPRecovery(boolean usesHPRecovery) { this.usesHPRecovery = usesHPRecovery; }

    public boolean isUsesAlternativeSystem() { return usesAlternativeSystem; }
    public void setUsesAlternativeSystem(boolean usesAlternativeSystem) {
        this.usesAlternativeSystem = usesAlternativeSystem;
    }

    public boolean isUsesAdvancedRolling() { return usesAdvancedRolling; }
    public void setUsesAdvancedRolling(boolean usesAdvancedRolling) { this.usesAdvancedRolling = usesAdvancedRolling; }

    public boolean isUsesDeathAndDying() { return usesDeathAndDying; }
    public void setUsesDeathAndDying(boolean usesDeathAndDying) { this.usesDeathAndDying = usesDeathAndDying; }

    public boolean isUsesHealingRules() { return usesHealingRules; }
    public void setUsesHealingRules(boolean usesHealingRules) { this.usesHealingRules = usesHealingRules; }

    public boolean isUsesVariantRules() { return usesVariantRules; }
    public void setUsesVariantRules(boolean usesVariantRules) { this.usesVariantRules = usesVariantRules; }

    // === UTILITY METHODS ===

    /**
     * Gets a human-readable description of the HP system
     */
    public String getHPSystemDescription() {
        StringBuilder desc = new StringBuilder();

        // Hit die
        desc.append("HD: ").append(hitDieType);

        // HP gain method
        switch (hpGainMethod) {
            case "rolled":
                desc.append(" (rolled)");
                break;
            case "fixed":
                if (allowMultipleFixedGains) {
                    desc.append(" (fixed gain varies by character)");
                } else {
                    desc.append(" (").append(fixedHPPerLevel).append("/level)");
                }
                break;
            case "attribute_derived":
                return "HP derived from attributes (" + getAttributeDerivationMode() + ")";
            case "choice":
                desc.append(" (player choice)");
                break;
        }

        // Attribute modifier
        if (appliesConstitutionModifier) {
            desc.append(" + attribute modifier");
        }

        // First level
        if (firstLevelMaxHP) {
            desc.append(", max at 1st");
        }

        return desc.toString();
    }

    /**
     * Calculates the average HP for a given die type
     */
    public int getAverageForDie(String dieType) {
        if (customAverageValues.containsKey(dieType)) {
            return customAverageValues.get(dieType);
        }

        // Parse die type (e.g., "d8" -> 8)
        try {
            int sides = Integer.parseInt(dieType.substring(1));
            double avg = (sides + 1) / 2.0;

            switch (averageRoundingMethod) {
                case "up":
                    return (int) Math.ceil(avg);
                case "down":
                    return (int) Math.floor(avg);
                case "nearest":
                default:
                    return (int) Math.round(avg);
            }
        } catch (Exception e) {
            return 1; // Default minimum
        }
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validClassIds, Set<String> validRaceIds) {
        int removedCount = 0;

        // Clean up classToDieType Map (keys are CharacterClass ids)
        Iterator<Map.Entry<String, String>> classDieIter = classToDieType.entrySet().iterator();
        while (classDieIter.hasNext()) {
            Map.Entry<String, String> entry = classDieIter.next();
            String classId = entry.getKey();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classDieIter.remove();
                removedCount++;
            }
        }

        // Clean up conModifierMultipliers Map (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> conModIter = conModifierMultipliers.entrySet().iterator();
        while (conModIter.hasNext()) {
            Map.Entry<String, Integer> entry = conModIter.next();
            String classId = entry.getKey();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                conModIter.remove();
                removedCount++;
            }
        }

        // Clean up racialFavoredClassBonuses Map (keys are Race ids)
        Iterator<Map.Entry<String, Integer>> raceFavoredIter = racialFavoredClassBonuses.entrySet().iterator();
        while (raceFavoredIter.hasNext()) {
            Map.Entry<String, Integer> entry = raceFavoredIter.next();
            String raceId = entry.getKey();

            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceFavoredIter.remove();
                removedCount++;
            }
        }

        // Clean up racialHPBonuses Map (keys are Race ids)
        Iterator<Map.Entry<String, Integer>> raceHPBonusIter = racialHPBonuses.entrySet().iterator();
        while (raceHPBonusIter.hasNext()) {
            Map.Entry<String, Integer> entry = raceHPBonusIter.next();
            String raceId = entry.getKey();

            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceHPBonusIter.remove();
                removedCount++;
            }
        }

        // Clean up racialHPFormulas Map (keys are Race ids)
        Iterator<Map.Entry<String, String>> raceHPFormulaIter = racialHPFormulas.entrySet().iterator();
        while (raceHPFormulaIter.hasNext()) {
            Map.Entry<String, String> entry = raceHPFormulaIter.next();
            String raceId = entry.getKey();

            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceHPFormulaIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    public int cleanupAttributeDerivedReferences(Set<String> validAttributeIds) {
        Set<String> safeIds = validAttributeIds == null ? Set.of() : validAttributeIds;
        int removedCount = 0;
        if (!getAttributeDerivedDirectAttributeId().isEmpty()
            && !safeIds.contains(getAttributeDerivedDirectAttributeId())) {
            attributeDerivedDirectAttributeId = "";
            removedCount++;
        }
        Iterator<AttributeHPTerm> iterator = attributeDerivedTerms.iterator();
        while (iterator.hasNext()) {
            if (!safeIds.contains(iterator.next().getAttributeId())) {
                iterator.remove();
                removedCount++;
            }
        }
        return removedCount;
    }

    private int parseDieSides(String hitDieType) {
        String safe = Objects.toString(hitDieType, "").trim().toLowerCase();
        if (safe.startsWith("d")) {
            safe = safe.substring(1).trim();
        }
        if (safe.isEmpty()) {
            return 0;
        }
        try {
            int sides = Integer.parseInt(safe);
            return Math.max(0, sides);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String normalizeHpGainMethod(String value) {
        String safe = Objects.toString(value, "").trim().toLowerCase();
        if ("fixed".equals(safe) || "attribute_derived".equals(safe)) {
            return safe;
        }
        if ("average".equals(safe)) {
            return "fixed";
        }
        return "rolled";
    }

    private String normalizeAttributeDerivationMode(String value) {
        String safe = Objects.toString(value, "").trim().toLowerCase();
        if ("single_formula".equals(safe) || "multi_formula".equals(safe)) {
            return safe;
        }
        return "direct";
    }

    private String normalizeRoundingMethod(String value) {
        String safe = Objects.toString(value, "").trim().toLowerCase();
        if ("up".equals(safe) || "down".equals(safe)) {
            return safe;
        }
        return "nearest";
    }

    private int roundAttributeDerivedValue(double value) {
        switch (getAttributeDerivedRoundingMethod()) {
            case "up":
                return (int) Math.ceil(value);
            case "down":
                return (int) Math.floor(value);
            default:
                return (int) Math.round(value);
        }
    }

    private int calculateLegacyAverageGain() {
        int sides = getHitDieSides();
        double average = sides > 0
            ? getHitDieCount() * ((sides + 1) / 2.0) + getHitDieModifier()
            : fixedHPPerLevel;
        switch (normalizeRoundingMethod(averageRoundingMethod)) {
            case "up":
                return (int) Math.ceil(average);
            case "down":
                return (int) Math.floor(average);
            default:
                return (int) Math.round(average);
        }
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
        hitDieCount = Math.max(1, hitDieCount);
        hitDieSides = Math.max(0, hitDieSides);
        if ("average".equals(Objects.toString(hpGainMethod, "").trim().toLowerCase())) {
            fixedHPPerLevel = calculateLegacyAverageGain();
        }
        hpGainMethod = normalizeHpGainMethod(hpGainMethod);
        attributeDerivationMode = normalizeAttributeDerivationMode(attributeDerivationMode);
        attributeDerivedDirectAttributeId = Objects.toString(attributeDerivedDirectAttributeId, "").trim();
        setAttributeDerivedTerms(attributeDerivedTerms);
        setAttributeDerivedBaseValue(attributeDerivedBaseValue);
        setAttributeDerivedDivisor(attributeDerivedDivisor);
        attributeDerivedRoundingMethod = normalizeRoundingMethod(attributeDerivedRoundingMethod);
        hpModifierAttributeId = Objects.toString(hpModifierAttributeId, "").trim();
        if (!hpModifierAttributeId.isEmpty()) {
            appliesConstitutionModifier = true;
        }
    }
}
