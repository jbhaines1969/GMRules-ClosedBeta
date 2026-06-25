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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.CharacterElements.Skill;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Character class-specific fields for the element editor popup.
 */
public class ClassEditPanel extends JPanel {

    // *** MEMBERS ***
    private static final int LIST_VISIBLE_ROWS = 6;
    private static final int SKILL_POINTS_MIN = 0;
    private static final int SKILL_POINTS_MAX = 100;
    private static final int SKILL_POINTS_LEVEL_MIN = 1;
    private static final int SKILL_POINTS_LEVEL_DEFAULT_MAX = 20;
    private static final int REQUIRED_SCORE_MIN = 1;
    private static final int REQUIRED_SCORE_MAX = 30;
    private static final int MONEY_MIN = 0;
    private static final int MONEY_MAX = 100000000;

    private final JLabel primaryAttributeLabel = new JLabel();
    private final JComboBox<Attribute> primaryAttributeDropdown = new JComboBox<>();
    private final Attribute attributePlaceholder = new Attribute("");
    private final JLabel hitDieLabel = new JLabel();
    private final JComboBox<Integer> hitDieDropdown = new JComboBox<>();
    private final Integer hitDiePlaceholder = 0;
    private final JLabel hitDieModifierLabel = new JLabel();
    private final JSpinner hitDieModifierSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
    private final JLabel skillPointsLabel = new JLabel();
    private final JSpinner skillPointsSpinner =
        new JSpinner(new SpinnerNumberModel(0, SKILL_POINTS_MIN, SKILL_POINTS_MAX, 1));
    private final JCheckBox skillPointsSameCheck = new JCheckBox();
    private final JLabel skillPointsLevelLabel = new JLabel();
    private final JComboBox<Integer> skillPointsLevelDropdown = new JComboBox<>();
    private final Integer skillPointsLevelPlaceholder = 0;
    private final JLabel skillPointsValueLabel = new JLabel();
    private final JSpinner skillPointsValueSpinner =
        new JSpinner(new SpinnerNumberModel(0, SKILL_POINTS_MIN, SKILL_POINTS_MAX, 1));
    private final JButton addSkillPointsLevelButton = new JButton();
    private final DefaultListModel<SkillPointsEntry> skillPointsModel = new DefaultListModel<>();
    private final JList<SkillPointsEntry> skillPointsList = new JList<>(skillPointsModel);
    private final JScrollPane skillPointsScroll = new JScrollPane(skillPointsList);
    private final JButton removeSkillPointsLevelButton = new JButton();
    private final JLabel startingMoneyLabel = new JLabel();
    private final JSpinner startingMoneySpinner =
        new JSpinner(new SpinnerNumberModel(0, MONEY_MIN, MONEY_MAX, 1));

    private final JLabel classSkillsLabel = new JLabel();
    private final JComboBox<Skill> classSkillDropdown = new JComboBox<>();
    private final Skill skillPlaceholder = new Skill("");
    private final JButton addSkillButton = new JButton();
    private final JButton createSkillButton = new JButton();
    private final JButton removeSkillButton = new JButton();
    private final DefaultListModel<String> classSkillModel = new DefaultListModel<>();
    private final JList<String> classSkillList = new JList<>(classSkillModel);
    private final JScrollPane classSkillScroll = new JScrollPane(classSkillList);

    private final JLabel requiredAttributesLabel = new JLabel();
    private final JComboBox<Attribute> requiredAttributeDropdown = new JComboBox<>();
    private final JButton addRequiredButton = new JButton();
    private final JButton removeRequiredButton = new JButton();
    private final JLabel requiredScoreLabel = new JLabel();
    private final JSpinner requiredScoreSpinner =
        new JSpinner(new SpinnerNumberModel(REQUIRED_SCORE_MIN, REQUIRED_SCORE_MIN, REQUIRED_SCORE_MAX, 1));
    private final DefaultListModel<RequiredAttributeEntry> requiredModel = new DefaultListModel<>();
    private final JList<RequiredAttributeEntry> requiredList = new JList<>(requiredModel);
    private final JScrollPane requiredScroll = new JScrollPane(requiredList);

