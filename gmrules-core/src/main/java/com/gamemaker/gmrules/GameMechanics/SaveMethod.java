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

import com.gamemaker.gmrules.GameElements.*;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.util.*;

/**
 * Comprehensive saving throw system supporting various RPG mechanics.
 * Handles different save types, calculation methods, dice systems, and success conditions.
 * Designed for mechanical implementation by descendant applications.
 */
public class SaveMethod extends GameElement {

// *** MEMBERS ***
    // === SAVE TYPE SYSTEM ===
    private String saveOrganization = "grouped"; // "individual", "grouped", "skillBased", "hybrid"
    private Map<String,String> saveCategories = new HashMap<>(); // Save -> Category mapping

    // === GENERIC ARRAY REGISTRY ===
    private ArrayHandler arrayHandler = new ArrayHandler();

    // === BASE VALUE CALCULATION ===
    private String baseCalculationMethod = "classBased"; // "classBased", "attributeBased", "levelBased", "flatValue", "formula"
    private Map<String,String> saveToAttributeMap = new HashMap<>(); // "Fortitude" -> "Constitution"
    private Map<String,Integer> flatSaveValues = new HashMap<>(); // Save -> Base Value
    private Map<String,String> saveFormulas = new HashMap<>(); // Save -> Formula like "2+level/2+con_mod"

    // Class-based progression
    private Map<String,String> classProgressionType = new HashMap<>(); // Save -> "good", "poor", "custom"
    private Map<String,Map<Integer,Integer>> customProgressionTables = new HashMap<>(); // Save -> (Level -> Value)

    // === DICE SYSTEM ===
    private String diceSystem = "d20"; // "d20", "dicePool", "percentile", "2d6", "custom"
    private int diceCount = 1;
    private int diceSides = 20;
    private int diceModifier = 0;
    private boolean explodingDice = false;
    private int explodeThreshold = 0;

    // Dice pool specific
    private int poolTargetNumber = 7;
    private int poolSuccessThreshold = 1;
    private boolean poolCountSuccesses = true;

    // Percentile specific
    private boolean rollUnder = true; // true = roll under target, false = roll over

    // === SUCCESS MECHANICS ===
    private String successMethod = "beatDC"; // "beatDC", "degreesOfSuccess", "opposedRoll", "rollUnder"
    private int baseDifficultyClass = 15;
    private Map<String,Integer> difficultyByType = new HashMap<>(); // Save type -> base DC
    private boolean usesCriticalSuccess = false;
    private boolean usesCriticalFailure = false;
    private int criticalSuccessThreshold = 20;
    private int criticalFailureThreshold = 1;

    // Degrees of success
    private int degreeSuccessMargin = 5; // Success by 5+ = 1 degree, 10+ = 2 degrees, etc.
    private int maxDegrees = 4;

    // Opposed rolls
    private boolean allowTies = false;
    private String tieResolution = "defender"; // "defender", "attacker", "reroll", "both"

    // === MODIFIER SYSTEM ===
    private boolean usesSituationalModifiers = true;
    private Map<String,Integer> commonModifiers = new HashMap<>(); // "advantage" -> +2, "cover" -> +4
    private boolean usesAdvantageDisadvantage = false;
    private boolean stacksAdvantageDisadvantage = false;

    // Equipment and racial modifiers
    private boolean allowsEquipmentBonuses = true;
    private boolean allowsRacialModifiers = true;
    private Map<String,Integer> racialSaveModifiers = new HashMap<>();

    // Temporary effects
    private boolean usesTemporaryModifiers = true;

    // === SPECIAL MECHANICS ===
    private boolean allowsRerolls = false;
    private Map<String,String> rerollConditions = new HashMap<>(); // "natural1" -> "once"
    private boolean allowsTakingTen = false;
    private boolean allowsTakingTwenty = false;
    private Map<String,Integer> takeTenModifiers = new HashMap<>();

    // Resistance and immunity
    private boolean supportsResistance = false;
    private boolean supportsImmunity = false;
    private Map<String,String> resistanceTypes = new HashMap<>(); // "fire" -> "halfDamage"

