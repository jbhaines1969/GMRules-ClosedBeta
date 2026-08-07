/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.character;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameElement;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameMechanics.ArmorClassMethod;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameMechanics.CombatMethod;
import com.gamemaker.gmrules.GameMechanics.HPMethod;
import com.gamemaker.gmrules.GameMechanics.LevelingMethod;
import com.gamemaker.gmrules.GameMechanics.SaveMethod;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharacterFileBuilder {

// *** MEMBERS ***

// *** CONSTRUCTORS ***
    public CharacterFileBuilder() {
    }

// *** METHODS ***
    public CharacterFile build(Game game, CharacterDraft draft) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        CharacterDraft safeDraft = Objects.requireNonNullElseGet(draft, CharacterDraft::new);
        CharacterFile character = new CharacterFile();
        character.setCharacterName(safeDraft.getCharacterName());
        character.setSourceGameId(safeDraft.getGameId());
        character.setSourceGameHash(safeDraft.getGameHash());
        character.setSourceGameName(safeGame.getName());
        Map<String, String> draftRuleModes = safeDraft.getRuleModeSelections();
        character.setRuleModeSelections(draftRuleModes.isEmpty() ? buildRuleModeSelections(safeGame) : draftRuleModes);
        character.setCategoryPointSlotAssignments(safeDraft.getCategoryPointSlotAssignments());
        character.setRace(resolveElement(safeGame, ElementRegistryKey.RACES, safeDraft.getRaceId(), new Race("")));
        character.setCharacterClass(resolveElement(
            safeGame,
            ElementRegistryKey.CHARACTER_CLASSES,
            safeDraft.getClassId(),
            new CharacterClass("")
        ));
        character.setAttributeScores(resolveAttributeScores(safeGame, safeDraft));
        character.setRacialSkills(resolveSkillRanks(safeGame, safeDraft.getRacialSkillRanks()));
        character.setRacialTraitNames(safeDraft.getRacialTraitNames());
        character.setClassSkills(resolveSkillRanks(safeGame, safeDraft.getClassSkillRanks()));
        character.setSelectedSkills(resolveSkillRanks(safeGame, safeDraft.getSelectedSkillRanks()));
        character.setSelectedSpells(resolveElements(safeGame, ElementRegistryKey.SPELLS, safeDraft.getSelectedSpellIds()));
        character.setSelectedWeapons(resolveElements(safeGame, ElementRegistryKey.WEAPONS, safeDraft.getSelectedWeaponIds()));
        character.setSelectedArmor(resolveElements(safeGame, ElementRegistryKey.ARMOR, safeDraft.getSelectedArmorIds()));
        character.setSelectedEquipment(resolveElements(
            safeGame,
            ElementRegistryKey.EQUIPMENT,
            safeDraft.getSelectedEquipmentIds()
        ));
        character.setStartingMoneyAmount(safeDraft.getStartingMoneyAmount());
        character.setStartingMoneyCurrency(resolveElement(
            safeGame,
            ElementRegistryKey.CURRENCIES,
            safeDraft.getStartingMoneyCurrencyId(),
            new Currency("")
        ));
        character.setResolvedArmorClass(safeDraft.getResolvedArmorClass());
        character.setDiceSubstitutionsUsed(safeDraft.getDiceSubstitutionsUsed());
        return character;
    }

    public static Map<String, String> buildRuleModeSelections(Game game) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        LinkedHashMap<String, String> modes = new LinkedHashMap<>();
        AttributeGenerationMethod attributes = safeGame.getAttributeGenerationMethod();
        putMode(modes, "attributeGeneration.generationType", attributes.getGenerationType());
        putMode(modes, "attributeGeneration.hybridStages", String.join(",", attributes.getArray("hybridStages")));
        putMode(modes, "attributeGeneration.options", formatAttributeGenerationOptions(safeGame.getAttributeGenerationOptions()));
        putMode(modes, "attributeGeneration.defaultArrayType", attributes.getDefaultArrayType());
        putMode(modes, "attributeGeneration.standardArrayAssignmentMode", attributes.getStandardArrayAssignmentMode());
        putMode(modes, "attributeGeneration.assignInOrder", attributes.isAssignInOrder());
        putMode(modes, "attributeGeneration.allowReassignment", attributes.isAllowReassignment());
        putMode(modes, "attributeGeneration.allowDiceSubstitution", attributes.isAllowDiceSubstitution());
        putMode(modes, "attributeGeneration.diceSubstitutionValue", attributes.getDiceSubstitutionValue());
        putMode(modes, "attributeGeneration.maxDiceSubstitutions", attributes.getMaxDiceSubstitutions());
        putMode(modes, "attributeGeneration.allowNegativeAttributes", attributes.isAllowNegativeAttributes());
        putMode(modes, "attributeGeneration.assignByCategory", attributes.isAssignByCategory());
        putMode(modes, "attributeGeneration.categoryAssignmentMode", attributes.getCategoryAssignmentMode());
        putMode(modes, "attributeGeneration.categoryPointRules", formatCategoryPointRules(attributes));
        putMode(modes, "attributeGeneration.categoryPointSlots", formatCategoryPointSlots(attributes));

        HPMethod hp = safeGame.getHpMethod();
        putMode(modes, "hitPoints.hpGainMethod", hp.getHpGainMethod());
        putMode(modes, "hitPoints.allCharactersUseSameFixedGain", !hp.isAllowMultipleFixedGains());
        putMode(modes, "hitPoints.fixedHPPerLevel", hp.getFixedHPPerLevel());
        putMode(modes, "hitPoints.allCharactersUseSameHitDice", !hp.isAllowMultipleDiceTypes());
        putMode(modes, "hitPoints.hitDieCount", hp.getHitDieCount());
        putMode(modes, "hitPoints.hitDieSides", hp.getHitDieSides());
        putMode(modes, "hitPoints.hitDieModifier", hp.getHitDieModifier());
        putMode(modes, "hitPoints.firstLevelMethod", hp.getFirstLevelMethod());
        putMode(modes, "hitPoints.firstLevelMaxHP", hp.isFirstLevelMaxHP());
        putMode(modes, "hitPoints.hpModifierAttributeId", hp.getHpModifierAttributeId());
        putMode(modes, "hitPoints.allowNegativeAttributeModifier", hp.isAllowNegativeAttributeModifier());
        putMode(modes, "hitPoints.multiclassHPMethod", hp.getMulticlassHPMethod());
        putMode(modes, "hitPoints.attributeDerivationMode", hp.getAttributeDerivationMode());
        putMode(modes, "hitPoints.attributeDerivedDirectAttributeId", hp.getAttributeDerivedDirectAttributeId());
        putMode(modes, "hitPoints.attributeDerivedBaseValue", hp.getAttributeDerivedBaseValue());
        putMode(modes, "hitPoints.attributeDerivedDivisor", hp.getAttributeDerivedDivisor());
        putMode(modes, "hitPoints.attributeDerivedRoundingMethod", hp.getAttributeDerivedRoundingMethod());
        putMode(modes, "hitPoints.attributeDerivedTerms", formatAttributeDerivedTerms(hp));

        ArmorClassMethod armorClass = safeGame.getArmorClassMethod();
        putMode(modes, "armorClass.baseArmorClass", armorClass.getBaseArmorClass());
        putMode(modes, "armorClass.acAbilityAttributeId", armorClass.getAcAbilityAttributeId());

        LevelingMethod leveling = safeGame.getLevelingMethod();
        putMode(modes, "leveling.skillPointProgression", leveling.getSkillPointProgression());
        putMode(modes, "leveling.skillPointsSameAllLevels", leveling.isSkillPointsSameAllLevels());
        putMode(modes, "leveling.skillPointsModifiedByInt", leveling.isSkillPointsModifiedByInt());
        putMode(modes, "leveling.usesSkillRankCaps", leveling.isUsesSkillRankCaps());
        putMode(modes, "leveling.skillRankCapFormula", leveling.getSkillRankCapFormula());
        putMode(modes, "leveling.crossClassRankCapFormula", leveling.getCrossClassRankCapFormula());

        SaveMethod saves = safeGame.getSaveMethod();
        putMode(modes, "saves.baseCalculationMethod", saves.getBaseCalculationMethod());
        putMode(modes, "saves.successMethod", saves.getSuccessMethod());
        putMode(modes, "saves.groupSaveMethod", saves.getGroupSaveMethod());

        CombatMethod combat = safeGame.getCombatMethod();
        putMode(modes, "combat.usesAttackRolls", combat.isUsesAttackRolls());
        putMode(modes, "combat.attacksRollUnder", combat.isAttacksRollUnder());
        putMode(modes, "combat.usesArmorClassAsDefense", combat.isUsesArmorClassAsDefense());
        putMode(modes, "combat.criticalsEnabled", combat.isCriticalsEnabled());
        putMode(modes, "combat.criticalOnNatural20", combat.isCriticalOnNatural20());
        putMode(modes, "combat.natural1AutoMiss", combat.isNatural1AutoMiss());

        putMode(modes, "startingMoney.method", safeGame.getStartingMoneyMethod());
        return modes;
    }

    private static void putMode(Map<String, String> modes, String key, Object value) {
        String safeKey = Objects.toString(key, "").trim();
        if (!safeKey.isEmpty()) {
            modes.put(safeKey, Objects.toString(value, "").trim());
        }
    }

    private static String formatAttributeGenerationOptions(List<Game.AttributeGenerationOption> options) {
        List<String> values = new ArrayList<>();
        for (Game.AttributeGenerationOption option : options) {
            List<String> steps = new ArrayList<>();
            for (Game.AttributeGenerationStep step : option.getSteps()) {
                String methodType = Objects.toString(step.getMethodType(), "").trim();
                if (!methodType.isEmpty()) {
                    steps.add(methodType + ":" + Objects.toString(step.getApplicationMode(), "").trim());
                }
            }
            if (!steps.isEmpty()) {
                values.add(Objects.toString(option.getName(), "").trim() + "[" + String.join(">", steps) + "]");
            }
        }
        return String.join("|", values);
    }

    private static String formatCategoryPointSlots(AttributeGenerationMethod method) {
        List<String> values = new ArrayList<>();
        for (AttributeGenerationMethod.CategoryPointSlot slot : method.getCategoryPointSlots()) {
            values.add(slot.getId() + ":" + slot.getName() + ":" + slot.getAvailablePoints());
        }
        return String.join("|", values);
    }

    private static String formatCategoryPointRules(AttributeGenerationMethod method) {
        List<String> values = new ArrayList<>();
        for (AttributeGenerationMethod.CategoryPointRule rule : method.getCategoryPointRules()) {
            values.add(rule.getAttributeCategoryKey() + ":" + rule.getAvailablePoints());
        }
        return String.join("|", values);
    }

    private static String formatAttributeDerivedTerms(HPMethod method) {
        List<String> values = new ArrayList<>();
        for (HPMethod.AttributeHPTerm term : method.getAttributeDerivedTerms()) {
            values.add(term.getAttributeId() + ":" + term.getMultiplier());
        }
        return String.join("|", values);
    }

    private Map<Attribute, Integer> resolveAttributeScores(Game game, CharacterDraft draft) {
        LinkedHashMap<Attribute, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : draft.getAttributeScores().entrySet()) {
            Attribute attribute = game.getElementRegistry(ElementRegistryKey.ATTRIBUTES).getById(entry.getKey());
            if (attribute instanceof Attribute) {
                scores.put(attribute, Objects.requireNonNullElse(entry.getValue(), 0));
            }
        }
        return scores;
    }

    private Map<Skill, Integer> resolveSkillRanks(Game game, Map<String, Integer> ranks) {
        Map<String, Integer> safeRanks = Objects.requireNonNullElse(ranks, Map.of());
        LinkedHashMap<Skill, Integer> results = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeRanks.entrySet()) {
            String safeId = Objects.toString(entry.getKey(), "").trim();
            if (safeId.isEmpty()) {
                continue;
            }
            Skill skill = game.getElementRegistry(ElementRegistryKey.SKILLS).getById(safeId);
            if (skill instanceof Skill) {
                results.put(copyOf(skill), Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
            }
        }
        return results;
    }

    private <T extends GameElement & Serializable> T resolveElement(
        Game game,
        ElementRegistryKey<T> key,
        String id,
        T fallback
    ) {
        String safeId = Objects.toString(id, "").trim();
        T element = safeId.isEmpty() ? fallback : game.getElementRegistry(key).getById(safeId);
        return copyOf(Objects.requireNonNullElse(element, fallback));
    }

    private <T extends GameElement & Serializable> List<T> resolveElements(
        Game game,
        ElementRegistryKey<T> key,
        List<String> ids
    ) {
        List<String> safeIds = Objects.requireNonNullElse(ids, List.of());
        ArrayList<T> results = new ArrayList<>();
        for (String id : safeIds) {
            String safeId = Objects.toString(id, "").trim();
            if (safeId.isEmpty()) {
                continue;
            }
            T element = game.getElementRegistry(key).getById(safeId);
            if (element != null) {
                results.add(copyOf(element));
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> T copyOf(T value) {
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
            objectOutput.writeObject(value);
            objectOutput.flush();
            ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
            return (T) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to snapshot character element.", e);
        }
    }
}
