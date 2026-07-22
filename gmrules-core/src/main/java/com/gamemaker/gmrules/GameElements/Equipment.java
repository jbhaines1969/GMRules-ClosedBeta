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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents general equipment and gear in any RPG system.
 * Covers tools, adventuring gear, consumables, containers, and other non-combat items
 * from D&D, Pathfinder, GURPS, World of Darkness, FATE, and other systems.
 */
public class Equipment extends GameElement implements Serializable {

    private static final long serialVersionUID = 1L;

// *** MEMBERS ***
    // Core Item Properties
    private String equipmentTypeKey = "";
    private String category = "";
    private String subcategory = "";
    private String size = "";
    private double weight = 0.0;
    private String weightUnit = "";

    // Economic Properties
    private double cost = 0.0;
    private String currency = "";
    private String availability = "";
    private String marketValue = "";
    private boolean isValuable = false;
    private double depreciationRate = 0.0;

    // Physical Properties
    private String material = "";
    private String damageTypeId = "";
    private String durability = "";
    private int hitPoints = 0;
    private int hardness = 0;
    private String condition = "";

    // Usage and Function
    private int uses = 0;
    private String usageType = "";
    private String activationTime = "";
    private String usageRequirements = "";
    private boolean requiresTraining = false;

    // Container Properties
    private double capacity = 0.0;
    private String capacityType = "";
    private boolean preservesContents = false;
    private String preservationType = "";

    // Tool Properties
    private Map<String, Integer> skillBonuses = new LinkedHashMap<>();
    private String toolQuality = "";
    private boolean requiresMaintenace = false;

    // Component Slots (Vehicles, Modular Gear)
    private ArrayList<String> installedComponentIds = new ArrayList<>();
    private ArrayList<String> installedWeaponIds = new ArrayList<>();

    // Magic and Enhancement
    private boolean isMagical = false;
    private String magicAura = "";
    private int casterLevel = 0;
    private boolean requiresAttunement = false;
    private int charges = 0;
    private String chargeRegenerationRate = "";

    // Special Properties
    private Map<String, String> propertyDescriptions = new LinkedHashMap<>();
    private boolean isArtifact = false;
    private boolean isIntelligent = false;
    private String personality = "";
    private int ego = 0;

    // Environmental Factors
    private boolean isWaterproof = false;
    private boolean isFireproof = false;
    private String temperatureRange = "";

    // Crafting and Creation
    private String craftingDifficulty = "";
    private int craftingTime = 0;
    private String creator = "";
    private String craftingTradition = "";

    // Restrictions and Requirements
    private boolean cursed = false;
    private String curseDescription = "";

    // System-Specific Properties
    private String systemType = "";
    private Map<String, Object> systemProperties = new LinkedHashMap<>();
    private ArrayHandler arrayHandler = new ArrayHandler();

// *** CONSTRUCTORS ***
    public Equipment(String name) {
        super(name);
        initializeArrayRegistry();
    }

    public Equipment(String name, String description) {
        super(name, description);
        initializeArrayRegistry();
    }

// *** METHODS ***
    private void initializeArrayRegistry() {
        arrayHandler.putArray("materials", new ArrayList<String>());
        arrayHandler.putArray("allowedItems", new ArrayList<String>());
        arrayHandler.putArray("toolCategories", new ArrayList<String>());
        arrayHandler.putArray("enabledActions", new ArrayList<String>());
        arrayHandler.putArray("magicalEffects", new ArrayList<String>());
        arrayHandler.putArray("specialProperties", new ArrayList<String>());
        arrayHandler.putArray("environmentalEffects", new ArrayList<String>());
        arrayHandler.putArray("immunities", new ArrayList<String>());
        arrayHandler.putArray("craftingMaterials", new ArrayList<String>());
        arrayHandler.putArray("craftingSkills", new ArrayList<String>());
        arrayHandler.putArray("usageRestrictions", new ArrayList<String>());
        arrayHandler.putArray("alignmentRestrictions", new ArrayList<String>());
        arrayHandler.putArray("classRestrictions", new ArrayList<String>());
        arrayHandler.putArray("raceRestrictions", new ArrayList<String>());
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
    public String getEquipmentTypeKey() { return equipmentTypeKey; }
    public void setEquipmentTypeKey(String equipmentTypeKey) {
        this.equipmentTypeKey = Objects.toString(equipmentTypeKey, "");
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = Math.max(0.0, weight); }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }

