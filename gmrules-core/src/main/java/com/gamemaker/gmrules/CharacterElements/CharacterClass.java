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

import com.gamemaker.gmrules.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a character class in any RPG system.
 * Covers progression systems, features, multiclassing, archetypes, and spell lists
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, and other systems.
 */
public class CharacterClass extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Core Properties
    private String classType = "";                   // Base, Prestige, Archetype, etc.
    private String role = "";                        // Tank, DPS, Healer, Support, etc.
    private String hitDie = "";                      // d6, d8, d10, d12
    private int baseHitPoints = 0;                   // Starting HP
    private String primaryAttribute = "";              // Main ability score
    private Map<String, Integer> requiredAttributeScores = new LinkedHashMap<>();

    // Progression System
    private int maxLevel = 20;                       // Maximum class level
    private String progressionType = "";             // Linear, Milestone, Point-buy
    private Map<Integer, String> levelProgression = new LinkedHashMap<>(); // Level -> features gained
    private Map<Integer, Integer> experienceTable = new LinkedHashMap<>(); // Level -> XP required

    // Skill-Based Progression System
    private Map<String, Integer> skillTypeChoices = new LinkedHashMap<>(); // SkillType -> number to choose
    private Map<Integer, ArrayList<String>> automaticSkillsPerLevel = new LinkedHashMap<>(); // Level -> automatic skill names
    private Map<Integer, Map<String, Integer>> skillChoicesPerLevel = new LinkedHashMap<>(); // Level -> (SkillType -> choices)

    // Skill and Proficiency System
    private int skillPointsPerLevel = 0;            // Skill points gained per level
    private boolean skillPointsSameAllLevels = true;
    private Map<Integer, Integer> skillPointsByLevel = new LinkedHashMap<>();
    private int startingSkillPoints = 0;            // Skill points at 1st level

    // Spellcasting System
    private boolean isSpellcaster = false;          // Can cast spells
    private String spellcastingAbility = "";        // INT, WIS, CHA, etc.
    private String spellcastingType = "";           // Prepared, Spontaneous, Ritual, etc.
    private boolean ritualCasting = false;          // Can cast rituals
    private Map<Integer, Map<Integer, Integer>> spellSlotsPerLevel = new LinkedHashMap<>(); // Level -> Spell Level -> Slots
    private Map<String, ArrayList<String>> spellsKnown = new LinkedHashMap<>(); // Level -> spells known

    // Archetype and Subclass System
    private boolean hasArchetypes = false;          // Supports subclasses/archetypes
    private int archetypeLevel = 0;                 // Level when archetype is chosen
    private Map<String, String> archetypeDescriptions = new LinkedHashMap<>();
    private Map<String, Map<Integer, String>> archetypeFeatures = new LinkedHashMap<>(); // Archetype -> Level -> Feature

    // Multiclassing Rules
    private boolean allowsMulticlassing = true;     // Can multiclass into/out of
    private String multiclassProgression = "";      // How multiclass levels work

    // Resource Management
    private boolean hasClassResources = false;      // Uses class-specific resources
    private Map<String, String> resourceDescriptions = new LinkedHashMap<>();
    private Map<String, Map<Integer, Integer>> resourcesPerLevel = new LinkedHashMap<>(); // Resource -> Level -> Amount

    // Combat and Defense
    private int baseArmorClass = 10;                // Starting AC
    private String armorClassCalculation = "";      // How AC is calculated
    private boolean hasExtraAttacks = false;        // Gets multiple attacks
    private Map<Integer, Integer> attackProgression = new LinkedHashMap<>(); // Level -> number of attacks

    // Social and Exploration
    private boolean hasSpecialMovement = false;     // Special movement types
    private Map<String, String> movementTypes = new LinkedHashMap<>();    // Movement type -> details

    // Alignment and Restrictions
    private boolean hasEthicalCode = false;         // Follows specific code (Paladin, etc.)
    private String ethicalCode = "";                // Description of the code

    // Advancement and Customization
    private boolean hasChoices = false;             // Player choices during advancement
    private Map<Integer, ArrayList<String>> choicesPerLevel = new LinkedHashMap<>(); // Level -> available choices
    private boolean hasFeatSupport = false;         // Gets bonus feats
    private Map<Integer, Integer> bonusFeats = new LinkedHashMap<>();     // Level -> number of bonus feats

    // System-Specific Properties
    private String systemType = "";                 // D&D, Pathfinder, GURPS, etc.
    private Map<String, Object> systemProperties = new LinkedHashMap<>(); // System-specific data
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public CharacterClass(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public CharacterClass(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

    public CharacterClass(String name, String description, String role) {
        super(name, description);
        this.role = role;
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("savingThrowProficiencies", new ArrayList<String>());
        arrayHandler.putArray("automaticSkills", new ArrayList<String>());
        arrayHandler.putArray("classSkills", new ArrayList<String>());
        arrayHandler.putArray("weaponProficiencies", new ArrayList<String>());
        arrayHandler.putArray("armorProficiencies", new ArrayList<String>());
        arrayHandler.putArray("toolProficiencies", new ArrayList<String>());
        arrayHandler.putArray("spellLists", new ArrayList<String>());
        arrayHandler.putArray("availableArchetypes", new ArrayList<String>());
        arrayHandler.putArray("multiclassRequirements", new ArrayList<String>());
        arrayHandler.putArray("multiclassProficiencies", new ArrayList<String>());
        arrayHandler.putArray("resourceTypes", new ArrayList<String>());
        arrayHandler.putArray("combatFeatures", new ArrayList<String>());
        arrayHandler.putArray("socialFeatures", new ArrayList<String>());
        arrayHandler.putArray("explorationFeatures", new ArrayList<String>());
        arrayHandler.putArray("allowedAlignments", new ArrayList<String>());
        arrayHandler.putArray("classRestrictions", new ArrayList<String>());
        arrayHandler.putArray("forbiddenEquipment", new ArrayList<String>());
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

    // Core Properties
    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getHitDie() { return hitDie; }
    public void setHitDie(String hitDie) { this.hitDie = hitDie; }

    public int getBaseHitPoints() { return baseHitPoints; }
    public void setBaseHitPoints(int baseHitPoints) { this.baseHitPoints = Math.max(0, baseHitPoints); }

    public String getPrimaryAttribute() { return primaryAttribute; }
    public void setPrimaryAttribute(String primaryAttribute) { this.primaryAttribute = primaryAttribute; }

    public Map<String, Integer> getRequiredAttributeScores() { return requiredAttributeScores; }
    public void setRequiredAttributeScores(Map<String, Integer> requiredAttributeScores) {
        this.requiredAttributeScores = Objects.requireNonNullElseGet(requiredAttributeScores, LinkedHashMap::new);
    }
    public void addRequiredAttributeScore(String attributeId, int score) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (!safeId.isEmpty() && score > 0) {
            requiredAttributeScores.put(safeId, score);
        }
    }
    public boolean removeRequiredAttributeScore(String attributeId) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            return false;
        }
        return requiredAttributeScores.remove(safeId) != null;
    }

    // Progression System
    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = Math.max(1, maxLevel); }

    public String getProgressionType() { return progressionType; }
    public void setProgressionType(String progressionType) { this.progressionType = progressionType; }

    public Map<Integer, String> getLevelProgression() { return levelProgression; }
    public void setLevelProgression(Map<Integer, String> levelProgression) {
        this.levelProgression = levelProgression;
    }
    public void addLevelProgression(int level, String features) {
        if (level > 0) {
            levelProgression.put(level, features);
        }
    }
    public boolean removeLevelProgression(int level) {
        return levelProgression.remove(level) != null;
    }

    public Map<Integer, Integer> getExperienceTable() { return experienceTable; }
    public void setExperienceTable(Map<Integer, Integer> experienceTable) {
        this.experienceTable = experienceTable;
    }
    public void addExperienceRequirement(int level, int xpRequired) {
        if (level > 0 && xpRequired >= 0) {
            experienceTable.put(level, xpRequired);
        }
    }
    public boolean removeExperienceRequirement(int level) {
        return experienceTable.remove(level) != null;
    }

    // Skill-Based Progression Methods
    public Map<String, Integer> getSkillTypeChoices() { return skillTypeChoices; }
    public void setSkillTypeChoices(Map<String, Integer> skillTypeChoices) {
        this.skillTypeChoices = skillTypeChoices;
    }
    public void addSkillTypeChoice(String skillType, int choices) {
        if (!skillType.trim().isEmpty() && choices > 0) {
            skillTypeChoices.put(skillType, choices);
        }
    }
    public boolean removeSkillTypeChoice(String skillType) {
        return skillTypeChoices.remove(skillType) != null;
    }

    public Map<Integer, ArrayList<String>> getAutomaticSkillsPerLevel() { return automaticSkillsPerLevel; }
    public void setAutomaticSkillsPerLevel(Map<Integer, ArrayList<String>> automaticSkillsPerLevel) {
        this.automaticSkillsPerLevel = automaticSkillsPerLevel;
    }

    public Map<Integer, Map<String, Integer>> getSkillChoicesPerLevel() { return skillChoicesPerLevel; }
    public void setSkillChoicesPerLevel(Map<Integer, Map<String, Integer>> skillChoicesPerLevel) {
        this.skillChoicesPerLevel = skillChoicesPerLevel;
    }

    // Skills and Proficiencies
    public int getSkillPointsPerLevel() { return skillPointsPerLevel; }
    public void setSkillPointsPerLevel(int skillPointsPerLevel) { this.skillPointsPerLevel = Math.max(0, skillPointsPerLevel); }

    public boolean isSkillPointsSameAllLevels() { return skillPointsSameAllLevels; }
    public void setSkillPointsSameAllLevels(boolean skillPointsSameAllLevels) {
        this.skillPointsSameAllLevels = skillPointsSameAllLevels;
    }

    public Map<Integer, Integer> getSkillPointsByLevel() { return new LinkedHashMap<>(skillPointsByLevel); }
    public void setSkillPointsByLevel(Map<Integer, Integer> skillPointsByLevel) {
        this.skillPointsByLevel = Objects.requireNonNullElseGet(skillPointsByLevel, LinkedHashMap::new);
    }
    public int getSkillPointsForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (skillPointsSameAllLevels) {
            return skillPointsPerLevel;
        }
        Integer points = skillPointsByLevel.get(level);
        return points == null ? 0 : Math.max(0, points);
    }
    public void setSkillPointsForLevel(int level, int points) {
        if (level > 0) {
            skillPointsByLevel.put(level, Math.max(0, points));
        }
    }
    public void removeSkillPointsForLevel(int level) {
        skillPointsByLevel.remove(level);
    }

    public int getStartingSkillPoints() { return startingSkillPoints; }
    public void setStartingSkillPoints(int startingSkillPoints) { this.startingSkillPoints = Math.max(0, startingSkillPoints); }

    // Spellcasting
    public boolean isSpellcaster() { return isSpellcaster; }
    public void setSpellcaster(boolean isSpellcaster) { this.isSpellcaster = isSpellcaster; }

    public String getSpellcastingAbility() { return spellcastingAbility; }
    public void setSpellcastingAbility(String spellcastingAbility) { this.spellcastingAbility = spellcastingAbility; }

    public String getSpellcastingType() { return spellcastingType; }
    public void setSpellcastingType(String spellcastingType) { this.spellcastingType = spellcastingType; }

    public boolean hasRitualCasting() { return ritualCasting; }
    public void setRitualCasting(boolean ritualCasting) { this.ritualCasting = ritualCasting; }

    public Map<Integer, Map<Integer, Integer>> getSpellSlotsPerLevel() { return spellSlotsPerLevel; }
    public void setSpellSlotsPerLevel(Map<Integer, Map<Integer, Integer>> spellSlotsPerLevel) {
        this.spellSlotsPerLevel = spellSlotsPerLevel;
    }

    public Map<String, ArrayList<String>> getSpellsKnown() { return spellsKnown; }
    public void setSpellsKnown(Map<String, ArrayList<String>> spellsKnown) {
        this.spellsKnown = spellsKnown;
    }

    // Archetypes
    public boolean hasArchetypes() { return hasArchetypes; }
    public void setHasArchetypes(boolean hasArchetypes) { this.hasArchetypes = hasArchetypes; }

    public int getArchetypeLevel() { return archetypeLevel; }
    public void setArchetypeLevel(int archetypeLevel) { this.archetypeLevel = Math.max(0, archetypeLevel); }

    public Map<String, String> getArchetypeDescriptions() { return archetypeDescriptions; }
    public void setArchetypeDescriptions(Map<String, String> archetypeDescriptions) {
        this.archetypeDescriptions = archetypeDescriptions;
    }

    public Map<String, Map<Integer, String>> getArchetypeFeatures() { return archetypeFeatures; }
    public void setArchetypeFeatures(Map<String, Map<Integer, String>> archetypeFeatures) {
        this.archetypeFeatures = archetypeFeatures;
    }

    // Multiclassing
    public boolean allowsMulticlassing() { return allowsMulticlassing; }
    public void setAllowsMulticlassing(boolean allowsMulticlassing) { this.allowsMulticlassing = allowsMulticlassing; }

    public String getMulticlassProgression() { return multiclassProgression; }
    public void setMulticlassProgression(String multiclassProgression) { this.multiclassProgression = multiclassProgression; }

    // Resources
    public boolean hasClassResources() { return hasClassResources; }
    public void setHasClassResources(boolean hasClassResources) { this.hasClassResources = hasClassResources; }

    public Map<String, String> getResourceDescriptions() { return resourceDescriptions; }
    public void setResourceDescriptions(Map<String, String> resourceDescriptions) {
        this.resourceDescriptions = resourceDescriptions;
    }

    public Map<String, Map<Integer, Integer>> getResourcesPerLevel() { return resourcesPerLevel; }
    public void setResourcesPerLevel(Map<String, Map<Integer, Integer>> resourcesPerLevel) {
        this.resourcesPerLevel = resourcesPerLevel;
    }

    // Combat
    public int getBaseArmorClass() { return baseArmorClass; }
    public void setBaseArmorClass(int baseArmorClass) { this.baseArmorClass = Math.max(0, baseArmorClass); }

    public String getArmorClassCalculation() { return armorClassCalculation; }
    public void setArmorClassCalculation(String armorClassCalculation) { this.armorClassCalculation = armorClassCalculation; }

    public boolean hasExtraAttacks() { return hasExtraAttacks; }
    public void setHasExtraAttacks(boolean hasExtraAttacks) { this.hasExtraAttacks = hasExtraAttacks; }

    public Map<Integer, Integer> getAttackProgression() { return attackProgression; }
    public void setAttackProgression(Map<Integer, Integer> attackProgression) {
        this.attackProgression = attackProgression;
    }

    // Social and Exploration
    public boolean hasSpecialMovement() { return hasSpecialMovement; }
    public void setHasSpecialMovement(boolean hasSpecialMovement) { this.hasSpecialMovement = hasSpecialMovement; }

    public Map<String, String> getMovementTypes() { return movementTypes; }
    public void setMovementTypes(Map<String, String> movementTypes) {
        this.movementTypes = movementTypes;
    }

    // Alignment and Restrictions
    public boolean hasEthicalCode() { return hasEthicalCode; }
    public void setHasEthicalCode(boolean hasEthicalCode) { this.hasEthicalCode = hasEthicalCode; }

    public String getEthicalCode() { return ethicalCode; }
    public void setEthicalCode(String ethicalCode) { this.ethicalCode = ethicalCode; }

    // Advancement and Customization
    public boolean hasChoices() { return hasChoices; }
    public void setHasChoices(boolean hasChoices) { this.hasChoices = hasChoices; }

    public Map<Integer, ArrayList<String>> getChoicesPerLevel() { return choicesPerLevel; }
    public void setChoicesPerLevel(Map<Integer, ArrayList<String>> choicesPerLevel) {
        this.choicesPerLevel = choicesPerLevel;
    }

    public boolean hasFeatSupport() { return hasFeatSupport; }
    public void setHasFeatSupport(boolean hasFeatSupport) { this.hasFeatSupport = hasFeatSupport; }

    public Map<Integer, Integer> getBonusFeats() { return bonusFeats; }
    public void setBonusFeats(Map<Integer, Integer> bonusFeats) {
        this.bonusFeats = bonusFeats;
    }

    // System Properties
    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) {
        this.systemProperties = systemProperties;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        arrayHandler = Objects.requireNonNullElseGet(arrayHandler, ArrayHandler::new);
        requiredAttributeScores = Objects.requireNonNullElseGet(requiredAttributeScores, LinkedHashMap::new);
        skillPointsByLevel = Objects.requireNonNullElseGet(skillPointsByLevel, LinkedHashMap::new);
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @param validSkillIds Set of valid Skill ids currently in the game
     * @param validWeaponIds Set of valid Weapon ids currently in the game
     * @param validArmorIds Set of valid Armor ids currently in the game
     * @param validEquipmentIds Set of valid Equipment ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validAttributeIds,
        Set<String> validSkillIds,
        Set<String> validWeaponIds,
        Set<String> validArmorIds,
        Set<String> validEquipmentIds
    ) {
        int removedCount = 0;

        // Clean up primaryAbility (references Attribute id)
        if (!primaryAttribute.isEmpty()) {
            boolean exists = validAttributeIds.contains(primaryAttribute);
            if (!exists) {
                primaryAttribute = "";
                removedCount++;
            }
        }

        // Clean up spellcastingAbility (references Attribute id)
        if (!spellcastingAbility.isEmpty()) {
            boolean exists = validAttributeIds.contains(spellcastingAbility);
            if (!exists) {
                spellcastingAbility = "";
                removedCount++;
            }
        }

        // Clean up savingThrowProficiencies (references Attribute ids)
        ArrayList<String> savingThrowProficiencies = arrayHandler.getObjectArray("savingThrowProficiencies");
        Iterator<String> saveIter = savingThrowProficiencies.iterator();
        while (saveIter.hasNext()) {
            String save = saveIter.next();
            boolean exists = validAttributeIds.contains(save);
            if (!exists) {
                saveIter.remove();
                removedCount++;
            }
        }

        // Clean up requiredAttributeScores (references Attribute ids)
        Iterator<Map.Entry<String, Integer>> requiredIter = requiredAttributeScores.entrySet().iterator();
        while (requiredIter.hasNext()) {
            Map.Entry<String, Integer> entry = requiredIter.next();
            String attributeId = Objects.toString(entry.getKey(), "");
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                requiredIter.remove();
                removedCount++;
            }
        }

        // Clean up automaticSkills (references Skill ids)
        ArrayList<String> automaticSkills = arrayHandler.getObjectArray("automaticSkills");
        Iterator<String> autoSkillIter = automaticSkills.iterator();
        while (autoSkillIter.hasNext()) {
            String skillId = autoSkillIter.next();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                autoSkillIter.remove();
                removedCount++;
            }
        }

        // Clean up classSkills (references Skill ids)
        ArrayList<String> classSkills = arrayHandler.getObjectArray("classSkills");
        Iterator<String> classSkillIter = classSkills.iterator();
        while (classSkillIter.hasNext()) {
            String skillId = classSkillIter.next();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                classSkillIter.remove();
                removedCount++;
            }
        }

        // Clean up automaticSkillsPerLevel (nested skill id lists)
        Iterator<Map.Entry<Integer, ArrayList<String>>> autoSkillPerLevelIter = automaticSkillsPerLevel.entrySet().iterator();
        while (autoSkillPerLevelIter.hasNext()) {
            Map.Entry<Integer, ArrayList<String>> entry = autoSkillPerLevelIter.next();
            ArrayList<String> skillNames = entry.getValue();
            int beforeSize = skillNames.size();
            skillNames.removeIf(skillId -> !validSkillIds.contains(skillId));
            removedCount += (beforeSize - skillNames.size());
            if (skillNames.isEmpty()) {
                autoSkillPerLevelIter.remove();
            }
        }

        // Clean up weaponProficiencies (references Weapon ids)
        ArrayList<String> weaponProficiencies = arrayHandler.getObjectArray("weaponProficiencies");
        Iterator<String> weaponIter = weaponProficiencies.iterator();
        while (weaponIter.hasNext()) {
            String weaponId = weaponIter.next();
            boolean exists = validWeaponIds.contains(weaponId);
            if (!exists) {
                weaponIter.remove();
                removedCount++;
            }
        }

        // Clean up armorProficiencies (references Armor ids)
        ArrayList<String> armorProficiencies = arrayHandler.getObjectArray("armorProficiencies");
        Iterator<String> armorIter = armorProficiencies.iterator();
        while (armorIter.hasNext()) {
            String armorId = armorIter.next();
            boolean exists = validArmorIds.contains(armorId);
            if (!exists) {
                armorIter.remove();
                removedCount++;
            }
        }

        // Clean up toolProficiencies (references Equipment ids)
        ArrayList<String> toolProficiencies = arrayHandler.getObjectArray("toolProficiencies");
        Iterator<String> toolIter = toolProficiencies.iterator();
        while (toolIter.hasNext()) {
            String toolId = toolIter.next();
            boolean exists = validEquipmentIds.contains(toolId);
            if (!exists) {
                toolIter.remove();
                removedCount++;
            }
        }

        // Clean up multiclassProficiencies (could reference weapons, armor, or equipment)
        // TODO: Determine if these should be validated against specific types or left freeform

        // Clean up forbiddenEquipment (references Equipment ids)
        ArrayList<String> forbiddenEquipment = arrayHandler.getObjectArray("forbiddenEquipment");
        Iterator<String> forbiddenIter = forbiddenEquipment.iterator();
        while (forbiddenIter.hasNext()) {
            String equipmentId = forbiddenIter.next();
            boolean exists = validEquipmentIds.contains(equipmentId);
            if (!exists) {
                forbiddenIter.remove();
                removedCount++;
            }
        }

        // TODO: spellLists - should reference SpellList names when that class exists
        // TODO: allowedAlignments - should alignments be a Game array?
        // TODO: availableArchetypes - should reference CharacterClass archetype names

        return removedCount;
    }
}
