/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks

package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a creature (monster, animal, construct) in any RPG system.
 * Extends Species with combat statistics, behavioral patterns, and encounter mechanics.
 * For intelligent races that can be PCs/NPCs, use Race class instead.
 * Covers creatures from D&D, Pathfinder, GURPS, and other RPG systems.
 */
public class Creature extends Species implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Combat Statistics
    private double challengeRating = 0.0;            // Challenge Rating (D&D) or equivalent
    private int experienceValue = 0;                 // XP awarded for defeating
    private String hitDice = "";                     // Hit dice formula (e.g., "4d8+12")
    private int hitPoints = 0;                       // Average hit points
    private int armorClass = 10;                     // Base armor class
    private String naturalArmor = "";                // Natural armor description

    // Ability Scores (if different from species defaults)
    private Map<String, Integer> abilityScores = new LinkedHashMap<>(); // Ability -> score
    private Map<String, Integer> savingThrows = new LinkedHashMap<>();  // Save -> bonus

    // Combat Actions and Abilities
    private Map<String, String> attackDetails = new LinkedHashMap<>();  // Attack -> full details
    private Map<String, String> specialAbilities = new LinkedHashMap<>(); // Ability -> description

    // Skills and Feats (Creature-specific)
    private Map<String, Integer> skills = new LinkedHashMap<>();        // Skill -> modifier

    // Behavioral and Ecological
    private String alignment = "";                   // Creature's alignment
    private String organization = "";                // How they group (solitary, pack, etc.)
    private String treasure = "";                    // Treasure type/amount
    private String advancement = "";                 // How creature can advance
    private String levelAdjustment = "";             // LA if used as PC race

    // Environment and Ecology
    private String climate = "";                     // Preferred climate
    private String terrain = "";                     // Preferred terrain
    private String activityCycle = "";               // When active (day, night, etc.)
    private String diet = "";                        // What they eat
    private int encounterFrequency = 0;              // How commonly encountered

    // Behavior Patterns
    private String intelligence = "";                // Intelligence level description
    private String temperament = "";                 // Aggressive, passive, curious, etc.
    private String tactics = "";                     // Combat tactics description
    private String socialStructure = "";             // Pack hierarchy, etc.

    // Reproduction and Lifecycle
    private String maturationRate = "";              // How fast they mature
    private String lifespanCategory = "";            // Short, average, long-lived
    private String reproductionMethod = "";          // How they reproduce
    private int offspringCount = 0;                  // Typical offspring number
    private String gestationPeriod = "";             // Time to birth/hatching

    // Creature Variants and Scaling
    private boolean hasVariants = false;             // Has different variants
    private Map<String, String> variantDetails = new LinkedHashMap<>(); // Variant -> differences
    private boolean canAdvance = false;              // Can gain HD/levels
    private String advancementRules = "";            // How advancement works

    // Economic and Social Impact
    private boolean isTrainable = false;             // Can be trained/tamed
    private String domestication = "";               // Domestication difficulty
    private String economicValue = "";               // Trade/economic importance
    private Map<String, String> partValues = new LinkedHashMap<>();     // Part -> value/use

    // Magical Properties
    private boolean isMagicalBeast = false;          // Innately magical
    private String magicAura = "";                   // Type of magic aura
    private boolean affectedBySpells = true;        // Normal spell interaction

    // Encounter and Adventure Hooks
    private String role = "";                        // Ecological/story role

    // System-Specific Combat
    private String combatRating = "";                // System-specific rating
    private Map<String, String> systemCombatData = new LinkedHashMap<>(); // System -> combat data
    private boolean usesTactics = false;             // Uses complex tactics
    private String combatStyle = "";                 // Fighting style description
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Creature(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Creature(String name, String description) {
        super(name);
        setDescription(description);
        initializeArrayRegistry();
    }

    public Creature(String name, String description, String speciesType) {
        super(name);
        setDescription(description);
        setSpeciesType(speciesType);
        initializeArrayRegistry();
    }

    public Creature(String name, String description, String speciesType, double challengeRating) {
        super(name);
        setDescription(description);
        setSpeciesType(speciesType);
        this.challengeRating = Math.max(0.0, challengeRating);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("attacks", new ArrayList<String>());
        arrayHandler.putArray("specialAttacks", new ArrayList<String>());
        arrayHandler.putArray("specialDefenses", new ArrayList<String>());
        arrayHandler.putArray("feats", new ArrayList<String>());
        arrayHandler.putArray("behaviorTriggers", new ArrayList<String>());
        arrayHandler.putArray("variants", new ArrayList<String>());
        arrayHandler.putArray("templateOptions", new ArrayList<String>());
        arrayHandler.putArray("usefulParts", new ArrayList<String>());
        arrayHandler.putArray("magicalComponents", new ArrayList<String>());
        arrayHandler.putArray("spellImmunities", new ArrayList<String>());
        arrayHandler.putArray("encounterTypes", new ArrayList<String>());
        arrayHandler.putArray("plotHooks", new ArrayList<String>());
        arrayHandler.putArray("allies", new ArrayList<String>());
        arrayHandler.putArray("enemies", new ArrayList<String>());
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

    public java.util.Set<String> getArrayNames() {
        return arrayHandler.getArrayNames();
    }

    // Combat Statistics
    public double getChallengeRating() { return challengeRating; }
    public void setChallengeRating(double challengeRating) { this.challengeRating = Math.max(0.0, challengeRating); }

    public int getExperienceValue() { return experienceValue; }
    public void setExperienceValue(int experienceValue) { this.experienceValue = Math.max(0, experienceValue); }

    public String getHitDice() { return hitDice; }
    public void setHitDice(String hitDice) { this.hitDice = hitDice; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = Math.max(0, hitPoints); }

    public int getArmorClass() { return armorClass; }
    public void setArmorClass(int armorClass) { this.armorClass = Math.max(0, armorClass); }

    public String getNaturalArmor() { return naturalArmor; }
    public void setNaturalArmor(String naturalArmor) { this.naturalArmor = naturalArmor; }

    // Ability Scores
    public Map<String, Integer> getAbilityScores() { return abilityScores; }
    public void setAbilityScores(Map<String, Integer> abilityScores) {
        this.abilityScores = abilityScores;
    }
    public void addAbilityScore(String ability, int score) {
        if (!ability.trim().isEmpty()) {
            abilityScores.put(ability, Math.max(1, score));
        }
    }
    public boolean removeAbilityScore(String ability) {
        return abilityScores.remove(ability) != null;
    }

    public Map<String, Integer> getSavingThrows() { return savingThrows; }
    public void setSavingThrows(Map<String, Integer> savingThrows) {
        this.savingThrows = savingThrows;
    }
    public void addSavingThrow(String saveType, int bonus) {
        if (!saveType.trim().isEmpty()) {
            savingThrows.put(saveType, bonus);
        }
    }
    public boolean removeSavingThrow(String saveType) {
        return savingThrows.remove(saveType) != null;
    }

    public Map<String, String> getAttackDetails() { return attackDetails; }
    public void setAttackDetails(Map<String, String> attackDetails) {
        this.attackDetails = attackDetails;
    }
    public void addAttackDetail(String attack, String details) {
        if (!attack.trim().isEmpty()) {
            attackDetails.put(attack, details);
        }
    }
    public boolean removeAttackDetail(String attack) {
        return attackDetails.remove(attack) != null;
    }

    public Map<String, String> getSpecialAbilities() { return specialAbilities; }
    public void setSpecialAbilities(Map<String, String> specialAbilities) {
        this.specialAbilities = specialAbilities;
    }
    public void addSpecialAbility(String ability, String description) {
        if (!ability.trim().isEmpty()) {
            specialAbilities.put(ability, description);
        }
    }
    public boolean removeSpecialAbility(String ability) {
        return specialAbilities.remove(ability) != null;
    }

    // Skills and Knowledge
    public Map<String, Integer> getSkills() { return skills; }
    public void setSkills(Map<String, Integer> skills) {
        this.skills = skills;
    }
    public void addSkill(String skill, int modifier) {
        if (!skill.trim().isEmpty()) {
            skills.put(skill, modifier);
        }
    }
    public boolean removeSkill(String skill) {
        return skills.remove(skill) != null;
    }

    // Behavioral and Ecological Properties
    public String getAlignment() { return alignment; }
    public void setAlignment(String alignment) { this.alignment = alignment; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getTreasure() { return treasure; }
    public void setTreasure(String treasure) { this.treasure = treasure; }

    public String getAdvancement() { return advancement; }
    public void setAdvancement(String advancement) { this.advancement = advancement; }

    public String getLevelAdjustment() { return levelAdjustment; }
    public void setLevelAdjustment(String levelAdjustment) { this.levelAdjustment = levelAdjustment; }

    // Environment and Ecology
    public String getClimate() { return climate; }
    public void setClimate(String climate) { this.climate = climate; }

    public String getTerrain() { return terrain; }
    public void setTerrain(String terrain) { this.terrain = terrain; }

    public String getActivityCycle() { return activityCycle; }
    public void setActivityCycle(String activityCycle) { this.activityCycle = activityCycle; }

    public String getDiet() { return diet; }
    public void setDiet(String diet) { this.diet = diet; }

    public int getEncounterFrequency() { return encounterFrequency; }
    public void setEncounterFrequency(int encounterFrequency) { this.encounterFrequency = Math.max(0, encounterFrequency); }

    // Behavior Patterns
    public String getIntelligence() { return intelligence; }
    public void setIntelligence(String intelligence) { this.intelligence = intelligence; }

    public String getTemperament() { return temperament; }
    public void setTemperament(String temperament) { this.temperament = temperament; }

    public String getTactics() { return tactics; }
    public void setTactics(String tactics) { this.tactics = tactics; }

    public String getSocialStructure() { return socialStructure; }
    public void setSocialStructure(String socialStructure) { this.socialStructure = socialStructure; }

    // Reproduction and Lifecycle
    public String getMaturationRate() { return maturationRate; }
    public void setMaturationRate(String maturationRate) { this.maturationRate = maturationRate; }

    public String getLifespanCategory() { return lifespanCategory; }
    public void setLifespanCategory(String lifespanCategory) { this.lifespanCategory = lifespanCategory; }

    public String getReproductionMethod() { return reproductionMethod; }
    public void setReproductionMethod(String reproductionMethod) { this.reproductionMethod = reproductionMethod; }

    public int getOffspringCount() { return offspringCount; }
    public void setOffspringCount(int offspringCount) { this.offspringCount = Math.max(0, offspringCount); }

    public String getGestationPeriod() { return gestationPeriod; }
    public void setGestationPeriod(String gestationPeriod) { this.gestationPeriod = gestationPeriod; }

    // Creature Variants and Scaling
    public boolean hasVariants() { return hasVariants; }
    public void setHasVariants(boolean hasVariants) { this.hasVariants = hasVariants; }

    public Map<String, String> getVariantDetails() { return variantDetails; }
    public void setVariantDetails(Map<String, String> variantDetails) {
        this.variantDetails = variantDetails;
    }
    public void addVariantDetail(String variant, String details) {
        if (!variant.trim().isEmpty()) {
            variantDetails.put(variant, details);
        }
    }
    public boolean removeVariantDetail(String variant) {
        return variantDetails.remove(variant) != null;
    }

    public boolean canAdvance() { return canAdvance; }
    public void setCanAdvance(boolean canAdvance) { this.canAdvance = canAdvance; }

    public String getAdvancementRules() { return advancementRules; }
    public void setAdvancementRules(String advancementRules) { this.advancementRules = advancementRules; }

    // Economic and Social Impact
    public boolean isTrainable() { return isTrainable; }
    public void setTrainable(boolean isTrainable) { this.isTrainable = isTrainable; }

    public String getDomestication() { return domestication; }
    public void setDomestication(String domestication) { this.domestication = domestication; }

    public String getEconomicValue() { return economicValue; }
    public void setEconomicValue(String economicValue) { this.economicValue = economicValue; }

    public Map<String, String> getPartValues() { return partValues; }
    public void setPartValues(Map<String, String> partValues) {
        this.partValues = partValues;
    }
    public void addPartValue(String part, String value) {
        if (!part.trim().isEmpty()) {
            partValues.put(part, value);
        }
    }
    public boolean removePartValue(String part) {
        return partValues.remove(part) != null;
    }

    // Magical Properties
    public boolean isMagicalBeast() { return isMagicalBeast; }
    public void setMagicalBeast(boolean isMagicalBeast) { this.isMagicalBeast = isMagicalBeast; }

    public String getMagicAura() { return magicAura; }
    public void setMagicAura(String magicAura) { this.magicAura = magicAura; }

    public boolean affectedBySpells() { return affectedBySpells; }
    public void setAffectedBySpells(boolean affectedBySpells) { this.affectedBySpells = affectedBySpells; }

    // Encounter and Adventure Hooks
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // System-Specific Combat
    public String getCombatRating() { return combatRating; }
    public void setCombatRating(String combatRating) { this.combatRating = combatRating; }

    public Map<String, String> getSystemCombatData() { return systemCombatData; }
    public void setSystemCombatData(Map<String, String> systemCombatData) {
        this.systemCombatData = systemCombatData;
    }
    public void addSystemCombatData(String system, String data) {
        if (!system.trim().isEmpty()) {
            systemCombatData.put(system, data);
        }
    }
    public boolean removeSystemCombatData(String system) {
        return systemCombatData.remove(system) != null;
    }

    public boolean usesTactics() { return usesTactics; }
    public void setUsesTactics(boolean usesTactics) { this.usesTactics = usesTactics; }

    public String getCombatStyle() { return combatStyle; }
    public void setCombatStyle(String combatStyle) { this.combatStyle = combatStyle; }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }

        // Creature-specific validation
        // Challenge rating should be reasonable
        if (challengeRating < 0.0 || challengeRating > 50.0) {
            // Allow but warn - some systems might have extreme CRs
        }

        // XP value should align with CR (rough validation)
        if (experienceValue > 0 && challengeRating > 0.0 && (experienceValue < challengeRating * 10 || experienceValue > challengeRating * 10000)) {
            // Allow but warn - XP/CR ratios vary by system
        }

        // Hit points should be positive if specified
        if (hitPoints < 0) {
            return false;
        }

        return true;
    }
}