    // Economic Properties
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = Math.max(0.0, cost); }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getMarketValue() { return marketValue; }
    public void setMarketValue(String marketValue) { this.marketValue = marketValue; }

    public boolean isValuable() { return isValuable; }
    public void setValuable(boolean isValuable) { this.isValuable = isValuable; }

    public double getDepreciationRate() { return depreciationRate; }
    public void setDepreciationRate(double depreciationRate) { this.depreciationRate = depreciationRate; }

    // Physical Properties
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getDamageTypeId() { return Objects.toString(damageTypeId, "").trim(); }
    public void setDamageTypeId(String damageTypeId) {
        this.damageTypeId = Objects.toString(damageTypeId, "").trim();
    }

    public String getDurability() { return durability; }
    public void setDurability(String durability) { this.durability = durability; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = Math.max(0, hitPoints); }

    public int getHardness() { return hardness; }
    public void setHardness(int hardness) { this.hardness = Math.max(0, hardness); }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    // Usage Properties
    public int getUses() { return uses; }
    public void setUses(int uses) { this.uses = Math.max(0, uses); }

    public String getUsageType() { return usageType; }
    public void setUsageType(String usageType) { this.usageType = usageType; }

    public String getActivationTime() { return activationTime; }
    public void setActivationTime(String activationTime) { this.activationTime = activationTime; }

    public String getUsageRequirements() { return usageRequirements; }
    public void setUsageRequirements(String usageRequirements) { this.usageRequirements = usageRequirements; }

    public boolean requiresTraining() { return requiresTraining; }
    public void setRequiresTraining(boolean requiresTraining) { this.requiresTraining = requiresTraining; }

    // Container Properties
    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = Math.max(0.0, capacity); }

    public String getCapacityType() { return capacityType; }
    public void setCapacityType(String capacityType) { this.capacityType = capacityType; }

    public boolean preservesContents() { return preservesContents; }
    public void setPreservesContents(boolean preservesContents) { this.preservesContents = preservesContents; }

    public String getPreservationType() { return preservationType; }
    public void setPreservationType(String preservationType) { this.preservationType = preservationType; }

    // Tool Properties
    public Map<String, Integer> getSkillBonuses() { return skillBonuses; }
    public void setSkillBonuses(Map<String, Integer> skillBonuses) { this.skillBonuses = skillBonuses; }
    public void addSkillBonus(String skill, int bonus) {
        if (!skill.trim().isEmpty()) {
            skillBonuses.put(skill, bonus);
        }
    }

    public String getToolQuality() { return toolQuality; }
    public void setToolQuality(String toolQuality) { this.toolQuality = toolQuality; }

    public boolean requiresMaintenace() { return requiresMaintenace; }
    public void setRequiresMaintenace(boolean requiresMaintenace) { this.requiresMaintenace = requiresMaintenace; }

    // Component Slots
    public List<String> getInstalledComponentIds() { return new ArrayList<>(installedComponentIds); }
    public void setInstalledComponentIds(Collection<String> installedComponentIds) {
        this.installedComponentIds = normalizeIdList(installedComponentIds);
    }
    public void addInstalledComponentId(String componentId) {
        String safeId = Objects.toString(componentId, "").trim();
        if (!safeId.isEmpty()) {
            installedComponentIds.add(safeId);
        }
    }
    public boolean removeInstalledComponentId(String componentId) {
        String safeId = Objects.toString(componentId, "").trim();
        if (safeId.isEmpty()) {
            return false;
        }
        return installedComponentIds.remove(safeId);
    }
    public void clearInstalledComponentIds() { installedComponentIds.clear(); }

    public List<String> getInstalledWeaponIds() { return new ArrayList<>(installedWeaponIds); }
    public void setInstalledWeaponIds(Collection<String> installedWeaponIds) {
        this.installedWeaponIds = normalizeIdList(installedWeaponIds);
    }
    public void addInstalledWeaponId(String weaponId) {
        String safeId = Objects.toString(weaponId, "").trim();
        if (!safeId.isEmpty()) {
            installedWeaponIds.add(safeId);
        }
    }
    public boolean removeInstalledWeaponId(String weaponId) {
        String safeId = Objects.toString(weaponId, "").trim();
        if (safeId.isEmpty()) {
            return false;
        }
        return installedWeaponIds.remove(safeId);
    }
    public void clearInstalledWeaponIds() { installedWeaponIds.clear(); }

    private ArrayList<String> normalizeIdList(Collection<String> values) {
        Collection<String> safeValues = Objects.requireNonNullElse(values, List.of());
        ArrayList<String> results = new ArrayList<>();
        for (String value : safeValues) {
            String safeValue = Objects.toString(value, "").trim();
            if (!safeValue.isEmpty()) {
                results.add(safeValue);
            }
        }
        return results;
    }


    // Magic Properties
    public boolean isMagical() { return isMagical; }
    public void setMagical(boolean isMagical) { this.isMagical = isMagical; }

    public String getMagicAura() { return magicAura; }
    public void setMagicAura(String magicAura) { this.magicAura = magicAura; }

    public int getCasterLevel() { return casterLevel; }
    public void setCasterLevel(int casterLevel) { this.casterLevel = Math.max(0, casterLevel); }

    public boolean requiresAttunement() { return requiresAttunement; }
    public void setRequiresAttunement(boolean requiresAttunement) { this.requiresAttunement = requiresAttunement; }

    public int getCharges() { return charges; }
    public void setCharges(int charges) { this.charges = Math.max(0, charges); }

    public String getChargeRegenerationRate() { return chargeRegenerationRate; }
    public void setChargeRegenerationRate(String chargeRegenerationRate) { this.chargeRegenerationRate = chargeRegenerationRate; }

    // Special Properties
    public Map<String, String> getPropertyDescriptions() { return propertyDescriptions; }
    public void setPropertyDescriptions(Map<String, String> propertyDescriptions) { this.propertyDescriptions = propertyDescriptions; }

    public boolean isArtifact() { return isArtifact; }
    public void setArtifact(boolean isArtifact) { this.isArtifact = isArtifact; }

    public boolean isIntelligent() { return isIntelligent; }
    public void setIntelligent(boolean isIntelligent) { this.isIntelligent = isIntelligent; }

    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }

    public int getEgo() { return ego; }
    public void setEgo(int ego) { this.ego = Math.max(0, ego); }

    // Environmental Properties
    public boolean isWaterproof() { return isWaterproof; }
    public void setWaterproof(boolean isWaterproof) { this.isWaterproof = isWaterproof; }

    public boolean isFireproof() { return isFireproof; }
    public void setFireproof(boolean isFireproof) { this.isFireproof = isFireproof; }

    public String getTemperatureRange() { return temperatureRange; }
    public void setTemperatureRange(String temperatureRange) { this.temperatureRange = temperatureRange; }

    // Crafting Properties
    public String getCraftingDifficulty() { return craftingDifficulty; }
    public void setCraftingDifficulty(String craftingDifficulty) { this.craftingDifficulty = craftingDifficulty; }

    public int getCraftingTime() { return craftingTime; }
    public void setCraftingTime(int craftingTime) { this.craftingTime = Math.max(0, craftingTime); }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getCraftingTradition() { return craftingTradition; }
    public void setCraftingTradition(String craftingTradition) { this.craftingTradition = craftingTradition; }

    // Restrictions
    public boolean isCursed() { return cursed; }
    public void setCursed(boolean cursed) { this.cursed = cursed; }

    public String getCurseDescription() { return curseDescription; }
    public void setCurseDescription(String curseDescription) { this.curseDescription = curseDescription; }

    // System Properties
    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public Map<String, Object> getSystemProperties() { return systemProperties; }
    public void setSystemProperties(Map<String, Object> systemProperties) { this.systemProperties = systemProperties; }

    /**
     * Cleans up all orphaned references that no longer exist in the game.
     * Called by GameIO after deserialization to maintain referential integrity.
     * @param validEffectIds Set of valid Effect ids currently in the game
     * @param validSkillIds Set of valid Skill ids currently in the game
     * @param validClassIds Set of valid CharacterClass ids currently in the game
     * @param validRaceIds Set of valid Race ids currently in the game
     * @param validEquipmentTypeKeys Set of valid equipment type keys currently in the game
     * @return number of orphaned references removed
     */
    public int cleanupOrphanedReferences(
        Set<String> validEffectIds,
        Set<String> validSkillIds,
        Set<String> validClassIds,
        Set<String> validRaceIds,
        Set<String> validEquipmentTypeKeys,
        Set<String> validDamageTypeIds
    ) {
        int removedCount = 0;

        // Clean up effect names
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

        // Clean up skillBonuses (keys are Skill ids)
        Iterator<Map.Entry<String, Integer>> skillIter = skillBonuses.entrySet().iterator();
        while (skillIter.hasNext()) {
            Map.Entry<String, Integer> entry = skillIter.next();
            String skillId = entry.getKey();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                skillIter.remove();
                removedCount++;
            }
        }

        // Clean up classRestrictions
        ArrayList<String> classRestrictions = arrayHandler.getObjectArray("classRestrictions");
        Iterator<String> classIter = classRestrictions.iterator();
        while (classIter.hasNext()) {
            String classId = classIter.next();
            boolean exists = validClassIds.contains(classId);
            if (!exists) {
                classIter.remove();
                removedCount++;
            }
        }

        // Clean up raceRestrictions
        ArrayList<String> raceRestrictions = arrayHandler.getObjectArray("raceRestrictions");
        Iterator<String> raceIter = raceRestrictions.iterator();
        while (raceIter.hasNext()) {
            String raceId = raceIter.next();
            boolean exists = validRaceIds.contains(raceId);
            if (!exists) {
                raceIter.remove();
                removedCount++;
            }
        }

        // Clean up craftingSkills (references Skill ids)
        ArrayList<String> craftingSkills = arrayHandler.getObjectArray("craftingSkills");
        Iterator<String> craftSkillIter = craftingSkills.iterator();
        while (craftSkillIter.hasNext()) {
            String skillId = craftSkillIter.next();
            boolean exists = validSkillIds.contains(skillId);
            if (!exists) {
                craftSkillIter.remove();
                removedCount++;
            }
        }

        // Clean up equipment type key
        if (!equipmentTypeKey.isEmpty()) {
            boolean exists = validEquipmentTypeKeys.contains(equipmentTypeKey);
            if (!exists) {
                equipmentTypeKey = "";
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

}