    private final JPanel hitDiePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel skillPointsSamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel skillPointsLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel skillPointsListPanel = new JPanel(new BorderLayout());
    private final JPanel classSkillInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel classSkillListPanel = new JPanel(new BorderLayout());
    private final JPanel requiredInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel requiredListPanel = new JPanel(new BorderLayout());
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public ClassEditPanel(MainStage mainStage, Game game, CharacterClass characterClass) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        populateAttributes();
        populateSkills();
        populateHitDieOptions();
        loadClass(characterClass);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public void applyToClass(CharacterClass characterClass) {
        CharacterClass safeClass = Objects.requireNonNullElse(characterClass, new CharacterClass(""));
        Attribute selectedAttribute = (Attribute) primaryAttributeDropdown.getSelectedItem();
        if (selectedAttribute == null || selectedAttribute == attributePlaceholder) {
            safeClass.setPrimaryAttribute("");
        } else {
            safeClass.setPrimaryAttribute(Objects.toString(selectedAttribute.getId(), ""));
        }
        int hitDieSides = resolveHitDieSides();
        int hitDieModifier = (Integer) hitDieModifierSpinner.getValue();
        safeClass.setHitDie(buildHitDie(hitDieSides, hitDieModifier));
        safeClass.setSkillPointsPerLevel((Integer) skillPointsSpinner.getValue());
        safeClass.setSkillPointsSameAllLevels(skillPointsSameCheck.isSelected());
        safeClass.setSkillPointsByLevel(resolveSkillPointsByLevel());
        game.setClassStartingMoney(Objects.toString(safeClass.getId(), ""), (Integer) startingMoneySpinner.getValue());

        safeClass.clearArray("classSkills");
        for (int index = 0; index < classSkillModel.getSize(); index++) {
            String skillId = Objects.toString(classSkillModel.getElementAt(index), "").trim();
            if (!skillId.isEmpty()) {
                safeClass.addToArray("classSkills", skillId);
            }
        }

        Map<String, Integer> requiredScores = new LinkedHashMap<>();
        for (int index = 0; index < requiredModel.getSize(); index++) {
            RequiredAttributeEntry entry = requiredModel.getElementAt(index);
            if (entry != null && !entry.getAttributeId().isEmpty() && entry.getScore() > 0) {
                requiredScores.put(entry.getAttributeId(), entry.getScore());
            }
        }
        safeClass.setRequiredAttributeScores(requiredScores);
    }

