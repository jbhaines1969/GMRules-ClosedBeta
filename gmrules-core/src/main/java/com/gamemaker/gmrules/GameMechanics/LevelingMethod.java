/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import com.gamemaker.gmrules.AtomicElements.*;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.util.*;

/**
 * Comprehensive leveling and character advancement system supporting various RPG progression mechanics.
 * Handles XP progression, alternative advancement, multiclassing, prestige classes, ability score increases,
 * level caps, deleveling, and variant progression systems.
 * Designed for mechanical implementation by descendant applications.
 */
public class LevelingMethod extends GameElement {

// *** MEMBERS ***
    // === XP PROGRESSION SYSTEM ===
    private String xpSystemType = "table"; // "table", "formula", "fibonacci", "milestone", "hybrid"
    private Map<Integer,Integer> xpTable = new LinkedHashMap<>(); // Level -> XP required
    private String xpFormula = "1000*level^2"; // Formula for XP calculation
    private int baseXP = 1000; // Base XP for formula calculations
    private double xpMultiplier = 1.0; // Multiplier for XP progression
    private String xpScalingMethod = "exponential"; // "linear", "exponential", "quadratic", "custom"

    // XP award mechanics
    private String xpAwardMethod = "encounter"; // "encounter", "story", "objective", "hybrid"
    private boolean usesPartyXPSharing = true;
    private String xpSharingMethod = "equal"; // "equal", "byLevel", "byContribution", "custom"
    private Map<String,Double> xpSourceMultipliers = new HashMap<>(); // Source -> multiplier

    // XP penalties and bonuses
    private boolean usesXPPenalties = false;
    private Map<String,Integer> xpPenaltyConditions = new HashMap<>(); // Condition -> penalty %
    private boolean allowsNegativeXP = false;
    private int minimumXP = 0;

    // === ALTERNATIVE ADVANCEMENT SYSTEMS ===
    private String alternativeAdvancement = "none"; // "none", "milestone", "sessionBased", "storyBased", "training"

    // Milestone advancement
    private boolean usesMilestoneAdvancement = false;
    private Map<String,Integer> milestoneXPValues = new HashMap<>(); // Type -> XP value
    private boolean milestonesReplaceXP = false;

    // Session-based advancement
    private boolean usesSessionBasedAdvancement = false;
    private int sessionsPerLevel = 4;
    private Map<Integer,Integer> sessionsByLevel = new HashMap<>(); // Level -> sessions required
    private boolean sessionsVaryByLevel = false;

    // Story-based advancement
    private boolean usesStoryAdvancement = false;
    private Map<String,Integer> storyMilestoneLevels = new HashMap<>(); // Milestone -> level granted

    // === LEVEL CAPS AND MAXIMUM LEVELS ===
    private int maximumLevel = 20;
    private boolean hasLevelCap = true;
    private boolean allowsEpicLevels = false;
    private int epicLevelStart = 21;
    private String epicProgressionType = "standard"; // "standard", "slower", "milestone", "custom"

    // Soft caps
    private boolean usesSoftCaps = false;
    private Map<Integer,Double> softCapMultipliers = new HashMap<>(); // Level -> XP multiplier

    // Level-specific caps
    private Map<String,Integer> classLevelCaps = new HashMap<>(); // Class -> max level
    private Map<String,Integer> racialLevelAdjustment = new HashMap<>(); // Race -> LA value

    // === MULTICLASS RULES ===
    private boolean allowsMulticlassing = true;
    private int minimumLevelForMulticlass = 1;
    private Map<String,Integer> multiclassRequirements = new HashMap<>(); // Attribute -> minimum value

    // XP penalties for multiclassing
    private boolean usesMulticlassXPPenalty = false;
    private int xpPenaltyThreshold = 1; // Level difference that triggers penalty
    private int xpPenaltyAmount = 20; // Percentage penalty
    private String xpPenaltyCalculation = "levelDifference"; // "levelDifference", "perClass", "custom"

    // Favored class mechanics
    private boolean usesFavoredClass = false;
    private Map<String,String> raceFavoredClass = new HashMap<>(); // Race -> favored class
    private boolean favoredClassNegatesPenalty = true;
    private boolean allowsMultipleFavoredClasses = false;

    // Multiclass stacking
    private String stackingMethod = "separate"; // "separate", "combined", "hybrid"
    private Map<String,String> progressionStackingRules = new HashMap<>(); // Feature -> stacking rule

    // === PRESTIGE CLASSES ===
    private boolean allowsPrestigeClasses = false;
    private int minimumLevelForPrestige = 5;

    // Prestige requirements
    private Map<String,Map<String,Object>> prestigeRequirements = new HashMap<>(); // Class -> requirements map
    private boolean prestigeRequiresFeats = true;
    private boolean prestigeRequiresSkills = true;
    private boolean prestigeRequiresAbilityScores = true;

    // Prestige progression
    private Map<String,Integer> prestigeClassLevels = new HashMap<>(); // Class -> max levels
    private String prestigeStackingMethod = "full"; // "full", "partial", "none"

    // === LEVEL ADJUSTMENT & ECL ===
    private boolean usesLevelAdjustment = false;
    private boolean usesEffectiveCharacterLevel = false;
    private Map<String,Integer> templateLevelAdjustment = new HashMap<>(); // Template -> LA
    private String eclCalculation = "characterLevel+LA"; // Formula for ECL
    private int maximumLevelAdjustment = 5;

    // LA buyoff mechanics
    private boolean allowsLABuyoff = false;
    private Map<Integer,Integer> laBuyoffXPCost = new HashMap<>(); // LA value -> XP cost
    private int minimumLevelForBuyoff = 6;

    // === LEVEL-UP TIMING ===
    private String levelUpTiming = "immediate"; // "immediate", "rest", "training", "downtime", "milestone"
    private boolean requiresRest = false;
    private String restTypeRequired = "long"; // "short", "long", "extended", "custom"

    // Training requirements
    private boolean requiresTraining = false;
    private int trainingDaysPerLevel = 7;
    private String trainingCostFormula = "100*newLevel^2"; // Gold cost
    private boolean trainingRequiresTrainer = false;
    private Map<Integer,Integer> trainingTimeByLevel = new HashMap<>(); // Level -> days

    // Level-up costs
    private boolean hasLevelUpCost = false;
    private String costCalculation = "formula"; // "formula", "fixed", "byClass", "none"
    private int fixedLevelUpCost = 1000;
    private Map<String,String> classLevelUpCosts = new HashMap<>(); // Class -> formula

    // Downtime requirements
    private boolean requiresDowntime = false;
    private int downtimeDaysRequired = 1;

    // === ABILITY SCORE INCREASES ===
    private boolean grantsAbilityIncreases = true;
    private int abilityIncreaseFrequency = 4; // Every X levels
    private int abilityPointsPerIncrease = 2; // Points to distribute
    private int maxIncreasePerAttribute = 2; // Max points in single attribute

    // Ability increase methods
    private String abilityIncreaseMethod = "points"; // "points", "choice", "roll", "custom"
    private boolean allowsAttributeSwapping = false;
    private boolean abilityIncreasesCumulative = true;
    private int maximumAbilityScore = 20; // Cap on ability scores from ASI

    // Alternative ability increase systems
    private Map<String,Integer> classAbilityProgression = new HashMap<>(); // Class -> frequency
    private boolean usesFlexibleIncreases = false; // Choose ability or feat
    private Map<Integer,String> levelBonusTypes = new HashMap<>(); // Level -> "ability|feat|special"

    // === DELEVELING & LEVEL DRAIN ===
    private boolean allowsDeleveling = false;
    private String delevelMethod = "subtract"; // "subtract", "percentXP", "fullLevel", "custom"

    // Level drain mechanics
    private boolean usesLevelDrain = false;
    private boolean levelDrainTemporary = true;
    private String levelDrainRestoration = "restoration"; // "restoration", "time", "none", "custom"
    private int daysToRestoreDrainedLevel = 7;

    // XP loss mechanics
    private boolean allowsXPLoss = false;
    private String xpLossCalculation = "percentage"; // "percentage", "fixed", "level", "custom"
    private int xpLossPercentage = 50;
    private int fixedXPLoss = 1000;
    private boolean xpLossCanDelevel = true;

    // Death and XP
    private boolean deathCausesXPLoss = false;
    private int deathXPPenalty = 10; // Percentage
    private boolean resurrectionRestoresXP = true;

    // === GESTALT RULES ===
    private boolean allowsGestalt = false;
    private String gestaltMethod = "dual"; // "dual", "triple", "custom"
    private int maxGestaltClasses = 2;

    // Gestalt progression
    private String gestaltHPMethod = "higher"; // "higher", "average", "both", "roll"
    private String gestaltSaveMethod = "best"; // "best", "stack", "average"
    private String gestaltBABMethod = "highest"; // "highest", "stack", "average"