    // Group saves
    private boolean allowsGroupSaves = false;
    private String groupSaveMethod = "individual"; // "individual", "best", "worst", "average"

    // === CONDITIONAL MECHANICS ===
    private Map<String,String> conditionalRules = new HashMap<>(); // "undead" -> "immuneToFortitude"
    private Map<String,Map<String,Object>> effectModifiers = new HashMap<>();

    // === CONFIGURATION FLAGS ===
    private boolean usesAdvancedDice = false;
    private boolean usesDegreesOfSuccess = false;
    private boolean usesOpposedRolls = false;
    private boolean usesComplexModifiers = false;
    private boolean usesSpecialMechanics = false;
    private boolean usesConditionalRules = false;

// *** CONSTRUCTORS ***
    public SaveMethod(String name) {
        super(name);
        initializeArrayRegistry();
        initializeDefaults();
    }

    public SaveMethod(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
        initializeDefaults();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("saveTypes", new ArrayList<String>());
        arrayHandler.putArray("skillBasedSaves", new ArrayList<String>());
        arrayHandler.putArray("temporaryEffects", new ArrayList<String>());
        arrayHandler.putArray("saveVsEffects", new ArrayList<String>());
    }

    private void initializeDefaults() {
        // Default D&D-style saves
        ArrayList<String> saveTypes = getArray("saveTypes");
        saveTypes.add("Fortitude");
        saveTypes.add("Reflex");
        saveTypes.add("Will");

        // Default attribute mappings
        saveToAttributeMap.put("Fortitude", "Constitution");
        saveToAttributeMap.put("Reflex", "Dexterity");
        saveToAttributeMap.put("Will", "Wisdom");

        // Default progression types (D&D 3.5 style)
        classProgressionType.put("Fortitude", "good");
        classProgressionType.put("Reflex", "poor");
        classProgressionType.put("Will", "poor");

        // Common modifiers
        commonModifiers.put("flanked", -2);
        commonModifiers.put("prone", -4);
        commonModifiers.put("cover", 2);
        commonModifiers.put("partialCover", 1);

        // Default effect types
        ArrayList<String> effectTypes = getArray("saveVsEffects");
        effectTypes.add("poison");
        effectTypes.add("disease");
        effectTypes.add("charm");
        effectTypes.add("fear");
        effectTypes.add("paralysis");

        // Temporary effect types
        ArrayList<String> tempEffects = getArray("temporaryEffects");
        tempEffects.add("blessing");
        tempEffects.add("curse");
        tempEffects.add("enhancement");
        tempEffects.add("penalty");
    }

