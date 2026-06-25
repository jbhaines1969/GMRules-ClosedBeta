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

import com.gamemaker.gmrules.AtomicElements.*;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.CharacterElements.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a biological species in any RPG system.
 * Contains shared traits between playable races and creatures including physical properties,
 * aging, movement, senses, resistances, and environmental adaptations.
 * Serves as base class for Race (PCs/NPCs) and Creature (monsters/animals/constructs).
 */
public abstract class Species extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Physical Properties
    private String speciesType = "";
    private String subtype = "";
    private String size = "";
    private String bodyType = "";
    private int baseHeight = 0;
    private int heightVariation = 0;
    private int baseWeight = 0;
    private int weightVariation = 0;

    // Lifespan and Aging
    private int maturityAge = 18;
    private int middleAge = 35;
    private int oldAge = 53;
    private int venerableAge = 70;
    private int maximumAge = 100;
    private boolean agingAffectsStats = false;

    // Ability Score Modifiers
    private Map<String, Integer> abilityModifiers = new LinkedHashMap<>();
    private boolean hasFlexibleModifiers = false;
    private int flexibleBonusAmount = 0;
    private ArrayList<AttributeScoreLimit> attributeScoreLimits = new ArrayList<>();

    // Movement (Senses now handled by Skills)
    private int baseSpeed = 30;
    private Map<String, Integer> movementTypes = new LinkedHashMap<>();

    // Resistances, Immunities, and Save Modifiers
    private Map<String, Integer> saveModifiers = new LinkedHashMap<>();
    // NOTE: Damage/Condition/Energy resistances and immunities now handled via Race.racialTraitIds (Effects)

    // Natural Traits and Abilities now handled by Skills with limitedToRaces

    // Environmental Adaptation
    private boolean aquatic = false;
    private boolean amphibious = false;
    private String breathingType = "";

    // Natural Weapons now handled by Skills with limitedToRaces

    // Magic and Spell Resistance
    private boolean hasSpellResistance = false;
    private int spellResistanceValue = 0;
    // NOTE: Spell resistance types now handled via Race.racialTraitIds (Effects)

    // Special Properties
    private boolean isUndead = false;
    private boolean isConstruct = false;
    private boolean requiresSustenance = true;
    private String sustenanceType = "";
    // NOTE: Shapechanging now handled via Race.racialTraitIds (Effects)

    // Reproduction and Genetics (Species-level)
    private String reproductionType = "";
    private boolean canCrossbreed = false;

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

    // *** CONSTRUCTORS ***
    public Species(String name) {
        super(name);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("flexibleModifierChoices", new ArrayList<String>());
        arrayHandler.putArray("preferredEnvironments", new ArrayList<String>());
        arrayHandler.putArray("environmentalAdaptations", new ArrayList<String>());
        arrayHandler.putArray("climateAdaptations", new ArrayList<String>());
        arrayHandler.putArray("compatibleSpecies", new ArrayList<String>());
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
    public String getSpeciesType() { return speciesType; }
    public void setSpeciesType(String speciesType) { this.speciesType = speciesType; }

    public String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }

    // Physical Characteristics
    public int getBaseHeight() { return baseHeight; }
    public void setBaseHeight(int baseHeight) { this.baseHeight = Math.max(0, baseHeight); }

    public int getHeightVariation() { return heightVariation; }
    public void setHeightVariation(int heightVariation) { this.heightVariation = Math.max(0, heightVariation); }

    public int getBaseWeight() { return baseWeight; }
    public void setBaseWeight(int baseWeight) { this.baseWeight = Math.max(0, baseWeight); }

    public int getWeightVariation() { return weightVariation; }
    public void setWeightVariation(int weightVariation) { this.weightVariation = Math.max(0, weightVariation); }

    // Aging - Complete CRUD following Skill pattern
    public int getMaturityAge() { return maturityAge; }
    public void setMaturityAge(int maturityAge) { this.maturityAge = Math.max(0, maturityAge); }

    public int getMiddleAge() { return middleAge; }
    public void setMiddleAge(int middleAge) { this.middleAge = Math.max(0, middleAge); }

    public int getOldAge() { return oldAge; }
    public void setOldAge(int oldAge) { this.oldAge = Math.max(0, oldAge); }

    public int getVenerableAge() { return venerableAge; }
    public void setVenerableAge(int venerableAge) { this.venerableAge = Math.max(0, venerableAge); }

    public int getMaximumAge() { return maximumAge; }
    public void setMaximumAge(int maximumAge) { this.maximumAge = Math.max(1, maximumAge); }

    public boolean agingAffectsStats() { return agingAffectsStats; }
    public void setAgingAffectsStats(boolean agingAffectsStats) { this.agingAffectsStats = agingAffectsStats; }

    // Ability Modifiers - Full CRUD following Skill pattern
    public Map<String, Integer> getAbilityModifiers() { return abilityModifiers; }
    public void setAbilityModifiers(Map<String, Integer> abilityModifiers) { this.abilityModifiers = abilityModifiers; }
    public void addAbilityModifier(String ability, int modifier) {
        if (!ability.trim().isEmpty()) {
            abilityModifiers.put(ability, modifier);
        }
    }
    public boolean removeAbilityModifier(String ability) {
        return abilityModifiers.remove(ability) != null;
    }

    public boolean hasFlexibleModifiers() { return hasFlexibleModifiers; }
    public void setHasFlexibleModifiers(boolean hasFlexibleModifiers) { this.hasFlexibleModifiers = hasFlexibleModifiers; }

    public int getFlexibleBonusAmount() { return flexibleBonusAmount; }
    public void setFlexibleBonusAmount(int flexibleBonusAmount) { this.flexibleBonusAmount = Math.max(0, flexibleBonusAmount); }

    public List<AttributeScoreLimit> getAttributeScoreLimits() {
        return copyAttributeScoreLimits(attributeScoreLimits);
    }

    public void setAttributeScoreLimits(Collection<AttributeScoreLimit> attributeScoreLimits) {
        this.attributeScoreLimits = copyAttributeScoreLimits(attributeScoreLimits);
    }

    public void addAttributeScoreLimit(String attributeId, int min, int max) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        attributeScoreLimits.add(new AttributeScoreLimit(safeId, min, max));
    }

    public boolean removeAttributeScoreLimit(String attributeId) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            return false;
        }
        boolean removed = false;
        Iterator<AttributeScoreLimit> iter = attributeScoreLimits.iterator();
        while (iter.hasNext()) {
            AttributeScoreLimit limit = iter.next();
            if (safeId.equals(limit.getAttributeId())) {
                iter.remove();
                removed = true;
            }
        }
        return removed;
    }

    public void clearAttributeScoreLimits() {
        attributeScoreLimits.clear();
    }

    // Movement and Senses - Full CRUD following Skill pattern
    public int getBaseSpeed() { return baseSpeed; }
    public void setBaseSpeed(int baseSpeed) { this.baseSpeed = Math.max(0, baseSpeed); }

    public Map<String, Integer> getMovementTypes() { return movementTypes; }
    public void setMovementTypes(Map<String, Integer> movementTypes) { this.movementTypes = movementTypes; }
    public void addMovementType(String type, int speed) {
        if (!type.trim().isEmpty()) {
            movementTypes.put(type, Math.max(0, speed));
        }
    }
    public boolean removeMovementType(String type) {
        return movementTypes.remove(type) != null;
    }

    // Senses now handled by Skills with limitedToRaces

    // Resistances and Immunities now handled via Race.racialTraitIds (Effects)

    // Save Modifiers - Full CRUD following Skill pattern
    public Map<String, Integer> getSaveModifiers() { return saveModifiers; }
    public void setSaveModifiers(Map<String, Integer> saveModifiers) { this.saveModifiers = saveModifiers; }
    public void addSaveModifier(String saveType, int modifier) {
        if (!saveType.trim().isEmpty()) {
            saveModifiers.put(saveType, modifier);
        }
    }
    public boolean removeSaveModifier(String saveType) {
        return saveModifiers.remove(saveType) != null;
    }

    // Natural Traits and Abilities now handled by Skills - see Race.racialSkills

    // Environmental Adaptation - Full CRUD following Skill pattern
    public boolean isAquatic() { return aquatic; }
    public void setAquatic(boolean aquatic) { this.aquatic = aquatic; }

    public boolean isAmphibious() { return amphibious; }
    public void setAmphibious(boolean amphibious) { this.amphibious = amphibious; }

    public String getBreathingType() { return breathingType; }
    public void setBreathingType(String breathingType) { this.breathingType = breathingType; }

    // Natural Weapons now handled by Skills - see Race.racialSkills

    // Magic Resistance - Full CRUD following Skill pattern
    public boolean hasSpellResistance() { return hasSpellResistance; }
    public void setHasSpellResistance(boolean hasSpellResistance) { this.hasSpellResistance = hasSpellResistance; }

    public int getSpellResistanceValue() { return spellResistanceValue; }
    public void setSpellResistanceValue(int spellResistanceValue) { this.spellResistanceValue = Math.max(0, spellResistanceValue); }

    // Spell resistance types now handled via Race.racialTraitIds (Effects)

    // Special Properties
    public boolean isUndead() { return isUndead; }
    public void setUndead(boolean isUndead) { this.isUndead = isUndead; }

    public boolean isConstruct() { return isConstruct; }
    public void setConstruct(boolean isConstruct) { this.isConstruct = isConstruct; }

    public boolean requiresSustenance() { return requiresSustenance; }
    public void setRequiresSustenance(boolean requiresSustenance) { this.requiresSustenance = requiresSustenance; }

    public String getSustenanceType() { return sustenanceType; }
    public void setSustenanceType(String sustenanceType) { this.sustenanceType = sustenanceType; }

    // Shapechanging now handled via Race.racialTraitIds (Effects)

    // Reproduction and Genetics - Full CRUD following Skill pattern
    public String getReproductionType() { return reproductionType; }
    public void setReproductionType(String reproductionType) { this.reproductionType = reproductionType; }

    public boolean canCrossbreed() { return canCrossbreed; }
    public void setCanCrossbreed(boolean canCrossbreed) { this.canCrossbreed = canCrossbreed; }

    // System Properties - Full CRUD following Skill pattern
    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) { this.systemProperties = systemProperties; }
    public void addSystemProperty(String key, Object value) {
        if (!key.trim().isEmpty()) {
            systemProperties.put(key, value);
        }
    }
    public boolean removeSystemProperty(String key) {
        return systemProperties.remove(key) != null;
    }

    public static class AttributeScoreLimit implements Serializable {

    // *** MEMBERS ***
        private static final long serialVersionUID = 1L;
        private String attributeId = "";
        private int min = 0;
        private int max = 0;

    // *** CONSTRUCTORS ***
        public AttributeScoreLimit(String attributeId, int min, int max) {
            this.attributeId = Objects.toString(attributeId, "");
            this.min = min;
            this.max = max;
        }

        public AttributeScoreLimit(AttributeScoreLimit source) {
            AttributeScoreLimit safeSource = Objects.requireNonNullElse(source, new AttributeScoreLimit("", 0, 0));
            this.attributeId = Objects.toString(safeSource.attributeId, "");
            this.min = safeSource.min;
            this.max = safeSource.max;
        }

    // *** METHODS ***
        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = Objects.toString(attributeId, "");
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validAttributeIds Set of valid Attribute ids currently in the game
     * @param validMovementTypeKeys Set of valid movement type keys currently in the game
     * @param validSpeciesIds Set of valid Species/Race ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validAttributeIds, Set<String> validMovementTypeKeys, Set<String> validSpeciesIds) {
        int removedCount = 0;

        // Clean up abilityModifiers (keys are Attribute ids)
        Iterator<Map.Entry<String, Integer>> abilityIter = abilityModifiers.entrySet().iterator();
        while (abilityIter.hasNext()) {
            Map.Entry<String, Integer> entry = abilityIter.next();
            String attributeId = entry.getKey();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                abilityIter.remove();
                removedCount++;
            }
        }

        // Clean up flexibleModifierChoices (references Attribute ids)
        ArrayList<String> flexibleModifierChoices = arrayHandler.getObjectArray("flexibleModifierChoices");
        Iterator<String> flexIter = flexibleModifierChoices.iterator();
        while (flexIter.hasNext()) {
            String attributeId = flexIter.next();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                flexIter.remove();
                removedCount++;
            }
        }

        // Clean up attributeScoreLimits (attribute ids)
        Iterator<AttributeScoreLimit> limitIter = attributeScoreLimits.iterator();
        while (limitIter.hasNext()) {
            AttributeScoreLimit limit = limitIter.next();
            String attributeId = limit.getAttributeId();
            boolean exists = validAttributeIds.contains(attributeId);
            if (!exists) {
                limitIter.remove();
                removedCount++;
            }
        }

        // Clean up movementTypes (keys reference movement type keys)
        Iterator<Map.Entry<String, Integer>> moveIter = movementTypes.entrySet().iterator();
        while (moveIter.hasNext()) {
            Map.Entry<String, Integer> entry = moveIter.next();
            String moveType = entry.getKey();
            boolean exists = validMovementTypeKeys.contains(moveType);
            if (!exists) {
                moveIter.remove();
                removedCount++;
            }
        }

        // Clean up compatibleSpecies (references Species/Race ids)
        ArrayList<String> compatibleSpecies = arrayHandler.getObjectArray("compatibleSpecies");
        Iterator<String> speciesIter = compatibleSpecies.iterator();
        while (speciesIter.hasNext()) {
            String speciesId = speciesIter.next();
            boolean exists = validSpeciesIds.contains(speciesId);
            if (!exists) {
                speciesIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    private ArrayList<AttributeScoreLimit> copyAttributeScoreLimits(Collection<AttributeScoreLimit> limits) {
        Collection<AttributeScoreLimit> safeLimits = Objects.requireNonNullElse(limits, List.of());
        ArrayList<AttributeScoreLimit> copy = new ArrayList<>();
        for (AttributeScoreLimit limit : safeLimits) {
            copy.add(new AttributeScoreLimit(limit));
        }
        return copy;
    }

}
