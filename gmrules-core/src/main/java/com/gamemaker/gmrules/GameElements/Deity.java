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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a deity in any RPG pantheon system.
 * Covers divine hierarchies, portfolios, worship mechanics, and divine intervention
 * from D&D, Pathfinder, GURPS, World of Darkness, and other mythological systems.
 */
public class Deity extends GameElement implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    // Divine Status and Hierarchy
    private String divineRank = "";                  // Lesser, Intermediate, Greater, etc.
    private String deityType = "";                   // God, Goddess, Primordial, etc.
    private String cosmicRole = "";                  // Creator, Destroyer, Maintainer, etc.
    private int divineLevel = 0;                     // Numerical divine power level
    private boolean isAscended = false;              // Mortal who became divine
    private String ascensionMethod = "";             // How they became divine

    // Portfolio and Domains
    private String primaryPortfolio = "";            // Main area of divine influence
    private String holySymbol = "";                  // Sacred symbol representation

    // Alignment and Philosophy
    private String alignment = "";                   // Lawful Good, Chaotic Evil, etc.
    private String moralCode = "";                   // Divine commandments
    private String philosophy = "";                  // Core beliefs

    // Worship and Followers
    private String worshipStyle = "";                // How worship is conducted

    // Divine Servants and Hierarchy
    private String divineRealm = "";                 // Home plane/realm

    // Divine Powers and Abilities
    private boolean canGrantSpells = true;           // Can grant divine magic
    private int maxSpellLevel = 9;                   // Highest spell level granted
    private boolean canCreateAvatars = false;        // Can manifest avatars
    private int avatarLimit = 1;                     // Maximum simultaneous avatars

    // Manifestation and Interaction
    private String preferredManifestation = "";      // How they appear
    private boolean directIntervention = false;      // Directly aids followers
    private String interventionStyle = "";           // How they intervene
    private String communicationMethod = "";         // How they speak to mortals

    // Holy Sites and Organizations
    private String priesthood = "";                  // Priestly hierarchy

    // Cultural and Mythological
    private String mythology = "";                   // Origin stories
    private String origin = "";                      // Where worship began
    private String familialRelations = "";           // Divine family

    // Physical and Symbolic
    private String appearance = "";                  // Physical description

    // Clerical and Divine Magic
    private boolean allowsUndead = false;            // Permits undead creation

    // Relationships and Politics
    private String politicalStance = "";             // Divine politics
    private String stance = "";                      // Current divine agenda
    private String eschatology = "";                 // End times beliefs

    // System-Specific Properties
    private String systemType = "";                  // D&D, Pathfinder, etc.
    private Map<String, Object> systemProperties = new LinkedHashMap<>(); // System-specific data
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Deity(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Deity(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

    public Deity(String name, String description, String divineRank) {
        super(name, description);
        this.divineRank = divineRank;
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("divineAspects", new ArrayList<String>());
        arrayHandler.putArray("domains", new ArrayList<String>());
        arrayHandler.putArray("subdomains", new ArrayList<String>());
        arrayHandler.putArray("portfolios", new ArrayList<String>());
        arrayHandler.putArray("oppositions", new ArrayList<String>());
        arrayHandler.putArray("commandments", new ArrayList<String>());
        arrayHandler.putArray("anathemas", new ArrayList<String>());
        arrayHandler.putArray("virtues", new ArrayList<String>());
        arrayHandler.putArray("sins", new ArrayList<String>());
        arrayHandler.putArray("worshipperTypes", new ArrayList<String>());
        arrayHandler.putArray("clericAlignments", new ArrayList<String>());
        arrayHandler.putArray("favoredClasses", new ArrayList<String>());
        arrayHandler.putArray("holyDays", new ArrayList<String>());
        arrayHandler.putArray("rituals", new ArrayList<String>());
        arrayHandler.putArray("offerings", new ArrayList<String>());
        arrayHandler.putArray("divineServants", new ArrayList<String>());
        arrayHandler.putArray("heralds", new ArrayList<String>());
        arrayHandler.putArray("alliedDeities", new ArrayList<String>());
        arrayHandler.putArray("enemyDeities", new ArrayList<String>());
        arrayHandler.putArray("pantheon", new ArrayList<String>());
        arrayHandler.putArray("divinePowers", new ArrayList<String>());
        arrayHandler.putArray("spheresOfInfluence", new ArrayList<String>());
        arrayHandler.putArray("grantedPowers", new ArrayList<String>());
        arrayHandler.putArray("manifestationForms", new ArrayList<String>());
        arrayHandler.putArray("signs", new ArrayList<String>());
        arrayHandler.putArray("holySites", new ArrayList<String>());
        arrayHandler.putArray("temples", new ArrayList<String>());
        arrayHandler.putArray("religiousOrders", new ArrayList<String>());
        arrayHandler.putArray("sacredTexts", new ArrayList<String>());
        arrayHandler.putArray("artifacts", new ArrayList<String>());
        arrayHandler.putArray("myths", new ArrayList<String>());
        arrayHandler.putArray("cultures", new ArrayList<String>());
        arrayHandler.putArray("alternativeNames", new ArrayList<String>());
        arrayHandler.putArray("epithets", new ArrayList<String>());
        arrayHandler.putArray("sacredAnimals", new ArrayList<String>());
        arrayHandler.putArray("sacredPlants", new ArrayList<String>());
        arrayHandler.putArray("sacredColors", new ArrayList<String>());
        arrayHandler.putArray("sacredNumbers", new ArrayList<String>());
        arrayHandler.putArray("sacredMaterials", new ArrayList<String>());
        arrayHandler.putArray("sacredWeapons", new ArrayList<String>());
        arrayHandler.putArray("clericDomains", new ArrayList<String>());
        arrayHandler.putArray("spellRestrictions", new ArrayList<String>());
        arrayHandler.putArray("bonusSpells", new ArrayList<String>());
        arrayHandler.putArray("channelOptions", new ArrayList<String>());
        arrayHandler.putArray("mortalAllies", new ArrayList<String>());
        arrayHandler.putArray("mortalEnemies", new ArrayList<String>());
        arrayHandler.putArray("prophecies", new ArrayList<String>());
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

    // Divine Status
    public String getDivineRank() { return divineRank; }
    public void setDivineRank(String divineRank) { this.divineRank = divineRank; }

    public String getDeityType() { return deityType; }
    public void setDeityType(String deityType) { this.deityType = deityType; }

    public String getCosmicRole() { return cosmicRole; }
    public void setCosmicRole(String cosmicRole) { this.cosmicRole = cosmicRole; }

    public int getDivineLevel() { return divineLevel; }
    public void setDivineLevel(int divineLevel) { this.divineLevel = Math.max(0, divineLevel); }

    public boolean isAscended() { return isAscended; }
    public void setAscended(boolean isAscended) { this.isAscended = isAscended; }

    public String getAscensionMethod() { return ascensionMethod; }
    public void setAscensionMethod(String ascensionMethod) { this.ascensionMethod = ascensionMethod; }

    // Portfolio and Domains
    public String getPrimaryPortfolio() { return primaryPortfolio; }
    public void setPrimaryPortfolio(String primaryPortfolio) { this.primaryPortfolio = primaryPortfolio; }

    public String getHolySymbol() { return holySymbol; }
    public void setHolySymbol(String holySymbol) { this.holySymbol = holySymbol; }

    // Alignment and Philosophy
    public String getAlignment() { return alignment; }
    public void setAlignment(String alignment) { this.alignment = alignment; }

    public String getMoralCode() { return moralCode; }
    public void setMoralCode(String moralCode) { this.moralCode = moralCode; }

    public String getPhilosophy() { return philosophy; }
    public void setPhilosophy(String philosophy) { this.philosophy = philosophy; }

    // Worship and Followers
    public String getWorshipStyle() { return worshipStyle; }
    public void setWorshipStyle(String worshipStyle) { this.worshipStyle = worshipStyle; }

    // Divine Servants
    public String getDivineRealm() { return divineRealm; }
    public void setDivineRealm(String divineRealm) { this.divineRealm = divineRealm; }

    // Divine Powers
    public boolean canGrantSpells() { return canGrantSpells; }
    public void setCanGrantSpells(boolean canGrantSpells) { this.canGrantSpells = canGrantSpells; }

    public int getMaxSpellLevel() { return maxSpellLevel; }
    public void setMaxSpellLevel(int maxSpellLevel) { this.maxSpellLevel = Math.max(0, Math.min(9, maxSpellLevel)); }

    public boolean canCreateAvatars() { return canCreateAvatars; }
    public void setCanCreateAvatars(boolean canCreateAvatars) { this.canCreateAvatars = canCreateAvatars; }

    public int getAvatarLimit() { return avatarLimit; }
    public void setAvatarLimit(int avatarLimit) { this.avatarLimit = Math.max(0, avatarLimit); }

    // Manifestation
    public String getPreferredManifestation() { return preferredManifestation; }
    public void setPreferredManifestation(String preferredManifestation) { this.preferredManifestation = preferredManifestation; }

    public boolean hasDirectIntervention() { return directIntervention; }
    public void setDirectIntervention(boolean directIntervention) { this.directIntervention = directIntervention; }

    public String getInterventionStyle() { return interventionStyle; }
    public void setInterventionStyle(String interventionStyle) { this.interventionStyle = interventionStyle; }

    public String getCommunicationMethod() { return communicationMethod; }
    public void setCommunicationMethod(String communicationMethod) { this.communicationMethod = communicationMethod; }

    // Holy Sites and Organizations
    public String getPriesthood() { return priesthood; }
    public void setPriesthood(String priesthood) { this.priesthood = priesthood; }

    // Cultural and Mythological
    public String getMythology() { return mythology; }
    public void setMythology(String mythology) { this.mythology = mythology; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    // Physical and Symbolic
    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    // Clerical and Divine Magic
    public boolean allowsUndead() { return allowsUndead; }
    public void setAllowsUndead(boolean allowsUndead) { this.allowsUndead = allowsUndead; }

    // Relationships and Politics
    public String getPoliticalStance() { return politicalStance; }
    public void setPoliticalStance(String politicalStance) { this.politicalStance = politicalStance; }

    public String getStance() { return stance; }
    public void setStance(String stance) { this.stance = stance; }

    public String getEschatology() { return eschatology; }
    public void setEschatology(String eschatology) { this.eschatology = eschatology; }

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
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validDeityIds Set of valid Deity ids currently in the game
     * @param validPantheonIds Set of valid Pantheon ids currently in the game
     * @param validWeaponIds Set of valid Weapon ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validClassIds,
        Set<String> validDeityIds,
        Set<String> validPantheonIds,
        Set<String> validWeaponIds
    ) {
        int removedCount = 0;

        // Clean up favoredClasses (references CharacterClass ids)
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

        // Clean up alliedDeities (references other Deity ids)
        ArrayList<String> alliedDeities = arrayHandler.getObjectArray("alliedDeities");
        Iterator<String> alliedIter = alliedDeities.iterator();
        while (alliedIter.hasNext()) {
            String deityId = alliedIter.next();
            boolean exists = validDeityIds.contains(deityId);
            if (!exists) {
                alliedIter.remove();
                removedCount++;
            }
        }

        // Clean up enemyDeities (references other Deity ids)
        ArrayList<String> enemyDeities = arrayHandler.getObjectArray("enemyDeities");
        Iterator<String> enemyIter = enemyDeities.iterator();
        while (enemyIter.hasNext()) {
            String deityId = enemyIter.next();
            boolean exists = validDeityIds.contains(deityId);
            if (!exists) {
                enemyIter.remove();
                removedCount++;
            }
        }

        // Clean up pantheon (references Pantheon ids)
        ArrayList<String> pantheon = arrayHandler.getObjectArray("pantheon");
        Iterator<String> pantheonIter = pantheon.iterator();
        while (pantheonIter.hasNext()) {
            String pantheonId = pantheonIter.next();
            boolean exists = validPantheonIds.contains(pantheonId);
            if (!exists) {
                pantheonIter.remove();
                removedCount++;
            }
        }

        // Clean up sacredWeapons (references Weapon ids)
        ArrayList<String> sacredWeapons = arrayHandler.getObjectArray("sacredWeapons");
        Iterator<String> weaponIter = sacredWeapons.iterator();
        while (weaponIter.hasNext()) {
            String weaponId = weaponIter.next();
            boolean exists = validWeaponIds.contains(weaponId);
            if (!exists) {
                weaponIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }
}