    // === E6/E8 VARIANT RULES ===
    private boolean usesE6Rules = false;
    private boolean usesE8Rules = false;
    private int variantLevelCap = 6;

    // Post-cap advancement
    private String postCapAdvancement = "feats"; // "feats", "abilities", "both", "custom"
    private int xpPerPostCapBonus = 5000;
    private Map<String,Integer> postCapBonusCosts = new HashMap<>(); // Bonus -> XP cost

    // === CLASS PROGRESSION CHOICES ===
    private boolean allowsSubclassSelection = true;
    private Map<String,Integer> subclassSelectionLevel = new HashMap<>(); // Class -> level
    private boolean subclassIsPermament = true;

    // Archetype selection
    private boolean allowsArchetypes = false;
    private String archetypeSelectionTiming = "creation"; // "creation", "level1", "level3", "variable"
    private boolean allowsMultipleArchetypes = false;
    private Map<String,ArrayList<String>> archetypeConflicts = new HashMap<>(); // Archetype -> incompatible list

    // Retraining and respec
    private boolean allowsRetraining = false;
    private String retrainingCostFormula = "50*level";
    private int retrainingDaysPerChoice = 5;
    private boolean retrainingRequiresDowntime = true;

    // Full respec
    private boolean allowsFullRespec = false;
    private String respecCost = "none"; // "none", "gold", "quest", "punishment"
    private int respecGoldCost = 10000;

    // === VARIANT PROGRESSION SYSTEMS ===
    private Map<String,Object> variantRules = new HashMap<>();

    // Accelerated progression
    private boolean usesAcceleratedProgression = false;
    private double accelerationMultiplier = 0.5; // XP multiplier

    // Slow progression
    private boolean usesSlowProgression = false;
    private double slowProgressionMultiplier = 2.0;
    private String slowProgressionReason = "gritty"; // "gritty", "epic", "custom"

    // Custom progression tracks
    private boolean usesCustomTracks = false;
    private Map<String,Map<Integer,Integer>> customProgressionTracks = new HashMap<>(); // Track name -> level->XP
    private Map<String,String> characterTrackAssignment = new HashMap<>(); // Character -> track name

    // === FEAT & CLASS FEATURE PROGRESSION ===
    private boolean grantsFeatProgression = true;
    private int baseFeatFrequency = 3; // Every X levels
    private Map<String,Integer> classFeatProgression = new HashMap<>(); // Class -> frequency

    // Class features
    private boolean usesDelayedClassFeatures = false;
    private Map<String,Map<Integer,String>> classFeatureDelays = new HashMap<>(); // Class -> level->feature
    private boolean allowsClassFeatureSwapping = false;

    // === SKILL POINTS & RANKS ===
    private boolean grantsSkillPoints = true;
    private String skillPointProgression = "byClass"; // "byClass", "fixed", "intelligence", "custom"
    private int baseSkillPointsPerLevel = 4;
    private boolean skillPointsSameAllLevels = true;
    private Map<Integer,Integer> skillPointsByLevel = new LinkedHashMap<>();
    private boolean skillPointsModifiedByInt = true;
    private int minimumSkillPointsPerLevel = 1;

    // Skill rank caps
    private boolean usesSkillRankCaps = true;
    private String skillRankCapFormula = "level+3"; // For class skills
    private String crossClassRankCapFormula = "(level+3)/2";
    private boolean usesConsolidatedSkills = false;

    // === WEALTH BY LEVEL ===
    private boolean usesWealthByLevel = false;
    private Map<Integer,Integer> wealthProgression = new HashMap<>(); // Level -> gold
    private String wealthCalculation = "table"; // "table", "formula", "milestone"
    private boolean wealthAffectedByClass = false;

    // === PLANAR BINDING & MYTHIC PROGRESSION ===
    private boolean usesMythicTiers = false;
    private int maximumMythicTier = 10;
    private String mythicProgressionMethod = "trials"; // "trials", "xp", "milestone"
    private boolean mythicIndependentOfLevel = true;

    // === CONFIGURATION FLAGS ===
    private boolean usesXPProgression = true;
    private boolean usesAlternativeProgression = false;
    private boolean usesMulticlassRules = false;
    private boolean usesPrestigeClasses = false;
    private boolean usesECLSystem = false;
    private boolean usesTrainingRules = false;
    private boolean usesAbilityIncreases = true;
    private boolean usesDelevelingRules = false;
    private boolean usesGestaltRules = false;
    private boolean usesVariantCaps = false;
    private boolean usesRetrainingRules = false;
    private boolean usesWealthProgression = false;
    private boolean usesMythicRules = false;

    // === ARRAY REGISTRY ===
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public LevelingMethod(String name) {
        super(name);
        initializeArrayRegistry();
        initializeDefaults();
    }

    public LevelingMethod(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
        initializeDefaults();
    }

// *** METHODS ***

    /**
     * Initialize array registry for generic array management
     */
    private void initializeArrayRegistry() {
        arrayHandler.putArray("milestoneTypes", new ArrayList<String>());
        arrayHandler.putArray("storyMilestones", new ArrayList<String>());
        arrayHandler.putArray("softCapReasons", new ArrayList<String>());
        arrayHandler.putArray("multiclassRestrictions", new ArrayList<String>());
        arrayHandler.putArray("stackableProgression", new ArrayList<String>());
        arrayHandler.putArray("prestigeClassNames", new ArrayList<String>());
        arrayHandler.putArray("prestigeStacksWith", new ArrayList<String>());
        arrayHandler.putArray("buyoffRestrictions", new ArrayList<String>());
        arrayHandler.putArray("downtimeActivities", new ArrayList<String>());
        arrayHandler.putArray("abilityIncreaseLevels", new ArrayList<Integer>());
        arrayHandler.putArray("delevelCauses", new ArrayList<String>());
        arrayHandler.putArray("gestaltRestrictions", new ArrayList<String>());
        arrayHandler.putArray("gestaltStackingFeatures", new ArrayList<String>());
        arrayHandler.putArray("availablePostCapBonuses", new ArrayList<String>());
        arrayHandler.putArray("subclassChangeConditions", new ArrayList<String>());
        arrayHandler.putArray("retrainableChoices", new ArrayList<String>());
        arrayHandler.putArray("respecRestrictions", new ArrayList<String>());
        arrayHandler.putArray("enabledVariants", new ArrayList<String>());
        arrayHandler.putArray("acceleratedLevels", new ArrayList<Integer>());
        arrayHandler.putArray("bonusFeatLevels", new ArrayList<Integer>());
    }

