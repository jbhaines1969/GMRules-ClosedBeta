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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents armor in any RPG system.
 * Covers AC systems, damage reduction, mobility restrictions, and magical properties
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, Savage Worlds, and other systems.
 */
public class Armor extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Core Properties
    private String armorType = "";                   // Light, Medium, Heavy, Shield, etc.
    private String armorCategory = "";               // Clothing, Padded, Chain, Plate, etc.
    private String armorGroup = "";                  // Natural, Manufactured, Magical, etc.
    private String size = "";                        // Tiny, Small, Medium, Large, etc.
    private double weight = 0.0;                     // Weight in pounds/kg
    private String cost = "";                        // Cost in game currency

    // Armor Class and Defense
    private int armorClass = 10;                     // Base AC provided
    private int armorBonus = 0;                      // Armor bonus to AC
    private int shieldBonus = 0;                     // Shield bonus to AC (if shield)
    private int maxDexBonus = 99;                    // Maximum Dex bonus allowed
    private boolean allowsDexBonus = true;           // Whether Dex bonus applies
    private String acCalculation = "";               // Custom AC calculation formula

    // Damage Reduction and Resistances
    private int damageReduction = 0;                 // Flat damage reduction
    private String drType = "";                      // Type of DR (magic, silver, etc.)
    private Map<String, Integer> resistanceValues = new LinkedHashMap<>(); // Resistance -> value

    // Movement and Mobility
    private int armorCheckPenalty = 0;               // Penalty to skills
    private int speedReduction = 0;                  // Speed penalty in feet
    private boolean restrictsMobility = false;       // Restricts certain actions
    private boolean allowsRunning = true;            // Can run in this armor
    private boolean allowsSwimming = true;           // Can swim in this armor

    // Stealth and Detection
    private int stealthPenalty = 0;                  // Penalty to stealth
    private boolean isSilent = false;                // Makes no noise
    private boolean isConcealing = false;            // Helps hide identity
    private int disguiseBonus = 0;                   // Bonus to disguise checks
    private boolean isInvisible = false;             // Provides invisibility

    // Magic and Enhancement
    private boolean isMagical = false;               // Magical armor
    private int enhancementBonus = 0;                // +1, +2, etc.
    private boolean hasCharges = false;              // Uses charges
    private int currentCharges = 0;                  // Current charge count
    private int maximumCharges = 0;                  // Maximum charges

    // Proficiency and Requirements
    private int minimumStrength = 0;                 // STR requirement

    // Construction and Durability
    private int hardness = 0;                        // Damage resistance
    private int hitPoints = 0;                       // Armor HP
    private int breakingPoint = 0;                   // When armor breaks
    private boolean isIndestructible = false;        // Cannot be destroyed
    private String material = "";                    // Steel, Leather, Mithril, etc.
    private String craftsmanship = "";               // Masterwork, Poor, etc.
    private boolean isMasterwork = false;            // Masterwork quality

    // Environmental Protection
    private boolean weatherResistant = false;        // Protects from weather
    private boolean pressureSealed = false;          // Sealed against pressure
    private boolean radiationShielded = false;       // Protects from radiation
    private boolean breathingApparatus = false;      // Provides breathing

    // Special Armor Features
    private boolean isShield = false;                // Is a shield
    private boolean allowsSpellcasting = true;       // Can cast spells
    private int spellFailureChance = 0;              // Arcane spell failure
    private boolean providesBonus = false;           // Provides stat bonuses
    private Map<String, Integer> statBonuses = new LinkedHashMap<>(); // Stat -> bonus

    // Combat Modifiers
    private Map<String, Integer> combatModifiers = new LinkedHashMap<>(); // Situation -> modifier
    private boolean allowsCharging = true;           // Can charge in armor
    private boolean allowsTumbling = true;          // Can tumble in armor
    private int initiativeModifier = 0;              // Initiative bonus/penalty

    // Appearance and Identification
    private String appearance = "";                  // Physical description
    private String history = "";                     // Armor's background
    private boolean isArtifact = false;              // Legendary artifact
    private String artifactLevel = "";               // Minor, Major, etc.
    private String origin = "";                      // Where it was made
    private String creator = "";                     // Who made it

    // Wear and Compatibility
    private int donTime = 60;                        // Time to put on (seconds)
    private int doffTime = 30;                       // Time to take off (seconds)
    private boolean requiresAssistance = false;      // Needs help to don/doff
    private boolean stacksWithOtherArmor = false;    // Can layer with other armor

    // System-Specific Properties
    private String systemType = "";                  // D&D, Pathfinder, GURPS, etc.
    private Map<String, Object> systemProperties = new LinkedHashMap<>(); // System-specific data
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Armor(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Armor(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

    public Armor(String name, String description, String armorType) {
        super(name, description);
        this.armorType = armorType;
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("damageImmunities", new ArrayList<String>());
        arrayHandler.putArray("energyResistances", new ArrayList<String>());
        arrayHandler.putArray("effects", new ArrayList<Effect>());
        arrayHandler.putArray("movementRestrictions", new ArrayList<String>());
        arrayHandler.putArray("enchantments", new ArrayList<String>());
        arrayHandler.putArray("specialQualities", new ArrayList<String>());
        arrayHandler.putArray("proficiencyRequired", new ArrayList<String>());
        arrayHandler.putArray("restrictedClasses", new ArrayList<String>());
        arrayHandler.putArray("favoredClasses", new ArrayList<String>());
        arrayHandler.putArray("prerequisites", new ArrayList<String>());
        arrayHandler.putArray("vulnerabilities", new ArrayList<String>());
        arrayHandler.putArray("environmentalProtection", new ArrayList<String>());
        arrayHandler.putArray("armorSpells", new ArrayList<String>());
        arrayHandler.putArray("combatRestrictions", new ArrayList<String>());
        arrayHandler.putArray("notableFeatures", new ArrayList<String>());
        arrayHandler.putArray("incompatibleArmor", new ArrayList<String>());
        arrayHandler.putArray("armorSlots", new ArrayList<String>());
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
    public String getArmorType() { return armorType; }
    public void setArmorType(String armorType) { this.armorType = armorType; }

    public String getArmorCategory() { return armorCategory; }
    public void setArmorCategory(String armorCategory) { this.armorCategory = armorCategory; }

    public String getArmorGroup() { return armorGroup; }
    public void setArmorGroup(String armorGroup) { this.armorGroup = armorGroup; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = Math.max(0.0, weight); }

    public String getCost() { return cost; }
    public void setCost(String cost) { this.cost = cost; }

    // AC and Defense
    public int getArmorClass() { return armorClass; }
    public void setArmorClass(int armorClass) { this.armorClass = Math.max(0, armorClass); }

    public int getArmorBonus() { return armorBonus; }
    public void setArmorBonus(int armorBonus) { this.armorBonus = Math.max(0, armorBonus); }

    public int getShieldBonus() { return shieldBonus; }
    public void setShieldBonus(int shieldBonus) { this.shieldBonus = Math.max(0, shieldBonus); }

    public int getMaxDexBonus() { return maxDexBonus; }
    public void setMaxDexBonus(int maxDexBonus) { this.maxDexBonus = Math.max(0, maxDexBonus); }

    public boolean allowsDexBonus() { return allowsDexBonus; }
    public void setAllowsDexBonus(boolean allowsDexBonus) { this.allowsDexBonus = allowsDexBonus; }

    public String getAcCalculation() { return acCalculation; }
    public void setAcCalculation(String acCalculation) { this.acCalculation = acCalculation; }

    // Damage Reduction
    public int getDamageReduction() { return damageReduction; }
    public void setDamageReduction(int damageReduction) { this.damageReduction = Math.max(0, damageReduction); }

    public String getDrType() { return drType; }
    public void setDrType(String drType) { this.drType = drType; }

    // Movement and Mobility
    public int getArmorCheckPenalty() { return armorCheckPenalty; }
    public void setArmorCheckPenalty(int armorCheckPenalty) { this.armorCheckPenalty = armorCheckPenalty; }

    public int getSpeedReduction() { return speedReduction; }
    public void setSpeedReduction(int speedReduction) { this.speedReduction = Math.max(0, speedReduction); }

    public boolean restrictsMobility() { return restrictsMobility; }
    public void setRestrictsMobility(boolean restrictsMobility) { this.restrictsMobility = restrictsMobility; }

    // Stealth and Detection
    public int getStealthPenalty() { return stealthPenalty; }
    public void setStealthPenalty(int stealthPenalty) { this.stealthPenalty = stealthPenalty; }

    public boolean isSilent() { return isSilent; }
    public void setSilent(boolean isSilent) { this.isSilent = isSilent; }

    public boolean isConcealing() { return isConcealing; }
    public void setConcealing(boolean isConcealing) { this.isConcealing = isConcealing; }

    public int getDisguiseBonus() { return disguiseBonus; }
    public void setDisguiseBonus(int disguiseBonus) { this.disguiseBonus = disguiseBonus; }

    // Magic and Enhancement
    public boolean isMagical() { return isMagical; }
    public void setMagical(boolean isMagical) { this.isMagical = isMagical; }

    public int getEnhancementBonus() { return enhancementBonus; }
    public void setEnhancementBonus(int enhancementBonus) { this.enhancementBonus = Math.max(0, enhancementBonus); }

    public boolean hasCharges() { return hasCharges; }
    public void setHasCharges(boolean hasCharges) { this.hasCharges = hasCharges; }

    public int getCurrentCharges() { return currentCharges; }
    public void setCurrentCharges(int currentCharges) { this.currentCharges = Math.max(0, currentCharges); }

    public int getMaximumCharges() { return maximumCharges; }
    public void setMaximumCharges(int maximumCharges) { this.maximumCharges = Math.max(0, maximumCharges); }

    // Proficiency and Requirements
    public int getMinimumStrength() { return minimumStrength; }
    public void setMinimumStrength(int minimumStrength) { this.minimumStrength = Math.max(0, minimumStrength); }

    // Durability
    public int getHardness() { return hardness; }
    public void setHardness(int hardness) { this.hardness = Math.max(0, hardness); }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = Math.max(0, hitPoints); }

    public boolean isIndestructible() { return isIndestructible; }
    public void setIndestructible(boolean isIndestructible) { this.isIndestructible = isIndestructible; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public boolean isMasterwork() { return isMasterwork; }
    public void setMasterwork(boolean isMasterwork) { this.isMasterwork = isMasterwork; }

    // Environmental Protection
    public boolean isWeatherResistant() { return weatherResistant; }
    public void setWeatherResistant(boolean weatherResistant) { this.weatherResistant = weatherResistant; }

    // Special Features
    public boolean isShield() { return isShield; }
    public void setShield(boolean isShield) { this.isShield = isShield; }

    public boolean allowsSpellcasting() { return allowsSpellcasting; }
    public void setAllowsSpellcasting(boolean allowsSpellcasting) { this.allowsSpellcasting = allowsSpellcasting; }

    public int getSpellFailureChance() { return spellFailureChance; }
    public void setSpellFailureChance(int spellFailureChance) { this.spellFailureChance = Math.max(0, Math.min(100, spellFailureChance)); }

    public Map<String, Integer> getStatBonuses() { return statBonuses; }
    public void setStatBonuses(Map<String, Integer> statBonuses) {
        this.statBonuses = statBonuses;
    }
    public void addStatBonus(String stat, int bonus) {
        if (!stat.trim().isEmpty()) {
            statBonuses.put(stat, bonus);
        }
    }

    // Combat Modifiers
    public int getInitiativeModifier() { return initiativeModifier; }
    public void setInitiativeModifier(int initiativeModifier) { this.initiativeModifier = initiativeModifier; }

    public boolean allowsCharging() { return allowsCharging; }
    public void setAllowsCharging(boolean allowsCharging) { this.allowsCharging = allowsCharging; }

    // Appearance
    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public boolean isArtifact() { return isArtifact; }
    public void setArtifact(boolean isArtifact) { this.isArtifact = isArtifact; }

    public String getArtifactLevel() { return artifactLevel; }
    public void setArtifactLevel(String artifactLevel) { this.artifactLevel = artifactLevel; }

    // Wear and Compatibility
    public int getDonTime() { return donTime; }
    public void setDonTime(int donTime) { this.donTime = Math.max(0, donTime); }

    public int getDoffTime() { return doffTime; }
    public void setDoffTime(int doffTime) { this.doffTime = Math.max(0, doffTime); }

    public boolean requiresAssistance() { return requiresAssistance; }
    public void setRequiresAssistance(boolean requiresAssistance) { this.requiresAssistance = requiresAssistance; }

    // System Properties
    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) {
        this.systemProperties = systemProperties;
    }
    public void addSystemProperty(String key, Object value) {
        if (!key.trim().isEmpty()) {
            systemProperties.put(key, value);
        }
    }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences() {
        int removedCount = 0;
        return removedCount;
    }
}
