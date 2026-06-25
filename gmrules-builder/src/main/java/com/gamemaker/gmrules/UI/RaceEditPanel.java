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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.CharacterElements.Race;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameElements.Species;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Race-specific fields for the element editor popup.
 */
public class RaceEditPanel extends JPanel {

    // *** MEMBERS ***
    private static final int LIST_VISIBLE_ROWS = 6;
    private static final int VALUE_MIN = -1000;
    private static final int VALUE_MAX = 1000;

    private final JLabel traitsLabel = new JLabel();
    private final JLabel defaultSkillsLabel = new JLabel();
    private final JComboBox<Skill> traitDropdown = new JComboBox<>();
    private final JButton addTraitButton = new JButton();
    private final DefaultListModel<String> traitModel = new DefaultListModel<>();
    private final JList<String> traitList = new JList<>(traitModel);
    private final JScrollPane traitScroll = new JScrollPane(traitList);
    private final JButton removeTraitButton = new JButton();
    private final JLabel startingMoneyModifierLabel = new JLabel();
    private final JSpinner startingMoneyModifierSpinner =
        new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));

    private final JLabel attributeLimitsLabel = new JLabel();
    private final JLabel attributeSelectLabel = new JLabel();
    private final JComboBox<Attribute> attributeDropdown = new JComboBox<>();
    private final JLabel attributeMinLabel = new JLabel();
    private final JSpinner attributeMinSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel attributeMaxLabel = new JLabel();
    private final JSpinner attributeMaxSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JButton addAttributeLimitButton = new JButton();
    private final DefaultListModel<AttributeLimitEntry> attributeLimitModel = new DefaultListModel<>();
    private final JList<AttributeLimitEntry> attributeLimitList = new JList<>(attributeLimitModel);
    private final JScrollPane attributeLimitScroll = new JScrollPane(attributeLimitList);
    private final JButton removeAttributeLimitButton = new JButton();

    private final JButton createSkillButton = new JButton();

    private final JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel listPanel = new JPanel(new BorderLayout());
    private final JPanel attributeLimitInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel attributeLimitListPanel = new JPanel(new BorderLayout());
    private final JPanel createPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public RaceEditPanel(MainStage mainStage, Game game, Race race) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        populateSkills();
        populateAttributes();
        loadRace(race);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public void applyToRace(Race race) {
        Race safeRace = Objects.requireNonNullElse(race, new Race(""));
        safeRace.clearArray("racialSkills");
        for (int index = 0; index < traitModel.getSize(); index++) {
            String skillId = Objects.toString(traitModel.getElementAt(index), "").trim();
            if (!skillId.isEmpty()) {
                safeRace.addToArray("racialSkills", skillId);
            }
        }
        safeRace.clearAttributeScoreLimits();
        for (int index = 0; index < attributeLimitModel.getSize(); index++) {
            AttributeLimitEntry entry = attributeLimitModel.getElementAt(index);
            safeRace.addAttributeScoreLimit(entry.attributeId, entry.min, entry.max);
        }
        game.setRaceStartingMoneyModifier(
            Objects.toString(safeRace.getId(), ""),
            (Integer) startingMoneyModifierSpinner.getValue()
        );
    }

    private void configureText() {
        traitsLabel.setText(Localization.get("races.traits.title"));
        defaultSkillsLabel.setText(Localization.get("races.traits.default"));
        addTraitButton.setText(Localization.get("races.traits.add"));
        removeTraitButton.setText(Localization.get("common.remove.selected"));
        startingMoneyModifierLabel.setText(Localization.get("money.race.modifier"));
        attributeLimitsLabel.setText(Localization.get("races.attributes.title"));
        attributeSelectLabel.setText(Localization.get("races.attributes.select"));
        attributeMinLabel.setText(Localization.get("attributes.edit.min_value"));
        attributeMaxLabel.setText(Localization.get("attributes.edit.max_value"));
        addAttributeLimitButton.setText(Localization.get("races.attributes.add"));
        removeAttributeLimitButton.setText(Localization.get("common.remove.selected"));
        createSkillButton.setText(Localization.get("skills.create"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        selectorPanel.setAlignmentX(LEFT_ALIGNMENT);
        listPanel.setAlignmentX(LEFT_ALIGNMENT);
        attributeLimitInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        attributeLimitListPanel.setAlignmentX(LEFT_ALIGNMENT);
        createPanel.setAlignmentX(LEFT_ALIGNMENT);

        traitList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        traitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attributeLimitList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        attributeLimitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        traitsLabel.setAlignmentX(LEFT_ALIGNMENT);
        defaultSkillsLabel.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneyModifierLabel.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneyModifierSpinner.setAlignmentX(LEFT_ALIGNMENT);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        traitDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Skill skill = (Skill) value;
            label.setText(skill == null ? "" : skill.getDisplayName());
            return label;
        });

        attributeDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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

        traitList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String skillId = Objects.toString(value, "");
            ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
            Skill skill = registry.getById(skillId);
            label.setText(skill == null ? skillId : skill.getDisplayName());
            return label;
        });

        attributeLimitList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            AttributeLimitEntry entry = (AttributeLimitEntry) value;
            String attributeLabel = resolveAttributeLabel(entry.attributeId);
            String minLabel = Localization.get("attributes.edit.min_value");
            String maxLabel = Localization.get("attributes.edit.max_value");
            label.setText(attributeLabel + " (" + minLabel + ": " + entry.min + ", " + maxLabel + ": " + entry.max + ")");
            return label;
        });

        Dimension comboSize = traitDropdown.getPreferredSize();
        traitDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
        attributeDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboSize.height));
    }

    private void populateSkills() {
        traitDropdown.removeAllItems();
        Skill placeholder = new Skill("");
        placeholder.setName(Localization.get("races.traits.select"));
        traitDropdown.addItem(placeholder);
        ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
        List<Skill> skills = registry.getAll();
        skills.sort(Comparator.comparing(
            skill -> Objects.toString(skill.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Skill skill : skills) {
            traitDropdown.addItem(skill);
        }
    }

    private void populateAttributes() {
        attributeDropdown.removeAllItems();
        Attribute placeholder = new Attribute("");
        placeholder.setName(Localization.get("races.attributes.select"));
        attributeDropdown.addItem(placeholder);
        List<Attribute> attributes = Objects.requireNonNullElse(game.getObjectArray("attributes"), List.of());
        for (Attribute attribute : attributes) {
            attributeDropdown.addItem(attribute);
        }
    }

    private void loadRace(Race race) {
        Race safeRace = Objects.requireNonNullElse(race, new Race(""));
        traitModel.clear();
        List<String> racialSkills = safeRace.getArray("racialSkills");
        if (racialSkills != null) {
            for (String skillId : racialSkills) {
                String safeId = Objects.toString(skillId, "").trim();
                if (!safeId.isEmpty() && !traitModel.contains(safeId)) {
                    traitModel.addElement(safeId);
                }
            }
        }
        attributeLimitModel.clear();
        for (Species.AttributeScoreLimit limit : safeRace.getAttributeScoreLimits()) {
            String attributeId = Objects.toString(limit.getAttributeId(), "").trim();
            if (!attributeId.isEmpty()) {
                addOrUpdateAttributeLimit(attributeId, limit.getMin(), limit.getMax());
            }
        }
        startingMoneyModifierSpinner.setValue(game.getRaceStartingMoneyModifier(Objects.toString(safeRace.getId(), "")));
    }

    private void buildLayout() {
        add(traitsLabel);
        add(Box.createVerticalStrut(6));
        add(defaultSkillsLabel);
        add(Box.createVerticalStrut(6));
        selectorPanel.add(traitDropdown);
        selectorPanel.add(addTraitButton);
        add(selectorPanel);

        listPanel.add(traitScroll, BorderLayout.CENTER);
        listPanel.add(removeTraitButton, BorderLayout.SOUTH);
        add(listPanel);
        add(Box.createVerticalStrut(12));

        add(startingMoneyModifierLabel);
        add(Box.createVerticalStrut(6));
        add(startingMoneyModifierSpinner);
        add(Box.createVerticalStrut(12));

        add(attributeLimitsLabel);
        add(Box.createVerticalStrut(6));
        attributeLimitInputPanel.add(attributeSelectLabel);
        attributeLimitInputPanel.add(attributeDropdown);
        attributeLimitInputPanel.add(attributeMinLabel);
        attributeLimitInputPanel.add(attributeMinSpinner);
        attributeLimitInputPanel.add(attributeMaxLabel);
        attributeLimitInputPanel.add(attributeMaxSpinner);
        attributeLimitInputPanel.add(addAttributeLimitButton);
        add(attributeLimitInputPanel);
        add(Box.createVerticalStrut(6));

        attributeLimitListPanel.add(attributeLimitScroll, BorderLayout.CENTER);
        attributeLimitListPanel.add(removeAttributeLimitButton, BorderLayout.SOUTH);
        add(attributeLimitListPanel);
        add(Box.createVerticalStrut(12));

        createPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        createPanel.add(createSkillButton);
        add(createPanel);
    }

    private void registerActions() {
        addTraitButton.addActionListener(event -> addSelectedTrait());
        removeTraitButton.addActionListener(event -> removeSelectedTrait());
        addAttributeLimitButton.addActionListener(event -> addSelectedAttributeLimit());
        removeAttributeLimitButton.addActionListener(event -> removeSelectedAttributeLimit());
        createSkillButton.addActionListener(event -> createSkill());
    }

    private void addSelectedTrait() {
        if (traitDropdown.getSelectedIndex() <= 0) {
            return;
        }
        Skill selected = (Skill) traitDropdown.getSelectedItem();
        if (selected == null) {
            return;
        }
        String skillId = Objects.toString(selected.getId(), "").trim();
        if (skillId.isEmpty()) {
            return;
        }
        if (!traitModel.contains(skillId)) {
            traitModel.addElement(skillId);
        }
    }

    private void removeSelectedTrait() {
        int index = traitList.getSelectedIndex();
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
        traitModel.remove(index);
    }

    private void addSelectedAttributeLimit() {
        if (attributeDropdown.getSelectedIndex() <= 0) {
            return;
        }
        Attribute selected = (Attribute) attributeDropdown.getSelectedItem();
        if (selected == null) {
            return;
        }
        String attributeId = Objects.toString(selected.getId(), "").trim();
        if (attributeId.isEmpty()) {
            return;
        }
        int min = (Integer) attributeMinSpinner.getValue();
        int max = (Integer) attributeMaxSpinner.getValue();
        if (min > max) {
            PopupAlert.show(mainStage, Localization.get("attributes.edit.range.invalid"));
            return;
        }
        addOrUpdateAttributeLimit(attributeId, min, max);
    }

    private void removeSelectedAttributeLimit() {
        int index = attributeLimitList.getSelectedIndex();
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
        attributeLimitModel.remove(index);
    }

    private void addOrUpdateAttributeLimit(String attributeId, int min, int max) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            return;
        }
        int existingIndex = findAttributeLimitIndex(safeId);
        AttributeLimitEntry entry = new AttributeLimitEntry(safeId, min, max);
        if (existingIndex >= 0) {
            attributeLimitModel.setElementAt(entry, existingIndex);
            return;
        }
        attributeLimitModel.addElement(entry);
    }

    private int findAttributeLimitIndex(String attributeId) {
        for (int index = 0; index < attributeLimitModel.getSize(); index++) {
            AttributeLimitEntry entry = attributeLimitModel.getElementAt(index);
            if (attributeId.equals(entry.attributeId)) {
                return index;
            }
        }
        return -1;
    }

    private String resolveAttributeLabel(String attributeId) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            return "";
        }
        List<Attribute> attributes = Objects.requireNonNullElse(game.getObjectArray("attributes"), List.of());
        for (Attribute attribute : attributes) {
            if (safeId.equals(attribute.getId())) {
                return attribute.getDisplayName();
            }
        }
        return safeId;
    }

    private void createSkill() {
        Skill skill = new Skill("");
        SkillEditPanel editPanel = new SkillEditPanel(mainStage, game, skill);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("skills.edit.title"),
            skill.getName(),
            skill.getDescription(),
            editPanel
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
        if (registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        skill.setName(name);
        skill.setDescription(result.getDescription());
        editPanel.applyToSkill(skill);
        registry.add(skill);
        populateSkills();
        String skillId = Objects.toString(skill.getId(), "").trim();
        if (!skillId.isEmpty() && !traitModel.contains(skillId)) {
            traitModel.addElement(skillId);
        }
        saveGame();
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private static final class AttributeLimitEntry {

        // *** MEMBERS ***
        private final String attributeId;
        private final int min;
        private final int max;

        // *** CONSTRUCTORS ***
        private AttributeLimitEntry(String attributeId, int min, int max) {
            this.attributeId = Objects.toString(attributeId, "");
            this.min = min;
            this.max = max;
        }
    }
}