    /**
     * Initialize default values
     */
    private void initializeDefaults() {
        // Standard D&D 5e XP table
        xpTable.put(1, 0);
        xpTable.put(2, 300);
        xpTable.put(3, 900);
        xpTable.put(4, 2700);
        xpTable.put(5, 6500);
        xpTable.put(6, 14000);
        xpTable.put(7, 23000);
        xpTable.put(8, 34000);
        xpTable.put(9, 48000);
        xpTable.put(10, 64000);
        xpTable.put(11, 85000);
        xpTable.put(12, 100000);
        xpTable.put(13, 120000);
        xpTable.put(14, 140000);
        xpTable.put(15, 165000);
        xpTable.put(16, 195000);
        xpTable.put(17, 225000);
        xpTable.put(18, 265000);
        xpTable.put(19, 305000);
        xpTable.put(20, 355000);

        // Default XP source multipliers
        xpSourceMultipliers.put("combat", 1.0);
        xpSourceMultipliers.put("quest", 1.0);
        xpSourceMultipliers.put("roleplay", 1.0);
        xpSourceMultipliers.put("exploration", 1.0);

        // Default milestone types
        ArrayList<String> milestoneTypes = getArray("milestoneTypes");
        milestoneTypes.add("major");
        milestoneTypes.add("minor");
        milestoneTypes.add("chapter");
        milestoneTypes.add("arc");

        // Default stackable progression
        ArrayList<String> stackableProgression = getArray("stackableProgression");
        stackableProgression.add("baseAttackBonus");
        stackableProgression.add("savingThrows");

        // Default prestige stacks
        ArrayList<String> prestigeStacksWith = getArray("prestigeStacksWith");
        prestigeStacksWith.add("baseAttackBonus");
        prestigeStacksWith.add("savingThrows");
        prestigeStacksWith.add("hitDice");

        // Default retrainable choices
        ArrayList<String> retrainableChoices = getArray("retrainableChoices");
        retrainableChoices.add("skills");
        retrainableChoices.add("feats");
        retrainableChoices.add("spells");

        // Default gestalt stacking
        ArrayList<String> gestaltStackingFeatures = getArray("gestaltStackingFeatures");
        gestaltStackingFeatures.add("hitPoints");
        gestaltStackingFeatures.add("skillPoints");
        gestaltStackingFeatures.add("classFeatures");

        // Default delevel causes
        ArrayList<String> delevelCauses = getArray("delevelCauses");
        delevelCauses.add("death");
        delevelCauses.add("curse");
        delevelCauses.add("energyDrain");

        // Default ability increase levels
        ArrayList<Integer> abilityIncreaseLevels = getArray("abilityIncreaseLevels");
        abilityIncreaseLevels.add(4);
        abilityIncreaseLevels.add(8);
        abilityIncreaseLevels.add(12);
        abilityIncreaseLevels.add(16);
        abilityIncreaseLevels.add(20);

        // Default bonus feat levels (Fighter-style)
        ArrayList<Integer> bonusFeatLevels = getArray("bonusFeatLevels");
        bonusFeatLevels.add(1);
        bonusFeatLevels.add(2);
        bonusFeatLevels.add(4);
        bonusFeatLevels.add(6);
        bonusFeatLevels.add(8);
        bonusFeatLevels.add(10);

        // Default wealth by level (5e starting gold)
        wealthProgression.put(1, 0);
        wealthProgression.put(2, 300);
        wealthProgression.put(3, 900);
        wealthProgression.put(4, 2700);
        wealthProgression.put(5, 6500);
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

    // === XP PROGRESSION GETTERS/SETTERS ===
    public String getXpSystemType() { return xpSystemType; }
    public void setXpSystemType(String xpSystemType) { this.xpSystemType = xpSystemType; }

    public Map<Integer,Integer> getXpTable() { return xpTable; }
    public void setXpTable(Map<Integer,Integer> xpTable) {
        this.xpTable = xpTable != null ? xpTable : new LinkedHashMap<>();
    }
    public void setXPForLevel(int level, int xp) { xpTable.put(level, xp); }
    public void removeXPForLevel(int level) { xpTable.remove(level); }

    public String getXpFormula() { return xpFormula; }
    public void setXpFormula(String xpFormula) { this.xpFormula = xpFormula; }

    public int getBaseXP() { return baseXP; }
    public void setBaseXP(int baseXP) { this.baseXP = baseXP; }

    public double getXpMultiplier() { return xpMultiplier; }
    public void setXpMultiplier(double xpMultiplier) { this.xpMultiplier = xpMultiplier; }

    public String getXpScalingMethod() { return xpScalingMethod; }
    public void setXpScalingMethod(String xpScalingMethod) { this.xpScalingMethod = xpScalingMethod; }

    public String getXpAwardMethod() { return xpAwardMethod; }
    public void setXpAwardMethod(String xpAwardMethod) { this.xpAwardMethod = xpAwardMethod; }

    public boolean isUsesPartyXPSharing() { return usesPartyXPSharing; }
    public void setUsesPartyXPSharing(boolean usesPartyXPSharing) { this.usesPartyXPSharing = usesPartyXPSharing; }

    public String getXpSharingMethod() { return xpSharingMethod; }
    public void setXpSharingMethod(String xpSharingMethod) { this.xpSharingMethod = xpSharingMethod; }

    public Map<String,Double> getXpSourceMultipliers() { return xpSourceMultipliers; }
    public void setXpSourceMultipliers(Map<String,Double> xpSourceMultipliers) {
        this.xpSourceMultipliers = xpSourceMultipliers != null ? xpSourceMultipliers : new HashMap<>();
    }
    public void setXPSourceMultiplier(String source, double multiplier) { xpSourceMultipliers.put(source, multiplier); }
    public void removeXPSourceMultiplier(String source) { xpSourceMultipliers.remove(source); }

    public boolean isUsesXPPenalties() { return usesXPPenalties; }
    public void setUsesXPPenalties(boolean usesXPPenalties) { this.usesXPPenalties = usesXPPenalties; }

    public Map<String,Integer> getXpPenaltyConditions() { return xpPenaltyConditions; }
    public void setXpPenaltyConditions(Map<String,Integer> xpPenaltyConditions) {
        this.xpPenaltyConditions = xpPenaltyConditions != null ? xpPenaltyConditions : new HashMap<>();
    }
    public void setXPPenaltyCondition(String condition, int penalty) { xpPenaltyConditions.put(condition, penalty); }
    public void removeXPPenaltyCondition(String condition) { xpPenaltyConditions.remove(condition); }

    public boolean isAllowsNegativeXP() { return allowsNegativeXP; }
    public void setAllowsNegativeXP(boolean allowsNegativeXP) { this.allowsNegativeXP = allowsNegativeXP; }

    public int getMinimumXP() { return minimumXP; }
    public void setMinimumXP(int minimumXP) { this.minimumXP = minimumXP; }

    // === ALTERNATIVE ADVANCEMENT GETTERS/SETTERS ===
    public String getAlternativeAdvancement() { return alternativeAdvancement; }
    public void setAlternativeAdvancement(String alternativeAdvancement) {
        this.alternativeAdvancement = alternativeAdvancement;
    }

    public boolean isUsesMilestoneAdvancement() { return usesMilestoneAdvancement; }
    public void setUsesMilestoneAdvancement(boolean usesMilestoneAdvancement) {
        this.usesMilestoneAdvancement = usesMilestoneAdvancement;
    }

    public Map<String,Integer> getMilestoneXPValues() { return milestoneXPValues; }
    public void setMilestoneXPValues(Map<String,Integer> milestoneXPValues) {
        this.milestoneXPValues = milestoneXPValues != null ? milestoneXPValues : new HashMap<>();
    }
    public void setMilestoneXPValue(String type, int xp) { milestoneXPValues.put(type, xp); }
    public void removeMilestoneXPValue(String type) { milestoneXPValues.remove(type); }

    public boolean isMilestonesReplaceXP() { return milestonesReplaceXP; }
    public void setMilestonesReplaceXP(boolean milestonesReplaceXP) { this.milestonesReplaceXP = milestonesReplaceXP; }

    public boolean isUsesSessionBasedAdvancement() { return usesSessionBasedAdvancement; }
    public void setUsesSessionBasedAdvancement(boolean usesSessionBasedAdvancement) {
        this.usesSessionBasedAdvancement = usesSessionBasedAdvancement;
    }

    public int getSessionsPerLevel() { return sessionsPerLevel; }
    public void setSessionsPerLevel(int sessionsPerLevel) { this.sessionsPerLevel = sessionsPerLevel; }

    public Map<Integer,Integer> getSessionsByLevel() { return sessionsByLevel; }
    public void setSessionsByLevel(Map<Integer,Integer> sessionsByLevel) {
        this.sessionsByLevel = sessionsByLevel != null ? sessionsByLevel : new HashMap<>();
    }
    public void setSessionsForLevel(int level, int sessions) { sessionsByLevel.put(level, sessions); }
    public void removeSessionsForLevel(int level) { sessionsByLevel.remove(level); }

    public boolean isSessionsVaryByLevel() { return sessionsVaryByLevel; }
    public void setSessionsVaryByLevel(boolean sessionsVaryByLevel) { this.sessionsVaryByLevel = sessionsVaryByLevel; }

    public boolean isUsesStoryAdvancement() { return usesStoryAdvancement; }
    public void setUsesStoryAdvancement(boolean usesStoryAdvancement) {
        this.usesStoryAdvancement = usesStoryAdvancement;
    }

    public Map<String,Integer> getStoryMilestoneLevels() { return storyMilestoneLevels; }
    public void setStoryMilestoneLevels(Map<String,Integer> storyMilestoneLevels) {
        this.storyMilestoneLevels = storyMilestoneLevels != null ? storyMilestoneLevels : new HashMap<>();
    }
    public void setStoryMilestoneLevel(String milestone, int level) { storyMilestoneLevels.put(milestone, level); }
    public void removeStoryMilestoneLevel(String milestone) { storyMilestoneLevels.remove(milestone); }

    // === LEVEL CAPS GETTERS/SETTERS ===
    public int getMaximumLevel() { return maximumLevel; }
    public void setMaximumLevel(int maximumLevel) { this.maximumLevel = maximumLevel; }

    public boolean isHasLevelCap() { return hasLevelCap; }
    public void setHasLevelCap(boolean hasLevelCap) { this.hasLevelCap = hasLevelCap; }

    public boolean isAllowsEpicLevels() { return allowsEpicLevels; }
    public void setAllowsEpicLevels(boolean allowsEpicLevels) { this.allowsEpicLevels = allowsEpicLevels; }

    public int getEpicLevelStart() { return epicLevelStart; }
    public void setEpicLevelStart(int epicLevelStart) { this.epicLevelStart = epicLevelStart; }

    public String getEpicProgressionType() { return epicProgressionType; }
    public void setEpicProgressionType(String epicProgressionType) { this.epicProgressionType = epicProgressionType; }

    public boolean isUsesSoftCaps() { return usesSoftCaps; }
    public void setUsesSoftCaps(boolean usesSoftCaps) { this.usesSoftCaps = usesSoftCaps; }

    public Map<Integer,Double> getSoftCapMultipliers() { return softCapMultipliers; }
    public void setSoftCapMultipliers(Map<Integer,Double> softCapMultipliers) {
        this.softCapMultipliers = softCapMultipliers != null ? softCapMultipliers : new HashMap<>();
    }
    public void setSoftCapMultiplier(int level, double multiplier) { softCapMultipliers.put(level, multiplier); }
    public void removeSoftCapMultiplier(int level) { softCapMultipliers.remove(level); }

    public Map<String,Integer> getClassLevelCaps() { return classLevelCaps; }
    public void setClassLevelCaps(Map<String,Integer> classLevelCaps) {
        this.classLevelCaps = classLevelCaps != null ? classLevelCaps : new HashMap<>();
    }
    public void setClassLevelCap(String className, int cap) { classLevelCaps.put(className, cap); }
    public void removeClassLevelCap(String className) { classLevelCaps.remove(className); }

    public Map<String,Integer> getRacialLevelAdjustment() { return racialLevelAdjustment; }
    public void setRacialLevelAdjustment(Map<String,Integer> racialLevelAdjustment) {
        this.racialLevelAdjustment = racialLevelAdjustment != null ? racialLevelAdjustment : new HashMap<>();
    }
    public void setRacialLA(String race, int la) { racialLevelAdjustment.put(race, la); }
    public void removeRacialLA(String race) { racialLevelAdjustment.remove(race); }

    // === MULTICLASS GETTERS/SETTERS ===
    public boolean isAllowsMulticlassing() { return allowsMulticlassing; }
    public void setAllowsMulticlassing(boolean allowsMulticlassing) { this.allowsMulticlassing = allowsMulticlassing; }

    public int getMinimumLevelForMulticlass() { return minimumLevelForMulticlass; }
    public void setMinimumLevelForMulticlass(int minimumLevelForMulticlass) {
        this.minimumLevelForMulticlass = minimumLevelForMulticlass;
    }

    public Map<String,Integer> getMulticlassRequirements() { return multiclassRequirements; }
    public void setMulticlassRequirements(Map<String,Integer> multiclassRequirements) {
        this.multiclassRequirements = multiclassRequirements != null ? multiclassRequirements : new HashMap<>();
    }
    public void setMulticlassRequirement(String attribute, int minimum) {
        multiclassRequirements.put(attribute, minimum);
    }
    public void removeMulticlassRequirement(String attribute) { multiclassRequirements.remove(attribute); }

    public boolean isUsesMulticlassXPPenalty() { return usesMulticlassXPPenalty; }
    public void setUsesMulticlassXPPenalty(boolean usesMulticlassXPPenalty) {
        this.usesMulticlassXPPenalty = usesMulticlassXPPenalty;
    }

    public int getXpPenaltyThreshold() { return xpPenaltyThreshold; }
    public void setXpPenaltyThreshold(int xpPenaltyThreshold) { this.xpPenaltyThreshold = xpPenaltyThreshold; }

    public int getXpPenaltyAmount() { return xpPenaltyAmount; }
    public void setXpPenaltyAmount(int xpPenaltyAmount) { this.xpPenaltyAmount = xpPenaltyAmount; }

    public String getXpPenaltyCalculation() { return xpPenaltyCalculation; }
    public void setXpPenaltyCalculation(String xpPenaltyCalculation) {
        this.xpPenaltyCalculation = xpPenaltyCalculation;
    }

    public boolean isUsesFavoredClass() { return usesFavoredClass; }
    public void setUsesFavoredClass(boolean usesFavoredClass) { this.usesFavoredClass = usesFavoredClass; }

    public Map<String,String> getRaceFavoredClass() { return raceFavoredClass; }
    public void setRaceFavoredClass(Map<String,String> raceFavoredClass) {
        this.raceFavoredClass = raceFavoredClass != null ? raceFavoredClass : new HashMap<>();
    }
    public void setFavoredClass(String race, String className) { raceFavoredClass.put(race, className); }
    public void removeFavoredClass(String race) { raceFavoredClass.remove(race); }

    public boolean isFavoredClassNegatesPenalty() { return favoredClassNegatesPenalty; }
    public void setFavoredClassNegatesPenalty(boolean favoredClassNegatesPenalty) {
        this.favoredClassNegatesPenalty = favoredClassNegatesPenalty;
    }

    public boolean isAllowsMultipleFavoredClasses() { return allowsMultipleFavoredClasses; }
    public void setAllowsMultipleFavoredClasses(boolean allowsMultipleFavoredClasses) {
        this.allowsMultipleFavoredClasses = allowsMultipleFavoredClasses;
    }

    public String getStackingMethod() { return stackingMethod; }
    public void setStackingMethod(String stackingMethod) { this.stackingMethod = stackingMethod; }

    public Map<String,String> getProgressionStackingRules() { return progressionStackingRules; }
    public void setProgressionStackingRules(Map<String,String> progressionStackingRules) {
        this.progressionStackingRules = progressionStackingRules != null ? progressionStackingRules : new HashMap<>();
    }
    public void setProgressionStackingRule(String feature, String rule) { progressionStackingRules.put(feature, rule); }
    public void removeProgressionStackingRule(String feature) { progressionStackingRules.remove(feature); }

    // === PRESTIGE CLASS GETTERS/SETTERS ===
    public boolean isAllowsPrestigeClasses() { return allowsPrestigeClasses; }
    public void setAllowsPrestigeClasses(boolean allowsPrestigeClasses) {
        this.allowsPrestigeClasses = allowsPrestigeClasses;
    }

    public int getMinimumLevelForPrestige() { return minimumLevelForPrestige; }
    public void setMinimumLevelForPrestige(int minimumLevelForPrestige) {
        this.minimumLevelForPrestige = minimumLevelForPrestige;
    }

    public Map<String,Map<String,Object>> getPrestigeRequirements() { return prestigeRequirements; }
    public void setPrestigeRequirements(Map<String,Map<String,Object>> prestigeRequirements) {
        this.prestigeRequirements = prestigeRequirements != null ? prestigeRequirements : new HashMap<>();
    }
    public void setPrestigeRequirement(String className, Map<String,Object> requirements) {
        prestigeRequirements.put(className, requirements);
    }
    public void removePrestigeRequirement(String className) { prestigeRequirements.remove(className); }

    public boolean isPrestigeRequiresFeats() { return prestigeRequiresFeats; }
    public void setPrestigeRequiresFeats(boolean prestigeRequiresFeats) {
        this.prestigeRequiresFeats = prestigeRequiresFeats;
    }

    public boolean isPrestigeRequiresSkills() { return prestigeRequiresSkills; }
    public void setPrestigeRequiresSkills(boolean prestigeRequiresSkills) {
        this.prestigeRequiresSkills = prestigeRequiresSkills;
    }

    public boolean isPrestigeRequiresAbilityScores() { return prestigeRequiresAbilityScores; }
    public void setPrestigeRequiresAbilityScores(boolean prestigeRequiresAbilityScores) {
        this.prestigeRequiresAbilityScores = prestigeRequiresAbilityScores;
    }

    public Map<String,Integer> getPrestigeClassLevels() { return prestigeClassLevels; }
    public void setPrestigeClassLevels(Map<String,Integer> prestigeClassLevels) {
        this.prestigeClassLevels = prestigeClassLevels != null ? prestigeClassLevels : new HashMap<>();
    }
    public void setPrestigeClassLevel(String className, int maxLevel) { prestigeClassLevels.put(className, maxLevel); }
    public void removePrestigeClassLevel(String className) { prestigeClassLevels.remove(className); }

    public String getPrestigeStackingMethod() { return prestigeStackingMethod; }
    public void setPrestigeStackingMethod(String prestigeStackingMethod) {
        this.prestigeStackingMethod = prestigeStackingMethod;
    }

    // === LEVEL ADJUSTMENT & ECL GETTERS/SETTERS ===
    public boolean isUsesLevelAdjustment() { return usesLevelAdjustment; }
    public void setUsesLevelAdjustment(boolean usesLevelAdjustment) {
        this.usesLevelAdjustment = usesLevelAdjustment;
    }

    public boolean isUsesEffectiveCharacterLevel() { return usesEffectiveCharacterLevel; }
    public void setUsesEffectiveCharacterLevel(boolean usesEffectiveCharacterLevel) {
        this.usesEffectiveCharacterLevel = usesEffectiveCharacterLevel;
    }

    public Map<String,Integer> getTemplateLevelAdjustment() { return templateLevelAdjustment; }
    public void setTemplateLevelAdjustment(Map<String,Integer> templateLevelAdjustment) {
        this.templateLevelAdjustment = templateLevelAdjustment != null ? templateLevelAdjustment : new HashMap<>();
    }
    public void setTemplateLA(String template, int la) { templateLevelAdjustment.put(template, la); }
    public void removeTemplateLA(String template) { templateLevelAdjustment.remove(template); }

    public String getEclCalculation() { return eclCalculation; }
    public void setEclCalculation(String eclCalculation) { this.eclCalculation = eclCalculation; }

    public int getMaximumLevelAdjustment() { return maximumLevelAdjustment; }
    public void setMaximumLevelAdjustment(int maximumLevelAdjustment) {
        this.maximumLevelAdjustment = maximumLevelAdjustment;
    }

    public boolean isAllowsLABuyoff() { return allowsLABuyoff; }
    public void setAllowsLABuyoff(boolean allowsLABuyoff) { this.allowsLABuyoff = allowsLABuyoff; }

    public Map<Integer,Integer> getLaBuyoffXPCost() { return laBuyoffXPCost; }
    public void setLaBuyoffXPCost(Map<Integer,Integer> laBuyoffXPCost) {
        this.laBuyoffXPCost = laBuyoffXPCost != null ? laBuyoffXPCost : new HashMap<>();
    }
    public void setLABuyoffCost(int laValue, int xpCost) { laBuyoffXPCost.put(laValue, xpCost); }
    public void removeLABuyoffCost(int laValue) { laBuyoffXPCost.remove(laValue); }

    public int getMinimumLevelForBuyoff() { return minimumLevelForBuyoff; }
    public void setMinimumLevelForBuyoff(int minimumLevelForBuyoff) {
        this.minimumLevelForBuyoff = minimumLevelForBuyoff;
    }

    // === LEVEL-UP TIMING GETTERS/SETTERS ===
    public String getLevelUpTiming() { return levelUpTiming; }
    public void setLevelUpTiming(String levelUpTiming) { this.levelUpTiming = levelUpTiming; }

    public boolean isRequiresRest() { return requiresRest; }
    public void setRequiresRest(boolean requiresRest) { this.requiresRest = requiresRest; }

    public String getRestTypeRequired() { return restTypeRequired; }
    public void setRestTypeRequired(String restTypeRequired) { this.restTypeRequired = restTypeRequired; }

    public boolean isRequiresTraining() { return requiresTraining; }
    public void setRequiresTraining(boolean requiresTraining) { this.requiresTraining = requiresTraining; }

    public int getTrainingDaysPerLevel() { return trainingDaysPerLevel; }
    public void setTrainingDaysPerLevel(int trainingDaysPerLevel) { this.trainingDaysPerLevel = trainingDaysPerLevel; }

    public String getTrainingCostFormula() { return trainingCostFormula; }
    public void setTrainingCostFormula(String trainingCostFormula) { this.trainingCostFormula = trainingCostFormula; }

    public boolean isTrainingRequiresTrainer() { return trainingRequiresTrainer; }
    public void setTrainingRequiresTrainer(boolean trainingRequiresTrainer) {
        this.trainingRequiresTrainer = trainingRequiresTrainer;
    }

    public Map<Integer,Integer> getTrainingTimeByLevel() { return trainingTimeByLevel; }
    public void setTrainingTimeByLevel(Map<Integer,Integer> trainingTimeByLevel) {
        this.trainingTimeByLevel = trainingTimeByLevel != null ? trainingTimeByLevel : new HashMap<>();
    }
    public void setTrainingTime(int level, int days) { trainingTimeByLevel.put(level, days); }
    public void removeTrainingTime(int level) { trainingTimeByLevel.remove(level); }

    public boolean isHasLevelUpCost() { return hasLevelUpCost; }
    public void setHasLevelUpCost(boolean hasLevelUpCost) { this.hasLevelUpCost = hasLevelUpCost; }

    public String getCostCalculation() { return costCalculation; }
    public void setCostCalculation(String costCalculation) { this.costCalculation = costCalculation; }

    public int getFixedLevelUpCost() { return fixedLevelUpCost; }
    public void setFixedLevelUpCost(int fixedLevelUpCost) { this.fixedLevelUpCost = fixedLevelUpCost; }

    public Map<String,String> getClassLevelUpCosts() { return classLevelUpCosts; }
    public void setClassLevelUpCosts(Map<String,String> classLevelUpCosts) {
        this.classLevelUpCosts = classLevelUpCosts != null ? classLevelUpCosts : new HashMap<>();
    }
    public void setClassLevelUpCost(String className, String formula) { classLevelUpCosts.put(className, formula); }
    public void removeClassLevelUpCost(String className) { classLevelUpCosts.remove(className); }

    public boolean isRequiresDowntime() { return requiresDowntime; }
    public void setRequiresDowntime(boolean requiresDowntime) { this.requiresDowntime = requiresDowntime; }

    public int getDowntimeDaysRequired() { return downtimeDaysRequired; }
    public void setDowntimeDaysRequired(int downtimeDaysRequired) { this.downtimeDaysRequired = downtimeDaysRequired; }

    // === ABILITY SCORE INCREASE GETTERS/SETTERS ===
    public boolean isGrantsAbilityIncreases() { return grantsAbilityIncreases; }
    public void setGrantsAbilityIncreases(boolean grantsAbilityIncreases) {
        this.grantsAbilityIncreases = grantsAbilityIncreases;
    }

    public int getAbilityIncreaseFrequency() { return abilityIncreaseFrequency; }
    public void setAbilityIncreaseFrequency(int abilityIncreaseFrequency) {
        this.abilityIncreaseFrequency = abilityIncreaseFrequency;
    }

    public int getAbilityPointsPerIncrease() { return abilityPointsPerIncrease; }
    public void setAbilityPointsPerIncrease(int abilityPointsPerIncrease) {
        this.abilityPointsPerIncrease = abilityPointsPerIncrease;
    }

    public int getMaxIncreasePerAttribute() { return maxIncreasePerAttribute; }
    public void setMaxIncreasePerAttribute(int maxIncreasePerAttribute) {
        this.maxIncreasePerAttribute = maxIncreasePerAttribute;
    }

    public String getAbilityIncreaseMethod() { return abilityIncreaseMethod; }
    public void setAbilityIncreaseMethod(String abilityIncreaseMethod) {
        this.abilityIncreaseMethod = abilityIncreaseMethod;
    }

    public boolean isAllowsAttributeSwapping() { return allowsAttributeSwapping; }
    public void setAllowsAttributeSwapping(boolean allowsAttributeSwapping) {
        this.allowsAttributeSwapping = allowsAttributeSwapping;
    }

    public boolean isAbilityIncreasesCumulative() { return abilityIncreasesCumulative; }
    public void setAbilityIncreasesCumulative(boolean abilityIncreasesCumulative) {
        this.abilityIncreasesCumulative = abilityIncreasesCumulative;
    }

    public int getMaximumAbilityScore() { return maximumAbilityScore; }
    public void setMaximumAbilityScore(int maximumAbilityScore) { this.maximumAbilityScore = maximumAbilityScore; }

    public Map<String,Integer> getClassAbilityProgression() { return classAbilityProgression; }
    public void setClassAbilityProgression(Map<String,Integer> classAbilityProgression) {
        this.classAbilityProgression = classAbilityProgression != null ? classAbilityProgression : new HashMap<>();
    }
    public void setClassAbilityFrequency(String className, int frequency) {
        classAbilityProgression.put(className, frequency);
    }
    public void removeClassAbilityFrequency(String className) { classAbilityProgression.remove(className); }

    public boolean isUsesFlexibleIncreases() { return usesFlexibleIncreases; }
    public void setUsesFlexibleIncreases(boolean usesFlexibleIncreases) {
        this.usesFlexibleIncreases = usesFlexibleIncreases;
    }

    public Map<Integer,String> getLevelBonusTypes() { return levelBonusTypes; }
    public void setLevelBonusTypes(Map<Integer,String> levelBonusTypes) {
        this.levelBonusTypes = levelBonusTypes != null ? levelBonusTypes : new HashMap<>();
    }
    public void setLevelBonusType(int level, String type) { levelBonusTypes.put(level, type); }
    public void removeLevelBonusType(int level) { levelBonusTypes.remove(level); }

    // === DELEVELING GETTERS/SETTERS ===
    public boolean isAllowsDeleveling() { return allowsDeleveling; }
    public void setAllowsDeleveling(boolean allowsDeleveling) { this.allowsDeleveling = allowsDeleveling; }

    public String getDelevelMethod() { return delevelMethod; }
    public void setDelevelMethod(String delevelMethod) { this.delevelMethod = delevelMethod; }

    public boolean isUsesLevelDrain() { return usesLevelDrain; }
    public void setUsesLevelDrain(boolean usesLevelDrain) { this.usesLevelDrain = usesLevelDrain; }

    public boolean isLevelDrainTemporary() { return levelDrainTemporary; }
    public void setLevelDrainTemporary(boolean levelDrainTemporary) { this.levelDrainTemporary = levelDrainTemporary; }

    public String getLevelDrainRestoration() { return levelDrainRestoration; }
    public void setLevelDrainRestoration(String levelDrainRestoration) {
        this.levelDrainRestoration = levelDrainRestoration;
    }

    public int getDaysToRestoreDrainedLevel() { return daysToRestoreDrainedLevel; }
    public void setDaysToRestoreDrainedLevel(int daysToRestoreDrainedLevel) {
        this.daysToRestoreDrainedLevel = daysToRestoreDrainedLevel;
    }

    public boolean isAllowsXPLoss() { return allowsXPLoss; }
    public void setAllowsXPLoss(boolean allowsXPLoss) { this.allowsXPLoss = allowsXPLoss; }

    public String getXpLossCalculation() { return xpLossCalculation; }
    public void setXpLossCalculation(String xpLossCalculation) { this.xpLossCalculation = xpLossCalculation; }

    public int getXpLossPercentage() { return xpLossPercentage; }
    public void setXpLossPercentage(int xpLossPercentage) { this.xpLossPercentage = xpLossPercentage; }

    public int getFixedXPLoss() { return fixedXPLoss; }
    public void setFixedXPLoss(int fixedXPLoss) { this.fixedXPLoss = fixedXPLoss; }

    public boolean isXpLossCanDelevel() { return xpLossCanDelevel; }
    public void setXpLossCanDelevel(boolean xpLossCanDelevel) { this.xpLossCanDelevel = xpLossCanDelevel; }

    public boolean isDeathCausesXPLoss() { return deathCausesXPLoss; }
    public void setDeathCausesXPLoss(boolean deathCausesXPLoss) { this.deathCausesXPLoss = deathCausesXPLoss; }

    public int getDeathXPPenalty() { return deathXPPenalty; }
    public void setDeathXPPenalty(int deathXPPenalty) { this.deathXPPenalty = deathXPPenalty; }

    public boolean isResurrectionRestoresXP() { return resurrectionRestoresXP; }
    public void setResurrectionRestoresXP(boolean resurrectionRestoresXP) {
        this.resurrectionRestoresXP = resurrectionRestoresXP;
    }

    // === GESTALT GETTERS/SETTERS ===
    public boolean isAllowsGestalt() { return allowsGestalt; }
    public void setAllowsGestalt(boolean allowsGestalt) { this.allowsGestalt = allowsGestalt; }

    public String getGestaltMethod() { return gestaltMethod; }
    public void setGestaltMethod(String gestaltMethod) { this.gestaltMethod = gestaltMethod; }

    public int getMaxGestaltClasses() { return maxGestaltClasses; }
    public void setMaxGestaltClasses(int maxGestaltClasses) { this.maxGestaltClasses = maxGestaltClasses; }

    public String getGestaltHPMethod() { return gestaltHPMethod; }
    public void setGestaltHPMethod(String gestaltHPMethod) { this.gestaltHPMethod = gestaltHPMethod; }

    public String getGestaltSaveMethod() { return gestaltSaveMethod; }
    public void setGestaltSaveMethod(String gestaltSaveMethod) { this.gestaltSaveMethod = gestaltSaveMethod; }

    public String getGestaltBABMethod() { return gestaltBABMethod; }
    public void setGestaltBABMethod(String gestaltBABMethod) { this.gestaltBABMethod = gestaltBABMethod; }

    // === E6/E8 GETTERS/SETTERS ===
    public boolean isUsesE6Rules() { return usesE6Rules; }
    public void setUsesE6Rules(boolean usesE6Rules) { this.usesE6Rules = usesE6Rules; }

    public boolean isUsesE8Rules() { return usesE8Rules; }
    public void setUsesE8Rules(boolean usesE8Rules) { this.usesE8Rules = usesE8Rules; }

    public int getVariantLevelCap() { return variantLevelCap; }
    public void setVariantLevelCap(int variantLevelCap) { this.variantLevelCap = variantLevelCap; }

    public String getPostCapAdvancement() { return postCapAdvancement; }
    public void setPostCapAdvancement(String postCapAdvancement) { this.postCapAdvancement = postCapAdvancement; }

    public int getXpPerPostCapBonus() { return xpPerPostCapBonus; }
    public void setXpPerPostCapBonus(int xpPerPostCapBonus) { this.xpPerPostCapBonus = xpPerPostCapBonus; }

    public Map<String,Integer> getPostCapBonusCosts() { return postCapBonusCosts; }
    public void setPostCapBonusCosts(Map<String,Integer> postCapBonusCosts) {
        this.postCapBonusCosts = postCapBonusCosts != null ? postCapBonusCosts : new HashMap<>();
    }
    public void setPostCapBonusCost(String bonus, int xpCost) { postCapBonusCosts.put(bonus, xpCost); }
    public void removePostCapBonusCost(String bonus) { postCapBonusCosts.remove(bonus); }

    // === CLASS PROGRESSION GETTERS/SETTERS ===
    public boolean isAllowsSubclassSelection() { return allowsSubclassSelection; }
    public void setAllowsSubclassSelection(boolean allowsSubclassSelection) {
        this.allowsSubclassSelection = allowsSubclassSelection;
    }

    public Map<String,Integer> getSubclassSelectionLevel() { return subclassSelectionLevel; }
    public void setSubclassSelectionLevel(Map<String,Integer> subclassSelectionLevel) {
        this.subclassSelectionLevel = subclassSelectionLevel != null ? subclassSelectionLevel : new HashMap<>();
    }
    public void setSubclassLevel(String className, int level) { subclassSelectionLevel.put(className, level); }
    public void removeSubclassLevel(String className) { subclassSelectionLevel.remove(className); }

    public boolean isSubclassIsPermament() { return subclassIsPermament; }
    public void setSubclassIsPermament(boolean subclassIsPermament) { this.subclassIsPermament = subclassIsPermament; }

    public boolean isAllowsArchetypes() { return allowsArchetypes; }
    public void setAllowsArchetypes(boolean allowsArchetypes) { this.allowsArchetypes = allowsArchetypes; }

    public String getArchetypeSelectionTiming() { return archetypeSelectionTiming; }
    public void setArchetypeSelectionTiming(String archetypeSelectionTiming) {
        this.archetypeSelectionTiming = archetypeSelectionTiming;
    }

    public boolean isAllowsMultipleArchetypes() { return allowsMultipleArchetypes; }
    public void setAllowsMultipleArchetypes(boolean allowsMultipleArchetypes) {
        this.allowsMultipleArchetypes = allowsMultipleArchetypes;
    }

    public Map<String,ArrayList<String>> getArchetypeConflicts() { return archetypeConflicts; }
    public void setArchetypeConflicts(Map<String,ArrayList<String>> archetypeConflicts) {
        this.archetypeConflicts = archetypeConflicts != null ? archetypeConflicts : new HashMap<>();
    }
    public void setArchetypeConflict(String archetype, ArrayList<String> conflicts) {
        archetypeConflicts.put(archetype, conflicts);
    }
    public void removeArchetypeConflict(String archetype) { archetypeConflicts.remove(archetype); }

    public boolean isAllowsRetraining() { return allowsRetraining; }
    public void setAllowsRetraining(boolean allowsRetraining) { this.allowsRetraining = allowsRetraining; }

    public String getRetrainingCostFormula() { return retrainingCostFormula; }
    public void setRetrainingCostFormula(String retrainingCostFormula) {
        this.retrainingCostFormula = retrainingCostFormula;
    }

    public int getRetrainingDaysPerChoice() { return retrainingDaysPerChoice; }
    public void setRetrainingDaysPerChoice(int retrainingDaysPerChoice) {
        this.retrainingDaysPerChoice = retrainingDaysPerChoice;
    }

    public boolean isRetrainingRequiresDowntime() { return retrainingRequiresDowntime; }
    public void setRetrainingRequiresDowntime(boolean retrainingRequiresDowntime) {
        this.retrainingRequiresDowntime = retrainingRequiresDowntime;
    }

    public boolean isAllowsFullRespec() { return allowsFullRespec; }
    public void setAllowsFullRespec(boolean allowsFullRespec) { this.allowsFullRespec = allowsFullRespec; }

    public String getRespecCost() { return respecCost; }
    public void setRespecCost(String respecCost) { this.respecCost = respecCost; }

    public int getRespecGoldCost() { return respecGoldCost; }
    public void setRespecGoldCost(int respecGoldCost) { this.respecGoldCost = respecGoldCost; }

    // === VARIANT PROGRESSION GETTERS/SETTERS ===
    public Map<String,Object> getVariantRules() { return variantRules; }
    public void setVariantRules(Map<String,Object> variantRules) {
        this.variantRules = variantRules != null ? variantRules : new HashMap<>();
    }
    public void setVariantRule(String rule, Object value) { variantRules.put(rule, value); }
    public void removeVariantRule(String rule) { variantRules.remove(rule); }

    public boolean isUsesAcceleratedProgression() { return usesAcceleratedProgression; }
    public void setUsesAcceleratedProgression(boolean usesAcceleratedProgression) {
        this.usesAcceleratedProgression = usesAcceleratedProgression;
    }

    public double getAccelerationMultiplier() { return accelerationMultiplier; }
    public void setAccelerationMultiplier(double accelerationMultiplier) {
        this.accelerationMultiplier = accelerationMultiplier;
    }

    public boolean isUsesSlowProgression() { return usesSlowProgression; }
    public void setUsesSlowProgression(boolean usesSlowProgression) { this.usesSlowProgression = usesSlowProgression; }

    public double getSlowProgressionMultiplier() { return slowProgressionMultiplier; }
    public void setSlowProgressionMultiplier(double slowProgressionMultiplier) {
        this.slowProgressionMultiplier = slowProgressionMultiplier;
    }

    public String getSlowProgressionReason() { return slowProgressionReason; }
    public void setSlowProgressionReason(String slowProgressionReason) {
        this.slowProgressionReason = slowProgressionReason;
    }

    public boolean isUsesCustomTracks() { return usesCustomTracks; }
    public void setUsesCustomTracks(boolean usesCustomTracks) { this.usesCustomTracks = usesCustomTracks; }

    public Map<String,Map<Integer,Integer>> getCustomProgressionTracks() { return customProgressionTracks; }
    public void setCustomProgressionTracks(Map<String,Map<Integer,Integer>> customProgressionTracks) {
        this.customProgressionTracks = customProgressionTracks != null ? customProgressionTracks : new HashMap<>();
    }
    public void setCustomProgressionTrack(String trackName, Map<Integer,Integer> levelXP) {
        customProgressionTracks.put(trackName, levelXP);
    }
    public void removeCustomProgressionTrack(String trackName) { customProgressionTracks.remove(trackName); }

    public Map<String,String> getCharacterTrackAssignment() { return characterTrackAssignment; }
    public void setCharacterTrackAssignment(Map<String,String> characterTrackAssignment) {
        this.characterTrackAssignment = characterTrackAssignment != null ? characterTrackAssignment : new HashMap<>();
    }
    public void assignCharacterToTrack(String characterId, String trackName) {
        characterTrackAssignment.put(characterId, trackName);
    }
    public void removeCharacterTrackAssignment(String characterId) { characterTrackAssignment.remove(characterId); }

    // === FEAT & CLASS FEATURE GETTERS/SETTERS ===
    public boolean isGrantsFeatProgression() { return grantsFeatProgression; }
    public void setGrantsFeatProgression(boolean grantsFeatProgression) {
        this.grantsFeatProgression = grantsFeatProgression;
    }

    public int getBaseFeatFrequency() { return baseFeatFrequency; }
    public void setBaseFeatFrequency(int baseFeatFrequency) { this.baseFeatFrequency = baseFeatFrequency; }

    public Map<String,Integer> getClassFeatProgression() { return classFeatProgression; }
    public void setClassFeatProgression(Map<String,Integer> classFeatProgression) {
        this.classFeatProgression = classFeatProgression != null ? classFeatProgression : new HashMap<>();
    }
    public void setClassFeatFrequency(String className, int frequency) { classFeatProgression.put(className, frequency); }
    public void removeClassFeatFrequency(String className) { classFeatProgression.remove(className); }

    public boolean isUsesDelayedClassFeatures() { return usesDelayedClassFeatures; }
    public void setUsesDelayedClassFeatures(boolean usesDelayedClassFeatures) {
        this.usesDelayedClassFeatures = usesDelayedClassFeatures;
    }

    public Map<String,Map<Integer,String>> getClassFeatureDelays() { return classFeatureDelays; }
    public void setClassFeatureDelays(Map<String,Map<Integer,String>> classFeatureDelays) {
        this.classFeatureDelays = classFeatureDelays != null ? classFeatureDelays : new HashMap<>();
    }
    public void setClassFeatureDelay(String className, Map<Integer,String> delays) {
        classFeatureDelays.put(className, delays);
    }
    public void removeClassFeatureDelay(String className) { classFeatureDelays.remove(className); }

    public boolean isAllowsClassFeatureSwapping() { return allowsClassFeatureSwapping; }
    public void setAllowsClassFeatureSwapping(boolean allowsClassFeatureSwapping) {
        this.allowsClassFeatureSwapping = allowsClassFeatureSwapping;
    }

    // === SKILL POINTS GETTERS/SETTERS ===
    public boolean isGrantsSkillPoints() { return grantsSkillPoints; }
    public void setGrantsSkillPoints(boolean grantsSkillPoints) { this.grantsSkillPoints = grantsSkillPoints; }

    public String getSkillPointProgression() { return skillPointProgression; }
    public void setSkillPointProgression(String skillPointProgression) {
        this.skillPointProgression = skillPointProgression;
    }

    public int getBaseSkillPointsPerLevel() { return baseSkillPointsPerLevel; }
    public void setBaseSkillPointsPerLevel(int baseSkillPointsPerLevel) {
        this.baseSkillPointsPerLevel = baseSkillPointsPerLevel;
    }

    public boolean isSkillPointsSameAllLevels() {
        if (skillPointsSameAllLevels) {
            return true;
        }
        return skillPointsByLevel == null || skillPointsByLevel.isEmpty();
    }
    public void setSkillPointsSameAllLevels(boolean skillPointsSameAllLevels) {
        this.skillPointsSameAllLevels = skillPointsSameAllLevels;
    }

    public Map<Integer,Integer> getSkillPointsByLevel() {
        Map<Integer, Integer> safe = skillPointsByLevel != null ? skillPointsByLevel : new LinkedHashMap<>();
        return new LinkedHashMap<>(safe);
    }
    public void setSkillPointsByLevel(Map<Integer,Integer> skillPointsByLevel) {
        this.skillPointsByLevel = skillPointsByLevel != null ? skillPointsByLevel : new LinkedHashMap<>();
    }
    public void setSkillPointsForLevel(int level, int points) {
        if (level <= 0) {
            return;
        }
        Map<Integer, Integer> safe = skillPointsByLevel != null ? skillPointsByLevel : new LinkedHashMap<>();
        safe.put(level, Math.max(0, points));
        skillPointsByLevel = safe;
    }
    public void removeSkillPointsForLevel(int level) {
        if (level <= 0) {
            return;
        }
        Map<Integer, Integer> safe = skillPointsByLevel != null ? skillPointsByLevel : new LinkedHashMap<>();
        safe.remove(level);
        skillPointsByLevel = safe;
    }
    public int getSkillPointsForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (skillPointsSameAllLevels) {
            return Math.max(0, baseSkillPointsPerLevel);
        }
        Map<Integer, Integer> safe = skillPointsByLevel != null ? skillPointsByLevel : new LinkedHashMap<>();
        Integer points = safe.get(level);
        return points == null ? 0 : Math.max(0, points);
    }

