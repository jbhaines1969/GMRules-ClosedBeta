/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects, not null.
 - Normalize absent external values immediately.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.CharacterElements.Background;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Heritage;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Core-owned authoritative character state.
 *
 * <p>Game definitions are snapshotted when they enter a character. Their stable
 * definition IDs are preserved, but later edits to the source Game cannot alter
 * this character or another character. Collection getters are structurally
 * read-only and return the character-owned definition snapshots without
 * serialization on ordinary reads. Definition objects must be treated as
 * read-only by consumers; character state changes belong behind core operations.
 * Catalog equipment IDs currently identify definitions, not unique owned-item
 * instances.</p>
 *
 * <p>Successful construction proves only that supplied references resolved and
 * the supported state was assembled. Draft-supplied scores, money, and Defense
 * remain provisional until their owning mechanics validate them. This model does
 * not yet represent item instances, specializations, multiclass state, general
 * resources, or combat-session state.</p>
 */
public final class GMRCharacter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = UUID.randomUUID().toString();
    private String name = "";
    private String sourceGameId = "";
    private String sourceGameHash = "";
    private String sourceGameName = "";
    private String sourceGameVersion = "";
    private Map<String, String> ruleModeSelections = new LinkedHashMap<>();
    private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
    private Race race = new Race("");
    private boolean raceSelected = false;
    private Heritage heritage = new Heritage("");
    private boolean heritageSelected = false;
    private Background background = new Background("");
    private boolean backgroundSelected = false;
    private CharacterClass characterClass = new CharacterClass("");
    private boolean characterClassSelected = false;
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
    private boolean startingMoneyCurrencySelected = false;
    private int resolvedArmorClass = 0;
    private int diceSubstitutionsUsed = 0;
    private Map<String, Integer> rollAdjustmentUses = new LinkedHashMap<>();
    private Map<String, Integer> rollAdjustmentResourceSpent = new LinkedHashMap<>();

    public GMRCharacter() {
    }

    public GMRCharacter(GMRCharacter source) {
        copyFrom(Objects.requireNonNullElseGet(source, GMRCharacter::new));
    }

    public static ConstructionResult construct(Game game, ConstructionInput input) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        ConstructionInput safeInput = Objects.requireNonNullElseGet(input, ConstructionInput::new);
        ArrayList<Diagnostic> diagnostics = new ArrayList<>();

        Race resolvedRace = resolveOptional(safeGame, ElementRegistryKey.RACES, safeInput.raceId, "race", diagnostics);
        Heritage resolvedHeritage = resolveOptional(
            safeGame,
            ElementRegistryKey.HERITAGES,
            safeInput.heritageId,
            "heritage",
            diagnostics
        );
        Background resolvedBackground = resolveOptional(
            safeGame,
            ElementRegistryKey.BACKGROUNDS,
            safeInput.backgroundId,
            "background",
            diagnostics
        );
        CharacterClass resolvedClass = resolveOptional(
            safeGame,
            ElementRegistryKey.CHARACTER_CLASSES,
            safeInput.characterClassId,
            "characterClass",
            diagnostics
        );
        Currency resolvedCurrency = resolveOptional(
            safeGame,
            ElementRegistryKey.CURRENCIES,
            safeInput.startingMoneyCurrencyId,
            "startingMoneyCurrency",
            diagnostics
        );
        if (!safeInput.heritageId.isEmpty() && !safeInput.raceId.isEmpty()
            && resolvedHeritage != null && resolvedRace != null
            && !resolvedHeritage.isUnrestrictedAncestry()
            && !resolvedHeritage.getAncestryIds().contains(resolvedRace.getId())) {
            diagnostics.add(new Diagnostic(
                DiagnosticCode.INVALID_SELECTION,
                "heritage",
                safeInput.heritageId,
                Heritage.class.getName(),
                Heritage.class.getName(),
                "Heritage is not available to the selected Race/Ancestry."
            ));
        }
        Map<Attribute, Integer> resolvedAttributes = resolveRanks(
            safeGame,
            ElementRegistryKey.ATTRIBUTES,
            safeInput.attributeScores,
            "attributeScores",
            false,
            diagnostics
        );
        Map<Skill, Integer> resolvedRacialSkills = resolveRanks(
            safeGame,
            ElementRegistryKey.SKILLS,
            safeInput.racialSkillRanks,
            "racialSkills",
            true,
            diagnostics
        );
        Map<Skill, Integer> resolvedBackgroundSkills = resolveRanks(
            safeGame,
            ElementRegistryKey.SKILLS,
            safeInput.backgroundSkillRanks,
            "backgroundSkills",
            true,
            diagnostics
        );
        Map<Skill, Integer> resolvedClassSkills = resolveRanks(
            safeGame,
            ElementRegistryKey.SKILLS,
            safeInput.classSkillRanks,
            "classSkills",
            true,
            diagnostics
        );
        Map<Skill, Integer> resolvedSelectedSkills = resolveRanks(
            safeGame,
            ElementRegistryKey.SKILLS,
            safeInput.selectedSkillRanks,
            "selectedSkills",
            true,
            diagnostics
        );
        List<Spell> resolvedSpells = resolveList(
            safeGame,
            ElementRegistryKey.SPELLS,
            safeInput.selectedSpellIds,
            "selectedSpells",
            diagnostics
        );
        List<Weapon> resolvedWeapons = resolveList(
            safeGame,
            ElementRegistryKey.WEAPONS,
            safeInput.selectedWeaponIds,
            "selectedWeapons",
            diagnostics
        );
        List<Armor> resolvedArmor = resolveList(
            safeGame,
            ElementRegistryKey.ARMOR,
            safeInput.selectedArmorIds,
            "selectedArmor",
            diagnostics
        );
        List<Equipment> resolvedEquipment = resolveList(
            safeGame,
            ElementRegistryKey.EQUIPMENT,
            safeInput.selectedEquipmentIds,
            "selectedEquipment",
            diagnostics
        );

        if (!diagnostics.isEmpty()) {
            return new ConstructionResult(new GMRCharacter(), diagnostics);
        }

        GMRCharacter character = new GMRCharacter();
        character.setId(safeInput.characterId);
        character.setName(safeInput.characterName);
        character.setSourceGameId(safeInput.sourceGameId.isEmpty() ? safeGame.getId() : safeInput.sourceGameId);
        character.setSourceGameHash(safeInput.sourceGameHash);
        character.setSourceGameName(
            safeInput.sourceGameName.isEmpty() ? safeGame.getName() : safeInput.sourceGameName
        );
        character.setSourceGameVersion(
            safeInput.sourceGameVersion.isEmpty() ? safeGame.getVersion() : safeInput.sourceGameVersion
        );
        character.setRuleModeSelections(safeInput.ruleModeSelections);
        character.setCategoryPointSlotAssignments(safeInput.categoryPointSlotAssignments);
        character.setRace(resolvedRace, !safeInput.raceId.isEmpty());
        character.setHeritage(resolvedHeritage, !safeInput.heritageId.isEmpty());
        character.setBackground(resolvedBackground, !safeInput.backgroundId.isEmpty());
        character.setCharacterClass(resolvedClass, !safeInput.characterClassId.isEmpty());
        character.setAttributeScores(resolvedAttributes);
        character.setRacialSkills(resolvedRacialSkills);
        character.setRacialTraitNames(safeInput.racialTraitNames);
        character.setBackgroundSkills(resolvedBackgroundSkills);
        character.setClassSkills(resolvedClassSkills);
        character.setSelectedSkills(resolvedSelectedSkills);
        character.setSelectedSpells(resolvedSpells);
        character.setSelectedWeapons(resolvedWeapons);
        character.setSelectedArmor(resolvedArmor);
        character.setSelectedEquipment(resolvedEquipment);
        character.setStartingMoneyAmount(safeInput.startingMoneyAmount);
        character.setStartingMoneyCurrency(resolvedCurrency, !safeInput.startingMoneyCurrencyId.isEmpty());
        character.setResolvedArmorClass(safeInput.resolvedArmorClass);
        character.setDiceSubstitutionsUsed(safeInput.diceSubstitutionsUsed);
        character.setRollAdjustmentUses(safeInput.rollAdjustmentUses);
        character.setRollAdjustmentResourceSpent(safeInput.rollAdjustmentResourceSpent);
        return new ConstructionResult(character, List.of());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        String safeId = Objects.toString(id, "").trim();
        this.id = safeId.isEmpty() ? UUID.randomUUID().toString() : safeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.toString(name, "").trim();
    }

    public String getSourceGameId() { return sourceGameId; }
    public void setSourceGameId(String value) { sourceGameId = Objects.toString(value, "").trim(); }
    public String getSourceGameHash() { return sourceGameHash; }
    public void setSourceGameHash(String value) { sourceGameHash = Objects.toString(value, "").trim(); }
    public String getSourceGameName() { return sourceGameName; }
    public void setSourceGameName(String value) { sourceGameName = Objects.toString(value, "").trim(); }
    public String getSourceGameVersion() { return sourceGameVersion; }
    public void setSourceGameVersion(String value) { sourceGameVersion = Objects.toString(value, "").trim(); }

    public Map<String, String> getRuleModeSelections() {
        return Collections.unmodifiableMap(ruleModeSelections);
    }

    public void setRuleModeSelections(Map<String, String> values) {
        ruleModeSelections = copyStringMap(values, false);
    }

    public Map<String, String> getCategoryPointSlotAssignments() {
        return Collections.unmodifiableMap(categoryPointSlotAssignments);
    }

    public void setCategoryPointSlotAssignments(Map<String, String> values) {
        categoryPointSlotAssignments = copyStringMap(values, true);
    }

    public boolean hasRace() { return raceSelected; }
    public Race getRace() { return race; }
    public void setRace(Race value) { setRace(value, hasMeaningfulElement(value)); }
    public void setRace(Race value, boolean selected) {
        race = snapshot(Objects.requireNonNullElseGet(value, () -> new Race("")));
        raceSelected = selected;
    }

    public boolean hasHeritage() { return heritageSelected; }
    public Heritage getHeritage() { return heritage; }
    public void setHeritage(Heritage value) { setHeritage(value, hasMeaningfulElement(value)); }
    public void setHeritage(Heritage value, boolean selected) {
        heritage = snapshot(Objects.requireNonNullElseGet(value, () -> new Heritage("")));
        heritageSelected = selected;
    }

    public boolean hasBackground() { return backgroundSelected; }
    public Background getBackground() { return background; }
    public void setBackground(Background value) { setBackground(value, hasMeaningfulElement(value)); }
    public void setBackground(Background value, boolean selected) {
        background = snapshot(Objects.requireNonNullElseGet(value, () -> new Background("")));
        backgroundSelected = selected;
    }

    public boolean hasCharacterClass() { return characterClassSelected; }
    public CharacterClass getCharacterClass() { return characterClass; }
    public void setCharacterClass(CharacterClass value) { setCharacterClass(value, hasMeaningfulElement(value)); }
    public void setCharacterClass(CharacterClass value, boolean selected) {
        characterClass = snapshot(Objects.requireNonNullElseGet(value, () -> new CharacterClass("")));
        characterClassSelected = selected;
    }

    public Map<Attribute, Integer> getAttributeScores() { return Collections.unmodifiableMap(attributeScores); }
    public void setAttributeScores(Map<Attribute, Integer> values) { attributeScores = snapshotMap(values, false); }
    public Map<Skill, Integer> getRacialSkills() { return Collections.unmodifiableMap(racialSkills); }
    public void setRacialSkills(Map<Skill, Integer> values) { racialSkills = snapshotMap(values, true); }
    public List<String> getRacialTraitNames() { return Collections.unmodifiableList(racialTraitNames); }
    public void setRacialTraitNames(List<String> values) { racialTraitNames = copyStrings(values); }
    public Map<Skill, Integer> getBackgroundSkills() { return Collections.unmodifiableMap(backgroundSkills); }
    public void setBackgroundSkills(Map<Skill, Integer> values) { backgroundSkills = snapshotMap(values, true); }
    public Map<Skill, Integer> getClassSkills() { return Collections.unmodifiableMap(classSkills); }
    public void setClassSkills(Map<Skill, Integer> values) { classSkills = snapshotMap(values, true); }
    public Map<Skill, Integer> getSelectedSkills() { return Collections.unmodifiableMap(selectedSkills); }
    public void setSelectedSkills(Map<Skill, Integer> values) { selectedSkills = snapshotMap(values, true); }
    public List<Spell> getSelectedSpells() { return Collections.unmodifiableList(selectedSpells); }
    public void setSelectedSpells(List<Spell> values) { selectedSpells = snapshotList(values); }
    public List<Weapon> getSelectedWeapons() { return Collections.unmodifiableList(selectedWeapons); }
    public void setSelectedWeapons(List<Weapon> values) { selectedWeapons = snapshotList(values); }
    public List<Armor> getSelectedArmor() { return Collections.unmodifiableList(selectedArmor); }
    public void setSelectedArmor(List<Armor> values) { selectedArmor = snapshotList(values); }
    public List<Equipment> getSelectedEquipment() { return Collections.unmodifiableList(selectedEquipment); }
    public void setSelectedEquipment(List<Equipment> values) { selectedEquipment = snapshotList(values); }
    public int getStartingMoneyAmount() { return Math.max(0, startingMoneyAmount); }
    public void setStartingMoneyAmount(int value) { startingMoneyAmount = Math.max(0, value); }
    public boolean hasStartingMoneyCurrency() { return startingMoneyCurrencySelected; }
    public Currency getStartingMoneyCurrency() { return startingMoneyCurrency; }
    public void setStartingMoneyCurrency(Currency value) { setStartingMoneyCurrency(value, hasMeaningfulElement(value)); }
    public void setStartingMoneyCurrency(Currency value, boolean selected) {
        startingMoneyCurrency = snapshot(Objects.requireNonNullElseGet(value, () -> new Currency("")));
        startingMoneyCurrencySelected = selected;
    }
    public int getResolvedArmorClass() { return Math.max(0, resolvedArmorClass); }
    public void setResolvedArmorClass(int value) { resolvedArmorClass = Math.max(0, value); }
    public int getDiceSubstitutionsUsed() { return Math.max(0, diceSubstitutionsUsed); }
    public void setDiceSubstitutionsUsed(int value) { diceSubstitutionsUsed = Math.max(0, value); }
    public Map<String, Integer> getRollAdjustmentUses() { return Collections.unmodifiableMap(rollAdjustmentUses); }
    public void setRollAdjustmentUses(Map<String, Integer> values) { rollAdjustmentUses = copyNonNegativeMap(values); }
    public Map<String, Integer> getRollAdjustmentResourceSpent() {
        return Collections.unmodifiableMap(rollAdjustmentResourceSpent);
    }
    public void setRollAdjustmentResourceSpent(Map<String, Integer> values) {
        rollAdjustmentResourceSpent = copyNonNegativeMap(values);
    }

    private void copyFrom(GMRCharacter source) {
        GMRCharacter copy = snapshot(source);
        id = copy.id;
        name = copy.name;
        sourceGameId = copy.sourceGameId;
        sourceGameHash = copy.sourceGameHash;
        sourceGameName = copy.sourceGameName;
        sourceGameVersion = copy.sourceGameVersion;
        ruleModeSelections = copy.ruleModeSelections;
        categoryPointSlotAssignments = copy.categoryPointSlotAssignments;
        race = copy.race;
        raceSelected = copy.raceSelected;
        heritage = copy.heritage;
        heritageSelected = copy.heritageSelected;
        background = copy.background;
        backgroundSelected = copy.backgroundSelected;
        characterClass = copy.characterClass;
        characterClassSelected = copy.characterClassSelected;
        attributeScores = copy.attributeScores;
        racialSkills = copy.racialSkills;
        racialTraitNames = copy.racialTraitNames;
        backgroundSkills = copy.backgroundSkills;
        classSkills = copy.classSkills;
        selectedSkills = copy.selectedSkills;
        selectedSpells = copy.selectedSpells;
        selectedWeapons = copy.selectedWeapons;
        selectedArmor = copy.selectedArmor;
        selectedEquipment = copy.selectedEquipment;
        startingMoneyAmount = copy.startingMoneyAmount;
        startingMoneyCurrency = copy.startingMoneyCurrency;
        startingMoneyCurrencySelected = copy.startingMoneyCurrencySelected;
        resolvedArmorClass = copy.resolvedArmorClass;
        diceSubstitutionsUsed = copy.diceSubstitutionsUsed;
        rollAdjustmentUses = copy.rollAdjustmentUses;
        rollAdjustmentResourceSpent = copy.rollAdjustmentResourceSpent;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setId(id);
        setName(name);
        setSourceGameId(sourceGameId);
        setSourceGameHash(sourceGameHash);
        setSourceGameName(sourceGameName);
        setSourceGameVersion(sourceGameVersion);
        setRuleModeSelections(ruleModeSelections);
        setCategoryPointSlotAssignments(categoryPointSlotAssignments);
        race = Objects.requireNonNullElseGet(race, () -> new Race(""));
        heritage = Objects.requireNonNullElseGet(heritage, () -> new Heritage(""));
        background = Objects.requireNonNullElseGet(background, () -> new Background(""));
        characterClass = Objects.requireNonNullElseGet(characterClass, () -> new CharacterClass(""));
        attributeScores = new LinkedHashMap<>(Objects.requireNonNullElse(attributeScores, Map.of()));
        racialSkills = new LinkedHashMap<>(Objects.requireNonNullElse(racialSkills, Map.of()));
        racialTraitNames = copyStrings(racialTraitNames);
        backgroundSkills = new LinkedHashMap<>(Objects.requireNonNullElse(backgroundSkills, Map.of()));
        classSkills = new LinkedHashMap<>(Objects.requireNonNullElse(classSkills, Map.of()));
        selectedSkills = new LinkedHashMap<>(Objects.requireNonNullElse(selectedSkills, Map.of()));
        selectedSpells = new ArrayList<>(Objects.requireNonNullElse(selectedSpells, List.of()));
        selectedWeapons = new ArrayList<>(Objects.requireNonNullElse(selectedWeapons, List.of()));
        selectedArmor = new ArrayList<>(Objects.requireNonNullElse(selectedArmor, List.of()));
        selectedEquipment = new ArrayList<>(Objects.requireNonNullElse(selectedEquipment, List.of()));
        startingMoneyCurrency = Objects.requireNonNullElseGet(startingMoneyCurrency, () -> new Currency(""));
        setStartingMoneyAmount(startingMoneyAmount);
        setResolvedArmorClass(resolvedArmorClass);
        setDiceSubstitutionsUsed(diceSubstitutionsUsed);
        setRollAdjustmentUses(rollAdjustmentUses);
        setRollAdjustmentResourceSpent(rollAdjustmentResourceSpent);
    }

    private static <T extends GameElement> T resolveOptional(
        Game game,
        ElementRegistryKey<T> key,
        String id,
        String path,
        List<Diagnostic> diagnostics
    ) {
        String safeId = Objects.toString(id, "").trim();
        if (safeId.isEmpty()) {
            return key.createDefault().getById("");
        }
        T value = game.getElementRegistry(key).getById(safeId);
        if (value == null) {
            diagnostics.add(diagnosticFor(game, key, safeId, path));
        }
        return value;
    }

    private static <T extends GameElement> Map<T, Integer> resolveRanks(
        Game game,
        ElementRegistryKey<T> key,
        Map<String, Integer> values,
        String path,
        boolean nonNegative,
        List<Diagnostic> diagnostics
    ) {
        LinkedHashMap<T, Integer> resolved = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : Objects.requireNonNullElse(values, Map.<String, Integer>of()).entrySet()) {
            String id = Objects.toString(entry.getKey(), "").trim();
            T value = game.getElementRegistry(key).getById(id);
            if (value == null) {
                diagnostics.add(diagnosticFor(game, key, id, path + "[" + id + "]"));
            } else {
                int number = Objects.requireNonNullElse(entry.getValue(), 0);
                resolved.put(value, nonNegative ? Math.max(0, number) : number);
            }
        }
        return resolved;
    }

    private static <T extends GameElement> List<T> resolveList(
        Game game,
        ElementRegistryKey<T> key,
        List<String> ids,
        String path,
        List<Diagnostic> diagnostics
    ) {
        ArrayList<T> resolved = new ArrayList<>();
        int index = 0;
        for (String rawId : Objects.requireNonNullElse(ids, List.<String>of())) {
            String id = Objects.toString(rawId, "").trim();
            T value = game.getElementRegistry(key).getById(id);
            if (value == null) {
                diagnostics.add(diagnosticFor(game, key, id, path + "[" + index + "]"));
            } else {
                resolved.add(value);
            }
            index++;
        }
        return resolved;
    }

    private static Diagnostic diagnosticFor(Game game, ElementRegistryKey<?> expected, String id, String path) {
        String actualType = findActualType(game, id);
        if (!actualType.isEmpty()) {
            return new Diagnostic(
                DiagnosticCode.WRONG_TYPE_REFERENCE,
                path,
                id,
                expected.getType().getSimpleName(),
                actualType,
                "Reference '" + id + "' resolves to " + actualType + ", not " + expected.getType().getSimpleName() + "."
            );
        }
        return new Diagnostic(
            DiagnosticCode.MISSING_REFERENCE,
            path,
            id,
            expected.getType().getSimpleName(),
            "",
            "Reference '" + id + "' was not found in the supplied Game."
        );
    }

    private static String findActualType(Game game, String id) {
        List<ElementRegistryKey<?>> keys = List.of(
            ElementRegistryKey.ATTRIBUTES, ElementRegistryKey.SKILLS, ElementRegistryKey.CHARACTER_CLASSES,
            ElementRegistryKey.BACKGROUNDS, ElementRegistryKey.RACES, ElementRegistryKey.SPELLS,
            ElementRegistryKey.EQUIPMENT, ElementRegistryKey.WEAPONS, ElementRegistryKey.ARMOR,
            ElementRegistryKey.CURRENCIES
        );
        for (ElementRegistryKey<?> key : keys) {
            GameElement value = game.getElementRegistry(key).getById(id);
            if (value != null) {
                return value.getClass().getSimpleName();
            }
        }
        return "";
    }

    private static boolean hasMeaningfulElement(GameElement value) {
        return value != null && !Objects.toString(value.getName(), "").trim().isEmpty();
    }

    private static LinkedHashMap<String, String> copyStringMap(Map<String, String> values, boolean lowerValues) {
        LinkedHashMap<String, String> copy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : Objects.requireNonNullElse(values, Map.<String, String>of()).entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            String value = Objects.toString(entry.getValue(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, lowerValues ? value.toLowerCase(Locale.ROOT) : value);
            }
        }
        return copy;
    }

    private static LinkedHashMap<String, Integer> copyNonNegativeMap(Map<String, Integer> values) {
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : Objects.requireNonNullElse(values, Map.<String, Integer>of()).entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
            }
        }
        return copy;
    }

    private static ArrayList<String> copyStrings(List<String> values) {
        ArrayList<String> copy = new ArrayList<>();
        for (String rawValue : Objects.requireNonNullElse(values, List.<String>of())) {
            String value = Objects.toString(rawValue, "").trim();
            if (!value.isEmpty() && !copy.contains(value)) {
                copy.add(value);
            }
        }
        return copy;
    }

    private static <T extends GameElement & Serializable> ArrayList<T> snapshotList(List<T> values) {
        return snapshot(new ArrayList<>(Objects.requireNonNullElse(values, List.of())));
    }

    private static <T extends GameElement & Serializable> LinkedHashMap<T, Integer> snapshotMap(
        Map<T, Integer> values,
        boolean nonNegative
    ) {
        LinkedHashMap<T, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<T, Integer> entry : Objects.requireNonNullElse(values, Map.<T, Integer>of()).entrySet()) {
            if (entry.getKey() != null) {
                int number = Objects.requireNonNullElse(entry.getValue(), 0);
                normalized.put(entry.getKey(), nonNegative ? Math.max(0, number) : number);
            }
        }
        return snapshot(normalized);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T snapshot(T value) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(value);
            }
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                return (T) input.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to snapshot character state.", e);
        }
    }

    public enum DiagnosticCode {
        MISSING_REFERENCE,
        WRONG_TYPE_REFERENCE,
        INVALID_SELECTION
    }

    public static final class Diagnostic implements Serializable {
        private static final long serialVersionUID = 1L;
        private final DiagnosticCode code;
        private final String path;
        private final String suppliedId;
        private final String expectedType;
        private final String actualType;
        private final String message;

        private Diagnostic(
            DiagnosticCode code,
            String path,
            String suppliedId,
            String expectedType,
            String actualType,
            String message
        ) {
            this.code = Objects.requireNonNull(code);
            this.path = Objects.toString(path, "");
            this.suppliedId = Objects.toString(suppliedId, "");
            this.expectedType = Objects.toString(expectedType, "");
            this.actualType = Objects.toString(actualType, "");
            this.message = Objects.toString(message, "");
        }

        public DiagnosticCode getCode() { return code; }
        public String getPath() { return path; }
        public String getSuppliedId() { return suppliedId; }
        public String getExpectedType() { return expectedType; }
        public String getActualType() { return actualType; }
        public String getMessage() { return message; }
    }

    public static final class ConstructionResult {
        private final GMRCharacter character;
        private final List<Diagnostic> diagnostics;

        private ConstructionResult(GMRCharacter character, List<Diagnostic> diagnostics) {
            this.character = Objects.requireNonNullElseGet(character, GMRCharacter::new);
            this.diagnostics = List.copyOf(Objects.requireNonNullElse(diagnostics, List.of()));
        }

        public boolean isSuccessful() { return diagnostics.isEmpty(); }
        public GMRCharacter getCharacter() { return character; }
        public List<Diagnostic> getDiagnostics() { return diagnostics; }
    }

    public static final class ConstructionInput {
        private String characterId = "";
        private String characterName = "";
        private String sourceGameId = "";
        private String sourceGameHash = "";
        private String sourceGameName = "";
        private String sourceGameVersion = "";
        private Map<String, String> ruleModeSelections = new LinkedHashMap<>();
        private Map<String, String> categoryPointSlotAssignments = new LinkedHashMap<>();
        private String raceId = "";
        private String heritageId = "";
        private String backgroundId = "";
        private String characterClassId = "";
        private Map<String, Integer> attributeScores = new LinkedHashMap<>();
        private Map<String, Integer> racialSkillRanks = new LinkedHashMap<>();
        private List<String> racialTraitNames = new ArrayList<>();
        private Map<String, Integer> backgroundSkillRanks = new LinkedHashMap<>();
        private Map<String, Integer> classSkillRanks = new LinkedHashMap<>();
        private Map<String, Integer> selectedSkillRanks = new LinkedHashMap<>();
        private List<String> selectedSpellIds = new ArrayList<>();
        private List<String> selectedWeaponIds = new ArrayList<>();
        private List<String> selectedArmorIds = new ArrayList<>();
        private List<String> selectedEquipmentIds = new ArrayList<>();
        private int startingMoneyAmount = 0;
        private String startingMoneyCurrencyId = "";
        private int resolvedArmorClass = 0;
        private int diceSubstitutionsUsed = 0;
        private Map<String, Integer> rollAdjustmentUses = new LinkedHashMap<>();
        private Map<String, Integer> rollAdjustmentResourceSpent = new LinkedHashMap<>();

        public ConstructionInput setCharacterId(String value) { characterId = text(value); return this; }
        public ConstructionInput setCharacterName(String value) { characterName = text(value); return this; }
        public ConstructionInput setSourceGameId(String value) { sourceGameId = text(value); return this; }
        public ConstructionInput setSourceGameHash(String value) { sourceGameHash = text(value); return this; }
        public ConstructionInput setSourceGameName(String value) { sourceGameName = text(value); return this; }
        public ConstructionInput setSourceGameVersion(String value) { sourceGameVersion = text(value); return this; }
        public ConstructionInput setRuleModeSelections(Map<String, String> value) { ruleModeSelections = copyStringMap(value, false); return this; }
        public ConstructionInput setCategoryPointSlotAssignments(Map<String, String> value) { categoryPointSlotAssignments = copyStringMap(value, true); return this; }
        public ConstructionInput setRaceId(String value) { raceId = text(value); return this; }
        public ConstructionInput setHeritageId(String value) { heritageId = text(value); return this; }
        public ConstructionInput setBackgroundId(String value) { backgroundId = text(value); return this; }
        public ConstructionInput setCharacterClassId(String value) { characterClassId = text(value); return this; }
        public ConstructionInput setAttributeScores(Map<String, Integer> value) { attributeScores = copyIntegerMap(value, false); return this; }
        public ConstructionInput setRacialSkillRanks(Map<String, Integer> value) { racialSkillRanks = copyIntegerMap(value, true); return this; }
        public ConstructionInput setRacialTraitNames(List<String> value) { racialTraitNames = copyStrings(value); return this; }
        public ConstructionInput setBackgroundSkillRanks(Map<String, Integer> value) { backgroundSkillRanks = copyIntegerMap(value, true); return this; }
        public ConstructionInput setClassSkillRanks(Map<String, Integer> value) { classSkillRanks = copyIntegerMap(value, true); return this; }
        public ConstructionInput setSelectedSkillRanks(Map<String, Integer> value) { selectedSkillRanks = copyIntegerMap(value, true); return this; }
        public ConstructionInput setSelectedSpellIds(List<String> value) { selectedSpellIds = copyIds(value); return this; }
        public ConstructionInput setSelectedWeaponIds(List<String> value) { selectedWeaponIds = copyIds(value); return this; }
        public ConstructionInput setSelectedArmorIds(List<String> value) { selectedArmorIds = copyIds(value); return this; }
        public ConstructionInput setSelectedEquipmentIds(List<String> value) { selectedEquipmentIds = copyIds(value); return this; }
        public ConstructionInput setStartingMoneyAmount(int value) { startingMoneyAmount = Math.max(0, value); return this; }
        public ConstructionInput setStartingMoneyCurrencyId(String value) { startingMoneyCurrencyId = text(value); return this; }
        public ConstructionInput setResolvedArmorClass(int value) { resolvedArmorClass = Math.max(0, value); return this; }
        public ConstructionInput setDiceSubstitutionsUsed(int value) { diceSubstitutionsUsed = Math.max(0, value); return this; }
        public ConstructionInput setRollAdjustmentUses(Map<String, Integer> value) { rollAdjustmentUses = copyNonNegativeMap(value); return this; }
        public ConstructionInput setRollAdjustmentResourceSpent(Map<String, Integer> value) { rollAdjustmentResourceSpent = copyNonNegativeMap(value); return this; }

        private static String text(String value) { return Objects.toString(value, "").trim(); }
        private static LinkedHashMap<String, Integer> copyIntegerMap(Map<String, Integer> values, boolean nonNegative) {
            LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : Objects.requireNonNullElse(values, Map.<String, Integer>of()).entrySet()) {
                String key = text(entry.getKey());
                if (!key.isEmpty()) {
                    int number = Objects.requireNonNullElse(entry.getValue(), 0);
                    copy.put(key, nonNegative ? Math.max(0, number) : number);
                }
            }
            return copy;
        }
        private static ArrayList<String> copyIds(List<String> values) {
            ArrayList<String> copy = new ArrayList<>();
            for (String rawValue : Objects.requireNonNullElse(values, List.<String>of())) {
                String value = text(rawValue);
                if (!value.isEmpty() && !copy.contains(value)) {
                    copy.add(value);
                }
            }
            return copy;
        }
    }
}
