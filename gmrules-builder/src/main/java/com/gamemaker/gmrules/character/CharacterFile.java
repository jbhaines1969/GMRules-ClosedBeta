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
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameElements.Armor;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameElements.Equipment;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.GameElements.Weapon;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CharacterFile implements Serializable {

// *** MEMBERS ***
    private static final long serialVersionUID = 1L;

    private String characterName = "";
    private String sourceGameId = "";
    private String sourceGameHash = "";
    private String sourceGameName = "";
    private Map<String, String> ruleModeSelections = new LinkedHashMap<>();
    private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
    private Race race = new Race("");
    private Background background = new Background("");
    private CharacterClass characterClass = new CharacterClass("");
    private Map<Attribute, Integer> attributeScores = new LinkedHashMap<>();
    private Map<Skill, Integer> racialSkills = new LinkedHashMap<>();
    private List<String> racialTraitNames = new ArrayList<>();
    private Map<Skill, Integer> backgroundSkills = new LinkedHashMap<>();
    private Map<Skill, Integer> classSkills = new LinkedHashMap<>();
    private Map<Skill, Integer> selectedSkills = new LinkedHashMap<>();
    private List<Spell> selectedSpells = new ArrayList<>();
    private List<Weapon> selectedWeapons = new ArrayList<>();
    private List<Armor> selectedArmor = new ArrayList<>();
    private List<Equipment> selectedEquipment = new ArrayList<>();
    private int startingMoneyAmount = 0;
    private Currency startingMoneyCurrency = new Currency("");
    private int resolvedArmorClass = 0;
    private int diceSubstitutionsUsed = 0;

// *** CONSTRUCTORS ***
    public CharacterFile() {
    }

// *** METHODS ***
    public static CharacterFile fromDraft(Game game, CharacterDraft draft) {
        return new CharacterFileBuilder().build(game, draft);
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = Objects.toString(characterName, "").trim();
    }

    public String getSourceGameId() {
        return sourceGameId;
    }

    public void setSourceGameId(String sourceGameId) {
        this.sourceGameId = Objects.toString(sourceGameId, "").trim();
    }

    public String getSourceGameHash() {
        return sourceGameHash;
    }

    public void setSourceGameHash(String sourceGameHash) {
        this.sourceGameHash = Objects.toString(sourceGameHash, "").trim();
    }

    public String getSourceGameName() {
        return sourceGameName;
    }

    public void setSourceGameName(String sourceGameName) {
        this.sourceGameName = Objects.toString(sourceGameName, "").trim();
    }

    public Map<String, String> getRuleModeSelections() {
        return new LinkedHashMap<>(Objects.requireNonNullElse(ruleModeSelections, Map.of()));
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

    public Map<String, String> getCategoryPointSlotAssignments() {
        return new LinkedHashMap<>(Objects.requireNonNullElse(categoryPointSlotAssignments, Map.of()));
    }

    public void setCategoryPointSlotAssignments(Map<String, String> categoryPointSlotAssignments) {
        Map<String, String> safeValues = Objects.requireNonNullElse(categoryPointSlotAssignments, Map.of());
        LinkedHashMap<String, String> copy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : safeValues.entrySet()) {
            String slotId = Objects.toString(entry.getKey(), "").trim();
            String categoryKey = Objects.toString(entry.getValue(), "").trim().toLowerCase(Locale.ROOT);
            if (!slotId.isEmpty() && !categoryKey.isEmpty() && !copy.containsValue(categoryKey)) {
                copy.put(slotId, categoryKey);
            }
        }
        this.categoryPointSlotAssignments = copy;
    }

    public Race getRace() {
        return copyOf(race);
    }

    public void setRace(Race race) {
        this.race = copyOf(Objects.requireNonNullElseGet(race, () -> new Race("")));
    }

    public Background getBackground() {
        return copyOf(background);
    }

    public void setBackground(Background background) {
        this.background = copyOf(Objects.requireNonNullElseGet(background, () -> new Background("")));
    }

    public CharacterClass getCharacterClass() {
        return copyOf(characterClass);
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = copyOf(Objects.requireNonNullElseGet(characterClass, () -> new CharacterClass("")));
    }

    public Map<Attribute, Integer> getAttributeScores() {
        return copyMap(attributeScores);
    }

    public void setAttributeScores(Map<Attribute, Integer> attributeScores) {
        this.attributeScores = copyMap(Objects.requireNonNullElse(attributeScores, Map.of()));
    }

    public Map<Skill, Integer> getRacialSkills() {
        return copySkillMap(racialSkills);
    }

    public void setRacialSkills(Map<Skill, Integer> racialSkills) {
        this.racialSkills = copySkillMap(Objects.requireNonNullElse(racialSkills, Map.of()));
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

    public Map<Skill, Integer> getBackgroundSkills() {
        return copySkillMap(backgroundSkills);
    }

    public void setBackgroundSkills(Map<Skill, Integer> backgroundSkills) {
        this.backgroundSkills = copySkillMap(Objects.requireNonNullElse(backgroundSkills, Map.of()));
    }

    public Map<Skill, Integer> getClassSkills() {
        return copySkillMap(classSkills);
    }

    public void setClassSkills(Map<Skill, Integer> classSkills) {
        this.classSkills = copySkillMap(Objects.requireNonNullElse(classSkills, Map.of()));
    }

    public Map<Skill, Integer> getSelectedSkills() {
        return copySkillMap(selectedSkills);
    }

    public void setSelectedSkills(Map<Skill, Integer> selectedSkills) {
        this.selectedSkills = copySkillMap(Objects.requireNonNullElse(selectedSkills, Map.of()));
    }

    public List<Spell> getSelectedSpells() {
        return copyList(selectedSpells);
    }

    public void setSelectedSpells(List<Spell> selectedSpells) {
        this.selectedSpells = copyList(Objects.requireNonNullElse(selectedSpells, List.of()));
    }

    public List<Weapon> getSelectedWeapons() {
        return copyList(selectedWeapons);
    }

    public void setSelectedWeapons(List<Weapon> selectedWeapons) {
        this.selectedWeapons = copyList(Objects.requireNonNullElse(selectedWeapons, List.of()));
    }

    public List<Armor> getSelectedArmor() {
        return copyList(selectedArmor);
    }

    public void setSelectedArmor(List<Armor> selectedArmor) {
        this.selectedArmor = copyList(Objects.requireNonNullElse(selectedArmor, List.of()));
    }

    public List<Equipment> getSelectedEquipment() {
        return copyList(selectedEquipment);
    }

    public void setSelectedEquipment(List<Equipment> selectedEquipment) {
        this.selectedEquipment = copyList(Objects.requireNonNullElse(selectedEquipment, List.of()));
    }

    public int getStartingMoneyAmount() {
        return Math.max(0, startingMoneyAmount);
    }

    public void setStartingMoneyAmount(int startingMoneyAmount) {
        this.startingMoneyAmount = Math.max(0, startingMoneyAmount);
    }

    public Currency getStartingMoneyCurrency() {
        return copyOf(startingMoneyCurrency);
    }

    public void setStartingMoneyCurrency(Currency startingMoneyCurrency) {
        this.startingMoneyCurrency = copyOf(Objects.requireNonNullElseGet(startingMoneyCurrency, () -> new Currency("")));
    }

    public int getResolvedArmorClass() {
        return Math.max(0, resolvedArmorClass);
    }

    public void setResolvedArmorClass(int resolvedArmorClass) {
        this.resolvedArmorClass = Math.max(0, resolvedArmorClass);
    }

    public int getDiceSubstitutionsUsed() {
        return Math.max(0, diceSubstitutionsUsed);
    }

    public void setDiceSubstitutionsUsed(int diceSubstitutionsUsed) {
        this.diceSubstitutionsUsed = Math.max(0, diceSubstitutionsUsed);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ruleModeSelections = new LinkedHashMap<>(Objects.requireNonNullElse(ruleModeSelections, Map.of()));
        setCategoryPointSlotAssignments(categoryPointSlotAssignments);
        background = Objects.requireNonNullElseGet(background, () -> new Background(""));
        backgroundSkills = new LinkedHashMap<>(Objects.requireNonNullElse(backgroundSkills, Map.of()));
        selectedSpells = new ArrayList<>(Objects.requireNonNullElse(selectedSpells, List.of()));
    }

    private static <T extends Serializable> List<T> copyList(List<T> values) {
        List<T> safeValues = Objects.requireNonNullElse(values, List.of());
        ArrayList<T> copy = new ArrayList<>();
        for (T value : safeValues) {
            copy.add(copyOf(value));
        }
        return copy;
    }

    private static Map<Attribute, Integer> copyMap(Map<Attribute, Integer> values) {
        Map<Attribute, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        LinkedHashMap<Attribute, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<Attribute, Integer> entry : safeValues.entrySet()) {
            copy.put(copyOf(entry.getKey()), Objects.requireNonNullElse(entry.getValue(), 0));
        }
        return copy;
    }

    private static Map<Skill, Integer> copySkillMap(Map<Skill, Integer> values) {
        Map<Skill, Integer> safeValues = Objects.requireNonNullElse(values, Map.of());
        LinkedHashMap<Skill, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<Skill, Integer> entry : safeValues.entrySet()) {
            copy.put(copyOf(entry.getKey()), Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T copyOf(T value) {
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
