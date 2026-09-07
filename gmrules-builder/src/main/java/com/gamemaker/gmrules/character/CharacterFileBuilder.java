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
import com.gamemaker.gmrules.CharacterElements.Background;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GMRCharacter;
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
        Map<String, String> draftRuleModes = safeDraft.getRuleModeSelections();
        GMRCharacter.ConstructionInput input = new GMRCharacter.ConstructionInput()
            .setCharacterId(safeDraft.getCharacterId())
            .setCharacterName(safeDraft.getCharacterName())
            .setSourceGameId(safeDraft.getGameId())
            .setSourceGameHash(safeDraft.getGameHash())
            .setSourceGameName(safeDraft.getGameName())
            .setSourceGameVersion(safeDraft.getGameVersion())
            .setRuleModeSelections(draftRuleModes.isEmpty() ? buildRuleModeSelections(safeGame) : draftRuleModes)
            .setCategoryPointSlotAssignments(safeDraft.getCategoryPointSlotAssignments())
            .setRaceId(safeDraft.getRaceId())
            .setBackgroundId(safeDraft.getBackgroundId())
            .setCharacterClassId(safeDraft.getClassId())
            .setAttributeScores(safeDraft.getAttributeScores())
            .setRacialSkillRanks(safeDraft.getRacialSkillRanks())
            .setRacialTraitNames(safeDraft.getRacialTraitNames())
            .setBackgroundSkillRanks(safeDraft.getBackgroundSkillRanks())
            .setClassSkillRanks(safeDraft.getClassSkillRanks())
            .setSelectedSkillRanks(safeDraft.getSelectedSkillRanks())
            .setSelectedSpellIds(safeDraft.getSelectedSpellIds())
            .setSelectedWeaponIds(safeDraft.getSelectedWeaponIds())
            .setSelectedArmorIds(safeDraft.getSelectedArmorIds())
            .setSelectedEquipmentIds(safeDraft.getSelectedEquipmentIds())
            .setStartingMoneyAmount(safeDraft.getStartingMoneyAmount())
            .setStartingMoneyCurrencyId(safeDraft.getStartingMoneyCurrencyId())
            .setResolvedArmorClass(safeDraft.getResolvedArmorClass())
            .setDiceSubstitutionsUsed(safeDraft.getDiceSubstitutionsUsed())
            .setRollAdjustmentUses(safeDraft.getRollAdjustmentUses())
            .setRollAdjustmentResourceSpent(safeDraft.getRollAdjustmentResourceSpent());
        GMRCharacter.ConstructionResult result = GMRCharacter.construct(safeGame, input);
        if (!result.isSuccessful()) {
            throw new IllegalArgumentException(formatDiagnostics(result.getDiagnostics()));
        }
        return new CharacterFile(result.getCharacter());
    }

    private String formatDiagnostics(List<GMRCharacter.Diagnostic> diagnostics) {
        return diagnostics.stream().map(GMRCharacter.Diagnostic::getMessage).reduce((left, right) -> left + " " + right)
            .orElse("Unable to construct character.");
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

}