    public boolean isSkillPointsModifiedByInt() { return skillPointsModifiedByInt; }
    public void setSkillPointsModifiedByInt(boolean skillPointsModifiedByInt) {
        this.skillPointsModifiedByInt = skillPointsModifiedByInt;
    }

    public int getMinimumSkillPointsPerLevel() { return minimumSkillPointsPerLevel; }
    public void setMinimumSkillPointsPerLevel(int minimumSkillPointsPerLevel) {
        this.minimumSkillPointsPerLevel = minimumSkillPointsPerLevel;
    }

    public boolean isUsesSkillRankCaps() { return usesSkillRankCaps; }
    public void setUsesSkillRankCaps(boolean usesSkillRankCaps) { this.usesSkillRankCaps = usesSkillRankCaps; }

    public String getSkillRankCapFormula() { return skillRankCapFormula; }
    public void setSkillRankCapFormula(String skillRankCapFormula) { this.skillRankCapFormula = skillRankCapFormula; }

    public String getCrossClassRankCapFormula() { return crossClassRankCapFormula; }
    public void setCrossClassRankCapFormula(String crossClassRankCapFormula) {
        this.crossClassRankCapFormula = crossClassRankCapFormula;
    }

    public boolean isUsesConsolidatedSkills() { return usesConsolidatedSkills; }
    public void setUsesConsolidatedSkills(boolean usesConsolidatedSkills) {
        this.usesConsolidatedSkills = usesConsolidatedSkills;
    }