    private void configureText() {
        primaryAttributeLabel.setText(Localization.get("classes.primary_attribute"));
        hitDieLabel.setText(Localization.get("classes.hit_die"));
        hitDieModifierLabel.setText(Localization.get("common.modifier"));
        skillPointsLabel.setText(Localization.get("classes.skill_points"));
        skillPointsSameCheck.setText(Localization.get("classes.skill_points.same_all"));
        skillPointsLevelLabel.setText(Localization.get("classes.skill_points.level"));
        skillPointsValueLabel.setText(Localization.get("classes.skill_points.value"));
        addSkillPointsLevelButton.setText(Localization.get("classes.skill_points.add"));
        classSkillsLabel.setText(Localization.get("classes.skills"));
        addSkillButton.setText(Localization.get("classes.skills.add"));
        createSkillButton.setText(Localization.get("skills.create"));
        removeSkillButton.setText(Localization.get("common.remove.selected"));
        requiredAttributesLabel.setText(Localization.get("classes.required_attributes"));
        requiredScoreLabel.setText(Localization.get("classes.required_attributes.score"));
        addRequiredButton.setText(Localization.get("classes.required_attributes.add"));
        removeRequiredButton.setText(Localization.get("common.remove.selected"));
        removeSkillPointsLevelButton.setText(Localization.get("common.remove.selected"));
        startingMoneyLabel.setText(Localization.get("money.class.starting"));
        attributePlaceholder.setName(Localization.get("classes.primary_attribute.none"));
        skillPlaceholder.setName(Localization.get("classes.skills.select"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        hitDiePanel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsSamePanel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsLevelPanel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsListPanel.setAlignmentX(LEFT_ALIGNMENT);
        classSkillInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        classSkillListPanel.setAlignmentX(LEFT_ALIGNMENT);
        requiredInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        requiredListPanel.setAlignmentX(LEFT_ALIGNMENT);

        primaryAttributeDropdown.setAlignmentX(LEFT_ALIGNMENT);
        hitDieLabel.setAlignmentX(LEFT_ALIGNMENT);
        hitDieDropdown.setAlignmentX(LEFT_ALIGNMENT);
        hitDieModifierLabel.setAlignmentX(LEFT_ALIGNMENT);
        hitDieModifierSpinner.setAlignmentX(LEFT_ALIGNMENT);
        Dimension hitDieSize = hitDieDropdown.getPreferredSize();
        hitDieDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, hitDieSize.height));
        hitDieModifierSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, hitDieSize.height));
        skillPointsLabel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsSpinner.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsSameCheck.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsLevelLabel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsLevelDropdown.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsValueLabel.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsValueSpinner.setAlignmentX(LEFT_ALIGNMENT);
        addSkillPointsLevelButton.setAlignmentX(LEFT_ALIGNMENT);
        skillPointsList.setAlignmentX(LEFT_ALIGNMENT);
        removeSkillPointsLevelButton.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneyLabel.setAlignmentX(LEFT_ALIGNMENT);
        startingMoneySpinner.setAlignmentX(LEFT_ALIGNMENT);

        Dimension skillPointsSize = skillPointsSpinner.getPreferredSize();
        skillPointsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, skillPointsSize.height));
        skillPointsLevelDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, skillPointsSize.height));
        skillPointsValueSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, skillPointsSize.height));
        startingMoneySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, skillPointsSize.height));

        classSkillList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        classSkillList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        skillPointsList.setVisibleRowCount(5);
        skillPointsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requiredList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        requiredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        primaryAttributeDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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
        requiredAttributeDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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

        classSkillDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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

        hitDieDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            int sides = value == null ? 0 : (Integer) value;
            if (sides <= 0) {
                label.setText(Localization.get("common.die.select"));
            } else {
                label.setText("d" + sides);
            }
            return label;
        });

        skillPointsLevelDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            int level = value == null ? 0 : (Integer) value;
            if (level <= 0) {
                label.setText(Localization.get("classes.skill_points.level.select"));
            } else {
                label.setText(Localization.format("classes.skill_points.level.label", level));
            }
            return label;
        });

        skillPointsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            SkillPointsEntry entry = (SkillPointsEntry) value;
            if (entry == null) {
                label.setText("");
                return label;
            }
            String levelLabel = Localization.format("classes.skill_points.level.label", entry.level());
            label.setText(levelLabel + " : " + entry.points());
            return label;
        });

        classSkillList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String id = Objects.toString(value, "");
            Skill skill = game.getElement("skills", id);
            label.setText(skill == null ? id : skill.getDisplayName());
            return label;
        });

        requiredList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            RequiredAttributeEntry entry = (RequiredAttributeEntry) value;
            String id = entry == null ? "" : entry.getAttributeId();
            Attribute attribute = game.getElement("attributes", id);
            String name = attribute == null ? id : attribute.getDisplayName();
            int score = entry == null ? 0 : entry.getScore();
            label.setText(name + " : " + score);
            return label;
        });
    }

    private void populateAttributes() {
        primaryAttributeDropdown.removeAllItems();
        requiredAttributeDropdown.removeAllItems();
        primaryAttributeDropdown.addItem(attributePlaceholder);
        requiredAttributeDropdown.addItem(attributePlaceholder);
        List<Attribute> attributes = Objects.requireNonNullElseGet(game.getObjectArray("attributes"), List::of);
        for (Attribute attribute : attributes) {
            primaryAttributeDropdown.addItem(attribute);
            requiredAttributeDropdown.addItem(attribute);
        }
    }

    private void populateSkills() {
        classSkillDropdown.removeAllItems();
        classSkillDropdown.addItem(skillPlaceholder);
        List<Skill> skills = Objects.requireNonNullElseGet(game.getObjectArray("skills"), List::of);
        List<Skill> sorted = new ArrayList<>(skills);
        sorted.sort(Comparator.comparing(
            skill -> Objects.toString(skill.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Skill skill : sorted) {
            classSkillDropdown.addItem(skill);
        }
    }

    private void populateHitDieOptions() {
        hitDieDropdown.removeAllItems();
        hitDieDropdown.addItem(hitDiePlaceholder);
        List<Integer> diceUsed = game.getDiceUsed();
        List<Integer> sorted = new ArrayList<>(diceUsed);
        sorted.sort(Integer::compareTo);
        for (Integer sides : sorted) {
            if (sides != null && sides > 0) {
                hitDieDropdown.addItem(sides);
            }
        }
    }

    private void populateSkillPointLevels(int maxLevel) {
        skillPointsLevelDropdown.removeAllItems();
        skillPointsLevelDropdown.addItem(skillPointsLevelPlaceholder);
        int limit = maxLevel > 0 ? maxLevel : SKILL_POINTS_LEVEL_DEFAULT_MAX;
        for (int level = SKILL_POINTS_LEVEL_MIN; level <= limit; level++) {
            skillPointsLevelDropdown.addItem(level);
        }
    }

    private void loadClass(CharacterClass characterClass) {
        CharacterClass safeClass = Objects.requireNonNullElse(characterClass, new CharacterClass(""));
        selectPrimaryAttribute(safeClass.getPrimaryAttribute());
        HitDieSelection hitDie = parseHitDie(Objects.toString(safeClass.getHitDie(), ""));
        selectHitDieSides(hitDie.sides());
        hitDieModifierSpinner.setValue(hitDie.modifier());
        skillPointsSpinner.setValue(safeClass.getSkillPointsPerLevel());
        skillPointsSameCheck.setSelected(safeClass.isSkillPointsSameAllLevels());
        populateSkillPointLevels(safeClass.getMaxLevel());
        loadSkillPointsByLevel(safeClass.getSkillPointsByLevel());
        updateSkillPointsModeUI();
        startingMoneySpinner.setValue(game.getClassStartingMoney(Objects.toString(safeClass.getId(), "")));

        classSkillModel.clear();
        List<String> classSkills = safeClass.getObjectArray("classSkills");
        if (classSkills != null) {
            for (String skillId : classSkills) {
                String safeId = Objects.toString(skillId, "").trim();
                if (!safeId.isEmpty() && !classSkillModel.contains(safeId)) {
                    classSkillModel.addElement(safeId);
                }
            }
        }

        requiredModel.clear();
        Map<String, Integer> requiredScores = safeClass.getRequiredAttributeScores();
        for (Map.Entry<String, Integer> entry : requiredScores.entrySet()) {
            String id = Objects.toString(entry.getKey(), "").trim();
            int score = Objects.requireNonNullElse(entry.getValue(), 0);
            if (!id.isEmpty() && score > 0) {
                requiredModel.addElement(new RequiredAttributeEntry(id, score));
            }
        }
    }

    private void selectPrimaryAttribute(String value) {
        String safeValue = Objects.toString(value, "");
        if (safeValue.isEmpty()) {
            primaryAttributeDropdown.setSelectedIndex(0);
            return;
        }
        for (int index = 0; index < primaryAttributeDropdown.getItemCount(); index++) {
            Attribute attribute = primaryAttributeDropdown.getItemAt(index);
            if (attribute == null || attribute == attributePlaceholder) {
                continue;
            }
            String id = Objects.toString(attribute.getId(), "");
            String name = Objects.toString(attribute.getName(), "");
            String displayName = Objects.toString(attribute.getDisplayName(), "");
            if (id.equals(safeValue)
                || name.equalsIgnoreCase(safeValue)
                || displayName.equalsIgnoreCase(safeValue)) {
                primaryAttributeDropdown.setSelectedIndex(index);
                return;
            }
        }
        primaryAttributeDropdown.setSelectedIndex(0);
    }

    private void selectHitDieSides(int sides) {
        if (sides <= 0) {
            hitDieDropdown.setSelectedIndex(0);
            return;
        }
        for (int index = 0; index < hitDieDropdown.getItemCount(); index++) {
            Integer value = hitDieDropdown.getItemAt(index);
            if (value != null && value == sides) {
                hitDieDropdown.setSelectedIndex(index);
                return;
            }
        }
        hitDieDropdown.addItem(sides);
        hitDieDropdown.setSelectedItem(sides);
    }

    private int resolveHitDieSides() {
        Integer value = (Integer) hitDieDropdown.getSelectedItem();
        if (value == null || value <= 0) {
            return 0;
        }
        return value;
    }

    private String buildHitDie(int sides, int modifier) {
        if (sides <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder("d").append(sides);
        if (modifier > 0) {
            builder.append("+").append(modifier);
        } else if (modifier < 0) {
            builder.append(modifier);
        }
        return builder.toString();
    }

    private HitDieSelection parseHitDie(String value) {
        String safeValue = Objects.toString(value, "").trim();
        if (safeValue.isEmpty()) {
            return new HitDieSelection(0, 0);
        }
        String compact = safeValue.replace(" ", "");
        int modifier = 0;
        int modIndex = -1;
        for (int index = 1; index < compact.length(); index++) {
            char ch = compact.charAt(index);
            if (ch == '+' || ch == '-') {
                modIndex = index;
                break;
            }
        }
        String dicePart = compact;
        if (modIndex > 0) {
            dicePart = compact.substring(0, modIndex);
            String modPart = compact.substring(modIndex).replace(" ", "");
            try {
                modifier = Integer.parseInt(modPart);
            } catch (NumberFormatException ignored) {
                modifier = 0;
            }
        }
        String lower = dicePart.toLowerCase();
        int dIndex = lower.indexOf('d');
        String sidesPart = dIndex >= 0 ? lower.substring(dIndex + 1) : lower;
        sidesPart = sidesPart.replaceAll("[^0-9]", "");
        if (sidesPart.isEmpty()) {
            return new HitDieSelection(0, modifier);
        }
        try {
            int sides = Integer.parseInt(sidesPart);
            return new HitDieSelection(sides, modifier);
        } catch (NumberFormatException ignored) {
            return new HitDieSelection(0, modifier);
        }
    }

    private void buildLayout() {
        add(primaryAttributeLabel);
        add(Box.createVerticalStrut(6));
        add(primaryAttributeDropdown);
        add(Box.createVerticalStrut(12));

        add(hitDieLabel);
        add(Box.createVerticalStrut(6));
        hitDiePanel.add(hitDieDropdown);
        hitDiePanel.add(hitDieModifierLabel);
        hitDiePanel.add(hitDieModifierSpinner);
        add(hitDiePanel);
        add(Box.createVerticalStrut(12));

        add(skillPointsLabel);
        add(Box.createVerticalStrut(6));
        skillPointsSamePanel.add(skillPointsSameCheck);
        skillPointsSamePanel.add(skillPointsSpinner);
        add(skillPointsSamePanel);
        add(Box.createVerticalStrut(6));
        skillPointsLevelPanel.add(skillPointsLevelLabel);
        skillPointsLevelPanel.add(skillPointsLevelDropdown);
        skillPointsLevelPanel.add(skillPointsValueLabel);
        skillPointsLevelPanel.add(skillPointsValueSpinner);
        skillPointsLevelPanel.add(addSkillPointsLevelButton);
        add(skillPointsLevelPanel);
        skillPointsListPanel.add(skillPointsScroll, BorderLayout.CENTER);
        skillPointsListPanel.add(removeSkillPointsLevelButton, BorderLayout.SOUTH);
        add(skillPointsListPanel);
        add(Box.createVerticalStrut(12));
        add(startingMoneyLabel);
        add(Box.createVerticalStrut(6));
        add(startingMoneySpinner);
        add(Box.createVerticalStrut(12));

        add(classSkillsLabel);
        add(Box.createVerticalStrut(6));
        classSkillInputPanel.add(classSkillDropdown);
        classSkillInputPanel.add(addSkillButton);
        classSkillInputPanel.add(createSkillButton);
        add(classSkillInputPanel);
        classSkillListPanel.add(classSkillScroll, BorderLayout.CENTER);
        classSkillListPanel.add(removeSkillButton, BorderLayout.SOUTH);
        add(classSkillListPanel);
        add(Box.createVerticalStrut(12));

        add(requiredAttributesLabel);
        add(Box.createVerticalStrut(6));
        requiredInputPanel.add(requiredAttributeDropdown);
        requiredInputPanel.add(requiredScoreLabel);
        requiredInputPanel.add(requiredScoreSpinner);
        requiredInputPanel.add(addRequiredButton);
        add(requiredInputPanel);
        requiredListPanel.add(requiredScroll, BorderLayout.CENTER);
        requiredListPanel.add(removeRequiredButton, BorderLayout.SOUTH);
        add(requiredListPanel);
    }

    private void registerActions() {
        addSkillButton.addActionListener(event -> addSelectedSkill());
        createSkillButton.addActionListener(event -> createSkill());
        removeSkillButton.addActionListener(event -> removeSelectedSkill());
        addRequiredButton.addActionListener(event -> addRequiredAttribute());
        removeRequiredButton.addActionListener(event -> removeSelectedRequiredAttribute());
        addSkillPointsLevelButton.addActionListener(event -> addSkillPointsEntry());
        removeSkillPointsLevelButton.addActionListener(event -> removeSelectedSkillPointsEntry());
        skillPointsSameCheck.addActionListener(event -> updateSkillPointsModeUI());
    }

    private void addSelectedSkill() {
        Skill selected = (Skill) classSkillDropdown.getSelectedItem();
        if (selected == null || selected == skillPlaceholder) {
            return;
        }
        String id = Objects.toString(selected.getId(), "").trim();
        if (!id.isEmpty() && !classSkillModel.contains(id)) {
            classSkillModel.addElement(id);
        }
    }

    private void removeSelectedSkill() {
        int index = classSkillList.getSelectedIndex();
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
        classSkillModel.remove(index);
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
        if (!skillId.isEmpty() && !classSkillModel.contains(skillId)) {
            classSkillModel.addElement(skillId);
        }
        saveGame();
    }

    private void addRequiredAttribute() {
        Attribute selected = (Attribute) requiredAttributeDropdown.getSelectedItem();
        if (selected == null || selected == attributePlaceholder) {
            return;
        }
        String id = Objects.toString(selected.getId(), "").trim();
        int score = (Integer) requiredScoreSpinner.getValue();
        if (id.isEmpty() || score <= 0) {
            return;
        }
        RequiredAttributeEntry existing = findRequiredEntry(id);
        if (existing != null) {
            requiredModel.removeElement(existing);
        }
        requiredModel.addElement(new RequiredAttributeEntry(id, score));
    }

    private RequiredAttributeEntry findRequiredEntry(String attributeId) {
        String safeId = Objects.toString(attributeId, "");
        for (int index = 0; index < requiredModel.getSize(); index++) {
            RequiredAttributeEntry entry = requiredModel.getElementAt(index);
            if (entry != null && safeId.equals(entry.getAttributeId())) {
                return entry;
            }
        }
        return null;
    }

    private void removeSelectedRequiredAttribute() {
        int index = requiredList.getSelectedIndex();
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
        requiredModel.remove(index);
    }

    private void addSkillPointsEntry() {
        Integer level = (Integer) skillPointsLevelDropdown.getSelectedItem();
        if (level == null || level <= 0) {
            return;
        }
        int points = (Integer) skillPointsValueSpinner.getValue();
        upsertSkillPointsEntry(level, points);
    }

    private void upsertSkillPointsEntry(int level, int points) {
        SkillPointsEntry existing = findSkillPointsEntry(level);
        if (existing != null) {
            skillPointsModel.removeElement(existing);
        }
        skillPointsModel.addElement(new SkillPointsEntry(level, points));
        sortSkillPointsEntries();
    }

    private SkillPointsEntry findSkillPointsEntry(int level) {
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            SkillPointsEntry entry = skillPointsModel.getElementAt(index);
            if (entry != null && entry.level() == level) {
                return entry;
            }
        }
        return null;
    }

    private void sortSkillPointsEntries() {
        List<SkillPointsEntry> entries = new ArrayList<>();
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            SkillPointsEntry entry = skillPointsModel.getElementAt(index);
            if (entry != null) {
                entries.add(entry);
            }
        }
        entries.sort(Comparator.comparingInt(SkillPointsEntry::level));
        skillPointsModel.clear();
        for (SkillPointsEntry entry : entries) {
            skillPointsModel.addElement(entry);
        }
    }

    private void removeSelectedSkillPointsEntry() {
        int index = skillPointsList.getSelectedIndex();
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
        skillPointsModel.remove(index);
    }

    private void loadSkillPointsByLevel(Map<Integer, Integer> pointsByLevel) {
        skillPointsModel.clear();
        Map<Integer, Integer> safeMap = Objects.requireNonNullElseGet(pointsByLevel, LinkedHashMap::new);
        List<SkillPointsEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : safeMap.entrySet()) {
            int level = Objects.requireNonNullElse(entry.getKey(), 0);
            int points = Objects.requireNonNullElse(entry.getValue(), 0);
            if (level > 0) {
                entries.add(new SkillPointsEntry(level, Math.max(0, points)));
            }
        }
        entries.sort(Comparator.comparingInt(SkillPointsEntry::level));
        for (SkillPointsEntry entry : entries) {
            skillPointsModel.addElement(entry);
        }
    }

    private Map<Integer, Integer> resolveSkillPointsByLevel() {
        Map<Integer, Integer> pointsByLevel = new LinkedHashMap<>();
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            SkillPointsEntry entry = skillPointsModel.getElementAt(index);
            if (entry != null && entry.level() > 0) {
                pointsByLevel.put(entry.level(), Math.max(0, entry.points()));
            }
        }
        return pointsByLevel;
    }

    private void updateSkillPointsModeUI() {
        boolean sameAll = skillPointsSameCheck.isSelected();
        skillPointsSpinner.setEnabled(sameAll);
        skillPointsLevelLabel.setEnabled(!sameAll);
        skillPointsLevelDropdown.setEnabled(!sameAll);
        skillPointsValueLabel.setEnabled(!sameAll);
        skillPointsValueSpinner.setEnabled(!sameAll);
        addSkillPointsLevelButton.setEnabled(!sameAll);
        skillPointsList.setEnabled(!sameAll);
        removeSkillPointsLevelButton.setEnabled(!sameAll);
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private static final class HitDieSelection {

        private final int sides;
        private final int modifier;

        private HitDieSelection(int sides, int modifier) {
            this.sides = sides;
            this.modifier = modifier;
        }

        private int sides() {
            return sides;
        }

        private int modifier() {
            return modifier;
        }
    }

    private static final class RequiredAttributeEntry {
        private final String attributeId;
        private final int score;

        private RequiredAttributeEntry(String attributeId, int score) {
            this.attributeId = Objects.toString(attributeId, "");
            this.score = score;
        }

        private String getAttributeId() {
            return attributeId;
        }

        private int getScore() {
            return score;
        }
    }

    private static final class SkillPointsEntry {
        private final int level;
        private final int points;

        private SkillPointsEntry(int level, int points) {
            this.level = level;
            this.points = points;
        }

        private int level() {
            return level;
        }

        private int points() {
            return points;
        }
    }
}