    // === GENERIC ARRAY HANDLER METHODS ===
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getArray(String arrayName) {
        return arrayHandler.getArray(arrayName);
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getObjectArray(String arrayName) {
        return arrayHandler.getObjectArray(arrayName);
    }

    public <T> void addToArray(String arrayName, T value) {
        arrayHandler.addElement(arrayName, value);
    }

    public <T> boolean removeFromArray(String arrayName, T value) {
        return arrayHandler.removeElement(arrayName, value);
    }

    public void clearArray(String arrayName) {
        arrayHandler.clearArray(arrayName);
    }

    public Set<String> getArrayNames() {
        return arrayHandler.getArrayNames();
    }

    // === SAVE TYPE SYSTEM METHODS ===
    public String getSaveOrganization() { return saveOrganization; }
    public void setSaveOrganization(String saveOrganization) { this.saveOrganization = saveOrganization; }


    public Map<String,String> getSaveCategories() { return saveCategories; }
    public void setSaveCategories(Map<String,String> saveCategories) {
        this.saveCategories = saveCategories != null ? saveCategories : new HashMap<>();
    }
    public void setSaveCategory(String save, String category) { saveCategories.put(save, category); }
    public void removeSaveCategory(String save) { saveCategories.remove(save); }


    // === BASE CALCULATION METHODS ===
    public String getBaseCalculationMethod() { return baseCalculationMethod; }
    public void setBaseCalculationMethod(String baseCalculationMethod) {
        this.baseCalculationMethod = baseCalculationMethod;
    }

    public Map<String,String> getSaveToAttributeMap() { return saveToAttributeMap; }
    public void setSaveToAttributeMap(Map<String,String> saveToAttributeMap) {
        this.saveToAttributeMap = saveToAttributeMap != null ? saveToAttributeMap : new HashMap<>();
    }
    public void setSaveToAttribute(String save, String attribute) { saveToAttributeMap.put(save, attribute); }
    public void removeSaveToAttribute(String save) { saveToAttributeMap.remove(save); }

    public Map<String,Integer> getFlatSaveValues() { return flatSaveValues; }
    public void setFlatSaveValues(Map<String,Integer> flatSaveValues) {
        this.flatSaveValues = flatSaveValues != null ? flatSaveValues : new HashMap<>();
    }
    public void setFlatSaveValue(String save, int value) { flatSaveValues.put(save, value); }
    public void removeFlatSaveValue(String save) { flatSaveValues.remove(save); }

    public Map<String,String> getSaveFormulas() { return saveFormulas; }
    public void setSaveFormulas(Map<String,String> saveFormulas) {
        this.saveFormulas = saveFormulas != null ? saveFormulas : new HashMap<>();
    }
    public void setSaveFormula(String save, String formula) { saveFormulas.put(save, formula); }
    public void removeSaveFormula(String save) { saveFormulas.remove(save); }

    public Map<String,String> getClassProgressionType() { return classProgressionType; }
    public void setClassProgressionType(Map<String,String> classProgressionType) {
        this.classProgressionType = classProgressionType != null ? classProgressionType : new HashMap<>();
    }
    public void setClassProgression(String save, String progressionType) {
        classProgressionType.put(save, progressionType);
    }
    public void removeClassProgression(String save) { classProgressionType.remove(save); }

    public Map<String,Map<Integer,Integer>> getCustomProgressionTables() { return customProgressionTables; }
    public void setCustomProgressionTables(Map<String,Map<Integer,Integer>> customProgressionTables) {
        this.customProgressionTables = customProgressionTables != null ? customProgressionTables : new HashMap<>();
    }
    public void setCustomProgression(String save, int level, int value) {
        customProgressionTables.computeIfAbsent(save, k -> new HashMap<>()).put(level, value);
    }
    public void removeCustomProgression(String save) { customProgressionTables.remove(save); }

    // === DICE SYSTEM METHODS ===
    public String getDiceSystem() { return diceSystem; }
    public void setDiceSystem(String diceSystem) { this.diceSystem = diceSystem; }

    public int getDiceCount() { return diceCount; }
    public void setDiceCount(int diceCount) { this.diceCount = diceCount; }

    public int getDiceSides() { return diceSides; }
    public void setDiceSides(int diceSides) { this.diceSides = diceSides; }

    public int getDiceModifier() { return diceModifier; }
    public void setDiceModifier(int diceModifier) { this.diceModifier = diceModifier; }

    public boolean isExplodingDice() { return explodingDice; }
    public void setExplodingDice(boolean explodingDice) { this.explodingDice = explodingDice; }

    public int getExplodeThreshold() { return explodeThreshold; }
    public void setExplodeThreshold(int explodeThreshold) { this.explodeThreshold = explodeThreshold; }

    public int getPoolTargetNumber() { return poolTargetNumber; }
    public void setPoolTargetNumber(int poolTargetNumber) { this.poolTargetNumber = poolTargetNumber; }

    public int getPoolSuccessThreshold() { return poolSuccessThreshold; }
    public void setPoolSuccessThreshold(int poolSuccessThreshold) { this.poolSuccessThreshold = poolSuccessThreshold; }

    public boolean isPoolCountSuccesses() { return poolCountSuccesses; }
    public void setPoolCountSuccesses(boolean poolCountSuccesses) { this.poolCountSuccesses = poolCountSuccesses; }

    public boolean isRollUnder() { return rollUnder; }
    public void setRollUnder(boolean rollUnder) { this.rollUnder = rollUnder; }

    // === SUCCESS MECHANICS METHODS ===
    public String getSuccessMethod() { return successMethod; }
    public void setSuccessMethod(String successMethod) { this.successMethod = successMethod; }

    public int getBaseDifficultyClass() { return baseDifficultyClass; }
    public void setBaseDifficultyClass(int baseDifficultyClass) { this.baseDifficultyClass = baseDifficultyClass; }

    public Map<String,Integer> getDifficultyByType() { return difficultyByType; }
    public void setDifficultyByType(Map<String,Integer> difficultyByType) {
        this.difficultyByType = difficultyByType != null ? difficultyByType : new HashMap<>();
    }
    public void setDifficultyByType(String saveType, int dc) { difficultyByType.put(saveType, dc); }
    public void removeDifficultyByType(String saveType) { difficultyByType.remove(saveType); }

    public boolean isUsesCriticalSuccess() { return usesCriticalSuccess; }
    public void setUsesCriticalSuccess(boolean usesCriticalSuccess) { this.usesCriticalSuccess = usesCriticalSuccess; }

    public boolean isUsesCriticalFailure() { return usesCriticalFailure; }
    public void setUsesCriticalFailure(boolean usesCriticalFailure) { this.usesCriticalFailure = usesCriticalFailure; }

    public int getCriticalSuccessThreshold() { return criticalSuccessThreshold; }
    public void setCriticalSuccessThreshold(int criticalSuccessThreshold) {
        this.criticalSuccessThreshold = criticalSuccessThreshold;
    }

    public int getCriticalFailureThreshold() { return criticalFailureThreshold; }
    public void setCriticalFailureThreshold(int criticalFailureThreshold) {
        this.criticalFailureThreshold = criticalFailureThreshold;
    }

    public int getDegreeSuccessMargin() { return degreeSuccessMargin; }
    public void setDegreeSuccessMargin(int degreeSuccessMargin) { this.degreeSuccessMargin = degreeSuccessMargin; }

    public int getMaxDegrees() { return maxDegrees; }
    public void setMaxDegrees(int maxDegrees) { this.maxDegrees = maxDegrees; }

    public boolean isAllowTies() { return allowTies; }
    public void setAllowTies(boolean allowTies) { this.allowTies = allowTies; }

    public String getTieResolution() { return tieResolution; }
    public void setTieResolution(String tieResolution) { this.tieResolution = tieResolution; }

    // === MODIFIER SYSTEM METHODS ===
    public boolean isUsesSituationalModifiers() { return usesSituationalModifiers; }
    public void setUsesSituationalModifiers(boolean usesSituationalModifiers) {
        this.usesSituationalModifiers = usesSituationalModifiers;
    }

    public Map<String,Integer> getCommonModifiers() { return commonModifiers; }
    public void setCommonModifiers(Map<String,Integer> commonModifiers) {
        this.commonModifiers = commonModifiers != null ? commonModifiers : new HashMap<>();
    }
    public void setCommonModifier(String condition, int modifier) { commonModifiers.put(condition, modifier); }
    public void removeCommonModifier(String condition) { commonModifiers.remove(condition); }

    public boolean isUsesAdvantageDisadvantage() { return usesAdvantageDisadvantage; }
    public void setUsesAdvantageDisadvantage(boolean usesAdvantageDisadvantage) {
        this.usesAdvantageDisadvantage = usesAdvantageDisadvantage;
    }

    public boolean isStacksAdvantageDisadvantage() { return stacksAdvantageDisadvantage; }
    public void setStacksAdvantageDisadvantage(boolean stacksAdvantageDisadvantage) {
        this.stacksAdvantageDisadvantage = stacksAdvantageDisadvantage;
    }

    public boolean isAllowsEquipmentBonuses() { return allowsEquipmentBonuses; }
    public void setAllowsEquipmentBonuses(boolean allowsEquipmentBonuses) {
        this.allowsEquipmentBonuses = allowsEquipmentBonuses;
    }

    public boolean isAllowsRacialModifiers() { return allowsRacialModifiers; }
    public void setAllowsRacialModifiers(boolean allowsRacialModifiers) {
        this.allowsRacialModifiers = allowsRacialModifiers;
    }

    public Map<String,Integer> getRacialSaveModifiers() { return racialSaveModifiers; }
    public void setRacialSaveModifiers(Map<String,Integer> racialSaveModifiers) {
        this.racialSaveModifiers = racialSaveModifiers != null ? racialSaveModifiers : new HashMap<>();
    }
    public void setRacialSaveModifier(String race, int modifier) { racialSaveModifiers.put(race, modifier); }
    public void removeRacialSaveModifier(String race) { racialSaveModifiers.remove(race); }

    public boolean isUsesTemporaryModifiers() { return usesTemporaryModifiers; }
    public void setUsesTemporaryModifiers(boolean usesTemporaryModifiers) {
        this.usesTemporaryModifiers = usesTemporaryModifiers;
    }


    // === SPECIAL MECHANICS METHODS ===
    public boolean isAllowsRerolls() { return allowsRerolls; }
    public void setAllowsRerolls(boolean allowsRerolls) { this.allowsRerolls = allowsRerolls; }

    public Map<String,String> getRerollConditions() { return rerollConditions; }
    public void setRerollConditions(Map<String,String> rerollConditions) {
        this.rerollConditions = rerollConditions != null ? rerollConditions : new HashMap<>();
    }
    public void setRerollCondition(String condition, String frequency) { rerollConditions.put(condition, frequency); }
    public void removeRerollCondition(String condition) { rerollConditions.remove(condition); }

    public boolean isAllowsTakingTen() { return allowsTakingTen; }
    public void setAllowsTakingTen(boolean allowsTakingTen) { this.allowsTakingTen = allowsTakingTen; }

    public boolean isAllowsTakingTwenty() { return allowsTakingTwenty; }
    public void setAllowsTakingTwenty(boolean allowsTakingTwenty) { this.allowsTakingTwenty = allowsTakingTwenty; }

    public Map<String,Integer> getTakeTenModifiers() { return takeTenModifiers; }
    public void setTakeTenModifiers(Map<String,Integer> takeTenModifiers) {
        this.takeTenModifiers = takeTenModifiers != null ? takeTenModifiers : new HashMap<>();
    }
    public void setTakeTenModifier(String condition, int modifier) { takeTenModifiers.put(condition, modifier); }
    public void removeTakeTenModifier(String condition) { takeTenModifiers.remove(condition); }

    public boolean isSupportsResistance() { return supportsResistance; }
    public void setSupportsResistance(boolean supportsResistance) { this.supportsResistance = supportsResistance; }

    public boolean isSupportsImmunity() { return supportsImmunity; }
    public void setSupportsImmunity(boolean supportsImmunity) { this.supportsImmunity = supportsImmunity; }

    public Map<String,String> getResistanceTypes() { return resistanceTypes; }
    public void setResistanceTypes(Map<String,String> resistanceTypes) {
        this.resistanceTypes = resistanceTypes != null ? resistanceTypes : new HashMap<>();
    }
    public void setResistanceType(String damageType, String resistanceEffect) {
        resistanceTypes.put(damageType, resistanceEffect);
    }
    public void removeResistanceType(String damageType) { resistanceTypes.remove(damageType); }

    public boolean isAllowsGroupSaves() { return allowsGroupSaves; }
    public void setAllowsGroupSaves(boolean allowsGroupSaves) { this.allowsGroupSaves = allowsGroupSaves; }

    public String getGroupSaveMethod() { return groupSaveMethod; }
    public void setGroupSaveMethod(String groupSaveMethod) { this.groupSaveMethod = groupSaveMethod; }

    // === CONDITIONAL MECHANICS METHODS ===
    public Map<String,String> getConditionalRules() { return conditionalRules; }
    public void setConditionalRules(Map<String,String> conditionalRules) {
        this.conditionalRules = conditionalRules != null ? conditionalRules : new HashMap<>();
    }
    public void setConditionalRule(String condition, String rule) { conditionalRules.put(condition, rule); }
    public void removeConditionalRule(String condition) { conditionalRules.remove(condition); }


    public Map<String,Map<String,Object>> getEffectModifiers() { return effectModifiers; }
    public void setEffectModifiers(Map<String,Map<String,Object>> effectModifiers) {
        this.effectModifiers = effectModifiers != null ? effectModifiers : new HashMap<>();
    }
    public void setEffectModifier(String effectName, String property, Object value) {
        effectModifiers.computeIfAbsent(effectName, k -> new HashMap<>()).put(property, value);
    }
    public void removeEffectModifiers(String effectName) { effectModifiers.remove(effectName); }

    // === CONFIGURATION FLAG METHODS ===
    public boolean isUsesAdvancedDice() { return usesAdvancedDice; }
    public void setUsesAdvancedDice(boolean usesAdvancedDice) { this.usesAdvancedDice = usesAdvancedDice; }

    public boolean isUsesDegreesOfSuccess() { return usesDegreesOfSuccess; }
    public void setUsesDegreesOfSuccess(boolean usesDegreesOfSuccess) {
        this.usesDegreesOfSuccess = usesDegreesOfSuccess;
    }

    public boolean isUsesOpposedRolls() { return usesOpposedRolls; }
    public void setUsesOpposedRolls(boolean usesOpposedRolls) { this.usesOpposedRolls = usesOpposedRolls; }

    public boolean isUsesComplexModifiers() { return usesComplexModifiers; }
    public void setUsesComplexModifiers(boolean usesComplexModifiers) {
        this.usesComplexModifiers = usesComplexModifiers;
    }

    public boolean isUsesSpecialMechanics() { return usesSpecialMechanics; }
    public void setUsesSpecialMechanics(boolean usesSpecialMechanics) {
        this.usesSpecialMechanics = usesSpecialMechanics;
    }

    public boolean isUsesConditionalRules() { return usesConditionalRules; }
    public void setUsesConditionalRules(boolean usesConditionalRules) {
        this.usesConditionalRules = usesConditionalRules;
    }

    // === UTILITY METHODS ===

    /**
     * Gets a human-readable description of the save system
     */
    public String getSaveSystemDescription() {
        StringBuilder desc = new StringBuilder();

        // Dice system
        switch (diceSystem) {
            case "d20":
                desc.append("d20");
                if (diceModifier != 0) desc.append(diceModifier > 0 ? "+" : "").append(diceModifier);
                break;
            case "dicePool":
                desc.append(diceCount).append("d").append(diceSides).append(" vs ").append(poolTargetNumber);
                break;
            case "percentile":
                desc.append("d100 ").append(rollUnder ? "under" : "over").append(" target");
                break;
            case "2d6":
                desc.append("2d6");
                if (diceModifier != 0) desc.append(diceModifier > 0 ? "+" : "").append(diceModifier);
                break;
        }

        // Success method
        desc.append(" ").append(successMethod.equals("beatDC") ? "vs DC" : successMethod);

        // Save organization
        desc.append(" (").append(saveOrganization).append(" saves)");

        return desc.toString();
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validAttributeIds, Set<String> validRaceIds) {
        int removedCount = 0;

        // Clean up saveToAttributeMap (values are Attribute ids)
        Iterator<Map.Entry<String, String>> attrIter = saveToAttributeMap.entrySet().iterator();
        while (attrIter.hasNext()) {
            Map.Entry<String, String> entry = attrIter.next();
            String attributeId = entry.getValue();

            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                attrIter.remove();
                removedCount++;
            }
        }

        // Clean up racialSaveModifiers (keys are Race ids)
        Iterator<Map.Entry<String, Integer>> raceIter = racialSaveModifiers.entrySet().iterator();
        while (raceIter.hasNext()) {
            Map.Entry<String, Integer> entry = raceIter.next();
            String raceId = entry.getKey();

            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }


}
