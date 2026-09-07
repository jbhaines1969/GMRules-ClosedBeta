/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null live fields or null checks outside serialization migration.
 - Represent empty values with empty strings, collections, and sentinel elements.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.character;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.CharacterElements.Background;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.GMRCharacter;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameElements.Armor;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameElements.Equipment;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameElements.Weapon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Persistence wrapper for the core-owned {@link GMRCharacter}. */
public class CharacterFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private GMRCharacter character = new GMRCharacter();

    /* Legacy v1 serialized fields. They are read only when migrating old files. */
    @Deprecated private String characterName = "";
    @Deprecated private String sourceGameId = "";
    @Deprecated private String sourceGameHash = "";
    @Deprecated private String sourceGameName = "";
    @Deprecated private Map<String, String> ruleModeSelections = new LinkedHashMap<>();
    @Deprecated private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
    @Deprecated private Race race = new Race("");
    @Deprecated private Background background = new Background("");
    @Deprecated private CharacterClass characterClass = new CharacterClass("");
    @Deprecated private Map<Attribute, Integer> attributeScores = new LinkedHashMap<>();
    @Deprecated private Map<Skill, Integer> racialSkills = new LinkedHashMap<>();
    @Deprecated private List<String> racialTraitNames = new ArrayList<>();
    @Deprecated private Map<Skill, Integer> backgroundSkills = new LinkedHashMap<>();
    @Deprecated private Map<Skill, Integer> classSkills = new LinkedHashMap<>();
    @Deprecated private Map<Skill, Integer> selectedSkills = new LinkedHashMap<>();
    @Deprecated private List<Spell> selectedSpells = new ArrayList<>();
    @Deprecated private List<Weapon> selectedWeapons = new ArrayList<>();
    @Deprecated private List<Armor> selectedArmor = new ArrayList<>();
    @Deprecated private List<Equipment> selectedEquipment = new ArrayList<>();
    @Deprecated private int startingMoneyAmount = 0;
    @Deprecated private Currency startingMoneyCurrency = new Currency("");
    @Deprecated private int resolvedArmorClass = 0;
    @Deprecated private int diceSubstitutionsUsed = 0;
    @Deprecated private Map<String, Integer> rollAdjustmentUses = new LinkedHashMap<>();
    @Deprecated private Map<String, Integer> rollAdjustmentResourceSpent = new LinkedHashMap<>();

    public CharacterFile() {
    }

    public CharacterFile(GMRCharacter character) {
        this.character = Objects.requireNonNullElseGet(character, GMRCharacter::new);
    }

    public static CharacterFile fromDraft(Game game, CharacterDraft draft) {
        return new CharacterFileBuilder().build(game, draft);
    }

    public GMRCharacter getCharacter() { return character; }
    public String getCharacterId() { return character.getId(); }
    public void setCharacterId(String value) { character.setId(value); }
    public String getCharacterName() { return character.getName(); }
    public void setCharacterName(String value) { character.setName(value); }
    public String getSourceGameId() { return character.getSourceGameId(); }
    public void setSourceGameId(String value) { character.setSourceGameId(value); }
    public String getSourceGameHash() { return character.getSourceGameHash(); }
    public void setSourceGameHash(String value) { character.setSourceGameHash(value); }
    public String getSourceGameName() { return character.getSourceGameName(); }
    public void setSourceGameName(String value) { character.setSourceGameName(value); }
    public String getSourceGameVersion() { return character.getSourceGameVersion(); }
    public void setSourceGameVersion(String value) { character.setSourceGameVersion(value); }
    public Map<String, String> getRuleModeSelections() { return character.getRuleModeSelections(); }
    public void setRuleModeSelections(Map<String, String> value) { character.setRuleModeSelections(value); }
    public Map<String, String> getCategoryPointSlotAssignments() { return character.getCategoryPointSlotAssignments(); }
    public void setCategoryPointSlotAssignments(Map<String, String> value) { character.setCategoryPointSlotAssignments(value); }
    public Race getRace() { return character.getRace(); }
    public void setRace(Race value) { character.setRace(value); }
    public Background getBackground() { return character.getBackground(); }
    public void setBackground(Background value) { character.setBackground(value); }
    public CharacterClass getCharacterClass() { return character.getCharacterClass(); }
    public void setCharacterClass(CharacterClass value) { character.setCharacterClass(value); }
    public Map<Attribute, Integer> getAttributeScores() { return character.getAttributeScores(); }
    public void setAttributeScores(Map<Attribute, Integer> value) { character.setAttributeScores(value); }
    public Map<Skill, Integer> getRacialSkills() { return character.getRacialSkills(); }
    public void setRacialSkills(Map<Skill, Integer> value) { character.setRacialSkills(value); }
    public List<String> getRacialTraitNames() { return character.getRacialTraitNames(); }
    public void setRacialTraitNames(List<String> value) { character.setRacialTraitNames(value); }
    public Map<Skill, Integer> getBackgroundSkills() { return character.getBackgroundSkills(); }
    public void setBackgroundSkills(Map<Skill, Integer> value) { character.setBackgroundSkills(value); }
    public Map<Skill, Integer> getClassSkills() { return character.getClassSkills(); }
    public void setClassSkills(Map<Skill, Integer> value) { character.setClassSkills(value); }
    public Map<Skill, Integer> getSelectedSkills() { return character.getSelectedSkills(); }
    public void setSelectedSkills(Map<Skill, Integer> value) { character.setSelectedSkills(value); }
    public List<Spell> getSelectedSpells() { return character.getSelectedSpells(); }
    public void setSelectedSpells(List<Spell> value) { character.setSelectedSpells(value); }
    public List<Weapon> getSelectedWeapons() { return character.getSelectedWeapons(); }
    public void setSelectedWeapons(List<Weapon> value) { character.setSelectedWeapons(value); }
    public List<Armor> getSelectedArmor() { return character.getSelectedArmor(); }
    public void setSelectedArmor(List<Armor> value) { character.setSelectedArmor(value); }
    public List<Equipment> getSelectedEquipment() { return character.getSelectedEquipment(); }
    public void setSelectedEquipment(List<Equipment> value) { character.setSelectedEquipment(value); }
    public int getStartingMoneyAmount() { return character.getStartingMoneyAmount(); }
    public void setStartingMoneyAmount(int value) { character.setStartingMoneyAmount(value); }
    public Currency getStartingMoneyCurrency() { return character.getStartingMoneyCurrency(); }
    public void setStartingMoneyCurrency(Currency value) { character.setStartingMoneyCurrency(value); }
    public int getResolvedArmorClass() { return character.getResolvedArmorClass(); }
    public void setResolvedArmorClass(int value) { character.setResolvedArmorClass(value); }
    public int getDiceSubstitutionsUsed() { return character.getDiceSubstitutionsUsed(); }
    public void setDiceSubstitutionsUsed(int value) { character.setDiceSubstitutionsUsed(value); }
    public Map<String, Integer> getRollAdjustmentUses() { return character.getRollAdjustmentUses(); }
    public void setRollAdjustmentUses(Map<String, Integer> value) { character.setRollAdjustmentUses(value); }
    public Map<String, Integer> getRollAdjustmentResourceSpent() { return character.getRollAdjustmentResourceSpent(); }
    public void setRollAdjustmentResourceSpent(Map<String, Integer> value) { character.setRollAdjustmentResourceSpent(value); }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (character == null) {
            GMRCharacter migrated = new GMRCharacter();
            migrated.setName(characterName);
            migrated.setSourceGameId(sourceGameId);
            migrated.setSourceGameHash(sourceGameHash);
            migrated.setSourceGameName(sourceGameName);
            migrated.setRuleModeSelections(ruleModeSelections);
            migrated.setCategoryPointSlotAssignments(categoryPointSlotAssignments);
            migrated.setRace(race);
            migrated.setBackground(background);
            migrated.setCharacterClass(characterClass);
            migrated.setAttributeScores(attributeScores);
            migrated.setRacialSkills(racialSkills);
            migrated.setRacialTraitNames(racialTraitNames);
            migrated.setBackgroundSkills(backgroundSkills);
            migrated.setClassSkills(classSkills);
            migrated.setSelectedSkills(selectedSkills);
            migrated.setSelectedSpells(selectedSpells);
            migrated.setSelectedWeapons(selectedWeapons);
            migrated.setSelectedArmor(selectedArmor);
            migrated.setSelectedEquipment(selectedEquipment);
            migrated.setStartingMoneyAmount(startingMoneyAmount);
            migrated.setStartingMoneyCurrency(startingMoneyCurrency);
            migrated.setResolvedArmorClass(resolvedArmorClass);
            migrated.setDiceSubstitutionsUsed(diceSubstitutionsUsed);
            migrated.setRollAdjustmentUses(rollAdjustmentUses);
            migrated.setRollAdjustmentResourceSpent(rollAdjustmentResourceSpent);
            character = migrated;
        }
        clearLegacyState();
    }

    private void clearLegacyState() {
        characterName = "";
        sourceGameId = "";
        sourceGameHash = "";
        sourceGameName = "";
        ruleModeSelections = new LinkedHashMap<>();
        categoryPointSlotAssignments = new LinkedHashMap<>();
        race = new Race("");
        background = new Background("");
        characterClass = new CharacterClass("");
        attributeScores = new LinkedHashMap<>();
        racialSkills = new LinkedHashMap<>();
        racialTraitNames = new ArrayList<>();
        backgroundSkills = new LinkedHashMap<>();
        classSkills = new LinkedHashMap<>();
        selectedSkills = new LinkedHashMap<>();
        selectedSpells = new ArrayList<>();
        selectedWeapons = new ArrayList<>();
        selectedArmor = new ArrayList<>();
        selectedEquipment = new ArrayList<>();
        startingMoneyAmount = 0;
        startingMoneyCurrency = new Currency("");
        resolvedArmorClass = 0;
        diceSubstitutionsUsed = 0;
        rollAdjustmentUses = new LinkedHashMap<>();
        rollAdjustmentResourceSpent = new LinkedHashMap<>();
    }
}
