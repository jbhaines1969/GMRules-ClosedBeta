/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.AtomicElements.EffectType;
import com.gamemaker.gmrules.AtomicElements.EffectTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.AtomicElements.SkillCategories;
import com.gamemaker.gmrules.AtomicElements.SkillCategory;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.SupportElements.Effect;

/**
 * Skill-specific fields for the element editor popup.
 */
public class SkillEditPanel extends JPanel {

    // *** MEMBERS ***
    private static final int ARMOR_MIN = -100;
    private static final int ARMOR_MAX = 100;

    private final JLabel categoryLabel = new JLabel();
    private final JComboBox<SkillCategory> categoryDropdown = new JComboBox<>();
    private final SkillCategory categoryPlaceholder = new SkillCategory("", "", "", true);
    private final JLabel createCategoryTitle = new JLabel();
    private final JButton createCategoryButton = new JButton();
    private final JLabel abilityLabel = new JLabel();
    private final JComboBox<Attribute> abilityDropdown = new JComboBox<>();
    private final Attribute abilityPlaceholder = new Attribute("");
    private final JCheckBox trainedOnlyCheck = new JCheckBox();
    private final JLabel armorPenaltyLabel = new JLabel();
    private final JSpinner armorPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, ARMOR_MIN, ARMOR_MAX, 1));
    private final JLabel startingMoneyModifierLabel = new JLabel();
    private final JSpinner startingMoneyModifierSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
    private final JLabel limitedClassesLabel = new JLabel();
    private final JComboBox<CharacterClass> limitedClassDropdown = new JComboBox<>();
    private final CharacterClass classPlaceholder = new CharacterClass("");
    private final JButton addLimitedClassButton = new JButton();
    private final DefaultListModel<String> limitedClassModel = new DefaultListModel<>();
    private final JList<String> limitedClassList = new JList<>(limitedClassModel);
    private final JScrollPane limitedClassScroll = new JScrollPane(limitedClassList);
    private final JButton removeLimitedClassButton = new JButton();
    private final JLabel limitedRacesLabel = new JLabel();
    private final JComboBox<Race> limitedRaceDropdown = new JComboBox<>();
    private final Race racePlaceholder = new Race("");
    private final JButton addLimitedRaceButton = new JButton();
    private final DefaultListModel<String> limitedRaceModel = new DefaultListModel<>();
    private final JList<String> limitedRaceList = new JList<>(limitedRaceModel);
    private final JScrollPane limitedRaceScroll = new JScrollPane(limitedRaceList);
    private final JButton removeLimitedRaceButton = new JButton();

    private final JLabel effectsLabel = new JLabel();
    private final JComboBox<Effect> effectDropdown = new JComboBox<>();
    private final Effect effectPlaceholder = new Effect("");
    private final EffectType effectTypePlaceholder = new EffectType("");
    private final JButton addEffectButton = new JButton();
    private final JButton removeEffectButton = new JButton();
    private final DefaultListModel<String> effectModel = new DefaultListModel<>();
    private final JList<String> effectList = new JList<>(effectModel);
    private final JScrollPane effectScroll = new JScrollPane(effectList);

    private final JButton createEffectButton = new JButton();

    private final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel abilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel limitedClassInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel limitedClassListPanel = new JPanel(new BorderLayout());
    private final JPanel limitedRaceInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel limitedRaceListPanel = new JPanel(new BorderLayout());
    private final JPanel effectsInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel effectsListPanel = new JPanel(new BorderLayout());
    private final JPanel createCategoryPanel = new JPanel();
    private final JPanel createEffectPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public SkillEditPanel(MainStage mainStage, Game game, Skill skill) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        populateCategories(skill);
        populateAbilities(skill);
        populateClasses();
        populateRaces();
        populateEffects();
        loadSkill(skill);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public void applyToSkill(Skill skill) {
        Skill safeSkill = Objects.requireNonNullElseGet(skill, () -> new Skill(""));
        SkillCategory selectedCategory = (SkillCategory) categoryDropdown.getSelectedItem();
        if (selectedCategory == null || selectedCategory == categoryPlaceholder) {
            safeSkill.setCategory("");
        } else {
            safeSkill.setCategory(Objects.toString(selectedCategory.getKey(), ""));
        }
        Attribute selected = (Attribute) abilityDropdown.getSelectedItem();
        if (selected == null || selected == abilityPlaceholder) {
            safeSkill.setRelatedAbility("");
        } else {
            safeSkill.setRelatedAbility(Objects.toString(selected.getId(), ""));
        }
        safeSkill.setTrainedOnly(trainedOnlyCheck.isSelected());
        safeSkill.setArmorCheckPenalty((Integer) armorPenaltySpinner.getValue());
        game.setTraitStartingMoneyModifier(
            Objects.toString(safeSkill.getId(), ""),
            (Integer) startingMoneyModifierSpinner.getValue()
        );
        safeSkill.clearArray("limitedToClasses");
        for (int index = 0; index < limitedClassModel.getSize(); index++) {
            safeSkill.addToArray("limitedToClasses", limitedClassModel.getElementAt(index));
        }
        safeSkill.clearArray("limitedToRaces");
        for (int index = 0; index < limitedRaceModel.getSize(); index++) {
            safeSkill.addToArray("limitedToRaces", limitedRaceModel.getElementAt(index));
        }
        safeSkill.clearArray("effectNames");
        for (int index = 0; index < effectModel.getSize(); index++) {
            safeSkill.addToArray("effectNames", effectModel.getElementAt(index));
        }
    }

    private void configureText() {
        categoryLabel.setText(Localization.get("skills.category"));
        createCategoryTitle.setText(Localization.get("skills.category.create"));
        createCategoryButton.setText(Localization.get("skills.category.create"));
        abilityLabel.setText(Localization.get("skills.ability"));
        trainedOnlyCheck.setText(Localization.get("skills.trained_only"));
        armorPenaltyLabel.setText(Localization.get("skills.armor_penalty"));
        startingMoneyModifierLabel.setText(Localization.get("money.trait.modifier"));
        limitedClassesLabel.setText(Localization.get("skills.limits.classes"));
        addLimitedClassButton.setText(Localization.get("skills.limits.add_class"));
        removeLimitedClassButton.setText(Localization.get("common.remove.selected"));
        limitedRacesLabel.setText(Localization.get("skills.limits.races"));
        addLimitedRaceButton.setText(Localization.get("skills.limits.add_race"));
        removeLimitedRaceButton.setText(Localization.get("common.remove.selected"));
        effectsLabel.setText(Localization.get("skills.effects"));
        addEffectButton.setText(Localization.get("skills.effects.add"));
        removeEffectButton.setText(Localization.get("common.remove.selected"));
        createEffectButton.setText(Localization.get("skills.effects.create"));
        categoryPlaceholder.setName(Localization.get("skills.category.none"));
        abilityPlaceholder.setName(Localization.get("skills.ability.none"));
        effectPlaceholder.setName(Localization.get("skills.effects.select"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        abilityPanel.setAlignmentX(LEFT_ALIGNMENT);
        limitedClassInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        limitedClassListPanel.setAlignmentX(LEFT_ALIGNMENT);
        limitedRaceInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        limitedRaceListPanel.setAlignmentX(LEFT_ALIGNMENT);
        effectsInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        effectsListPanel.setAlignmentX(LEFT_ALIGNMENT);
        createCategoryPanel.setAlignmentX(LEFT_ALIGNMENT);
        createEffectPanel.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneyModifierLabel.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneyModifierSpinner.setAlignmentX(LEFT_ALIGNMENT);

        effectList.setVisibleRowCount(6);
        effectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        limitedClassList.setVisibleRowCount(4);
        limitedClassList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        limitedRaceList.setVisibleRowCount(4);
        limitedRaceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        categoryDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            SkillCategory category = (SkillCategory) value;
            label.setText(category == null ? "" : category.getDisplayName());
            return label;
        });
        abilityDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Attribute attribute = (Attribute) value;
            label.setText(attribute == null ? "" : attribute.getDisplayName());
            return label;
        });

        effectDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Effect effect = (Effect) value;
            label.setText(effect == null ? "" : effect.getDisplayName());
            return label;
        });

        effectList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value);
            return label;
        });

        limitedClassDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            CharacterClass characterClass = (CharacterClass) value;
            label.setText(characterClass == null ? "" : characterClass.getDisplayName());
            return label;
        });
        limitedRaceDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Race race = (Race) value;
            label.setText(race == null ? "" : race.getDisplayName());
            return label;
        });

        limitedClassList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(resolveClassName(value));
            return label;
        });

        limitedRaceList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(resolveRaceName(value));
            return label;
        });

        Dimension comboSize = abilityDropdown.getPreferredSize();
        abilityDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
        categoryDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
        effectDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
        limitedClassDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
        limitedRaceDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
    }

    private void populateCategories(Skill skill) {
        categoryDropdown.removeAllItems();
        categoryDropdown.addItem(categoryPlaceholder);
        SkillCategories registry = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
        List<SkillCategory> categories = registry.getAll();
        categories.sort(Comparator.comparing(
            category -> Objects.toString(category.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (SkillCategory category : categories) {
            categoryDropdown.addItem(category);
        }
        selectCategory(skill.getCategory());
    }

    private void selectCategory(String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            categoryDropdown.setSelectedIndex(0);
            return;
        }
        for (int index = 0; index < categoryDropdown.getItemCount(); index++) {
            SkillCategory category = categoryDropdown.getItemAt(index);
            if (category == null || category == categoryPlaceholder) {
                continue;
            }
            String key = Objects.toString(category.getKey(), "");
            String name = Objects.toString(category.getName(), "");
            String displayName = Objects.toString(category.getDisplayName(), "");
            if (key.equalsIgnoreCase(safeValue)
                || name.equalsIgnoreCase(safeValue)
                || displayName.equalsIgnoreCase(safeValue)) {
                categoryDropdown.setSelectedIndex(index);
                return;
            }
        }
        categoryDropdown.setSelectedIndex(0);
    }

    private void populateAbilities(Skill skill) {
        abilityDropdown.removeAllItems();
        abilityDropdown.addItem(abilityPlaceholder);
        List<Attribute> attributes = Objects.requireNonNullElseGet(game.getObjectArray("attributes"), List::of);
        for (Attribute attribute : attributes) {
            abilityDropdown.addItem(attribute);
        }
        selectAbility(skill.getRelatedAbility());
    }

    private void populateClasses() {
        limitedClassDropdown.removeAllItems();
        classPlaceholder.setName(Localization.get("classes.skills.select"));
        limitedClassDropdown.addItem(classPlaceholder);
        List<CharacterClass> classes = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES).getAll();
        classes.sort(Comparator.comparing(
            characterClass -> Objects.toString(characterClass.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (CharacterClass characterClass : classes) {
            limitedClassDropdown.addItem(characterClass);
        }
    }

    private void populateRaces() {
        limitedRaceDropdown.removeAllItems();
        racePlaceholder.setName(Localization.get("races.select"));
        limitedRaceDropdown.addItem(racePlaceholder);
        List<Race> races = game.getElementRegistry(ElementRegistryKey.RACES).getAll();
        races.sort(Comparator.comparing(
            race -> Objects.toString(race.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Race race : races) {
            limitedRaceDropdown.addItem(race);
        }
    }

    private void selectAbility(String value) {
        String safeValue = Objects.toString(value, "");
        if (safeValue.isEmpty()) {
            abilityDropdown.setSelectedIndex(0);
            return;
        }
        for (int index = 0; index < abilityDropdown.getItemCount(); index++) {
            Attribute attribute = abilityDropdown.getItemAt(index);
            if (attribute == null || attribute == abilityPlaceholder) {
                continue;
            }
            String id = Objects.toString(attribute.getId(), "");
            String name = Objects.toString(attribute.getName(), "");
            String displayName = Objects.toString(attribute.getDisplayName(), "");
            String typeKey = Objects.toString(attribute.getType(), "");
            if (id.equals(safeValue)
                || displayName.equalsIgnoreCase(safeValue)
                || name.equalsIgnoreCase(safeValue)
                || typeKey.equalsIgnoreCase(safeValue)) {
                abilityDropdown.setSelectedIndex(index);
                return;
            }
        }
        abilityDropdown.setSelectedIndex(0);
    }

    private void populateEffects() {
        effectDropdown.removeAllItems();
        effectDropdown.addItem(effectPlaceholder);
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        List<Effect> effects = registry.getAll();
        effects.sort(Comparator.comparing(
            effect -> Objects.toString(effect.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Effect effect : effects) {
            effectDropdown.addItem(effect);
        }
    }

    private void loadSkill(Skill skill) {
        Skill safeSkill = Objects.requireNonNullElseGet(skill, () -> new Skill(""));
        selectCategory(safeSkill.getCategory());
        trainedOnlyCheck.setSelected(safeSkill.isTrainedOnly());
        armorPenaltySpinner.setValue(safeSkill.getArmorCheckPenalty());
        startingMoneyModifierSpinner.setValue(game.getTraitStartingMoneyModifier(Objects.toString(safeSkill.getId(), "")));
        limitedClassModel.clear();
        List<String> limitedClasses = safeSkill.getObjectArray("limitedToClasses");
        if (limitedClasses != null) {
            for (String classId : limitedClasses) {
                String safeId = Objects.toString(classId, "").trim();
                if (!safeId.isEmpty() && !limitedClassModel.contains(safeId)) {
                    limitedClassModel.addElement(safeId);
                }
            }
        }
        limitedRaceModel.clear();
        List<String> limitedRaces = safeSkill.getObjectArray("limitedToRaces");
        if (limitedRaces != null) {
            for (String raceId : limitedRaces) {
                String safeId = Objects.toString(raceId, "").trim();
                if (!safeId.isEmpty() && !limitedRaceModel.contains(safeId)) {
                    limitedRaceModel.addElement(safeId);
                }
            }
        }
        effectModel.clear();
        List<String> effectNames = safeSkill.getArray("effectNames");
        if (effectNames != null) {
            for (String effectName : effectNames) {
                if (effectName != null && !effectName.isEmpty()) {
                    effectModel.addElement(effectName);
                }
            }
        }
    }

    private void buildLayout() {
        add(categoryLabel);
        add(Box.createVerticalStrut(6));
        topPanel.add(categoryDropdown);
        add(topPanel);
        add(Box.createVerticalStrut(12));

        createCategoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        createCategoryPanel.add(createCategoryTitle);
        createCategoryPanel.add(createCategoryButton);
        add(createCategoryPanel);
        add(Box.createVerticalStrut(12));

        abilityPanel.add(abilityLabel);
        abilityPanel.add(abilityDropdown);
        abilityPanel.add(trainedOnlyCheck);
        abilityPanel.add(armorPenaltyLabel);
        abilityPanel.add(armorPenaltySpinner);
        add(abilityPanel);
        add(Box.createVerticalStrut(6));
        add(startingMoneyModifierLabel);
        add(Box.createVerticalStrut(6));
        add(startingMoneyModifierSpinner);
        add(Box.createVerticalStrut(12));

        add(limitedClassesLabel);
        add(Box.createVerticalStrut(6));
        limitedClassInputPanel.add(limitedClassDropdown);
        limitedClassInputPanel.add(addLimitedClassButton);
        add(limitedClassInputPanel);
        limitedClassListPanel.add(limitedClassScroll, BorderLayout.CENTER);
        limitedClassListPanel.add(removeLimitedClassButton, BorderLayout.SOUTH);
        add(limitedClassListPanel);
        add(Box.createVerticalStrut(12));

        add(limitedRacesLabel);
        add(Box.createVerticalStrut(6));
        limitedRaceInputPanel.add(limitedRaceDropdown);
        limitedRaceInputPanel.add(addLimitedRaceButton);
        add(limitedRaceInputPanel);
        limitedRaceListPanel.add(limitedRaceScroll, BorderLayout.CENTER);
        limitedRaceListPanel.add(removeLimitedRaceButton, BorderLayout.SOUTH);
        add(limitedRaceListPanel);
        add(Box.createVerticalStrut(12));

        add(effectsLabel);
        add(Box.createVerticalStrut(6));
        effectsInputPanel.add(effectDropdown);
        effectsInputPanel.add(addEffectButton);
        add(effectsInputPanel);
        effectsListPanel.add(effectScroll, BorderLayout.CENTER);
        effectsListPanel.add(removeEffectButton, BorderLayout.SOUTH);
        add(effectsListPanel);
        add(Box.createVerticalStrut(12));

        createEffectPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        createEffectPanel.add(createEffectButton);
        add(createEffectPanel);
    }

    private void registerActions() {
        addEffectButton.addActionListener(event -> addSelectedEffect());
        removeEffectButton.addActionListener(event -> removeSelectedEffect());
        addLimitedClassButton.addActionListener(event -> addLimitedClass());
        removeLimitedClassButton.addActionListener(event -> removeLimitedClass());
        addLimitedRaceButton.addActionListener(event -> addLimitedRace());
        removeLimitedRaceButton.addActionListener(event -> removeLimitedRace());
        createCategoryButton.addActionListener(event -> createCategory());
        createEffectButton.addActionListener(event -> createEffect());
    }

    private void addSelectedEffect() {
        Effect selected = (Effect) effectDropdown.getSelectedItem();
        if (selected == null || selected == effectPlaceholder) {
            return;
        }
        String name = Objects.toString(selected.getName(), "").trim();
        if (name.isEmpty()) {
            return;
        }
        if (effectModel.contains(name)) {
            return;
        }
        effectModel.addElement(name);
    }

    private void removeSelectedEffect() {
        int index = effectList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        effectModel.remove(index);
    }

    private void addLimitedClass() {
        CharacterClass selected = (CharacterClass) limitedClassDropdown.getSelectedItem();
        if (selected == null || selected == classPlaceholder) {
            return;
        }
        String classId = Objects.toString(selected.getId(), "").trim();
        if (classId.isEmpty() || limitedClassModel.contains(classId)) {
            return;
        }
        limitedClassModel.addElement(classId);
    }

    private void removeLimitedClass() {
        int index = limitedClassList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        limitedClassModel.remove(index);
    }

    private void addLimitedRace() {
        Race selected = (Race) limitedRaceDropdown.getSelectedItem();
        if (selected == null || selected == racePlaceholder) {
            return;
        }
        String raceId = Objects.toString(selected.getId(), "").trim();
        if (raceId.isEmpty() || limitedRaceModel.contains(raceId)) {
            return;
        }
        limitedRaceModel.addElement(raceId);
    }

    private void removeLimitedRace() {
        int index = limitedRaceList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        limitedRaceModel.remove(index);
    }

    private void createCategory() {
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("skills.category.create"),
            "",
            "",
            new JPanel()
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        String key = normalizeCategoryKey(name);
        if (key.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        SkillCategories registry = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
        if (registry.contains(key)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        SkillCategory category = new SkillCategory(key, name, result.getDescription(), true);
        registry.register(category);
        Skill selection = new Skill("");
        selection.setCategory(key);
        populateCategories(selection);
        saveGame();
    }

    private void createEffect() {
        DefaultListModel<String> typeModel = new DefaultListModel<>();
        JPanel typePanel = buildEffectTypePanel(typeModel);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("skills.effects.create"),
            "",
            "",
            typePanel
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        if (registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        Effect effect = new Effect(name, result.getDescription());
        effect.setEffectTypeKeys(collectEffectTypeKeys(typeModel));
        registry.add(effect);
        populateEffects();
        if (!effectModel.contains(name)) {
            effectModel.addElement(name);
        }
        saveGame();
    }

    private JPanel buildEffectTypePanel(DefaultListModel<String> model) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(Localization.get("effects.type"));
        label.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<EffectType> dropdown = new JComboBox<>();
        populateEffectTypeDropdown(dropdown);
        applyEffectTypeDropdownRenderer(dropdown);
        dropdown.setAlignmentX(LEFT_ALIGNMENT);

        JButton addButton = new JButton(Localization.get("effects.type.add"));
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        JButton createButton = new JButton(Localization.get("effecttypes.create"));
        createButton.setAlignmentX(LEFT_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        inputPanel.setAlignmentX(LEFT_ALIGNMENT);
        inputPanel.add(dropdown);
        inputPanel.add(addButton);
        inputPanel.add(createButton);

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(4);
        DefaultListCellRenderer selectedRenderer = new DefaultListCellRenderer();
        list.setCellRenderer((valueList, value, index, isSelected, cellHasFocus) -> {
            JLabel labelCell = (JLabel) selectedRenderer.getListCellRendererComponent(
                valueList,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            labelCell.setText(resolveEffectTypeLabel(Objects.toString(value, "")));
            return labelCell;
        });

        JScrollPane scroll = new JScrollPane(list);
        Dimension scrollSize = scroll.getPreferredSize();
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, scrollSize.height + 24));
        scroll.setAlignmentX(LEFT_ALIGNMENT);

        JButton removeButton = new JButton(Localization.get("common.remove.selected"));
        removeButton.setAlignmentX(LEFT_ALIGNMENT);
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        removePanel.setAlignmentX(LEFT_ALIGNMENT);
        removePanel.add(removeButton);

        addButton.addActionListener(event -> addSelectedEffectType(dropdown, model));
        createButton.addActionListener(event -> handleCreateEffectType(dropdown, model));
        removeButton.addActionListener(event -> removeSelectedEffectTypes(list, model));

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(6));
        panel.add(removePanel);
        return panel;
    }

    private void populateEffectTypeDropdown(JComboBox<EffectType> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem(effectTypePlaceholder);
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        List<EffectType> types = registry.getAll();
        types.sort(Comparator.comparing(
            type -> Objects.toString(type.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (EffectType type : types) {
            dropdown.addItem(type);
        }
    }

    private void handleCreateEffectType(JComboBox<EffectType> dropdown, DefaultListModel<String> model) {
        EffectType created = createEffectType();
        if (created == null) {
            return;
        }
        populateEffectTypeDropdown(dropdown);
        dropdown.setSelectedItem(created);
        model.addElement(created.getName());
        dropdown.setSelectedItem(effectTypePlaceholder);
    }

    private EffectType createEffectType() {
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("effecttypes.create.title"),
            "",
            "",
            new JPanel()
        );
        if (!result.isConfirmed()) {
            return null;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return null;
        }
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        if (registry.contains(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return null;
        }
        EffectType type = new EffectType(name, result.getDescription());
        registry.register(type);
        saveGame();
        return type;
    }

    private void applyEffectTypeDropdownRenderer(JComboBox<EffectType> dropdown) {
        DefaultListCellRenderer typeRenderer = new DefaultListCellRenderer();
        dropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) typeRenderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            EffectType type = (EffectType) value;
            String name = type == null ? "" : Objects.toString(type.getName(), "").trim();
            if (name.isEmpty()) {
                label.setText(Localization.get("effects.type.none"));
            } else {
                label.setText(name);
            }
            return label;
        });
    }

    private void addSelectedEffectType(JComboBox<EffectType> dropdown, DefaultListModel<String> model) {
        EffectType selected = (EffectType) dropdown.getSelectedItem();
        String name = selected == null ? "" : Objects.toString(selected.getName(), "").trim();
        if (name.isEmpty()) {
            return;
        }
        model.addElement(name);
        dropdown.setSelectedItem(effectTypePlaceholder);
    }

    private void removeSelectedEffectTypes(JList<String> list, DefaultListModel<String> model) {
        int[] selected = list.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        for (int index = selected.length - 1; index >= 0; index--) {
            model.remove(selected[index]);
        }
    }

    private List<String> collectEffectTypeKeys(DefaultListModel<String> model) {
        List<String> keys = new ArrayList<>();
        for (int index = 0; index < model.getSize(); index++) {
            String key = Objects.toString(model.getElementAt(index), "").trim();
            if (!key.isEmpty()) {
                keys.add(key);
            }
        }
        return keys;
    }

    private String resolveEffectTypeLabel(String key) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return Localization.get("effects.type.none");
        }
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        EffectType type = registry.get(safeKey);
        if (type == null) {
            String lower = safeKey.toLowerCase();
            for (EffectType candidate : registry.getAll()) {
                String name = Objects.toString(candidate.getName(), "").trim().toLowerCase();
                String display = Objects.toString(candidate.getDisplayName(), "").trim().toLowerCase();
                if (name.equals(lower) || display.equals(lower)) {
                    type = candidate;
                    break;
                }
            }
        }
        if (type != null) {
            String label = Objects.toString(type.getDisplayName(), "").trim();
            if (!label.isEmpty()) {
                return label;
            }
            String name = Objects.toString(type.getName(), "").trim();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return safeKey;
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private String normalizeCategoryKey(String value) {
        return Objects.toString(value, "").trim().toLowerCase();
    }

    private String resolveClassName(String classId) {
        String safeId = Objects.toString(classId, "").trim();
        if (safeId.isEmpty()) {
            return Localization.get("common.none");
        }
        CharacterClass characterClass = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES).getById(safeId);
        if (characterClass == null) {
            return safeId;
        }
        return characterClass.getDisplayName();
    }

    private String resolveRaceName(String raceId) {
        String safeId = Objects.toString(raceId, "").trim();
        if (safeId.isEmpty()) {
            return Localization.get("common.none");
        }
        Race race = game.getElementRegistry(ElementRegistryKey.RACES).getById(safeId);
        if (race == null) {
            return safeId;
        }
        return race.getDisplayName();
    }
}