    // === WEALTH GETTERS/SETTERS ===
    public boolean isUsesWealthByLevel() { return usesWealthByLevel; }
    public void setUsesWealthByLevel(boolean usesWealthByLevel) { this.usesWealthByLevel = usesWealthByLevel; }

    public Map<Integer,Integer> getWealthProgression() { return wealthProgression; }
    public void setWealthProgression(Map<Integer,Integer> wealthProgression) {
        this.wealthProgression = wealthProgression != null ? wealthProgression : new HashMap<>();
    }
    public void setWealthForLevel(int level, int gold) { wealthProgression.put(level, gold); }
    public void removeWealthForLevel(int level) { wealthProgression.remove(level); }

    public String getWealthCalculation() { return wealthCalculation; }
    public void setWealthCalculation(String wealthCalculation) { this.wealthCalculation = wealthCalculation; }

    public boolean isWealthAffectedByClass() { return wealthAffectedByClass; }
    public void setWealthAffectedByClass(boolean wealthAffectedByClass) {
        this.wealthAffectedByClass = wealthAffectedByClass;
    }

    // === MYTHIC GETTERS/SETTERS ===
    public boolean isUsesMythicTiers() { return usesMythicTiers; }
    public void setUsesMythicTiers(boolean usesMythicTiers) { this.usesMythicTiers = usesMythicTiers; }

