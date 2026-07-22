/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

// REFACTORED: members block + initialized + no null checks

package com.gamemaker.gmrules.SupportElements;

import com.gamemaker.gmrules.*;

import com.gamemaker.gmrules.AtomicElements.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an ACTIVE EFFECT INSTANCE applied to a specific target.
 * Effect represents a SPECIFIC APPLICATION (the instance).
 *
 * Example: Effect "Poisoned" tracks "Bob is poisoned by Giant Spider, 3 rounds remaining, 1d4 damage/round".
 *
 * Supports tracking active status effects, buffs, debuffs, and conditions
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, and other systems.
 */
public class Effect extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // === INSTANCE IDENTITY ===
    // Tracks who/what/when
    private String source = "";                      // What created this (spell, ability, item, trap)
    private String sourceName = "";                  // Name of source ("Fireball", "Rage", "Dragon Breath")
    private String appliedBy = "";                   // Who applied it (character/creature name)
    private String appliedTo = "";                   // Who it's applied to (target name)

    // === APPLICATION CONTEXT ===
    // When and how THIS instance was created
    private String applicationTime = "";             // When applied (timestamp/round number)
    private int appliedAtLevel = 0;                  // Caster/character level when applied
    private int casterLevel = 0;                     // Caster level (for spells)
    private Map<String, Object> applicationContext = new LinkedHashMap<>(); // Additional context data

    // === DURATION TRACKING ===
    // How long THIS instance has left
    private int durationRemaining = 0;               // Rounds/turns/minutes remaining
    private String durationUnit = "";                // Unit: rounds, minutes, hours, days
    private int totalDuration = 0;                   // Original duration when applied
    private String expirationTime = "";              // When it expires (timestamp/round)
    private boolean concentrationRequired = false;   // Requires ongoing concentration
    private String concentratingEntity = "";         // Who must concentrate
    private int concentrationChecksMade = 0;         // Number of concentration checks made

    // === INTENSITY & STACKING ===
    // How strong THIS instance is right now
    private int intensity = 1;                       // Current intensity/power level
    private int baseIntensity = 1;                   // Original intensity when applied
    private int stackCount = 1;                      // Current number of stacks
    private int maximumStacks = 1;                   // Max stacks for this instance
    private boolean stacksAdjusted = false;          // Has stacking changed since application

    // === CURRENT STATE ===
    // What's happening with THIS instance RIGHT NOW
    private String state = "active";                 // active, suppressed, suspended, dormant, expired
    private boolean active = true;                   // Currently having effect
    private boolean suppressed = false;              // Temporarily suppressed by another effect
    private boolean suspended = false;               // Paused/on hold
    private String inactiveReason = "";              // Why not currently active

    // === ACTUAL MODIFIERS ===
    // What THIS instance is doing RIGHT NOW
    private Map<String, Integer> activeModifiers = new LinkedHashMap<>(); // Stat -> current modifier value
    private String damagePerInterval = "";           // Ongoing damage formula for THIS instance
    private String damageTypeId = "";
    private int damageDiceCount = 0;
    private int damageDiceSides = 0;
    private int damageDiceModifier = 0;
    private String healingPerInterval = "";          // Ongoing healing formula for THIS instance
    private int healingDiceCount = 0;
    private int healingDiceSides = 0;
    private int healingDiceModifier = 0;
    private int damageDealt = 0;                     // Total damage dealt by this instance
    private int healingProvided = 0;                 // Total healing provided by this instance

    // === INSTANCE TRIGGERS ===
    // When THIS specific instance triggers/updates
    private int triggerInterval = 0;                 // How often this instance triggers (in rounds)
    private int lastTriggered = 0;                   // Last round/turn when triggered
    private int triggerCount = 0;                    // How many times triggered so far

    // === SAVING THROWS ===
    // Saves made against THIS instance
    private boolean saveAllowed = false;             // Can target save against this instance
    private String saveType = "";                    // Type of save required
    private int saveDC = 0;                          // DC for THIS instance's saves
    private String saveFrequency = "";               // How often saves occur
    private int saveSuccesses = 0;                   // Successful saves made
    private int saveFailures = 0;                    // Failed saves made
    private int savesNeededToEnd = 0;                // Saves needed to remove this instance
    private boolean savedAgainst = false;            // Has target successfully saved

    // === REMOVAL TRACKING ===
    // How THIS instance can end
    private boolean canBeDispelled = true;           // Can be magically removed
    private int dispelDC = 0;                        // DC to dispel THIS instance
    private boolean removable = true;                // Can be removed early
    private boolean permanent = false;               // Cannot naturally expire

    // === STACKING & INTERACTION ===
    // How THIS instance interacts with other effects
    private int stackingPriority = 0;                // Priority for stacking resolution

    // === TRACKING & HISTORY ===
    // What has happened to THIS instance over time
    private int applicationsCount = 1;               // Times this effect has been reapplied
    private String creationTime = "";                // When this instance was created
    private Map<String, Object> instanceData = new LinkedHashMap<>();  // Runtime instance data

    // === NOTES & CUSTOM DATA ===
    // User/GM information about THIS instance
    private String notes = "";                       // Notes about this specific instance
    private Map<String, String> customFields = new LinkedHashMap<>(); // User-defined instance fields

    // === SYSTEM PROPERTIES ===
    private String systemType = "";                  // D&D, Pathfinder, GURPS, etc.
    private Map<String, Object> systemProperties = new LinkedHashMap<>(); // System-specific instance data
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Effect(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Effect(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("suppressedBy", new ArrayList<String>());
        arrayHandler.putArray("effectTypeKeys", new ArrayList<String>());
        arrayHandler.putArray("grantsAbilities", new ArrayList<String>());
        arrayHandler.putArray("removesAbilities", new ArrayList<String>());
        arrayHandler.putArray("conditions", new ArrayList<String>());
        arrayHandler.putArray("triggeredEvents", new ArrayList<String>());
        arrayHandler.putArray("removalAttempts", new ArrayList<String>());
        arrayHandler.putArray("currentlyStackingWith", new ArrayList<String>());
        arrayHandler.putArray("currentlySuppressing", new ArrayList<String>());
        arrayHandler.putArray("reapplicationTimes", new ArrayList<String>());
        arrayHandler.putArray("modificationHistory", new ArrayList<String>());
        arrayHandler.putArray("eventLog", new ArrayList<String>());
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

    // === INSTANCE IDENTITY METHODS ===

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getAppliedBy() { return appliedBy; }
    public void setAppliedBy(String appliedBy) { this.appliedBy = appliedBy; }

    public String getAppliedTo() { return appliedTo; }
    public void setAppliedTo(String appliedTo) { this.appliedTo = appliedTo; }

    // === APPLICATION CONTEXT METHODS ===

    public String getApplicationTime() { return applicationTime; }
    public void setApplicationTime(String applicationTime) {
        this.applicationTime = applicationTime;
    }

    public int getAppliedAtLevel() { return appliedAtLevel; }
    public void setAppliedAtLevel(int appliedAtLevel) { this.appliedAtLevel = Math.max(0, appliedAtLevel); }

    public int getCasterLevel() { return casterLevel; }
    public void setCasterLevel(int casterLevel) { this.casterLevel = Math.max(0, casterLevel); }

    public Map<String, Object> getApplicationContext() { return applicationContext; }
    public void setApplicationContext(Map<String, Object> applicationContext) {
        this.applicationContext = applicationContext;
    }
    public void addApplicationContext(String key, Object value) {
        if (!key.trim().isEmpty()) {
            applicationContext.put(key, value);
        }
    }

    // === DURATION TRACKING METHODS ===

    public int getDurationRemaining() { return durationRemaining; }
    public void setDurationRemaining(int durationRemaining) {
        this.durationRemaining = Math.max(0, durationRemaining);
    }

    public String getDurationUnit() { return durationUnit; }
    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = Math.max(0, totalDuration); }

    public String getExpirationTime() { return expirationTime; }
    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean requiresConcentration() { return concentrationRequired; }
    public void setConcentrationRequired(boolean concentrationRequired) {
        this.concentrationRequired = concentrationRequired;
    }

    public String getConcentratingEntity() { return concentratingEntity; }
    public void setConcentratingEntity(String concentratingEntity) {
        this.concentratingEntity = concentratingEntity;
    }

    public int getConcentrationChecksMade() { return concentrationChecksMade; }
    public void setConcentrationChecksMade(int concentrationChecksMade) {
        this.concentrationChecksMade = Math.max(0, concentrationChecksMade);
    }

    // === INTENSITY & STACKING METHODS ===

    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = Math.max(1, intensity); }

    public int getBaseIntensity() { return baseIntensity; }
    public void setBaseIntensity(int baseIntensity) { this.baseIntensity = Math.max(1, baseIntensity); }

    public int getStackCount() { return stackCount; }
    public void setStackCount(int stackCount) { this.stackCount = Math.max(1, stackCount); }

    public int getMaximumStacks() { return maximumStacks; }
    public void setMaximumStacks(int maximumStacks) { this.maximumStacks = Math.max(1, maximumStacks); }

    public boolean isStacksAdjusted() { return stacksAdjusted; }
    public void setStacksAdjusted(boolean stacksAdjusted) { this.stacksAdjusted = stacksAdjusted; }

    // === CURRENT STATE METHODS ===

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isSuppressed() { return suppressed; }
    public void setSuppressed(boolean suppressed) { this.suppressed = suppressed; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    public String getInactiveReason() { return inactiveReason; }
    public void setInactiveReason(String inactiveReason) {
        this.inactiveReason = inactiveReason;
    }

    // === ACTUAL MODIFIERS METHODS ===

    public Map<String, Integer> getActiveModifiers() { return activeModifiers; }
    public void setActiveModifiers(Map<String, Integer> activeModifiers) {
        this.activeModifiers = activeModifiers;
    }
    public void addModifier(String stat, int value) {
        if (!stat.trim().isEmpty()) {
            activeModifiers.put(stat, value);
        }
    }
    public boolean removeModifier(String stat) {
        return activeModifiers.remove(stat) != null;
    }

    public String getDamagePerInterval() {
        DiceSpec spec = getDamageDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return damagePerInterval;
    }

    public DiceSpec getDamageDiceSpec() {
        return new DiceSpec(Math.max(0, damageDiceCount), Math.max(0, damageDiceSides), damageDiceModifier);
    }

    public void setDamageDiceSpec(DiceSpec spec) {
        DiceSpec safe = Objects.requireNonNullElseGet(spec, DiceSpec::new);
        damageDiceCount = Math.max(0, safe.getCount());
        damageDiceSides = Math.max(0, safe.getSides());
        damageDiceModifier = safe.getModifier();
        if (safe.isDefined()) {
            damagePerInterval = safe.toNotation();
        }
    }

    public int getDamageDiceCount() { return Math.max(0, damageDiceCount); }
    public void setDamageDiceCount(int damageDiceCount) { this.damageDiceCount = Math.max(0, damageDiceCount); }

    public int getDamageDiceSides() { return Math.max(0, damageDiceSides); }
    public void setDamageDiceSides(int damageDiceSides) { this.damageDiceSides = Math.max(0, damageDiceSides); }

    public int getDamageDiceModifier() { return damageDiceModifier; }
    public void setDamageDiceModifier(int damageDiceModifier) { this.damageDiceModifier = damageDiceModifier; }
    public void setDamagePerInterval(String damagePerInterval) {
        this.damagePerInterval = Objects.toString(damagePerInterval, "").trim();
        DiceSpec parsed = DiceSpec.parseSimpleNotation(this.damagePerInterval);
        if (parsed.isDefined()) {
            damageDiceCount = parsed.getCount();
            damageDiceSides = parsed.getSides();
            damageDiceModifier = parsed.getModifier();
        }
    }

    public String getDamageTypeId() { return Objects.toString(damageTypeId, "").trim(); }
    public void setDamageTypeId(String damageTypeId) {
        this.damageTypeId = Objects.toString(damageTypeId, "").trim();
    }

    public String getHealingPerInterval() {
        DiceSpec spec = getHealingDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return healingPerInterval;
    }

    public DiceSpec getHealingDiceSpec() {
        return new DiceSpec(Math.max(0, healingDiceCount), Math.max(0, healingDiceSides), healingDiceModifier);
    }

    public void setHealingDiceSpec(DiceSpec spec) {
        DiceSpec safe = Objects.requireNonNullElseGet(spec, DiceSpec::new);
        healingDiceCount = Math.max(0, safe.getCount());
        healingDiceSides = Math.max(0, safe.getSides());
        healingDiceModifier = safe.getModifier();
        if (safe.isDefined()) {
            healingPerInterval = safe.toNotation();
        }
    }

    public int getHealingDiceCount() { return Math.max(0, healingDiceCount); }
    public void setHealingDiceCount(int healingDiceCount) { this.healingDiceCount = Math.max(0, healingDiceCount); }

    public int getHealingDiceSides() { return Math.max(0, healingDiceSides); }
    public void setHealingDiceSides(int healingDiceSides) { this.healingDiceSides = Math.max(0, healingDiceSides); }

    public int getHealingDiceModifier() { return healingDiceModifier; }
    public void setHealingDiceModifier(int healingDiceModifier) { this.healingDiceModifier = healingDiceModifier; }
    public void setHealingPerInterval(String healingPerInterval) {
        this.healingPerInterval = Objects.toString(healingPerInterval, "").trim();
        DiceSpec parsed = DiceSpec.parseSimpleNotation(this.healingPerInterval);
        if (parsed.isDefined()) {
            healingDiceCount = parsed.getCount();
            healingDiceSides = parsed.getSides();
            healingDiceModifier = parsed.getModifier();
        }
    }

    public int getDamageDealt() { return damageDealt; }
    public void setDamageDealt(int damageDealt) { this.damageDealt = Math.max(0, damageDealt); }

    public int getHealingProvided() { return healingProvided; }
    public void setHealingProvided(int healingProvided) { this.healingProvided = Math.max(0, healingProvided); }

    // === INSTANCE TRIGGERS METHODS ===

    public int getTriggerInterval() { return triggerInterval; }
    public void setTriggerInterval(int triggerInterval) {
        this.triggerInterval = Math.max(0, triggerInterval);
    }

    public int getLastTriggered() { return lastTriggered; }
    public void setLastTriggered(int lastTriggered) { this.lastTriggered = Math.max(0, lastTriggered); }

    public int getTriggerCount() { return triggerCount; }
    public void setTriggerCount(int triggerCount) { this.triggerCount = Math.max(0, triggerCount); }

    // === SAVING THROWS METHODS ===

    public boolean isSaveAllowed() { return saveAllowed; }
    public void setSaveAllowed(boolean saveAllowed) { this.saveAllowed = saveAllowed; }

    public String getSaveType() { return saveType; }
    public void setSaveType(String saveType) { this.saveType = saveType; }

    public int getSaveDC() { return saveDC; }
    public void setSaveDC(int saveDC) { this.saveDC = Math.max(0, saveDC); }

    public String getSaveFrequency() { return saveFrequency; }
    public void setSaveFrequency(String saveFrequency) {
        this.saveFrequency = saveFrequency;
    }

    public int getSaveSuccesses() { return saveSuccesses; }
    public void setSaveSuccesses(int saveSuccesses) {
        this.saveSuccesses = Math.max(0, saveSuccesses);
    }

    public int getSaveFailures() { return saveFailures; }
    public void setSaveFailures(int saveFailures) {
        this.saveFailures = Math.max(0, saveFailures);
    }

    public int getSavesNeededToEnd() { return savesNeededToEnd; }
    public void setSavesNeededToEnd(int savesNeededToEnd) {
        this.savesNeededToEnd = Math.max(0, savesNeededToEnd);
    }

    public boolean isSavedAgainst() { return savedAgainst; }
    public void setSavedAgainst(boolean savedAgainst) { this.savedAgainst = savedAgainst; }

    // === REMOVAL TRACKING METHODS ===

    public boolean canBeDispelled() { return canBeDispelled; }
    public void setCanBeDispelled(boolean canBeDispelled) { this.canBeDispelled = canBeDispelled; }

    public int getDispelDC() { return dispelDC; }
    public void setDispelDC(int dispelDC) { this.dispelDC = Math.max(0, dispelDC); }

    public boolean isRemovable() { return removable; }
    public void setRemovable(boolean removable) { this.removable = removable; }

    public boolean isPermanent() { return permanent; }
    public void setPermanent(boolean permanent) { this.permanent = permanent; }

    // === STACKING & INTERACTION METHODS ===

    public int getStackingPriority() { return stackingPriority; }
    public void setStackingPriority(int stackingPriority) { this.stackingPriority = stackingPriority; }

    // === TRACKING & HISTORY METHODS ===

    public int getApplicationsCount() { return applicationsCount; }
    public void setApplicationsCount(int applicationsCount) {
        this.applicationsCount = Math.max(1, applicationsCount);
    }

    public String getCreationTime() { return creationTime; }
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Map<String, Object> getInstanceData() { return instanceData; }
    public void setInstanceData(Map<String, Object> instanceData) {
        this.instanceData = instanceData;
    }
    public void addInstanceData(String key, Object value) {
        if (!key.trim().isEmpty()) {
            instanceData.put(key, value);
        }
    }

    // === NOTES & CUSTOM DATA METHODS ===

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Map<String, String> getCustomFields() { return customFields; }
    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
    public void addCustomField(String key, String value) {
        if (!key.trim().isEmpty()) {
            customFields.put(key, value);
        }
    }
    public boolean removeCustomField(String key) {
        return customFields.remove(key) != null;
    }

    // === TYPE METHODS ===
    public ArrayList<String> getEffectTypeKeys() {
        return ensureEffectTypeKeys();
    }

    public void setEffectTypeKeys(java.util.Collection<String> effectTypeKeys) {
        ensureEffectTypeKeys();
        arrayHandler.replaceArray("effectTypeKeys", normalizeEffectTypeKeys(effectTypeKeys));
    }

    public void addEffectTypeKey(String effectTypeKey) {
        ArrayList<String> keys = ensureEffectTypeKeys();
        String safeKey = Objects.toString(effectTypeKey, "").trim();
        if (safeKey.isEmpty()) {
            return;
        }
        keys.add(safeKey);
    }

    public boolean removeEffectTypeKey(String effectTypeKey) {
        ArrayList<String> keys = ensureEffectTypeKeys();
        String safeKey = Objects.toString(effectTypeKey, "").trim();
        if (safeKey.isEmpty()) {
            return false;
        }
        return keys.remove(safeKey);
    }

    // === SYSTEM PROPERTIES METHODS ===

    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) {
        this.systemProperties = systemProperties;
    }
    public void addSystemProperty(String key, Object value) {
        if (!key.trim().isEmpty()) {
            systemProperties.put(key, value);
        }
    }
    public boolean removeSystemProperty(String key) {
        return systemProperties.remove(key) != null;
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game (for interaction tracking)
     * @param validEffectTypeKeys Set of valid EffectType keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validEffectIds,
        Set<String> validEffectTypeKeys,
        Set<String> validDamageTypeIds
    ) {
        int removedCount = 0;

        ArrayList<String> effectTypeKeys = ensureEffectTypeKeys();
        Iterator<String> typeIter = effectTypeKeys.iterator();
        while (typeIter.hasNext()) {
            String key = normalizeEffectTypeKey(typeIter.next());
            boolean exists = validEffectTypeKeys.contains(key);
            if (!exists) {
                typeIter.remove();
                removedCount++;
            }
        }

        // Clean up suppressedBy (references to other Effect ids)
        ArrayList<String> suppressedBy = arrayHandler.getObjectArray("suppressedBy");
        Iterator<String> suppressedByIter = suppressedBy.iterator();
        while (suppressedByIter.hasNext()) {
            String effectId = suppressedByIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                suppressedByIter.remove();
                removedCount++;
            }
        }

        // Clean up currentlyStackingWith (references to other Effect ids)
        ArrayList<String> currentlyStackingWith = arrayHandler.getObjectArray("currentlyStackingWith");
        Iterator<String> stackingIter = currentlyStackingWith.iterator();
        while (stackingIter.hasNext()) {
            String effectId = stackingIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                stackingIter.remove();
                removedCount++;
            }
        }

        // Clean up currentlySuppressing (references to other Effect ids)
        ArrayList<String> currentlySuppressing = arrayHandler.getObjectArray("currentlySuppressing");
        Iterator<String> suppressingIter = currentlySuppressing.iterator();
        while (suppressingIter.hasNext()) {
            String effectId = suppressingIter.next();
            boolean exists = validEffectIds.contains(effectId);
            if (!exists) {
                suppressingIter.remove();
                removedCount++;
            }
        }

        damageTypeId = getDamageTypeId();
        if (!damageTypeId.isEmpty() && !validDamageTypeIds.contains(damageTypeId)) {
            damageTypeId = "";
            removedCount++;
        }

        return removedCount;
    }

    private ArrayList<String> ensureEffectTypeKeys() {
        ArrayList<String> keys = arrayHandler.getObjectArray("effectTypeKeys");
        if (keys == null) {
            keys = new ArrayList<>();
            arrayHandler.putArray("effectTypeKeys", keys);
        }
        return keys;
    }

    private ArrayList<String> normalizeEffectTypeKeys(java.util.Collection<String> keys) {
        ArrayList<String> normalized = new ArrayList<>();
        java.util.Collection<String> safeKeys = Objects.requireNonNullElse(keys, java.util.List.of());
        for (String key : safeKeys) {
            String safeKey = Objects.toString(key, "").trim();
            if (!safeKey.isEmpty()) {
                normalized.add(safeKey);
            }
        }
        return normalized;
    }

    private String normalizeEffectTypeKey(String key) {
        return Objects.toString(key, "").trim().toLowerCase();
    }
}
