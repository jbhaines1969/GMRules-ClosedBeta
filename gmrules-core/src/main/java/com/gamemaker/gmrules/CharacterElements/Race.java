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

import com.gamemaker.gmrules.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a playable character race in any RPG system.
 * Extends Species with cultural, social, and player-character specific traits.
 * Handles both PC races (Humans, Elves) and NPC races (Goblins, Orcs) consistently.
 * For non-intelligent creatures, use Creature class instead.
 */
public class Race extends Species implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Race Configuration
    private boolean isPlayable = true;
    private String parentRace = "";

    // Cultural and Social (Race-specific)
    private String society = "";
    private String culture = "";

    // Racial Skills - Active abilities this race can perform
    // Examples: Stonecunning, Weapon Familiarity, Keen Senses
    // Skills reference races via limitedToRaces, Equipment has racial restrictions

    // Racial Traits - Passive effects from simply being this race
    // Examples: Darkvision, Poison Resistance, Fire Immunity, Fey Ancestry
    // Stored as Effect names for consistency with equipment/spell effects

    // NOTE: Basic reproduction (reproductionType, canCrossbreed, compatibleSpecies) moved to Species

    // Hybrid and Cultural Reproduction (Race-specific)
    private Map<String, String> offspringByRace = new LinkedHashMap<>();
    private boolean isHybrid = false;
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Race(String name) {
        super(name);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("favoredClasses", new ArrayList<String>());
        arrayHandler.putArray("typicalAlignments", new ArrayList<String>());
        arrayHandler.putArray("racialSkills", new ArrayList<String>());
        arrayHandler.putArray("racialTraitNames", new ArrayList<String>());
        arrayHandler.putArray("parentRaces", new ArrayList<String>());
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

    // Race Configuration
    public boolean isPlayable() { return isPlayable; }
    public void setPlayable(boolean isPlayable) { this.isPlayable = isPlayable; }

    public String getParentRace() { return parentRace; }
    public void setParentRace(String parentRace) { this.parentRace = parentRace; }

    // Cultural and Social - Full CRUD following Skill pattern

    // Cultural variants are now handled as separate Race entries with parentRace reference

    public String getSociety() { return society; }
    public void setSociety(String society) { this.society = society; }

    public String getCulture() { return culture; }
    public void setCulture(String culture) { this.culture = culture; }

    // Racial Skills - Full CRUD following Skill pattern
    // Racial Traits - Full CRUD following Skill pattern
    // Basic reproduction methods (reproductionType, canCrossbreed, compatibleSpecies) inherited from Species

    // Hybrid and Cultural Reproduction - Full CRUD following Skill pattern

    public Map<String, String> getOffspringByRace() { return offspringByRace; }
    public void setOffspringByRace(Map<String, String> offspringByRace) { this.offspringByRace = offspringByRace; }
    public void addOffspringType(String racePartner, String offspringType) {
        if (!racePartner.trim().isEmpty()) {
            offspringByRace.put(racePartner, offspringType);
        }
    }
    public boolean removeOffspringType(String racePartner) {
        return offspringByRace.remove(racePartner) != null;
    }

    public boolean isHybrid() { return isHybrid; }
    public void setHybrid(boolean isHybrid) { this.isHybrid = isHybrid; }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @param validSkillIds Set of valid Skill ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validEffectIds,
        Set<String> validClassIds,
        Set<String> validRaceIds,
        Set<String> validSkillIds,
        Set<String> validAttributeIds,
        Set<String> validMovementTypeKeys
    ) {
        int removedCount = super.cleanupOrphanedReferences(
            validAttributeIds,
            validMovementTypeKeys,
            validRaceIds
        );

        // Clean up parentRace reference
        if (!parentRace.isEmpty()) {
            boolean exists = validRaceIds.contains(parentRace);
            if (!exists) {
                parentRace = "";
                removedCount++;
            }
        }

        // Clean up favored classes
        ArrayList<String> favoredClasses = arrayHandler.getObjectArray("favoredClasses");
        Iterator<String> classIter = favoredClasses.iterator();
        while (classIter.hasNext()) {
            String classId = classIter.next();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classIter.remove();
                removedCount++;
            }
        }

        // TODO: typicalAlignments - Should alignments be a Game array?
        // Currently freeform - no validation

        // Clean up racial skills
        ArrayList<String> racialSkills = arrayHandler.getObjectArray("racialSkills");
        Iterator<String> skillIter = racialSkills.iterator();
        while (skillIter.hasNext()) {
            String skillId = skillIter.next();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                skillIter.remove();
                removedCount++;
            }
        }

        // Clean up racial trait ids (Effects)
        ArrayList<String> racialTraitNames = arrayHandler.getObjectArray("racialTraitNames");
        Iterator<String> traitIter = racialTraitNames.iterator();
        while (traitIter.hasNext()) {
            String traitId = traitIter.next();
            boolean exists = validEffectIds.contains(traitId);
            if (!exists) {
                traitIter.remove();
                removedCount++;
            }
        }

        // Clean up offspring by race (keys should be race names)
        Iterator<Map.Entry<String, String>> offspringIter = offspringByRace.entrySet().iterator();
        while (offspringIter.hasNext()) {
            Map.Entry<String, String> entry = offspringIter.next();
            boolean partnerExists = validRaceIds.contains(entry.getKey());
            boolean offspringExists = validRaceIds.contains(entry.getValue());
            if (!partnerExists || !offspringExists) {
                offspringIter.remove();
                removedCount++;
            }
        }

        // Clean up parent races (hybrid)
        ArrayList<String> parentRaces = arrayHandler.getObjectArray("parentRaces");
        Iterator<String> parentIter = parentRaces.iterator();
        while (parentIter.hasNext()) {
            String parentName = parentIter.next();
            boolean exists = validRaceIds.contains(parentName);
            if (!exists) {
                parentIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