    public int getMaximumMythicTier() { return maximumMythicTier; }
    public void setMaximumMythicTier(int maximumMythicTier) { this.maximumMythicTier = maximumMythicTier; }

    public String getMythicProgressionMethod() { return mythicProgressionMethod; }
    public void setMythicProgressionMethod(String mythicProgressionMethod) {
        this.mythicProgressionMethod = mythicProgressionMethod;
    }

    public boolean isMythicIndependentOfLevel() { return mythicIndependentOfLevel; }
    public void setMythicIndependentOfLevel(boolean mythicIndependentOfLevel) {
        this.mythicIndependentOfLevel = mythicIndependentOfLevel;
    }

    // === CONFIGURATION FLAG GETTERS/SETTERS ===
    public boolean isUsesXPProgression() { return usesXPProgression; }
    public void setUsesXPProgression(boolean usesXPProgression) { this.usesXPProgression = usesXPProgression; }

    public boolean isUsesAlternativeProgression() { return usesAlternativeProgression; }
    public void setUsesAlternativeProgression(boolean usesAlternativeProgression) {
        this.usesAlternativeProgression = usesAlternativeProgression;
    }

    public boolean isUsesMulticlassRules() { return usesMulticlassRules; }
    public void setUsesMulticlassRules(boolean usesMulticlassRules) { this.usesMulticlassRules = usesMulticlassRules; }

