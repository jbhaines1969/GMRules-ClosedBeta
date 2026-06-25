/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks


package com.gamemaker.gmrules.CharacterElements;

import com.gamemaker.gmrules.AtomicElements.*;

import com.gamemaker.gmrules.GameElements.*;

import com.gamemaker.gmrules.GameMechanics.*;

import com.gamemaker.gmrules.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a character skill in any RPG system.
 * Covers proficiency systems, ability dependencies, specializations, and difficulty scales
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, and other systems.
 */
public class Skill extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Properties
    private String category = "";
    private String type = "";
    private String relatedAbility = "";
    private boolean trainedOnly = false;
    private int armorCheckPenalty = 0;

    // Difficulty System Reference
    private String difficultySystemName = "";

    // Proficiency Systems
    private boolean usesProficiency = false;
    private boolean usesRanks = false;
    private boolean usesLevels = false;
    private int maxRanks = 0;

    // Specializations and Variants
    private boolean allowsSpecialization = false;
    private String specializationRule = "";

    // Advanced Mechanics
    private boolean canTakeUntrained = true;
    private boolean canRetry = true;
    private String retryConditions = "";
    private boolean hasTimeRequirement = false;
    private String timeRequired = "";

    // Opposed and Cooperative
    private boolean isOpposed = false;
    private String opposedBy = "";
    private boolean allowsCooperation = false;
    private String cooperationRule = "";

    // Synergy and Dependencies
    private Map<String, Integer> synergies = new LinkedHashMap<>();
    private Map<String, String> situationalModifiers = new LinkedHashMap<>();

    // Tool Requirements (Direct Implementation)
    private boolean requiresTools = false;
    private boolean usesTools = false;
    private Map<String, Short> toolModifiers = new LinkedHashMap<>();

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();

    // Usage Limitations (Direct Implementation)
    private boolean hasUsageLimits = false;
    private String usageLimitType = "";
    private int usageLimit = 0;
    private boolean causesFatigue = false;
    private String fatigueType = "";

    // Effects Integration - Store Effect names for referential integrity

    // Element-Level Configuration Flags - Control which UI sections are shown
    private boolean usesAbilities = true;
    private boolean usesDifficultySystem = true;
    private boolean usesSpecialization = true;
    private boolean usesOpposition = true;
    private boolean usesSynergies = true;
    private boolean usesTimeRequirements = true;
    private boolean usesFatigue = true;
    private boolean usesCooperation = true;
    private boolean usesRetry = true;
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Skill(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Skill(String name, String description) {
        super(name);
        setDescription(description);
        initializeArrayRegistry();
    }

    public Skill(String name, String description, String category) {
        super(name);
        setDescription(description);
        this.category = category != null ? category : "";
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("alternativeAbilities", new ArrayList<String>());
        arrayHandler.putArray("limitedToClasses", new ArrayList<String>());
        arrayHandler.putArray("limitedToRaces", new ArrayList<String>());
        arrayHandler.putArray("specializations", new ArrayList<String>());
        arrayHandler.putArray("prerequisites", new ArrayList<String>());
        arrayHandler.putArray("requiredTools", new ArrayList<String>());
        arrayHandler.putArray("effectNames", new ArrayList<String>());
    }

    // ===== GENERIC ARRAY HANDLER METHODS =====
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

    // Core Property Getters/Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRelatedAbility() { return relatedAbility; }
    public void setRelatedAbility(String relatedAbility) { this.relatedAbility = relatedAbility; }

    public boolean isTrainedOnly() { return trainedOnly; }
    public void setTrainedOnly(boolean trainedOnly) { this.trainedOnly = trainedOnly; }

    public int getArmorCheckPenalty() { return armorCheckPenalty; }
    public void setArmorCheckPenalty(int armorCheckPenalty) { this.armorCheckPenalty = armorCheckPenalty; }

    // Difficulty System Reference
    public String getDifficultySystemName() { return difficultySystemName; }
    public void setDifficultySystemName(String difficultySystemName) { this.difficultySystemName = difficultySystemName; }

    // Proficiency Systems
    public boolean usesProficiency() { return usesProficiency; }
    public void setUsesProficiency(boolean usesProficiency) { this.usesProficiency = usesProficiency; }

    public boolean usesRanks() { return usesRanks; }
    public void setUsesRanks(boolean usesRanks) { this.usesRanks = usesRanks; }

    public boolean usesLevels() { return usesLevels; }
    public void setUsesLevels(boolean usesLevels) { this.usesLevels = usesLevels; }

    public int getMaxRanks() { return maxRanks; }
    public void setMaxRanks(int maxRanks) { this.maxRanks = Math.max(0, maxRanks); }

    // Specializations
    public boolean allowsSpecialization() { return allowsSpecialization; }
    public void setAllowsSpecialization(boolean allowsSpecialization) { this.allowsSpecialization = allowsSpecialization; }

    public String getSpecializationRule() { return specializationRule; }
    public void setSpecializationRule(String specializationRule) { this.specializationRule = specializationRule; }

    // Advanced Mechanics
    public boolean canTakeUntrained() { return canTakeUntrained; }
    public void setCanTakeUntrained(boolean canTakeUntrained) { this.canTakeUntrained = canTakeUntrained; }

    public boolean canRetry() { return canRetry; }
    public void setCanRetry(boolean canRetry) { this.canRetry = canRetry; }

    public String getRetryConditions() { return retryConditions; }
    public void setRetryConditions(String retryConditions) { this.retryConditions = retryConditions; }

    public boolean hasTimeRequirement() { return hasTimeRequirement; }
    public void setHasTimeRequirement(boolean hasTimeRequirement) { this.hasTimeRequirement = hasTimeRequirement; }

    public String getTimeRequired() { return timeRequired; }
    public void setTimeRequired(String timeRequired) { this.timeRequired = timeRequired; }

    // Opposition and Cooperation
    public boolean isOpposed() { return isOpposed; }
    public void setOpposed(boolean isOpposed) { this.isOpposed = isOpposed; }

    public String getOpposedBy() { return opposedBy; }
    public void setOpposedBy(String opposedBy) { this.opposedBy = opposedBy; }

    public boolean allowsCooperation() { return allowsCooperation; }
    public void setAllowsCooperation(boolean allowsCooperation) { this.allowsCooperation = allowsCooperation; }

    public String getCooperationRule() { return cooperationRule; }
    public void setCooperationRule(String cooperationRule) { this.cooperationRule = cooperationRule; }

    // Synergies and Dependencies
    public Map<String, Integer> getSynergies() { return synergies; }
    public void setSynergies(Map<String, Integer> synergies) { this.synergies = synergies; }
    public void addSynergy(String skill, int bonus) {
        if (!skill.trim().isEmpty()) {
            synergies.put(skill, bonus);
        }
    }

    public Map<String, String> getSituationalModifiers() { return situationalModifiers; }
    public void setSituationalModifiers(Map<String, String> situationalModifiers) { this.situationalModifiers = situationalModifiers; }
    public void addSituationalModifier(String situation, String modifier) {
        if (!situation.trim().isEmpty()) {
            situationalModifiers.put(situation, modifier);
        }
    }

    // Tool Requirements
    public boolean requiresTools() { return requiresTools; }
    public void setRequiresTools(boolean requiresTools) { this.requiresTools = requiresTools; }

    public boolean usesTools() { return usesTools; }
    public void setUsesTools(boolean usesTools) { this.usesTools = usesTools; }

    public Map<String, Short> getToolModifiers() { return toolModifiers; }
    public void setToolModifiers(Map<String, Short> toolModifiers) { this.toolModifiers = toolModifiers; }
    public void addToolModifier(String tool, short modifier) {
        if (!tool.trim().isEmpty()) {
            toolModifiers.put(tool, modifier);
        }
    }

    // System Properties
    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) { this.systemProperties = systemProperties; }
    public void addSystemProperty(String key, Object value) {
        if (!key.trim().isEmpty()) {
            systemProperties.put(key, value);
        }
    }

    // Usage Limitations
    public boolean hasUsageLimits() { return hasUsageLimits; }
    public void setHasUsageLimits(boolean hasUsageLimits) { this.hasUsageLimits = hasUsageLimits; }

    public String getUsageLimitType() { return usageLimitType; }
    public void setUsageLimitType(String usageLimitType) { this.usageLimitType = usageLimitType; }

    public int getUsageLimit() { return usageLimit; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = Math.max(0, usageLimit); }

    public boolean causesFatigue() { return causesFatigue; }
    public void setCausesFatigue(boolean causesFatigue) { this.causesFatigue = causesFatigue; }

    public String getFatigueType() { return fatigueType; }
    public void setFatigueType(String fatigueType) { this.fatigueType = fatigueType; }

    // Effects Integration
    // Element-Level Configuration Methods
    public boolean usesAbilities() { return usesAbilities; }
    public void setUsesAbilities(boolean usesAbilities) { this.usesAbilities = usesAbilities; }

    public boolean usesDifficultySystem() { return usesDifficultySystem; }
    public void setUsesDifficultySystem(boolean usesDifficultySystem) { this.usesDifficultySystem = usesDifficultySystem; }

    public boolean usesSpecialization() { return usesSpecialization; }
    public void setUsesSpecialization(boolean usesSpecialization) { this.usesSpecialization = usesSpecialization; }

    public boolean usesOpposition() { return usesOpposition; }
    public void setUsesOpposition(boolean usesOpposition) { this.usesOpposition = usesOpposition; }

    public boolean usesSynergies() { return usesSynergies; }
    public void setUsesSynergies(boolean usesSynergies) { this.usesSynergies = usesSynergies; }

    public boolean usesTimeRequirements() { return usesTimeRequirements; }
    public void setUsesTimeRequirements(boolean usesTimeRequirements) { this.usesTimeRequirements = usesTimeRequirements; }

    public boolean usesFatigue() { return usesFatigue; }
    public void setUsesFatigue(boolean usesFatigue) { this.usesFatigue = usesFatigue; }

    public boolean usesCooperation() { return usesCooperation; }
    public void setUsesCooperation(boolean usesCooperation) { this.usesCooperation = usesCooperation; }

    public boolean usesRetry() { return usesRetry; }
    public void setUsesRetry(boolean usesRetry) { this.usesRetry = usesRetry; }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @param validSkillIds Set of valid Skill ids currently in the game
     * @param validDifficultySystemIds Set of valid DifficultySystem ids currently in the game
     * @param validSkillCategoryKeys Set of valid skill category keys currently in the game
     * @param validEquipmentIds Set of valid Equipment ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validEffectIds,
        Set<String> validAttributeIds,
        Set<String> validClassIds,
        Set<String> validRaceIds,
        Set<String> validSkillIds,
        Set<String> validDifficultySystemIds,
        Set<String> validSkillCategoryKeys,
        Set<String> validEquipmentIds
    ) {
        int removedCount = 0;

        // Clean up category (references skill category keys)
        if (!category.isEmpty()) {
            boolean categoryExists = validSkillCategoryKeys.contains(category);
            if (!categoryExists) {
                category = "";
                removedCount++;
            }
        }

        // Clean up related ability (Attribute id)
        if (!relatedAbility.isEmpty()) {
            boolean abilityExists = validAttributeIds.contains(relatedAbility);
            if (!abilityExists) {
                relatedAbility = "";
                removedCount++;
            }
        }

        // Clean up alternative abilities (Attribute ids)
        ArrayList<String> alternativeAbilities = arrayHandler.getObjectArray("alternativeAbilities");
        Iterator<String> altAbilityIter = alternativeAbilities.iterator();
        while (altAbilityIter.hasNext()) {
            String ability = altAbilityIter.next();
            boolean exists = validAttributeIds.contains(ability);
            if (!exists) {
                altAbilityIter.remove();
                removedCount++;
            }
        }

        // Clean up difficulty system reference (DifficultySystem id)
        if (!difficultySystemName.isEmpty()) {
            boolean systemExists = validDifficultySystemIds.contains(difficultySystemName);
            if (!systemExists) {
                difficultySystemName = "";
                removedCount++;
            }
        }

        // Clean up limited to classes
        ArrayList<String> limitedToClasses = arrayHandler.getObjectArray("limitedToClasses");
        Iterator<String> classIter = limitedToClasses.iterator();
        while (classIter.hasNext()) {
            String classId = classIter.next();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classIter.remove();
                removedCount++;
            }
        }

        // Clean up limited to races
        ArrayList<String> limitedToRaces = arrayHandler.getObjectArray("limitedToRaces");
        Iterator<String> raceIter = limitedToRaces.iterator();
        while (raceIter.hasNext()) {
            String raceId = raceIter.next();
            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceIter.remove();
                removedCount++;
            }
        }

        // TODO: specializations - Should these reference specific Skills or be freeform?
        // Currently treated as freeform strings - no validation needed until format is decided
        // Examples: "Knowledge (Arcana)", "Craft (Alchemy)" - likely freeform descriptors

        // Clean up opposedBy (references another Skill id)
        if (!opposedBy.isEmpty()) {
            boolean exists = validSkillIds.contains(opposedBy);
            if (!exists) {
                opposedBy = "";
                removedCount++;
            }
        }

        // Clean up synergies keys (reference other Skill ids)
        Iterator<Map.Entry<String, Integer>> synergyIter = synergies.entrySet().iterator();
        while (synergyIter.hasNext()) {
            Map.Entry<String, Integer> entry = synergyIter.next();
            String skillId = entry.getKey();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                synergyIter.remove();
                removedCount++;
            }
        }

        // Clean up prerequisites (references Skill ids)
        ArrayList<String> prerequisites = arrayHandler.getObjectArray("prerequisites");
        Iterator<String> prereqIter = prerequisites.iterator();
        while (prereqIter.hasNext()) {
            String prereqId = prereqIter.next();
            boolean exists = validSkillIds.contains(prereqId);
            if (!exists) {
                prereqIter.remove();
                removedCount++;
            }
        }

        // Clean up required tools (references Equipment ids)
        ArrayList<String> requiredTools = arrayHandler.getObjectArray("requiredTools");
        Iterator<String> toolIter = requiredTools.iterator();
        while (toolIter.hasNext()) {
            String toolId = toolIter.next();
            boolean exists = validEquipmentIds.contains(toolId);
            if (!exists) {
                toolIter.remove();
                removedCount++;
            }
        }

        // Clean up tool modifiers (keys reference Equipment ids)
        Iterator<Map.Entry<String, Short>> toolModIter = toolModifiers.entrySet().iterator();
        while (toolModIter.hasNext()) {
            Map.Entry<String, Short> entry = toolModIter.next();
            String toolId = entry.getKey();
            boolean exists = validEquipmentIds.contains(toolId);
            if (!exists) {
                toolModIter.remove();
                removedCount++;
            }
        }

        // Clean up effect ids
        ArrayList<String> effectNames = arrayHandler.getObjectArray("effectNames");
        Iterator<String> effectIter = effectNames.iterator();
        while (effectIter.hasNext()) {
            String effectId = effectIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                effectIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
