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
import com.gamemaker.gmrules.SupportElements.Effect;

import com.gamemaker.gmrules.CharacterElements.*;
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
 * Represents a weapon in any RPG system.
 * Covers damage systems, weapon properties, proficiency requirements, and special abilities
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, Savage Worlds, and other systems.
 */
public class Weapon extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Properties
    private String weaponType = "";
    private String weaponCategory = "";
    private String weaponGroup = "";
    private String size = "";
    private double weight = 0.0;
    private String weightUnit = "";
    private String cost = "";

    // Damage System
    private String damageRoll = "";
    private int damageDiceCount = 0;
    private int damageDiceSides = 0;
    private int damageDiceModifier = 0;
    private String criticalRange = "";
    private String criticalMultiplier = "";
    private Map<String, String> damageBySize = new LinkedHashMap<>();

    // Range and Reach
    private int reach = 5;
    private Map<String, Integer> ranges = new LinkedHashMap<>();
    private int shortRange = 0;
    private int mediumRange = 0;
    private int longRange = 0;
    private int extremeRange = 0;

    // Weapon Properties and Qualities
    private boolean isMagical = false;
    private int enhancementBonus = 0;

    // Proficiency and Requirements
    private int minimumStrength = 0;
    private int minimumDexterity = 0;

    // Ammunition and Loading
    private boolean requiresAmmunition = false;
    private String ammunitionType = "";
    private int ammunitionCapacity = 0;
    private String loadingTime = "";
    private boolean isThrown = false;
    private boolean returnsWhenThrown = false;

    // Durability and Maintenance
    private int hardness = 0;
    private int hitPoints = 0;
    private int breakingPoint = 0;
    private boolean isIndestructible = false;

    // Combat Modifiers
    private Map<String, Integer> attackModifiers = new LinkedHashMap<>();
    private Map<String, Integer> damageModifiers = new LinkedHashMap<>();
    private boolean allowsParrying = false;
    private int parryModifier = 0;

    // Weapon Speed and Initiative
    private int weaponSpeed = 0;
    private int initiativeModifier = 0;
    private int attacksPerRound = 1;
    private boolean isQuickDraw = false;

    // Material and Construction
    private String material = "";
    private String craftsmanship = "";
    private String origin = "";
    private String creator = "";
    private boolean isMasterwork = false;

    // Special Abilities and Powers
    private Map<String, String> powerDescriptions = new LinkedHashMap<>();
    private Map<String, Integer> powerUsesPerDay = new LinkedHashMap<>();
    private boolean hasCharges = false;
    private int currentCharges = 0;
    private int maximumCharges = 0;

    // Appearance and Flavor
    private String appearance = "";
    private String history = "";
    private boolean isArtifact = false;
    private String artifactLevel = "";

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Weapon(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Weapon(String name, String description) {
        super(name);
        setDescription(description);
        initializeArrayRegistry();
    }

    public Weapon(String name, String description, String weaponType) {
        super(name);
        setDescription(description);
        this.weaponType = weaponType;
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("weaponProperties", new ArrayList<String>());
        arrayHandler.putArray("specialQualities", new ArrayList<String>());
        arrayHandler.putArray("enchantments", new ArrayList<String>());
        arrayHandler.putArray("effects", new ArrayList<Effect>());
        arrayHandler.putArray("proficiencyRequired", new ArrayList<String>());
        arrayHandler.putArray("restrictedClasses", new ArrayList<String>());
        arrayHandler.putArray("favoredClasses", new ArrayList<String>());
        arrayHandler.putArray("prerequisites", new ArrayList<String>());
        arrayHandler.putArray("vulnerabilities", new ArrayList<String>());
        arrayHandler.putArray("combatManeuvers", new ArrayList<String>());
        arrayHandler.putArray("activePowers", new ArrayList<String>());
        arrayHandler.putArray("passivePowers", new ArrayList<String>());
        arrayHandler.putArray("notableFeatures", new ArrayList<String>());
    }

    private void ensureArrayRegistry() {
        ensureArray("weaponProperties", new ArrayList<String>());
        ensureArray("specialQualities", new ArrayList<String>());
        ensureArray("enchantments", new ArrayList<String>());
        ensureArray("effects", new ArrayList<Effect>());
        ensureArray("proficiencyRequired", new ArrayList<String>());
        ensureArray("restrictedClasses", new ArrayList<String>());
        ensureArray("favoredClasses", new ArrayList<String>());
        ensureArray("prerequisites", new ArrayList<String>());
        ensureArray("vulnerabilities", new ArrayList<String>());
        ensureArray("combatManeuvers", new ArrayList<String>());
        ensureArray("activePowers", new ArrayList<String>());
        ensureArray("passivePowers", new ArrayList<String>());
        ensureArray("notableFeatures", new ArrayList<String>());
    }

    private void ensureArray(String arrayName, ArrayList<?> fallback) {
        if (arrayHandler.getObjectArray(arrayName) == null) {
            arrayHandler.putArray(arrayName, fallback);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        arrayHandler = Objects.requireNonNullElseGet(arrayHandler, ArrayHandler::new);
        ensureArrayRegistry();
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
    public String getWeaponType() { return weaponType; }
    public void setWeaponType(String weaponType) { this.weaponType = weaponType; }

    public String getWeaponCategory() { return weaponCategory; }
    public void setWeaponCategory(String weaponCategory) { this.weaponCategory = weaponCategory; }

    public String getWeaponGroup() { return weaponGroup; }
    public void setWeaponGroup(String weaponGroup) { this.weaponGroup = weaponGroup; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = Math.max(0.0, weight); }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = Objects.toString(weightUnit, ""); }

    public String getCost() { return cost; }
    public void setCost(String cost) { this.cost = cost; }

    // Damage System
    public String getDamageRoll() {
        DiceSpec spec = getDamageDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return damageRoll;
    }

    /**
     * Legacy setter; also attempts to populate unambiguous dice fields from simple NdM+K notation.
     */
    public void setDamageRoll(String damageRoll) {
        this.damageRoll = Objects.toString(damageRoll, "").trim();
        DiceSpec parsed = DiceSpec.parseSimpleNotation(this.damageRoll);
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
            this.damageRoll = safe.toNotation();
        }
    }

    public String getCriticalRange() { return criticalRange; }
    public void setCriticalRange(String criticalRange) { this.criticalRange = criticalRange; }

    public String getCriticalMultiplier() { return criticalMultiplier; }
    public void setCriticalMultiplier(String criticalMultiplier) { this.criticalMultiplier = criticalMultiplier; }

    public Map<String, String> getDamageBySize() { return damageBySize; }
    public void setDamageBySize(Map<String, String> damageBySize) { this.damageBySize = damageBySize; }
    public void addDamageBySize(String size, String damage) {
        if (!size.trim().isEmpty()) {
            damageBySize.put(size, damage);
        }
    }

    // Range Properties
    public int getReach() { return reach; }
    public void setReach(int reach) { this.reach = Math.max(0, reach); }

    public Map<String, Integer> getRanges() { return ranges; }
    public void setRanges(Map<String, Integer> ranges) { this.ranges = ranges; }
    public void addRange(String type, int distance) {
        if (!type.trim().isEmpty()) {
            ranges.put(type, Math.max(0, distance));
        }
    }

    public int getShortRange() { return shortRange; }
    public void setShortRange(int shortRange) { this.shortRange = Math.max(0, shortRange); }

    public int getMediumRange() { return mediumRange; }
    public void setMediumRange(int mediumRange) { this.mediumRange = Math.max(0, mediumRange); }

    public int getLongRange() { return longRange; }
    public void setLongRange(int longRange) { this.longRange = Math.max(0, longRange); }

    public int getExtremeRange() { return extremeRange; }
    public void setExtremeRange(int extremeRange) { this.extremeRange = Math.max(0, extremeRange); }

    // Weapon Properties
    public boolean isMagical() { return isMagical; }
    public void setMagical(boolean isMagical) { this.isMagical = isMagical; }

    public int getEnhancementBonus() { return enhancementBonus; }
    public void setEnhancementBonus(int enhancementBonus) { this.enhancementBonus = Math.max(0, enhancementBonus); }

    // Proficiency and Requirements
    public int getMinimumStrength() { return minimumStrength; }
    public void setMinimumStrength(int minimumStrength) { this.minimumStrength = Math.max(0, minimumStrength); }

    public int getMinimumDexterity() { return minimumDexterity; }
    public void setMinimumDexterity(int minimumDexterity) { this.minimumDexterity = Math.max(0, minimumDexterity); }

    // Ammunition
    public boolean requiresAmmunition() { return requiresAmmunition; }
    public void setRequiresAmmunition(boolean requiresAmmunition) { this.requiresAmmunition = requiresAmmunition; }

    public String getAmmunitionType() { return ammunitionType; }
    public void setAmmunitionType(String ammunitionType) { this.ammunitionType = ammunitionType; }

    public int getAmmunitionCapacity() { return ammunitionCapacity; }
    public void setAmmunitionCapacity(int ammunitionCapacity) { this.ammunitionCapacity = Math.max(0, ammunitionCapacity); }

    public String getLoadingTime() { return loadingTime; }
    public void setLoadingTime(String loadingTime) { this.loadingTime = loadingTime; }

    public boolean isThrown() { return isThrown; }
    public void setThrown(boolean isThrown) { this.isThrown = isThrown; }

    public boolean returnsWhenThrown() { return returnsWhenThrown; }
    public void setReturnsWhenThrown(boolean returnsWhenThrown) { this.returnsWhenThrown = returnsWhenThrown; }

    // Durability
    public int getHardness() { return hardness; }
    public void setHardness(int hardness) { this.hardness = Math.max(0, hardness); }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = Math.max(0, hitPoints); }

    public int getBreakingPoint() { return breakingPoint; }
    public void setBreakingPoint(int breakingPoint) { this.breakingPoint = Math.max(0, breakingPoint); }

    public boolean isIndestructible() { return isIndestructible; }
    public void setIndestructible(boolean isIndestructible) { this.isIndestructible = isIndestructible; }

    // Combat Modifiers
    public Map<String, Integer> getAttackModifiers() { return attackModifiers; }
    public void setAttackModifiers(Map<String, Integer> attackModifiers) { this.attackModifiers = attackModifiers; }
    public void addAttackModifier(String situation, int modifier) {
        if (!situation.trim().isEmpty()) {
            attackModifiers.put(situation, modifier);
        }
    }

    public Map<String, Integer> getDamageModifiers() { return damageModifiers; }
    public void setDamageModifiers(Map<String, Integer> damageModifiers) { this.damageModifiers = damageModifiers; }
    public void addDamageModifier(String situation, int modifier) {
        if (!situation.trim().isEmpty()) {
            damageModifiers.put(situation, modifier);
        }
    }

    public boolean allowsParrying() { return allowsParrying; }
    public void setAllowsParrying(boolean allowsParrying) { this.allowsParrying = allowsParrying; }

    public int getParryModifier() { return parryModifier; }
    public void setParryModifier(int parryModifier) { this.parryModifier = parryModifier; }

    // Speed and Initiative
    public int getWeaponSpeed() { return weaponSpeed; }
    public void setWeaponSpeed(int weaponSpeed) { this.weaponSpeed = Math.max(0, weaponSpeed); }

    public int getInitiativeModifier() { return initiativeModifier; }
    public void setInitiativeModifier(int initiativeModifier) { this.initiativeModifier = initiativeModifier; }

    public int getAttacksPerRound() { return attacksPerRound; }
    public void setAttacksPerRound(int attacksPerRound) { this.attacksPerRound = Math.max(1, attacksPerRound); }

    public boolean isQuickDraw() { return isQuickDraw; }
    public void setQuickDraw(boolean isQuickDraw) { this.isQuickDraw = isQuickDraw; }

    // Material and Construction
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getCraftsmanship() { return craftsmanship; }
    public void setCraftsmanship(String craftsmanship) { this.craftsmanship = craftsmanship; }

    public boolean isMasterwork() { return isMasterwork; }
    public void setMasterwork(boolean isMasterwork) { this.isMasterwork = isMasterwork; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    // Special Powers
    public Map<String, String> getPowerDescriptions() { return powerDescriptions; }
    public void setPowerDescriptions(Map<String, String> powerDescriptions) { this.powerDescriptions = powerDescriptions; }
    public void addPowerDescription(String power, String description) {
        if (!power.trim().isEmpty()) {
            powerDescriptions.put(power, description);
        }
    }

    public Map<String, Integer> getPowerUsesPerDay() { return powerUsesPerDay; }
    public void setPowerUsesPerDay(Map<String, Integer> powerUsesPerDay) { this.powerUsesPerDay = powerUsesPerDay; }
    public void addPowerUsesPerDay(String power, int uses) {
        if (!power.trim().isEmpty()) {
            powerUsesPerDay.put(power, Math.max(0, uses));
        }
    }

    public boolean hasCharges() { return hasCharges; }
    public void setHasCharges(boolean hasCharges) { this.hasCharges = hasCharges; }

    public int getCurrentCharges() { return currentCharges; }
    public void setCurrentCharges(int currentCharges) { this.currentCharges = Math.max(0, currentCharges); }

    public int getMaximumCharges() { return maximumCharges; }
    public void setMaximumCharges(int maximumCharges) { this.maximumCharges = Math.max(0, maximumCharges); }

    // Appearance and Artifact Status
    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public boolean isArtifact() { return isArtifact; }
    public void setArtifact(boolean isArtifact) { this.isArtifact = isArtifact; }

    public String getArtifactLevel() { return artifactLevel; }
    public void setArtifactLevel(String artifactLevel) { this.artifactLevel = artifactLevel; }

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
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validClassIds) {
        int removedCount = 0;

        // Clean up restrictedClasses ArrayList (CharacterClass ids)
        ArrayList<String> restrictedClasses = arrayHandler.getObjectArray("restrictedClasses");
        Iterator<String> restrictedIter = restrictedClasses.iterator();
        while (restrictedIter.hasNext()) {
            String classId = restrictedIter.next();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                restrictedIter.remove();
                removedCount++;
            }
        }

        // Clean up favoredClasses ArrayList (CharacterClass ids)
        ArrayList<String> favoredClasses = arrayHandler.getObjectArray("favoredClasses");
        Iterator<String> favoredIter = favoredClasses.iterator();
        while (favoredIter.hasNext()) {
            String classId = favoredIter.next();

            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                favoredIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
