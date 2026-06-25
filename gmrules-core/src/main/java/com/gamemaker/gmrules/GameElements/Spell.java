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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a spell in any RPG magic system.
 * Covers casting mechanics, components, schools, metamagic, and effects
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, and other systems.
 */
public class Spell extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Spell Properties
    private String school = "";
    private String subschool = "";
    private int level = 0;
    private Map<String, Integer> classLevels = new LinkedHashMap<>();
    // Casting Mechanics
    private String castingTime = "";
    private String range = "";
    private String duration = "";
    private String area = "";
    private String target = "";
    private String savingThrow = "";

    // Components and Requirements
    private boolean verbalComponent = false;
    private boolean somaticComponent = false;
    private boolean focusComponent = false;
    private String focusDescription = "";
    private boolean divineComponent = false;

    // Advanced Casting
    private boolean isRitual = false;
    private String ritualTime = "";
    private boolean concentration = false;
    private String concentrationDuration = "";
    private boolean canCounterspell = true;

    // Spell Effects and Damage
    private String effect = "";
    private String damageFormula = "";
    private int damageDiceCount = 0;
    private int damageDiceSides = 0;
    private int damageDiceModifier = 0;
    private int maximumDamage = 0;
    private String healingFormula = "";
    private int healingDiceCount = 0;
    private int healingDiceSides = 0;
    private int healingDiceModifier = 0;
    private int maximumHealing = 0;
    private String secondaryEffect = "";

    // Scaling and Metamagic
    private boolean canScale = false;
    private String scalingType = "";
    private String scalingFormula = "";
    private boolean canMetamagic = true;
    private Map<String, String> metamagicEffects = new LinkedHashMap<>();

    // Spell Lists and Availability
    private boolean isCantrip = false;
    private boolean isOrison = false;
    private String spellType = "";

    // Restrictions and Limitations
    private boolean requiresLineOfSight = true;
    private boolean requiresLineOfEffect = true;
    private String oppositionSchool = "";

    // Spell Interaction
    private boolean isPermanent = false;
    private String permanencyRequirements = "";
    private String dismissal = "";

    // Cultural and Thematic
    private String tradition = "";
    private String culture = "";
    private String mythology = "";
    private String appearance = "";
    private String castingGesture = "";

    // Spell Research and Learning
    private int researchDC = 0;
    private String researchTime = "";
    private int researchCost = 0;
    private String creationMethod = "";
    private boolean canTeach = true;

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();

    // Generic Array Registry
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Spell(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Spell(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

    public Spell(String name, String description, String school, int level) {
        super(name, description);
        this.school = school;
        this.level = Math.max(0, level);
        initializeArrayRegistry();
    }

// *** METHODS ***

    private void initializeArrayRegistry() {
        arrayHandler.putArray("materialComponents", new ArrayList<String>());
        arrayHandler.putArray("metamagicRestrictions", new ArrayList<String>());
        arrayHandler.putArray("classList", new ArrayList<String>());
        arrayHandler.putArray("domains", new ArrayList<String>());
        arrayHandler.putArray("castingRestrictions", new ArrayList<String>());
        arrayHandler.putArray("dispelableBy", new ArrayList<String>());
        arrayHandler.putArray("stacksWith", new ArrayList<String>());
        arrayHandler.putArray("suppressedBy", new ArrayList<String>());
        arrayHandler.putArray("alternativeNames", new ArrayList<String>());
        arrayHandler.putArray("prerequisites", new ArrayList<String>());
        arrayHandler.putArray("effectNames", new ArrayList<String>());
        arrayHandler.putArray("schoolKeys", new ArrayList<String>());
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
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getSubschool() { return subschool; }
    public void setSubschool(String subschool) { this.subschool = subschool; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(0, level); }

    public Map<String, Integer> getClassLevels() { return classLevels; }
    public void setClassLevels(Map<String, Integer> classLevels) { this.classLevels = classLevels; }
    public void addClassLevel(String className, int level) {
        if (!className.trim().isEmpty()) {
            classLevels.put(className, Math.max(0, level));
        }
    }

    // Casting Mechanics
    public String getCastingTime() { return castingTime; }
    public void setCastingTime(String castingTime) { this.castingTime = castingTime; }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getSavingThrow() { return savingThrow; }
    public void setSavingThrow(String savingThrow) { this.savingThrow = savingThrow; }

    // Components
    public boolean hasVerbalComponent() { return verbalComponent; }
    public void setVerbalComponent(boolean verbalComponent) { this.verbalComponent = verbalComponent; }

    public boolean hasSomaticComponent() { return somaticComponent; }
    public void setSomaticComponent(boolean somaticComponent) { this.somaticComponent = somaticComponent; }

    public boolean hasFocusComponent() { return focusComponent; }
    public void setFocusComponent(boolean focusComponent) { this.focusComponent = focusComponent; }

    public String getFocusDescription() { return focusDescription; }
    public void setFocusDescription(String focusDescription) { this.focusDescription = focusDescription; }

    public boolean hasDivineComponent() { return divineComponent; }
    public void setDivineComponent(boolean divineComponent) { this.divineComponent = divineComponent; }

    // Advanced Casting
    public boolean isRitual() { return isRitual; }
    public void setRitual(boolean isRitual) { this.isRitual = isRitual; }

    public String getRitualTime() { return ritualTime; }
    public void setRitualTime(String ritualTime) { this.ritualTime = ritualTime; }

    public boolean requiresConcentration() { return concentration; }
    public void setConcentration(boolean concentration) { this.concentration = concentration; }

    public String getConcentrationDuration() { return concentrationDuration; }
    public void setConcentrationDuration(String concentrationDuration) { this.concentrationDuration = concentrationDuration; }

    public boolean canCounterspell() { return canCounterspell; }
    public void setCanCounterspell(boolean canCounterspell) { this.canCounterspell = canCounterspell; }

    // Effects and Damage
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }

    public String getDamageFormula() {
        DiceSpec spec = getDamageDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return damageFormula;
    }

    public void setDamageFormula(String damageFormula) {
        this.damageFormula = Objects.toString(damageFormula, "").trim();
        DiceSpec parsed = DiceSpec.parseSimpleNotation(this.damageFormula);
        if (parsed.isDefined()) {
            this.damageDiceCount = parsed.getCount();
            this.damageDiceSides = parsed.getSides();
            this.damageDiceModifier = parsed.getModifier();
        }
    }

    public int getDamageDiceCount() { return Math.max(0, damageDiceCount); }
    public void setDamageDiceCount(int damageDiceCount) { this.damageDiceCount = Math.max(0, damageDiceCount); }

    public int getDamageDiceSides() { return Math.max(0, damageDiceSides); }
    public void setDamageDiceSides(int damageDiceSides) { this.damageDiceSides = Math.max(0, damageDiceSides); }

    public int getDamageDiceModifier() { return damageDiceModifier; }
    public void setDamageDiceModifier(int damageDiceModifier) { this.damageDiceModifier = damageDiceModifier; }

    public DiceSpec getDamageDiceSpec() {
        return new DiceSpec(getDamageDiceCount(), getDamageDiceSides(), getDamageDiceModifier());
    }

    public void setDamageDiceSpec(DiceSpec spec) {
        DiceSpec safe = Objects.requireNonNullElseGet(spec, DiceSpec::new);
        setDamageDiceCount(safe.getCount());
        setDamageDiceSides(safe.getSides());
        setDamageDiceModifier(safe.getModifier());
        if (safe.isDefined()) {
            this.damageFormula = safe.toNotation();
        }
    }

    public int getMaximumDamage() { return maximumDamage; }
    public void setMaximumDamage(int maximumDamage) { this.maximumDamage = Math.max(0, maximumDamage); }

    public String getHealingFormula() {
        DiceSpec spec = getHealingDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return healingFormula;
    }

    public void setHealingFormula(String healingFormula) {
        this.healingFormula = Objects.toString(healingFormula, "").trim();
        DiceSpec parsed = DiceSpec.parseSimpleNotation(this.healingFormula);
        if (parsed.isDefined()) {
            this.healingDiceCount = parsed.getCount();
            this.healingDiceSides = parsed.getSides();
            this.healingDiceModifier = parsed.getModifier();
        }
    }

    public int getHealingDiceCount() { return Math.max(0, healingDiceCount); }
    public void setHealingDiceCount(int healingDiceCount) { this.healingDiceCount = Math.max(0, healingDiceCount); }

    public int getHealingDiceSides() { return Math.max(0, healingDiceSides); }
    public void setHealingDiceSides(int healingDiceSides) { this.healingDiceSides = Math.max(0, healingDiceSides); }

    public int getHealingDiceModifier() { return healingDiceModifier; }
    public void setHealingDiceModifier(int healingDiceModifier) { this.healingDiceModifier = healingDiceModifier; }

    public DiceSpec getHealingDiceSpec() {
        return new DiceSpec(getHealingDiceCount(), getHealingDiceSides(), getHealingDiceModifier());
    }

    public void setHealingDiceSpec(DiceSpec spec) {
        DiceSpec safe = Objects.requireNonNullElseGet(spec, DiceSpec::new);
        setHealingDiceCount(safe.getCount());
        setHealingDiceSides(safe.getSides());
        setHealingDiceModifier(safe.getModifier());
        if (safe.isDefined()) {
            this.healingFormula = safe.toNotation();
        }
    }

    public int getMaximumHealing() { return maximumHealing; }
    public void setMaximumHealing(int maximumHealing) { this.maximumHealing = Math.max(0, maximumHealing); }

    public String getSecondaryEffect() { return secondaryEffect; }
    public void setSecondaryEffect(String secondaryEffect) { this.secondaryEffect = secondaryEffect; }

    // Scaling and Metamagic
    public boolean canScale() { return canScale; }
    public void setCanScale(boolean canScale) { this.canScale = canScale; }

    public String getScalingType() { return scalingType; }
    public void setScalingType(String scalingType) { this.scalingType = scalingType; }

    public String getScalingFormula() { return scalingFormula; }
    public void setScalingFormula(String scalingFormula) { this.scalingFormula = scalingFormula; }

    public boolean canMetamagic() { return canMetamagic; }
    public void setCanMetamagic(boolean canMetamagic) { this.canMetamagic = canMetamagic; }

    public Map<String, String> getMetamagicEffects() { return metamagicEffects; }
    public void setMetamagicEffects(Map<String, String> metamagicEffects) { this.metamagicEffects = metamagicEffects; }
    public void addMetamagicEffect(String metamagic, String effect) {
        if (!metamagic.trim().isEmpty()) {
            metamagicEffects.put(metamagic, effect);
        }
    }

    public boolean isCantrip() { return isCantrip; }
    public void setCantrip(boolean isCantrip) { this.isCantrip = isCantrip; }

    public boolean isOrison() { return isOrison; }
    public void setOrison(boolean isOrison) { this.isOrison = isOrison; }

    public String getSpellType() { return spellType; }
    public void setSpellType(String spellType) { this.spellType = spellType; }

    public boolean requiresLineOfSight() { return requiresLineOfSight; }
    public void setRequiresLineOfSight(boolean requiresLineOfSight) { this.requiresLineOfSight = requiresLineOfSight; }

    public boolean requiresLineOfEffect() { return requiresLineOfEffect; }
    public void setRequiresLineOfEffect(boolean requiresLineOfEffect) { this.requiresLineOfEffect = requiresLineOfEffect; }

    public String getOppositionSchool() { return oppositionSchool; }
    public void setOppositionSchool(String oppositionSchool) { this.oppositionSchool = oppositionSchool; }

    public boolean isPermanent() { return isPermanent; }
    public void setPermanent(boolean isPermanent) { this.isPermanent = isPermanent; }

    public String getPermanencyRequirements() { return permanencyRequirements; }
    public void setPermanencyRequirements(String permanencyRequirements) { this.permanencyRequirements = permanencyRequirements; }

    public String getDismissal() { return dismissal; }
    public void setDismissal(String dismissal) { this.dismissal = dismissal; }

    // Cultural
    public String getTradition() { return tradition; }
    public void setTradition(String tradition) { this.tradition = tradition; }

    public String getCulture() { return culture; }
    public void setCulture(String culture) { this.culture = culture; }

    public String getMythology() { return mythology; }
    public void setMythology(String mythology) { this.mythology = mythology; }

    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    public String getCastingGesture() { return castingGesture; }
    public void setCastingGesture(String castingGesture) { this.castingGesture = castingGesture; }

    // Research
    public int getResearchDC() { return researchDC; }
    public void setResearchDC(int researchDC) { this.researchDC = Math.max(0, researchDC); }

    public String getResearchTime() { return researchTime; }
    public void setResearchTime(String researchTime) { this.researchTime = researchTime; }

    public int getResearchCost() { return researchCost; }
    public void setResearchCost(int researchCost) { this.researchCost = Math.max(0, researchCost); }

    public String getCreationMethod() { return creationMethod; }
    public void setCreationMethod(String creationMethod) { this.creationMethod = creationMethod; }

    public boolean canTeach() { return canTeach; }
    public void setCanTeach(boolean canTeach) { this.canTeach = canTeach; }

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

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validSpellIds Set of valid Spell ids currently in the game
     * @param validSpellSchoolKeys Set of valid spell school keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validEffectIds,
        Set<String> validClassIds,
        Set<String> validSpellIds,
        Set<String> validSpellSchoolKeys
    ) {
        int removedCount = 0;

        // Clean up school (references spell school keys)
        if (!school.isEmpty()) {
            boolean exists = validSpellSchoolKeys.contains(school);
            if (!exists) {
                school = "";
                removedCount++;
            }
        }

        // Clean up school keys list
        ArrayList<String> schoolKeys = getArray("schoolKeys");
        Iterator<String> schoolIter = schoolKeys.iterator();
        while (schoolIter.hasNext()) {
            String schoolKey = schoolIter.next();
            boolean exists = validSpellSchoolKeys.contains(schoolKey);
            if (!exists) {
                schoolIter.remove();
                removedCount++;
            }
        }

        // Clean up effect names
        ArrayList<String> effectNames = getArray("effectNames");
        Iterator<String> effectIter = effectNames.iterator();
        while (effectIter.hasNext()) {
            String effectId = effectIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                effectIter.remove();
                removedCount++;
            }
        }

        // Clean up classLevels Map (keys are CharacterClass ids)
        Iterator<Map.Entry<String, Integer>> classLevelIter = classLevels.entrySet().iterator();
        while (classLevelIter.hasNext()) {
            Map.Entry<String, Integer> entry = classLevelIter.next();
            String classId = entry.getKey();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classLevelIter.remove();
                removedCount++;
            }
        }

        // Clean up classList ArrayList (CharacterClass ids)
        ArrayList<String> classList = getArray("classList");
        Iterator<String> classListIter = classList.iterator();
        while (classListIter.hasNext()) {
            String classId = classListIter.next();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classListIter.remove();
                removedCount++;
            }
        }

        // Clean up prerequisites ArrayList (can reference other Spells)
        ArrayList<String> prerequisites = getArray("prerequisites");
        Iterator<String> prereqIter = prerequisites.iterator();
        while (prereqIter.hasNext()) {
            String prereq = prereqIter.next();

            // Prerequisites can be Spells or other elements (feats, etc.)
            // Only remove if it matches a spell name pattern and doesn't exist
            boolean exists = validSpellIds.contains(prereq);
            if (!exists) {
                prereqIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
