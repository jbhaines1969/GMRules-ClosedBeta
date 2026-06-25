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
 * Represents natural weapons in any RPG system.
 * Covers creature attacks, body weapons, and innate combat abilities
 * from D&D, Pathfinder, GURPS, World of Darkness, and other systems.
 */
public class NaturalWeapon extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Attack Properties
    private String attackType = "";
    private String bodyPart = "";
    private String size = "";
    private boolean isPrimary = true;
    private int numberOfAttacks = 1;

    // Damage System
    private String damageRoll = "";
    private int damageDiceCount = 0;
    private int damageDiceSides = 0;
    private int damageDiceModifier = 0;
    private String criticalRange = "";
    private String criticalMultiplier = "";
    private Map<String, String> damageBySize = new LinkedHashMap<>();

    // Attack Mechanics
    private int attackBonus = 0;
    private int reach = 5;
    private boolean hasReach = false;
    private String threatRange = "";
    private boolean canGrapple = false;
    private boolean canTrip = false;
    private boolean canDisarm = false;

    // Special Attack Properties
    private String grabConditions = "";
    private String constrictDamage = "";
    private boolean causesBleed = false;
    private String bleedDamage = "";

    // Poison and Disease
    private boolean isPoisonous = false;
    private String poisonType = "";
    private String poisonSave = "";
    private String poisonEffect = "";
    private boolean carriesDisease = false;
    private String diseaseType = "";
    private String diseaseSave = "";

    // Physical Characteristics
    private String appearance = "";
    private String texture = "";
    private boolean isRetractable = false;
    private boolean isRegenerable = false;
    private String regenerationTime = "";
    private boolean isDetachable = false;

    // Combat Maneuvers and Tactics
    private boolean allowsRendAttack = false;
    private String rendDamage = "";
    private boolean canPounce = false;
    private boolean canRake = false;
    private String rakeDamage = "";

    // Magical and Supernatural
    private boolean isMagical = false;
    private String alignmentType = "";
    private boolean bypassesDR = false;
    private boolean isGhost = false;

    // Environmental and Situational
    private boolean underwaterCapable = true;
    private boolean affectedByTemperature = false;
    private String optimalEnvironment = "";

    // Cultural and Evolutionary
    private String evolutionaryPurpose = "";
    private String culturalSignificance = "";
    private boolean isDisplayOnly = false;
    private boolean isDefensive = false;
    private String socialUse = "";
    private String huntingRole = "";

    // Creature Integration
    private boolean isUniversal = false;
    private boolean isRare = false;
    private String acquisitionMethod = "";
    private boolean isHereditary = true;

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public NaturalWeapon(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public NaturalWeapon(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("specialAttacks", new ArrayList<String>());
        arrayHandler.putArray("onHitEffects", new ArrayList<String>());
        arrayHandler.putArray("combatManeuvers", new ArrayList<String>());
        arrayHandler.putArray("magicalProperties", new ArrayList<String>());
        arrayHandler.putArray("drBypass", new ArrayList<String>());
        arrayHandler.putArray("environmentalBonuses", new ArrayList<String>());
        arrayHandler.putArray("ineffectiveAgainst", new ArrayList<String>());
        arrayHandler.putArray("associatedRaces", new ArrayList<String>());
        arrayHandler.putArray("associatedCreatures", new ArrayList<String>());
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

    // Core Properties
    public String getAttackType() { return attackType; }
    public void setAttackType(String attackType) { this.attackType = attackType; }

    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }

    public int getNumberOfAttacks() { return numberOfAttacks; }
    public void setNumberOfAttacks(int numberOfAttacks) { this.numberOfAttacks = Math.max(1, numberOfAttacks); }

    // Damage System
    public String getDamageRoll() {
        DiceSpec spec = getDamageDiceSpec();
        if (spec.isDefined()) {
            return spec.toNotation();
        }
        return damageRoll;
    }

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

    // Attack Mechanics
    public int getAttackBonus() { return attackBonus; }
    public void setAttackBonus(int attackBonus) { this.attackBonus = attackBonus; }

    public int getReach() { return reach; }
    public void setReach(int reach) { this.reach = Math.max(0, reach); }

    public boolean hasReach() { return hasReach; }
    public void setHasReach(boolean hasReach) { this.hasReach = hasReach; }

    public String getThreatRange() { return threatRange; }
    public void setThreatRange(String threatRange) { this.threatRange = threatRange; }

    public boolean canGrapple() { return canGrapple; }
    public void setCanGrapple(boolean canGrapple) { this.canGrapple = canGrapple; }

    public boolean canTrip() { return canTrip; }
    public void setCanTrip(boolean canTrip) { this.canTrip = canTrip; }

    public boolean canDisarm() { return canDisarm; }
    public void setCanDisarm(boolean canDisarm) { this.canDisarm = canDisarm; }

    // Special Attacks
    public String getGrabConditions() { return grabConditions; }
    public void setGrabConditions(String grabConditions) { this.grabConditions = grabConditions; }

    public String getConstrictDamage() { return constrictDamage; }
    public void setConstrictDamage(String constrictDamage) { this.constrictDamage = constrictDamage; }

    public boolean causesBleed() { return causesBleed; }
    public void setCausesBleed(boolean causesBleed) { this.causesBleed = causesBleed; }

    public String getBleedDamage() { return bleedDamage; }
    public void setBleedDamage(String bleedDamage) { this.bleedDamage = bleedDamage; }

    // Poison and Disease
    public boolean isPoisonous() { return isPoisonous; }
    public void setPoisonous(boolean isPoisonous) { this.isPoisonous = isPoisonous; }

    public String getPoisonType() { return poisonType; }
    public void setPoisonType(String poisonType) { this.poisonType = poisonType; }

    public String getPoisonSave() { return poisonSave; }
    public void setPoisonSave(String poisonSave) { this.poisonSave = poisonSave; }

    public String getPoisonEffect() { return poisonEffect; }
    public void setPoisonEffect(String poisonEffect) { this.poisonEffect = poisonEffect; }

    public boolean carriesDisease() { return carriesDisease; }
    public void setCarriesDisease(boolean carriesDisease) { this.carriesDisease = carriesDisease; }

    public String getDiseaseType() { return diseaseType; }
    public void setDiseaseType(String diseaseType) { this.diseaseType = diseaseType; }

    public String getDiseaseSave() { return diseaseSave; }
    public void setDiseaseSave(String diseaseSave) { this.diseaseSave = diseaseSave; }

    // Physical Characteristics
    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    public String getTexture() { return texture; }
    public void setTexture(String texture) { this.texture = texture; }

    public boolean isRetractable() { return isRetractable; }
    public void setRetractable(boolean isRetractable) { this.isRetractable = isRetractable; }

    public boolean isRegenerable() { return isRegenerable; }
    public void setRegenerable(boolean isRegenerable) { this.isRegenerable = isRegenerable; }

    public String getRegenerationTime() { return regenerationTime; }
    public void setRegenerationTime(String regenerationTime) { this.regenerationTime = regenerationTime; }

    public boolean isDetachable() { return isDetachable; }
    public void setDetachable(boolean isDetachable) { this.isDetachable = isDetachable; }

    // Combat Maneuvers
    public boolean allowsRendAttack() { return allowsRendAttack; }
    public void setAllowsRendAttack(boolean allowsRendAttack) { this.allowsRendAttack = allowsRendAttack; }

    public String getRendDamage() { return rendDamage; }
    public void setRendDamage(String rendDamage) { this.rendDamage = rendDamage; }

    public boolean canPounce() { return canPounce; }
    public void setCanPounce(boolean canPounce) { this.canPounce = canPounce; }

    public boolean canRake() { return canRake; }
    public void setCanRake(boolean canRake) { this.canRake = canRake; }

    public String getRakeDamage() { return rakeDamage; }
    public void setRakeDamage(String rakeDamage) { this.rakeDamage = rakeDamage; }

    // Magical Properties
    public boolean isMagical() { return isMagical; }
    public void setMagical(boolean isMagical) { this.isMagical = isMagical; }

    public String getAlignmentType() { return alignmentType; }
    public void setAlignmentType(String alignmentType) { this.alignmentType = alignmentType; }

    public boolean bypassesDR() { return bypassesDR; }
    public void setBypassesDR(boolean bypassesDR) { this.bypassesDR = bypassesDR; }

    public boolean isGhost() { return isGhost; }
    public void setGhost(boolean isGhost) { this.isGhost = isGhost; }

    // Environmental
    public boolean isUnderwaterCapable() { return underwaterCapable; }
    public void setUnderwaterCapable(boolean underwaterCapable) { this.underwaterCapable = underwaterCapable; }

    public boolean isAffectedByTemperature() { return affectedByTemperature; }
    public void setAffectedByTemperature(boolean affectedByTemperature) { this.affectedByTemperature = affectedByTemperature; }

    public String getOptimalEnvironment() { return optimalEnvironment; }
    public void setOptimalEnvironment(String optimalEnvironment) { this.optimalEnvironment = optimalEnvironment; }

    // Cultural and Evolution
    public String getEvolutionaryPurpose() { return evolutionaryPurpose; }
    public void setEvolutionaryPurpose(String evolutionaryPurpose) { this.evolutionaryPurpose = evolutionaryPurpose; }

    public String getCulturalSignificance() { return culturalSignificance; }
    public void setCulturalSignificance(String culturalSignificance) { this.culturalSignificance = culturalSignificance; }

    public boolean isDisplayOnly() { return isDisplayOnly; }
    public void setDisplayOnly(boolean isDisplayOnly) { this.isDisplayOnly = isDisplayOnly; }

    public boolean isDefensive() { return isDefensive; }
    public void setDefensive(boolean isDefensive) { this.isDefensive = isDefensive; }

    public String getSocialUse() { return socialUse; }
    public void setSocialUse(String socialUse) { this.socialUse = socialUse; }

    public String getHuntingRole() { return huntingRole; }
    public void setHuntingRole(String huntingRole) { this.huntingRole = huntingRole; }

    // Creature Integration
    public boolean isUniversal() { return isUniversal; }
    public void setUniversal(boolean isUniversal) { this.isUniversal = isUniversal; }

    public boolean isRare() { return isRare; }
    public void setRare(boolean isRare) { this.isRare = isRare; }

    public String getAcquisitionMethod() { return acquisitionMethod; }
    public void setAcquisitionMethod(String acquisitionMethod) { this.acquisitionMethod = acquisitionMethod; }

    public boolean isHereditary() { return isHereditary; }
    public void setHereditary(boolean isHereditary) { this.isHereditary = isHereditary; }

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
     * @param validRaceIds Set of valid Race ids currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(Set<String> validEffectIds, Set<String> validRaceIds) {
        int removedCount = 0;

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

        // Clean up associated races (ids)
        ArrayList<String> associatedRaces = arrayHandler.getObjectArray("associatedRaces");
        Iterator<String> raceIter = associatedRaces.iterator();
        while (raceIter.hasNext()) {
            String raceId = raceIter.next();
            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceIter.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

}