    public boolean isUsesPrestigeClasses() { return usesPrestigeClasses; }
    public void setUsesPrestigeClasses(boolean usesPrestigeClasses) { this.usesPrestigeClasses = usesPrestigeClasses; }

    public boolean isUsesECLSystem() { return usesECLSystem; }
    public void setUsesECLSystem(boolean usesECLSystem) { this.usesECLSystem = usesECLSystem; }

    public boolean isUsesTrainingRules() { return usesTrainingRules; }
    public void setUsesTrainingRules(boolean usesTrainingRules) { this.usesTrainingRules = usesTrainingRules; }

    public boolean isUsesAbilityIncreases() { return usesAbilityIncreases; }
    public void setUsesAbilityIncreases(boolean usesAbilityIncreases) { this.usesAbilityIncreases = usesAbilityIncreases; }

    public boolean isUsesDelevelingRules() { return usesDelevelingRules; }
    public void setUsesDelevelingRules(boolean usesDelevelingRules) { this.usesDelevelingRules = usesDelevelingRules; }

    public boolean isUsesGestaltRules() { return usesGestaltRules; }
    public void setUsesGestaltRules(boolean usesGestaltRules) { this.usesGestaltRules = usesGestaltRules; }

    public boolean isUsesVariantCaps() { return usesVariantCaps; }
    public void setUsesVariantCaps(boolean usesVariantCaps) { this.usesVariantCaps = usesVariantCaps; }

