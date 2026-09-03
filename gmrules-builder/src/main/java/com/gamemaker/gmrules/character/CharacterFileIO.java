/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.character;

import com.gamemaker.gmrules.Game;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharacterFileIO {

// *** MEMBERS ***
    private static final String FILE_EXTENSION = ".gmcf";
    private static final String HEADER = "GMRulesCharacterFile v1";
    private static final String CHARACTER_FILE_HEADER = "GMRulesCharacterFileObject v1";
    private static final String ATTRIBUTE_PREFIX = "attr.";
    private static final String CATEGORY_POINT_SLOT_PREFIX = "pointBuyCategorySlot.";
    private static final String RACIAL_SKILL_PREFIX = "racialSkill.";
    private static final String RACIAL_TRAIT_PREFIX = "racialTrait.";
    private static final String BACKGROUND_SKILL_PREFIX = "backgroundSkill.";
    private static final String CLASS_SKILL_PREFIX = "classSkill.";
    private static final String SELECTED_SKILL_PREFIX = "selectedSkill.";
    private static final String CLASS_SKILL_POINTS_PER_LEVEL = "classSkillPointsPerLevel";
    private static final String CLASS_SKILL_POINTS_SAME_ALL_LEVELS = "classSkillPointsSameAllLevels";
    private static final String CLASS_SKILL_POINTS_LEVEL_PREFIX = "classSkillPointsLevel.";
    private static final String SKILL_POINT_SOURCE = "skillPointSource";
    private static final String SKILL_POINT_PROGRESSION = "skillPointProgression";
    private static final String GLOBAL_SKILL_POINTS_PER_LEVEL = "globalSkillPointsPerLevel";
    private static final String GLOBAL_SKILL_POINTS_SAME_ALL_LEVELS = "globalSkillPointsSameAllLevels";
    private static final String GLOBAL_SKILL_POINTS_LEVEL_PREFIX = "globalSkillPointsLevel.";
    private static final String GLOBAL_SKILL_POINTS_MODIFIED_BY_INT = "globalSkillPointsModifiedByInt";
    private static final String GLOBAL_MINIMUM_SKILL_POINTS_PER_LEVEL = "globalMinimumSkillPointsPerLevel";
    private static final String RESOLVED_SKILL_POINTS_PER_LEVEL = "resolvedSkillPointsPerLevel";
    private static final String RESOLVED_SKILL_POINTS_SAME_ALL_LEVELS = "resolvedSkillPointsSameAllLevels";
    private static final String RESOLVED_SKILL_POINTS_LEVEL_PREFIX = "resolvedSkillPointsLevel.";
    private static final String RESOLVED_SKILL_POINTS_MODIFIED_BY_INT = "resolvedSkillPointsModifiedByInt";
    private static final String RESOLVED_MINIMUM_SKILL_POINTS_PER_LEVEL = "resolvedMinimumSkillPointsPerLevel";
    private static final String STARTING_MONEY_METHOD = "startingMoneyMethod";
    private static final String STARTING_MONEY_AMOUNT = "startingMoneyAmount";
    private static final String STARTING_MONEY_CURRENCY_ID = "startingMoneyCurrencyId";
    private static final String DICE_SUBSTITUTIONS_USED = "diceSubstitutionsUsed";
    private static final String ROLL_ADJUSTMENT_USE_PREFIX = "rollAdjustmentUse.";
    private static final String ROLL_ADJUSTMENT_RESOURCE_SPENT_PREFIX = "rollAdjustmentResourceSpent.";
    private static final String RESOLVED_ARMOR_CLASS = "resolvedArmorClass";
    private static final String RULE_MODE_PREFIX = "ruleMode.";
    private static final String SELECTED_SPELL_PREFIX = "selectedSpell.";
    private static final String SELECTED_WEAPON_PREFIX = "selectedWeapon.";
    private static final String SELECTED_ARMOR_PREFIX = "selectedArmor.";
    private static final String SELECTED_EQUIPMENT_PREFIX = "selectedEquipment.";

// *** CONSTRUCTORS ***
    public CharacterFileIO() {
    }

// *** METHODS ***
    public CharacterDraft read(Path path) throws IOException {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        List<String> lines = Files.readAllLines(safePath, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).startsWith(HEADER)) {
            throw new IOException("Invalid character file.");
        }
        CharacterDraft draft = new CharacterDraft();
        Map<String, Integer> racialSkillRanks = new java.util.LinkedHashMap<>();
        List<String> racialTraitNames = new ArrayList<>();
        Map<String, Integer> backgroundSkillRanks = new java.util.LinkedHashMap<>();
        Map<String, Integer> classSkillRanks = new java.util.LinkedHashMap<>();
        Map<String, Integer> selectedSkillRanks = new java.util.LinkedHashMap<>();
        List<String> selectedSpellIds = new ArrayList<>();
        List<String> selectedWeaponIds = new ArrayList<>();
        List<String> selectedArmorIds = new ArrayList<>();
        List<String> selectedEquipmentIds = new ArrayList<>();
        Map<String, String> ruleModeSelections = new java.util.LinkedHashMap<>();
        Map<String, Integer> rollAdjustmentUses = new java.util.LinkedHashMap<>();
        Map<String, Integer> rollAdjustmentResourceSpent = new java.util.LinkedHashMap<>();
        Map<String, String> categoryPointSlotAssignments = new java.util.LinkedHashMap<>();
        Map<Integer, Integer> classSkillPointsByLevel = new java.util.LinkedHashMap<>();
        Map<Integer, Integer> globalSkillPointsByLevel = new java.util.LinkedHashMap<>();
        Map<Integer, Integer> resolvedSkillPointsByLevel = new java.util.LinkedHashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = Objects.toString(lines.get(i), "").trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            if (key.equals("gameId")) {
                draft.setGameId(value);
            } else if (key.equals("gameHash")) {
                draft.setGameHash(value);
            } else if (key.equals("characterName")) {
                draft.setCharacterName(value);
            } else if (key.equals("raceId")) {
                draft.setRaceId(value);
            } else if (key.equals("backgroundId")) {
                draft.setBackgroundId(value);
            } else if (key.equals("classId")) {
                draft.setClassId(value);
            } else if (key.equals(SKILL_POINT_SOURCE)) {
                draft.setSkillPointSource(value);
            } else if (key.equals(SKILL_POINT_PROGRESSION)) {
                draft.setSkillPointProgression(value);
            } else if (key.equals(CLASS_SKILL_POINTS_PER_LEVEL)) {
                try {
                    draft.setClassSkillPointsPerLevel(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid skill point value.
                }
            } else if (key.equals(CLASS_SKILL_POINTS_SAME_ALL_LEVELS)) {
                draft.setClassSkillPointsSameAllLevels(Boolean.parseBoolean(value));
            } else if (key.equals(GLOBAL_SKILL_POINTS_PER_LEVEL)) {
                try {
                    draft.setGlobalSkillPointsPerLevel(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid global skill point value.
                }
            } else if (key.equals(GLOBAL_SKILL_POINTS_SAME_ALL_LEVELS)) {
                draft.setGlobalSkillPointsSameAllLevels(Boolean.parseBoolean(value));
            } else if (key.equals(GLOBAL_SKILL_POINTS_MODIFIED_BY_INT)) {
                draft.setGlobalSkillPointsModifiedByInt(Boolean.parseBoolean(value));
            } else if (key.equals(GLOBAL_MINIMUM_SKILL_POINTS_PER_LEVEL)) {
                try {
                    draft.setGlobalMinimumSkillPointsPerLevel(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid global minimum.
                }
            } else if (key.equals(RESOLVED_SKILL_POINTS_PER_LEVEL)) {
                try {
                    draft.setResolvedSkillPointsPerLevel(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid resolved skill point value.
                }
            } else if (key.equals(RESOLVED_SKILL_POINTS_SAME_ALL_LEVELS)) {
                draft.setResolvedSkillPointsSameAllLevels(Boolean.parseBoolean(value));
            } else if (key.equals(RESOLVED_SKILL_POINTS_MODIFIED_BY_INT)) {
                draft.setResolvedSkillPointsModifiedByInt(Boolean.parseBoolean(value));
            } else if (key.equals(RESOLVED_MINIMUM_SKILL_POINTS_PER_LEVEL)) {
                try {
                    draft.setResolvedMinimumSkillPointsPerLevel(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid resolved minimum.
                }
            } else if (key.equals(STARTING_MONEY_METHOD)) {
                draft.setStartingMoneyMethod(value);
            } else if (key.equals(STARTING_MONEY_CURRENCY_ID)) {
                draft.setStartingMoneyCurrencyId(value);
            } else if (key.equals(STARTING_MONEY_AMOUNT)) {
                try {
                    draft.setStartingMoneyAmount(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid starting money amount.
                }
            } else if (key.equals(DICE_SUBSTITUTIONS_USED)) {
                try {
                    draft.setDiceSubstitutionsUsed(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid substitution usage value.
                }
            } else if (key.startsWith(ROLL_ADJUSTMENT_USE_PREFIX)) {
                putNonNegativeInteger(
                    rollAdjustmentUses,
                    key.substring(ROLL_ADJUSTMENT_USE_PREFIX.length()),
                    value
                );
            } else if (key.startsWith(ROLL_ADJUSTMENT_RESOURCE_SPENT_PREFIX)) {
                putNonNegativeInteger(
                    rollAdjustmentResourceSpent,
                    key.substring(ROLL_ADJUSTMENT_RESOURCE_SPENT_PREFIX.length()),
                    value
                );
            } else if (key.equals(RESOLVED_ARMOR_CLASS)) {
                try {
                    draft.setResolvedArmorClass(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // Skip invalid value.
                }
            } else if (key.startsWith(RULE_MODE_PREFIX)) {
                String modeKey = key.substring(RULE_MODE_PREFIX.length()).trim();
                if (!modeKey.isEmpty()) {
                    ruleModeSelections.put(modeKey, value);
                }
            } else if (key.startsWith(CATEGORY_POINT_SLOT_PREFIX)) {
                String slotId = key.substring(CATEGORY_POINT_SLOT_PREFIX.length()).trim();
                if (!slotId.isEmpty() && !value.isEmpty()) {
                    categoryPointSlotAssignments.put(slotId, value);
                }
            } else if (key.startsWith(RACIAL_SKILL_PREFIX)) {
                putSkillRank(racialSkillRanks, value);
            } else if (key.startsWith(RACIAL_TRAIT_PREFIX)) {
                if (!value.isEmpty() && !racialTraitNames.contains(value)) {
                    racialTraitNames.add(value);
                }
            } else if (key.startsWith(BACKGROUND_SKILL_PREFIX)) {
                putSkillRank(backgroundSkillRanks, value);
            } else if (key.startsWith(CLASS_SKILL_PREFIX)) {
                putSkillRank(classSkillRanks, value);
            } else if (key.startsWith(SELECTED_SKILL_PREFIX)) {
                putSkillRank(selectedSkillRanks, value);
            } else if (key.startsWith(SELECTED_SPELL_PREFIX)) {
                if (!value.isEmpty() && !selectedSpellIds.contains(value)) {
                    selectedSpellIds.add(value);
                }
            } else if (key.startsWith(SELECTED_WEAPON_PREFIX)) {
                if (!value.isEmpty() && !selectedWeaponIds.contains(value)) {
                    selectedWeaponIds.add(value);
                }
            } else if (key.startsWith(SELECTED_ARMOR_PREFIX)) {
                if (!value.isEmpty() && !selectedArmorIds.contains(value)) {
                    selectedArmorIds.add(value);
                }
            } else if (key.startsWith(SELECTED_EQUIPMENT_PREFIX)) {
                if (!value.isEmpty() && !selectedEquipmentIds.contains(value)) {
                    selectedEquipmentIds.add(value);
                }
            } else if (key.startsWith(CLASS_SKILL_POINTS_LEVEL_PREFIX)) {
                String levelText = key.substring(CLASS_SKILL_POINTS_LEVEL_PREFIX.length()).trim();
                if (levelText.isEmpty()) {
                    continue;
                }
                try {
                    int level = Integer.parseInt(levelText);
                    int points = Integer.parseInt(value);
                    if (level > 0) {
                        classSkillPointsByLevel.put(level, Math.max(0, points));
                    }
                } catch (NumberFormatException ignored) {
                    // Skip invalid level/points entry.
                }
            } else if (key.startsWith(GLOBAL_SKILL_POINTS_LEVEL_PREFIX)) {
                String levelText = key.substring(GLOBAL_SKILL_POINTS_LEVEL_PREFIX.length()).trim();
                if (levelText.isEmpty()) {
                    continue;
                }
                try {
                    int level = Integer.parseInt(levelText);
                    int points = Integer.parseInt(value);
                    if (level > 0) {
                        globalSkillPointsByLevel.put(level, Math.max(0, points));
                    }
                } catch (NumberFormatException ignored) {
                    // Skip invalid global level/points entry.
                }
            } else if (key.startsWith(RESOLVED_SKILL_POINTS_LEVEL_PREFIX)) {
                String levelText = key.substring(RESOLVED_SKILL_POINTS_LEVEL_PREFIX.length()).trim();
                if (levelText.isEmpty()) {
                    continue;
                }
                try {
                    int level = Integer.parseInt(levelText);
                    int points = Integer.parseInt(value);
                    if (level > 0) {
                        resolvedSkillPointsByLevel.put(level, Math.max(0, points));
                    }
                } catch (NumberFormatException ignored) {
                    // Skip invalid resolved level/points entry.
                }
            } else if (key.startsWith(ATTRIBUTE_PREFIX)) {
                String attributeId = key.substring(ATTRIBUTE_PREFIX.length()).trim();
                if (attributeId.isEmpty()) {
                    continue;
                }
                try {
                    int score = Integer.parseInt(value);
                    Map<String, Integer> scores = draft.getAttributeScores();
                    scores.put(attributeId, score);
                    draft.setAttributeScores(scores);
                } catch (NumberFormatException ignored) {
                    // Skip invalid score values.
                }
            }
        }
        draft.setRacialSkillRanks(racialSkillRanks);
        draft.setRacialTraitNames(racialTraitNames);
        draft.setBackgroundSkillRanks(backgroundSkillRanks);
        draft.setClassSkillRanks(classSkillRanks);
        draft.setSelectedSkillRanks(selectedSkillRanks);
        draft.setSelectedSpellIds(selectedSpellIds);
        draft.setSelectedWeaponIds(selectedWeaponIds);
        draft.setSelectedArmorIds(selectedArmorIds);
        draft.setSelectedEquipmentIds(selectedEquipmentIds);
        draft.setRuleModeSelections(ruleModeSelections);
        draft.setRollAdjustmentUses(rollAdjustmentUses);
        draft.setRollAdjustmentResourceSpent(rollAdjustmentResourceSpent);
        draft.setCategoryPointSlotAssignments(categoryPointSlotAssignments);
        draft.setClassSkillPointsByLevel(classSkillPointsByLevel);
        draft.setGlobalSkillPointsByLevel(globalSkillPointsByLevel);
        draft.setResolvedSkillPointsByLevel(resolvedSkillPointsByLevel);
        return draft;
    }

    public Path write(CharacterDraft draft, Path path) throws IOException {
        CharacterDraft safeDraft = Objects.requireNonNullElseGet(draft, CharacterDraft::new);
        Path safePath = normalizePath(path);
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        lines.add("gameId=" + safeDraft.getGameId());
        lines.add("gameHash=" + safeDraft.getGameHash());
        lines.add("characterName=" + safeDraft.getCharacterName());
        List<Map.Entry<String, String>> ruleModes = new ArrayList<>(safeDraft.getRuleModeSelections().entrySet());
        ruleModes.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, String> entry : ruleModes) {
            lines.add(RULE_MODE_PREFIX + entry.getKey() + "=" + Objects.toString(entry.getValue(), ""));
        }
        List<Map.Entry<String, String>> categoryAssignments =
            new ArrayList<>(safeDraft.getCategoryPointSlotAssignments().entrySet());
        categoryAssignments.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, String> entry : categoryAssignments) {
            lines.add(CATEGORY_POINT_SLOT_PREFIX + entry.getKey() + "=" + entry.getValue());
        }
        if (!safeDraft.getRaceId().isEmpty()) {
            lines.add("raceId=" + safeDraft.getRaceId());
        }
        if (!safeDraft.getBackgroundId().isEmpty()) {
            lines.add("backgroundId=" + safeDraft.getBackgroundId());
        }
        if (!safeDraft.getClassId().isEmpty()) {
            lines.add("classId=" + safeDraft.getClassId());
        }
        List<Map.Entry<String, Integer>> backgroundSkillRanks =
            new ArrayList<>(safeDraft.getBackgroundSkillRanks().entrySet());
        backgroundSkillRanks.sort(Map.Entry.comparingByKey());
        for (int index = 0; index < backgroundSkillRanks.size(); index++) {
            lines.add(BACKGROUND_SKILL_PREFIX + index + "=" + formatSkillRank(backgroundSkillRanks.get(index)));
        }
        List<Map.Entry<String, Integer>> classSkillRanks = new ArrayList<>(safeDraft.getClassSkillRanks().entrySet());
        classSkillRanks.sort(Map.Entry.comparingByKey());
        for (int index = 0; index < classSkillRanks.size(); index++) {
            lines.add(CLASS_SKILL_PREFIX + index + "=" + formatSkillRank(classSkillRanks.get(index)));
        }
        List<Map.Entry<String, Integer>> selectedSkillRanks = new ArrayList<>(safeDraft.getSelectedSkillRanks().entrySet());
        selectedSkillRanks.sort(Map.Entry.comparingByKey());
        for (int index = 0; index < selectedSkillRanks.size(); index++) {
            lines.add(SELECTED_SKILL_PREFIX + index + "=" + formatSkillRank(selectedSkillRanks.get(index)));
        }
        List<String> selectedSpellIds = new ArrayList<>(safeDraft.getSelectedSpellIds());
        selectedSpellIds.sort(String::compareTo);
        for (int index = 0; index < selectedSpellIds.size(); index++) {
            lines.add(SELECTED_SPELL_PREFIX + index + "=" + selectedSpellIds.get(index));
        }
        List<String> selectedWeaponIds = new ArrayList<>(safeDraft.getSelectedWeaponIds());
        selectedWeaponIds.sort(String::compareTo);
        for (int index = 0; index < selectedWeaponIds.size(); index++) {
            lines.add(SELECTED_WEAPON_PREFIX + index + "=" + selectedWeaponIds.get(index));
        }
        List<String> selectedArmorIds = new ArrayList<>(safeDraft.getSelectedArmorIds());
        selectedArmorIds.sort(String::compareTo);
        for (int index = 0; index < selectedArmorIds.size(); index++) {
            lines.add(SELECTED_ARMOR_PREFIX + index + "=" + selectedArmorIds.get(index));
        }
        List<String> selectedEquipmentIds = new ArrayList<>(safeDraft.getSelectedEquipmentIds());
        selectedEquipmentIds.sort(String::compareTo);
        for (int index = 0; index < selectedEquipmentIds.size(); index++) {
            lines.add(SELECTED_EQUIPMENT_PREFIX + index + "=" + selectedEquipmentIds.get(index));
        }
        lines.add(CLASS_SKILL_POINTS_PER_LEVEL + "=" + safeDraft.getClassSkillPointsPerLevel());
        lines.add(CLASS_SKILL_POINTS_SAME_ALL_LEVELS + "=" + safeDraft.isClassSkillPointsSameAllLevels());
        List<Map.Entry<Integer, Integer>> skillPointsByLevel = new ArrayList<>(safeDraft.getClassSkillPointsByLevel().entrySet());
        skillPointsByLevel.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Integer, Integer> entry : skillPointsByLevel) {
            lines.add(CLASS_SKILL_POINTS_LEVEL_PREFIX + entry.getKey() + "=" + entry.getValue());
        }
        lines.add(SKILL_POINT_SOURCE + "=" + safeDraft.getSkillPointSource());
        lines.add(SKILL_POINT_PROGRESSION + "=" + safeDraft.getSkillPointProgression());
        lines.add(GLOBAL_SKILL_POINTS_PER_LEVEL + "=" + safeDraft.getGlobalSkillPointsPerLevel());
        lines.add(GLOBAL_SKILL_POINTS_SAME_ALL_LEVELS + "=" + safeDraft.isGlobalSkillPointsSameAllLevels());
        lines.add(GLOBAL_SKILL_POINTS_MODIFIED_BY_INT + "=" + safeDraft.isGlobalSkillPointsModifiedByInt());
        lines.add(GLOBAL_MINIMUM_SKILL_POINTS_PER_LEVEL + "=" + safeDraft.getGlobalMinimumSkillPointsPerLevel());
        List<Map.Entry<Integer, Integer>> globalSkillPointsByLevel =
            new ArrayList<>(safeDraft.getGlobalSkillPointsByLevel().entrySet());
        globalSkillPointsByLevel.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Integer, Integer> entry : globalSkillPointsByLevel) {
            lines.add(GLOBAL_SKILL_POINTS_LEVEL_PREFIX + entry.getKey() + "=" + entry.getValue());
        }
        lines.add(RESOLVED_SKILL_POINTS_PER_LEVEL + "=" + safeDraft.getResolvedSkillPointsPerLevel());
        lines.add(RESOLVED_SKILL_POINTS_SAME_ALL_LEVELS + "=" + safeDraft.isResolvedSkillPointsSameAllLevels());
        lines.add(RESOLVED_SKILL_POINTS_MODIFIED_BY_INT + "=" + safeDraft.isResolvedSkillPointsModifiedByInt());
        lines.add(RESOLVED_MINIMUM_SKILL_POINTS_PER_LEVEL + "=" + safeDraft.getResolvedMinimumSkillPointsPerLevel());
        lines.add(STARTING_MONEY_METHOD + "=" + safeDraft.getStartingMoneyMethod());
        lines.add(STARTING_MONEY_AMOUNT + "=" + safeDraft.getStartingMoneyAmount());
        lines.add(STARTING_MONEY_CURRENCY_ID + "=" + safeDraft.getStartingMoneyCurrencyId());
        lines.add(RESOLVED_ARMOR_CLASS + "=" + safeDraft.getResolvedArmorClass());
        lines.add(DICE_SUBSTITUTIONS_USED + "=" + safeDraft.getDiceSubstitutionsUsed());
        appendIntegerMap(lines, ROLL_ADJUSTMENT_USE_PREFIX, safeDraft.getRollAdjustmentUses());
        appendIntegerMap(
            lines,
            ROLL_ADJUSTMENT_RESOURCE_SPENT_PREFIX,
            safeDraft.getRollAdjustmentResourceSpent()
        );
        List<Map.Entry<Integer, Integer>> resolvedSkillPointsByLevel =
            new ArrayList<>(safeDraft.getResolvedSkillPointsByLevel().entrySet());
        resolvedSkillPointsByLevel.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Integer, Integer> entry : resolvedSkillPointsByLevel) {
            lines.add(RESOLVED_SKILL_POINTS_LEVEL_PREFIX + entry.getKey() + "=" + entry.getValue());
        }
        List<Map.Entry<String, Integer>> racialSkillRanks = new ArrayList<>(safeDraft.getRacialSkillRanks().entrySet());
        racialSkillRanks.sort(Map.Entry.comparingByKey());
        for (int index = 0; index < racialSkillRanks.size(); index++) {
            lines.add(RACIAL_SKILL_PREFIX + index + "=" + formatSkillRank(racialSkillRanks.get(index)));
        }
        List<String> racialTraitNames = new ArrayList<>(safeDraft.getRacialTraitNames());
        racialTraitNames.sort(String::compareTo);
        for (int index = 0; index < racialTraitNames.size(); index++) {
            lines.add(RACIAL_TRAIT_PREFIX + index + "=" + racialTraitNames.get(index));
        }
        List<Map.Entry<String, Integer>> scores = new ArrayList<>(safeDraft.getAttributeScores().entrySet());
        scores.sort(Comparator.comparing(Map.Entry::getKey));
        for (Map.Entry<String, Integer> entry : scores) {
            lines.add(ATTRIBUTE_PREFIX + entry.getKey() + "=" + entry.getValue());
        }
        Files.write(safePath, lines, StandardCharsets.UTF_8);
        return safePath;
    }

    public CharacterFile readCharacterFile(Path path) throws IOException {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        try (
            ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(safePath)))
        ) {
            String header = Objects.toString(input.readUTF(), "");
            if (!CHARACTER_FILE_HEADER.equals(header)) {
                throw new IOException("Invalid character file.");
            }
            Object value = input.readObject();
            if (value instanceof CharacterFile) {
                return (CharacterFile) value;
            }
            throw new IOException("Invalid character file.");
        } catch (ClassNotFoundException e) {
            throw new IOException("Character file contains an unknown type.", e);
        }
    }

    public Path write(CharacterFile characterFile, Path path) throws IOException {
        CharacterFile safeCharacterFile = Objects.requireNonNullElseGet(characterFile, CharacterFile::new);
        Path safePath = normalizePath(path);
        try (
            ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(safePath)))
        ) {
            output.writeUTF(CHARACTER_FILE_HEADER);
            output.writeObject(safeCharacterFile);
        }
        return safePath;
    }

    public Path write(Game game, CharacterDraft draft, Path path) throws IOException {
        CharacterFile characterFile = new CharacterFileBuilder().build(game, draft);
        return write(characterFile, path);
    }

    private void putSkillRank(Map<String, Integer> ranks, String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            return;
        }
        String skillId = safeValue;
        int rank = 0;
        int separator = safeValue.lastIndexOf('|');
        if (separator >= 0) {
            skillId = safeValue.substring(0, separator).trim();
            String rankText = safeValue.substring(separator + 1).trim();
            try {
                rank = Math.max(0, Integer.parseInt(rankText));
            } catch (NumberFormatException ignored) {
                // Legacy or malformed rank values default to unranked.
            }
        }
        if (!skillId.isEmpty() && !ranks.containsKey(skillId)) {
            ranks.put(skillId, rank);
        }
    }

    private void putNonNegativeInteger(Map<String, Integer> values, String key, String rawValue) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return;
        }
        try {
            values.put(safeKey, Math.max(0, Integer.parseInt(Objects.toString(rawValue, "0").trim())));
        } catch (NumberFormatException ignored) {
            // Skip malformed adjustment accounting.
        }
    }

    private void appendIntegerMap(
        List<String> lines,
        String prefix,
        Map<String, Integer> values
    ) {
        values.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> lines.add(
                prefix + entry.getKey() + "=" + Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0))
            ));
    }

    private String formatSkillRank(Map.Entry<String, Integer> entry) {
        String skillId = Objects.toString(entry.getKey(), "").trim();
        int rank = Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0));
        return skillId + "|" + rank;
    }

    public Path normalizePath(Path path) {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        String filename = Objects.toString(safePath.getFileName(), "").trim();
        if (filename.toLowerCase().endsWith(FILE_EXTENSION)) {
            return safePath;
        }
        if (filename.isEmpty()) {
            return safePath;
        }
        return safePath.resolveSibling(filename + FILE_EXTENSION);
    }

    public String normalizeFilename(String baseName) {
        String safe = Objects.toString(baseName, "").trim();
        if (safe.isEmpty()) {
            return "character" + FILE_EXTENSION;
        }
        String cleaned = safe.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (cleaned.toLowerCase().endsWith(FILE_EXTENSION)) {
            return cleaned;
        }
        return cleaned + FILE_EXTENSION;
    }

    public String computeHash(Path path) throws IOException {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        byte[] bytes = Files.readAllBytes(safePath);
        byte[] digest;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            digest = hasher.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Hashing algorithm missing.", e);
        }
        StringBuilder builder = new StringBuilder();
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
