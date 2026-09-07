/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.character;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CharacterDraft {

// *** MEMBERS ***
    private String gameId = "";
    private String gameHash = "";
    private String gameName = "";
    private String gameVersion = "";
    private String characterId = "";
    private String characterName = "";
    private Map<String, String> ruleModeSelections = new LinkedHashMap<>();
    private String raceId = "";
    private String backgroundId = "";
    private String classId = "";
    private Map<String, Integer> attributeScores = new LinkedHashMap<>();
    private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
    private Map<String, Integer> racialSkillRanks = new LinkedHashMap<>();
    private List<String> racialTraitNames = new ArrayList<>();
    private Map<String, Integer> backgroundSkillRanks = new LinkedHashMap<>();
    private Map<String, Integer> classSkillRanks = new LinkedHashMap<>();
    private Map<String, Integer> selectedSkillRanks = new LinkedHashMap<>();
    private int classSkillPointsPerLevel = 0;
    private boolean classSkillPointsSameAllLevels = true;
    private Map<Integer, Integer> classSkillPointsByLevel = new LinkedHashMap<>();
    private String skillPointSource = "class";
    private String skillPointProgression = "byClass";
    private int globalSkillPointsPerLevel = 0;
    private boolean globalSkillPointsSameAllLevels = true;
    private Map<Integer, Integer> globalSkillPointsByLevel = new LinkedHashMap<>();
    private boolean globalSkillPointsModifiedByInt = true;
    private int globalMinimumSkillPointsPerLevel = 0;
    private int resolvedSkillPointsPerLevel = 0;
    private boolean resolvedSkillPointsSameAllLevels = true;
    private Map<Integer, Integer> resolvedSkillPointsByLevel = new LinkedHashMap<>();
    private boolean resolvedSkillPointsModifiedByInt = true;
    private int resolvedMinimumSkillPointsPerLevel = 0;
    private String startingMoneyMethod = "base";
    private int startingMoneyAmount = 0;
    private String startingMoneyCurrencyId = "";
    private int diceSubstitutionsUsed = 0;
    private Map<String, Integer> rollAdjustmentUses = new LinkedHashMap<>();
    private Map<String, Integer> rollAdjustmentResourceSpent = new LinkedHashMap<>();
    private List<String> selectedSpellIds = new ArrayList<>();
    private List<String> selectedWeaponIds = new ArrayList<>();
    private List<String> selectedArmorIds = new ArrayList<>();
    private List<String> selectedEquipmentIds = new ArrayList<>();
    private int resolvedArmorClass = 0;

// *** CONSTRUCTORS ***
    public CharacterDraft() {
    }

    public CharacterDraft(String gameId, String gameHash) {
        this.gameId = Objects.toString(gameId, "");
        this.gameHash = Objects.toString(gameHash, "");
    }

// *** METHODS ***
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = Objects.toString(gameId, "");
    }

    public String getGameHash() {
        return gameHash;
    }

    public void setGameHash(String gameHash) {
        this.gameHash = Objects.toString(gameHash, "");
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = Objects.toString(gameName, "").trim();
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = Objects.toString(gameVersion, "").trim();
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = Objects.toString(characterId, "").trim();
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = Objects.toString(characterName, "").trim();
    }

    public Map<String, String> getRuleModeSelections() {
        return new LinkedHashMap<>(ruleModeSelections);
    }

    public void setRuleModeSelections(Map<String, String> ruleModeSelections) {
        Map<String, String> safeValues = Objects.requireNonNullElse(ruleModeSelections, Map.of());
        LinkedHashMap<String, String> copy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : safeValues.entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Objects.toString(entry.getValue(), "").trim());
            }
        }
        this.ruleModeSelections = copy;
    }

    public String getRaceId() {
        return raceId;
    }

    public void setRaceId(String raceId) {
        this.raceId = Objects.toString(raceId, "");
    }

    public String getClassId() {
        return classId;
    }

    public String getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(String backgroundId) {
        this.backgroundId = Objects.toString(backgroundId, "");
    }

    public void setClassId(String classId) {
        this.classId = Objects.toString(classId, "");
    }

    public Map<String, Integer> getAttributeScores() {
        return new LinkedHashMap<>(attributeScores);
    }

    public void setAttributeScores(Map<String, Integer> attributeScores) {
        Map<String, Integer> safeScores = Objects.requireNonNullElse(attributeScores, Map.of());
        Map<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeScores.entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Objects.requireNonNullElse(entry.getValue(), 0));
            }
        }
        this.attributeScores = copy;
    }

    public Map<String, String> getCategoryPointSlotAssignments() {
        return new LinkedHashMap<>(categoryPointSlotAssignments);
    }

    public void setCategoryPointSlotAssignments(Map<String, String> categoryPointSlotAssignments) {
        Map<String, String> safeAssignments = Objects.requireNonNullElse(categoryPointSlotAssignments, Map.of());
        LinkedHashMap<String, String> copy = new LinkedHashMap<>();
        HashSet<String> assignedCategoryKeys = new HashSet<>();
        for (Map.Entry<String, String> entry : safeAssignments.entrySet()) {
            String slotId = Objects.toString(entry.getKey(), "").trim();
            String categoryKey = Objects.toString(entry.getValue(), "").trim().toLowerCase(Locale.ROOT);
            if (!slotId.isEmpty() && !categoryKey.isEmpty() && assignedCategoryKeys.add(categoryKey)) {
                copy.put(slotId, categoryKey);
            }
        }
        this.categoryPointSlotAssignments = copy;
    }

    public List<String> getRacialSkillIds() {
        return new ArrayList<>(racialSkillRanks.keySet());
    }

    public void setRacialSkillIds(List<String> racialSkillIds) {
        this.racialSkillRanks = normalizeSkillRanks(racialSkillIds);
    }

    public Map<String, Integer> getRacialSkillRanks() {
        return new LinkedHashMap<>(racialSkillRanks);
    }

    public void setRacialSkillRanks(Map<String, Integer> racialSkillRanks) {
        this.racialSkillRanks = normalizeSkillRanks(racialSkillRanks);
    }

    public List<String> getRacialTraitNames() {
        return new ArrayList<>(racialTraitNames);
    }

    public void setRacialTraitNames(List<String> racialTraitNames) {
        List<String> safeValues = Objects.requireNonNullElse(racialTraitNames, List.of());
        ArrayList<String> copy = new ArrayList<>();
        for (String value : safeValues) {
            String safe = Objects.toString(value, "").trim();
            if (!safe.isEmpty() && !copy.contains(safe)) {
                copy.add(safe);
            }
        }
        this.racialTraitNames = copy;
    }

    public List<String> getBackgroundSkillIds() {
        return new ArrayList<>(backgroundSkillRanks.keySet());
    }

    public void setBackgroundSkillIds(List<String> backgroundSkillIds) {
        this.backgroundSkillRanks = normalizeSkillRanks(backgroundSkillIds);
    }

    public Map<String, Integer> getBackgroundSkillRanks() {
        return new LinkedHashMap<>(backgroundSkillRanks);
    }

    public void setBackgroundSkillRanks(Map<String, Integer> backgroundSkillRanks) {
        this.backgroundSkillRanks = normalizeSkillRanks(backgroundSkillRanks);
    }

    public List<String> getClassSkillIds() {
        return new ArrayList<>(classSkillRanks.keySet());
    }

    public void setClassSkillIds(List<String> classSkillIds) {
        this.classSkillRanks = normalizeSkillRanks(classSkillIds);
    }

    public Map<String, Integer> getClassSkillRanks() {
        return new LinkedHashMap<>(classSkillRanks);
    }

    public void setClassSkillRanks(Map<String, Integer> classSkillRanks) {
        this.classSkillRanks = normalizeSkillRanks(classSkillRanks);
    }

    public List<String> getSelectedSkillIds() {
        return new ArrayList<>(selectedSkillRanks.keySet());
    }

    public void setSelectedSkillIds(List<String> selectedSkillIds) {
        this.selectedSkillRanks = normalizeSkillRanks(selectedSkillIds);
    }

    public Map<String, Integer> getSelectedSkillRanks() {
        return new LinkedHashMap<>(selectedSkillRanks);
    }

    public void setSelectedSkillRanks(Map<String, Integer> selectedSkillRanks) {
        this.selectedSkillRanks = normalizeSkillRanks(selectedSkillRanks);
    }

    private Map<String, Integer> normalizeSkillRanks(List<String> selectedSkillIds) {
        List<String> safeValues = Objects.requireNonNullElse(selectedSkillIds, List.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (String value : safeValues) {
            String safe = Objects.toString(value, "").trim();
            if (!safe.isEmpty() && !copy.containsKey(safe)) {
                copy.put(safe, 0);
            }
        }
        return copy;
    }

    private Map<String, Integer> normalizeSkillRanks(Map<String, Integer> values) {
        Map<String, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeValues.entrySet()) {
            String safe = Objects.toString(entry.getKey(), "").trim();
            if (!safe.isEmpty()) {
                copy.put(safe, Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
            }
        }
        return copy;
    }

    public int getClassSkillPointsPerLevel() {
        return classSkillPointsPerLevel;
    }

    public void setClassSkillPointsPerLevel(int classSkillPointsPerLevel) {
        this.classSkillPointsPerLevel = Math.max(0, classSkillPointsPerLevel);
    }

    public boolean isClassSkillPointsSameAllLevels() {
        return classSkillPointsSameAllLevels;
    }

    public void setClassSkillPointsSameAllLevels(boolean classSkillPointsSameAllLevels) {
        this.classSkillPointsSameAllLevels = classSkillPointsSameAllLevels;
    }

    public Map<Integer, Integer> getClassSkillPointsByLevel() {
        return new LinkedHashMap<>(classSkillPointsByLevel);
    }

    public void setClassSkillPointsByLevel(Map<Integer, Integer> classSkillPointsByLevel) {
        Map<Integer, Integer> safeValues = Objects.requireNonNullElse(classSkillPointsByLevel, Map.of());
        LinkedHashMap<Integer, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : safeValues.entrySet()) {
            int level = Objects.requireNonNullElse(entry.getKey(), 0);
            int points = Objects.requireNonNullElse(entry.getValue(), 0);
            if (level > 0) {
                copy.put(level, Math.max(0, points));
            }
        }
        this.classSkillPointsByLevel = copy;
    }

    public String getSkillPointSource() {
        return skillPointSource;
    }

    public void setSkillPointSource(String skillPointSource) {
        String safe = Objects.toString(skillPointSource, "").trim();
        this.skillPointSource = safe.isEmpty() ? "class" : safe;
    }

    public String getSkillPointProgression() {
        return skillPointProgression;
    }

    public void setSkillPointProgression(String skillPointProgression) {
        String safe = Objects.toString(skillPointProgression, "").trim();
        this.skillPointProgression = safe.isEmpty() ? "byClass" : safe;
    }

    public int getGlobalSkillPointsPerLevel() {
        return globalSkillPointsPerLevel;
    }

    public void setGlobalSkillPointsPerLevel(int globalSkillPointsPerLevel) {
        this.globalSkillPointsPerLevel = Math.max(0, globalSkillPointsPerLevel);
    }

    public boolean isGlobalSkillPointsSameAllLevels() {
        return globalSkillPointsSameAllLevels;
    }

    public void setGlobalSkillPointsSameAllLevels(boolean globalSkillPointsSameAllLevels) {
        this.globalSkillPointsSameAllLevels = globalSkillPointsSameAllLevels;
    }

    public Map<Integer, Integer> getGlobalSkillPointsByLevel() {
        return new LinkedHashMap<>(globalSkillPointsByLevel);
    }

    public void setGlobalSkillPointsByLevel(Map<Integer, Integer> globalSkillPointsByLevel) {
        Map<Integer, Integer> safeValues = Objects.requireNonNullElse(globalSkillPointsByLevel, Map.of());
        LinkedHashMap<Integer, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : safeValues.entrySet()) {
            int level = Objects.requireNonNullElse(entry.getKey(), 0);
            int points = Objects.requireNonNullElse(entry.getValue(), 0);
            if (level > 0) {
                copy.put(level, Math.max(0, points));
            }
        }
        this.globalSkillPointsByLevel = copy;
    }

    public boolean isGlobalSkillPointsModifiedByInt() {
        return globalSkillPointsModifiedByInt;
    }

    public void setGlobalSkillPointsModifiedByInt(boolean globalSkillPointsModifiedByInt) {
        this.globalSkillPointsModifiedByInt = globalSkillPointsModifiedByInt;
    }

    public int getGlobalMinimumSkillPointsPerLevel() {
        return globalMinimumSkillPointsPerLevel;
    }

    public void setGlobalMinimumSkillPointsPerLevel(int globalMinimumSkillPointsPerLevel) {
        this.globalMinimumSkillPointsPerLevel = Math.max(0, globalMinimumSkillPointsPerLevel);
    }

    public int getResolvedSkillPointsPerLevel() {
        return resolvedSkillPointsPerLevel;
    }

    public void setResolvedSkillPointsPerLevel(int resolvedSkillPointsPerLevel) {
        this.resolvedSkillPointsPerLevel = Math.max(0, resolvedSkillPointsPerLevel);
    }

    public boolean isResolvedSkillPointsSameAllLevels() {
        return resolvedSkillPointsSameAllLevels;
    }

    public void setResolvedSkillPointsSameAllLevels(boolean resolvedSkillPointsSameAllLevels) {
        this.resolvedSkillPointsSameAllLevels = resolvedSkillPointsSameAllLevels;
    }

    public Map<Integer, Integer> getResolvedSkillPointsByLevel() {
        return new LinkedHashMap<>(resolvedSkillPointsByLevel);
    }

    public void setResolvedSkillPointsByLevel(Map<Integer, Integer> resolvedSkillPointsByLevel) {
        Map<Integer, Integer> safeValues = Objects.requireNonNullElse(resolvedSkillPointsByLevel, Map.of());
        LinkedHashMap<Integer, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : safeValues.entrySet()) {
            int level = Objects.requireNonNullElse(entry.getKey(), 0);
            int points = Objects.requireNonNullElse(entry.getValue(), 0);
            if (level > 0) {
                copy.put(level, Math.max(0, points));
            }
        }
        this.resolvedSkillPointsByLevel = copy;
    }

    public boolean isResolvedSkillPointsModifiedByInt() {
        return resolvedSkillPointsModifiedByInt;
    }

    public void setResolvedSkillPointsModifiedByInt(boolean resolvedSkillPointsModifiedByInt) {
        this.resolvedSkillPointsModifiedByInt = resolvedSkillPointsModifiedByInt;
    }

    public int getResolvedMinimumSkillPointsPerLevel() {
        return resolvedMinimumSkillPointsPerLevel;
    }

    public void setResolvedMinimumSkillPointsPerLevel(int resolvedMinimumSkillPointsPerLevel) {
        this.resolvedMinimumSkillPointsPerLevel = Math.max(0, resolvedMinimumSkillPointsPerLevel);
    }

    public String getStartingMoneyMethod() {
        return startingMoneyMethod;
    }

    public void setStartingMoneyMethod(String startingMoneyMethod) {
        String safe = Objects.toString(startingMoneyMethod, "").trim();
        this.startingMoneyMethod = safe.isEmpty() ? "base" : safe;
    }

    public int getStartingMoneyAmount() {
        return startingMoneyAmount;
    }

    public void setStartingMoneyAmount(int startingMoneyAmount) {
        this.startingMoneyAmount = Math.max(0, startingMoneyAmount);
    }

    public String getStartingMoneyCurrencyId() {
        return startingMoneyCurrencyId;
    }

    public void setStartingMoneyCurrencyId(String startingMoneyCurrencyId) {
        this.startingMoneyCurrencyId = Objects.toString(startingMoneyCurrencyId, "").trim();
    }

    public int getDiceSubstitutionsUsed() {
        return diceSubstitutionsUsed;
    }

    public void setDiceSubstitutionsUsed(int diceSubstitutionsUsed) {
        this.diceSubstitutionsUsed = Math.max(0, diceSubstitutionsUsed);
    }

    public Map<String, Integer> getRollAdjustmentUses() {
        return copyNonNegativeMap(rollAdjustmentUses);
    }

    public void setRollAdjustmentUses(Map<String, Integer> rollAdjustmentUses) {
        this.rollAdjustmentUses = copyNonNegativeMap(rollAdjustmentUses);
    }

    public Map<String, Integer> getRollAdjustmentResourceSpent() {
        return copyNonNegativeMap(rollAdjustmentResourceSpent);
    }

    public void setRollAdjustmentResourceSpent(Map<String, Integer> rollAdjustmentResourceSpent) {
        this.rollAdjustmentResourceSpent = copyNonNegativeMap(rollAdjustmentResourceSpent);
    }

    private static LinkedHashMap<String, Integer> copyNonNegativeMap(Map<String, Integer> source) {
        Map<String, Integer> safeSource = Objects.requireNonNullElse(source, Map.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeSource.entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
            }
        }
        return copy;
    }

    public List<String> getSelectedSpellIds() {
        return new ArrayList<>(selectedSpellIds);
    }

    public void setSelectedSpellIds(List<String> selectedSpellIds) {
        this.selectedSpellIds = normalizeIdList(selectedSpellIds);
    }

    public List<String> getSelectedWeaponIds() {
        return new ArrayList<>(selectedWeaponIds);
    }

    public void setSelectedWeaponIds(List<String> selectedWeaponIds) {
        this.selectedWeaponIds = normalizeIdList(selectedWeaponIds);
    }

    public List<String> getSelectedArmorIds() {
        return new ArrayList<>(selectedArmorIds);
    }

    public void setSelectedArmorIds(List<String> selectedArmorIds) {
        this.selectedArmorIds = normalizeIdList(selectedArmorIds);
    }

    public List<String> getSelectedEquipmentIds() {
        return new ArrayList<>(selectedEquipmentIds);
    }

    public void setSelectedEquipmentIds(List<String> selectedEquipmentIds) {
        this.selectedEquipmentIds = normalizeIdList(selectedEquipmentIds);
    }

    private List<String> normalizeIdList(List<String> values) {
        List<String> safeValues = Objects.requireNonNullElse(values, List.of());
        ArrayList<String> copy = new ArrayList<>();
        for (String value : safeValues) {
            String safe = Objects.toString(value, "").trim();
            if (!safe.isEmpty() && !copy.contains(safe)) {
                copy.add(safe);
            }
        }
        return copy;
    }

    public int getResolvedArmorClass() {
        return Math.max(0, resolvedArmorClass);
    }

    public void setResolvedArmorClass(int resolvedArmorClass) {
        this.resolvedArmorClass = Math.max(0, resolvedArmorClass);
    }
}