    public boolean isUsesRetrainingRules() { return usesRetrainingRules; }
    public void setUsesRetrainingRules(boolean usesRetrainingRules) { this.usesRetrainingRules = usesRetrainingRules; }

    public boolean isUsesWealthProgression() { return usesWealthProgression; }
    public void setUsesWealthProgression(boolean usesWealthProgression) {
        this.usesWealthProgression = usesWealthProgression;
    }

    public boolean isUsesMythicRules() { return usesMythicRules; }
    public void setUsesMythicRules(boolean usesMythicRules) { this.usesMythicRules = usesMythicRules; }

    // === UTILITY METHODS ===

    /**
     * Get XP required for a specific level
     * @param level the level to check
     * @return XP required, or -1 if not found
     */
    public int getXPForLevel(int level) {
        return xpTable.getOrDefault(level, -1);
    }

    /**
     * Calculate XP for level using formula if table doesn't contain it
     * @param level the level to calculate for
     * @return calculated XP
     */
    public int calculateXPForLevel(int level) {
        if (xpTable.containsKey(level)) {
            return xpTable.get(level);
        }

        // Simple formula evaluation (this is a placeholder - real implementation would parse formula)
        return (int) (baseXP * Math.pow(level, 2) * xpMultiplier);
    }

    /**
     * Gets a human-readable description of the leveling system
     */
    public String getLevelingSystemDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Progression: ").append(xpSystemType);
        desc.append(", Max Level: ").append(maximumLevel);

        if (usesMilestoneAdvancement) {
            desc.append(" (milestone)");
        }
        if (usesSessionBasedAdvancement) {
            desc.append(" (session-based)");
        }
        if (allowsMulticlassing) {
            desc.append(", Multiclass");
        }
        if (allowsPrestigeClasses) {
            desc.append(", Prestige");
        }

        return desc.toString();
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validClassIds, Set<String> validRaceIds, Set<String> validAttributeIds) {
        int removedCount = 0;

        // Clean up classLevelCaps (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> classCapIter = classLevelCaps.entrySet().iterator();
        while (classCapIter.hasNext()) {
            Map.Entry<String, Integer> entry = classCapIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classCapIter.remove();
                removedCount++;
            }
        }

        // Clean up racialLevelAdjustment (keys are Race ids)
        Iterator<Map.Entry<String, Integer>> raceLAIter = racialLevelAdjustment.entrySet().iterator();
        while (raceLAIter.hasNext()) {
            Map.Entry<String, Integer> entry = raceLAIter.next();
            String raceId = entry.getKey();
            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceLAIter.remove();
                removedCount++;
            }
        }

        // Clean up multiclassRequirements (keys are Attribute ids)
        Iterator<Map.Entry<String, Integer>> multiReqIter = multiclassRequirements.entrySet().iterator();
        while (multiReqIter.hasNext()) {
            Map.Entry<String, Integer> entry = multiReqIter.next();
            String attributeId = entry.getKey();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                multiReqIter.remove();
                removedCount++;
            }
        }

        // Clean up raceFavoredClass (keys are Race ids, values are CharacterClass ids)
        Iterator<Map.Entry<String, String>> favoredIter = raceFavoredClass.entrySet().iterator();
        while (favoredIter.hasNext()) {
            Map.Entry<String, String> entry = favoredIter.next();
            String raceId = entry.getKey();
            String classId = entry.getValue();

            boolean raceExists = validRaceIds.contains(raceId);
            boolean classExists = validClassIds.contains(classId);

            if (!raceExists || !classExists) {
                favoredIter.remove();
                removedCount++;
            }
        }

        // Clean up classLevelUpCosts (keys are CharacterClass ids)
        Iterator<Map.Entry<String, String>> levelUpCostIter = classLevelUpCosts.entrySet().iterator();
        while (levelUpCostIter.hasNext()) {
            Map.Entry<String, String> entry = levelUpCostIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                levelUpCostIter.remove();
                removedCount++;
            }
        }

        // Clean up classAbilityProgression (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> classAbilityIter = classAbilityProgression.entrySet().iterator();
        while (classAbilityIter.hasNext()) {
            Map.Entry<String, Integer> entry = classAbilityIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classAbilityIter.remove();
                removedCount++;
            }
        }

        // Clean up subclassSelectionLevel (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> subclassIter = subclassSelectionLevel.entrySet().iterator();
        while (subclassIter.hasNext()) {
            Map.Entry<String, Integer> entry = subclassIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                subclassIter.remove();
                removedCount++;
            }
        }

        // Clean up classFeatProgression (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> classFeatIter = classFeatProgression.entrySet().iterator();
        while (classFeatIter.hasNext()) {
            Map.Entry<String, Integer> entry = classFeatIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classFeatIter.remove();
                removedCount++;
            }
        }

        // Clean up prestigeRequirements (outer keys are CharacterClass ids)
        Iterator<Map.Entry<String, Map<String, Object>>> prestigeIter = prestigeRequirements.entrySet().iterator();
        while (prestigeIter.hasNext()) {
            Map.Entry<String, Map<String, Object>> entry = prestigeIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                prestigeIter.remove();
                removedCount++;
            }
        }

        // Clean up prestigeClassLevels (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> prestigeLevelIter = prestigeClassLevels.entrySet().iterator();
        while (prestigeLevelIter.hasNext()) {
            Map.Entry<String, Integer> entry = prestigeLevelIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                prestigeLevelIter.remove();
                removedCount++;
            }
        }

        // Clean up classFeatureDelays (outer keys are CharacterClass ids)
        Iterator<Map.Entry<String, Map<Integer, String>>> delayIter = classFeatureDelays.entrySet().iterator();
        while (delayIter.hasNext()) {
            Map.Entry<String, Map<Integer, String>> entry = delayIter.next();
            String classId = entry.getKey();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                delayIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    /**
     * Validates that the leveling method configuration is internally consistent
     */

}
