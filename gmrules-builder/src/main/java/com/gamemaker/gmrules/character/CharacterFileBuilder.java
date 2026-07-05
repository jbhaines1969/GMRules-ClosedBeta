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
