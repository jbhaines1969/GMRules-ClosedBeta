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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.AtomicElements.SkillCategories;
import com.gamemaker.gmrules.AtomicElements.SkillCategory;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.LevelingMethod;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.CharacterElements.Skill;

/**
 * Stage for defining skills.
 */
public class SkillsStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "skills";
    private static final int SKILL_POINTS_MIN = 0;
    private static final int SKILL_POINTS_MAX = 100;
    private static final int SKILL_POINTS_LEVEL_MIN = 1;
    private static final int SKILL_POINTS_LEVEL_DEFAULT_MAX = 20;
    private static final String PROGRESSION_BY_CLASS = "byClass";
    private static final String PROGRESSION_FIXED = "fixed";
    private static final String PROGRESSION_INTELLIGENCE = "intelligence";
    private static final String PROGRESSION_CUSTOM = "custom";

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JLabel progressionLabel = new JLabel();
    private final JComboBox<OptionItem> progressionDropdown = new JComboBox<>();
    private final JLabel baseSkillPointsLabel = new JLabel();
    private final JSpinner baseSkillPointsSpinner =
        new JSpinner(new SpinnerNumberModel(0, SKILL_POINTS_MIN, SKILL_POINTS_MAX, 1));
    private final JCheckBox skillPointsModifiedByIntCheck = new JCheckBox();
    private final JLabel minimumSkillPointsLabel = new JLabel();
    private final JSpinner minimumSkillPointsSpinner =
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
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Skill> skillListModel = new DefaultListModel<>();
    private final JList<Skill> skillList = new JList<>(skillListModel);
    private final JScrollPane skillScroll = new JScrollPane(skillList);
    private final JButton removeButton = new JButton();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel systemNamePanel = new JPanel();
    private final JPanel progressionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel progressionOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel progressionLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel progressionListPanel = new JPanel(new BorderLayout());
    private final JPanel listPanel = new JPanel(new BorderLayout());
    private final JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final LevelingMethod levelingMethod;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private String lastSkillCategoryKey = "";
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public SkillsStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.levelingMethod = this.game.getLevelingMethod();
        configureText();
        configureInputs();
        buildLayout();
        refreshSkills();
        loadSkillPointProgression();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        introArea.setText(Localization.get("skills.intro"));
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        progressionLabel.setText(Localization.get("skills.progression.type"));
        baseSkillPointsLabel.setText(Localization.get("skills.progression.base"));
        skillPointsModifiedByIntCheck.setText(Localization.get("skills.progression.mod_int"));
        minimumSkillPointsLabel.setText(Localization.get("skills.progression.minimum"));
        skillPointsSameCheck.setText(Localization.get("classes.skill_points.same_all"));
        skillPointsLevelLabel.setText(Localization.get("classes.skill_points.level"));
        skillPointsValueLabel.setText(Localization.get("classes.skill_points.value"));
        addSkillPointsLevelButton.setText(Localization.get("classes.skill_points.add"));
        removeSkillPointsLevelButton.setText(Localization.get("common.remove.selected"));
        addButton.setText(Localization.get("skills.add"));
        editButton.setText(Localization.get("common.edit"));
        removeButton.setText(Localization.get("common.remove.selected"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));
    }

    private void configureInputs() {
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setFont(UIManager.getFont("Label.font"));
        introArea.setEditable(false);
        introArea.setFocusable(false);
        introArea.setLineWrap(true);
        introArea.setWrapStyleWord(true);
        introArea.setOpaque(false);
        introArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setForeground(UIManager.getColor("Label.disabledForeground"));
        systemNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNameField.setColumns(NAME_FIELD_COLUMNS);
        Dimension systemFieldSize = systemNameField.getPreferredSize();
        systemNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, systemFieldSize.height));
        systemNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressionOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressionLevelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        skillScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        progressionDropdown.setModel(new DefaultComboBoxModel<>(new OptionItem[] {
            new OptionItem(PROGRESSION_BY_CLASS, Localization.get("skills.progression.by_class")),
            new OptionItem(PROGRESSION_FIXED, Localization.get("skills.progression.fixed")),
            new OptionItem(PROGRESSION_INTELLIGENCE, Localization.get("skills.progression.intelligence")),
            new OptionItem(PROGRESSION_CUSTOM, Localization.get("skills.progression.custom")),
        }));

        Dimension spinnerSize = baseSkillPointsSpinner.getPreferredSize();
        baseSkillPointsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerSize.height));
        minimumSkillPointsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerSize.height));
        skillPointsValueSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerSize.height));
        skillPointsLevelDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerSize.height));
        skillPointsList.setVisibleRowCount(5);
        skillPointsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        skillPointsLevelDropdown.addItem(skillPointsLevelPlaceholder);
        int maxLevel = Math.max(SKILL_POINTS_LEVEL_DEFAULT_MAX, game.getLevelingMethod().getMaximumLevel());
        for (int level = SKILL_POINTS_LEVEL_MIN; level <= maxLevel; level++) {
            skillPointsLevelDropdown.addItem(level);
        }

        skillList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        skillList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        skillList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String category = Objects.toString(value.getCategory(), "").trim();
            if (category.isEmpty()) {
                label.setText(value.getDisplayName());
            } else {
                SkillCategories registry = game.getRegistry(RegistryKey.SKILL_CATEGORIES);
                String categoryLabel = resolveCategoryLabel(category, registry);
                label.setText(value.getDisplayName() + " (" + categoryLabel + ")");
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
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(systemNameLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(systemNamePanel);
        contentPanel.add(Box.createVerticalStrut(12));

        progressionPanel.add(progressionLabel);
        progressionPanel.add(progressionDropdown);
        progressionPanel.add(baseSkillPointsLabel);
        progressionPanel.add(baseSkillPointsSpinner);
        progressionPanel.add(minimumSkillPointsLabel);
        progressionPanel.add(minimumSkillPointsSpinner);
        contentPanel.add(progressionPanel);
        contentPanel.add(Box.createVerticalStrut(6));

        progressionOptionsPanel.add(skillPointsModifiedByIntCheck);
        progressionOptionsPanel.add(skillPointsSameCheck);
        contentPanel.add(progressionOptionsPanel);
        contentPanel.add(Box.createVerticalStrut(6));

        progressionLevelPanel.add(skillPointsLevelLabel);
        progressionLevelPanel.add(skillPointsLevelDropdown);
        progressionLevelPanel.add(skillPointsValueLabel);
        progressionLevelPanel.add(skillPointsValueSpinner);
        progressionLevelPanel.add(addSkillPointsLevelButton);
        contentPanel.add(progressionLevelPanel);
        contentPanel.add(Box.createVerticalStrut(6));

        progressionListPanel.add(skillPointsScroll, BorderLayout.CENTER);
        JPanel progressionListButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        progressionListButtons.add(removeSkillPointsLevelButton);
        progressionListPanel.add(progressionListButtons, BorderLayout.SOUTH);
        contentPanel.add(progressionListPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        listPanel.add(skillScroll, BorderLayout.CENTER);
        listButtonPanel.add(addButton);
        listButtonPanel.add(editButton);
        listButtonPanel.add(removeButton);
        listPanel.add(listButtonPanel, BorderLayout.SOUTH);
        contentPanel.add(listPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        systemNameButton.addActionListener(event -> saveSystemName());
        systemNameField.addActionListener(event -> saveSystemName());
        progressionDropdown.addActionListener(event -> updateSkillPointProgressionFromInputs());
        baseSkillPointsSpinner.addChangeListener(event -> updateSkillPointProgressionFromInputs());
        minimumSkillPointsSpinner.addChangeListener(event -> updateSkillPointProgressionFromInputs());
        skillPointsModifiedByIntCheck.addActionListener(event -> updateSkillPointProgressionFromInputs());
        skillPointsSameCheck.addActionListener(event -> updateSkillPointProgressionFromInputs());
        addSkillPointsLevelButton.addActionListener(event -> addSkillPointsLevelEntry());
        removeSkillPointsLevelButton.addActionListener(event -> removeSkillPointsLevelEntry());
        addButton.addActionListener(event -> addSkill());
        editButton.addActionListener(event -> editSelectedSkill());
        removeButton.addActionListener(event -> removeSelectedSkill());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new SpellsStage(mainStage, game));
        });
    }

    private void loadSkillPointProgression() {
        loading = true;
        String progression = Objects.toString(levelingMethod.getSkillPointProgression(), PROGRESSION_BY_CLASS);
        setSelectedOption(progressionDropdown, progression);
        baseSkillPointsSpinner.setValue(Math.max(SKILL_POINTS_MIN, levelingMethod.getBaseSkillPointsPerLevel()));
        minimumSkillPointsSpinner.setValue(Math.max(SKILL_POINTS_MIN, levelingMethod.getMinimumSkillPointsPerLevel()));
        skillPointsModifiedByIntCheck.setSelected(levelingMethod.isSkillPointsModifiedByInt());
        skillPointsSameCheck.setSelected(levelingMethod.isSkillPointsSameAllLevels());
        skillPointsModel.clear();
        List<Integer> levels = new java.util.ArrayList<>(levelingMethod.getSkillPointsByLevel().keySet());
        levels.sort(Integer::compareTo);
        for (Integer level : levels) {
            int safeLevel = Objects.requireNonNullElse(level, 0);
            if (safeLevel <= 0) {
                continue;
            }
            int points = levelingMethod.getSkillPointsByLevel().getOrDefault(safeLevel, 0);
            skillPointsModel.addElement(new SkillPointsEntry(safeLevel, Math.max(SKILL_POINTS_MIN, points)));
        }
        loading = false;
        updateSkillPointControls();
    }

    private void updateSkillPointProgressionFromInputs() {
        if (loading) {
            return;
        }
        levelingMethod.setSkillPointProgression(getSelectedOptionValue(progressionDropdown));
        levelingMethod.setBaseSkillPointsPerLevel((Integer) baseSkillPointsSpinner.getValue());
        levelingMethod.setMinimumSkillPointsPerLevel((Integer) minimumSkillPointsSpinner.getValue());
        levelingMethod.setSkillPointsModifiedByInt(skillPointsModifiedByIntCheck.isSelected());
        levelingMethod.setSkillPointsSameAllLevels(skillPointsSameCheck.isSelected());
        levelingMethod.setSkillPointsByLevel(resolveSkillPointsByLevelMap());
        updateSkillPointControls();
        saveGame();
    }

    private void addSkillPointsLevelEntry() {
        Integer levelValue = (Integer) skillPointsLevelDropdown.getSelectedItem();
        int level = Objects.requireNonNullElse(levelValue, 0);
        if (level <= 0) {
            return;
        }
        int points = Math.max(SKILL_POINTS_MIN, (Integer) skillPointsValueSpinner.getValue());
        boolean replaced = false;
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            SkillPointsEntry existing = skillPointsModel.getElementAt(index);
            if (existing.level() == level) {
                skillPointsModel.set(index, new SkillPointsEntry(level, points));
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            skillPointsModel.addElement(new SkillPointsEntry(level, points));
        }
        sortSkillPointsModel();
        updateSkillPointProgressionFromInputs();
    }

    private void removeSkillPointsLevelEntry() {
        int selectedIndex = skillPointsList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        skillPointsModel.remove(selectedIndex);
        updateSkillPointProgressionFromInputs();
    }

    private void sortSkillPointsModel() {
        List<SkillPointsEntry> entries = new java.util.ArrayList<>();
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            entries.add(skillPointsModel.getElementAt(index));
        }
        entries.sort(Comparator.comparingInt(SkillPointsEntry::level));
        skillPointsModel.clear();
        for (SkillPointsEntry entry : entries) {
            skillPointsModel.addElement(entry);
        }
    }

    private java.util.Map<Integer, Integer> resolveSkillPointsByLevelMap() {
        java.util.LinkedHashMap<Integer, Integer> map = new java.util.LinkedHashMap<>();
        for (int index = 0; index < skillPointsModel.getSize(); index++) {
            SkillPointsEntry entry = skillPointsModel.getElementAt(index);
            if (entry.level() > 0) {
                map.put(entry.level(), Math.max(SKILL_POINTS_MIN, entry.points()));
            }
        }
        return map;
    }

    private void updateSkillPointControls() {
        String progression = getSelectedOptionValue(progressionDropdown);
        boolean classMode = PROGRESSION_BY_CLASS.equalsIgnoreCase(progression);
        boolean sameAll = skillPointsSameCheck.isSelected();
        baseSkillPointsLabel.setEnabled(!classMode);
        baseSkillPointsSpinner.setEnabled(!classMode);
        minimumSkillPointsLabel.setEnabled(!classMode);
        minimumSkillPointsSpinner.setEnabled(!classMode);
        skillPointsModifiedByIntCheck.setEnabled(!classMode);
        skillPointsSameCheck.setEnabled(!classMode);
        boolean levelMode = !classMode && !sameAll;
        skillPointsLevelLabel.setEnabled(levelMode);
        skillPointsLevelDropdown.setEnabled(levelMode);
        skillPointsValueLabel.setEnabled(levelMode);
        skillPointsValueSpinner.setEnabled(levelMode);
        addSkillPointsLevelButton.setEnabled(levelMode);
        skillPointsList.setEnabled(levelMode);
        removeSkillPointsLevelButton.setEnabled(levelMode);
    }

    private void addSkill() {
        Skill skill = new Skill("");
        if (!lastSkillCategoryKey.isEmpty()) {
            skill.setCategory(lastSkillCategoryKey);
        }
        editSkill(skill);
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void editSelectedSkill() {
        int selectedIndex = skillList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Skill selected = skillListModel.getElementAt(selectedIndex);
        editSkill(selected);
    }

    private void removeSelectedSkill() {
        int selectedIndex = skillList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        Skill selected = skillListModel.getElementAt(selectedIndex);
        ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
        registry.remove(selected);
        refreshSkills();
        saveGame();
    }

    private void editSkill(Skill selected) {
        Skill safeSkill = Objects.requireNonNullElse(selected, new Skill(""));
        SkillEditPanel editPanel = new SkillEditPanel(mainStage, game, safeSkill);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("skills.edit.title"),
            safeSkill.getName(),
            safeSkill.getDescription(),
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
        String previousName = safeSkill.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeSkill);
            safeSkill.setName(name);
            safeSkill.setDescription(result.getDescription());
            editPanel.applyToSkill(safeSkill);
            registry.add(safeSkill);
        } else {
            safeSkill.setDescription(result.getDescription());
            editPanel.applyToSkill(safeSkill);
        }
        lastSkillCategoryKey = safeSkill.getCategory();
        refreshSkills();
        saveGame();
    }

    private void refreshSkills() {
        skillListModel.clear();
        ElementRegistry<Skill> registry = game.getElementRegistry(ElementRegistryKey.SKILLS);
        List<Skill> skills = registry.getAll();
        skills.sort(Comparator.comparing(
            skill -> Objects.toString(skill.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Skill skill : skills) {
            skillListModel.addElement(skill);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("skills.title");
    }

    private void setSelectedOption(JComboBox<OptionItem> dropdown, String value) {
        String safeValue = Objects.toString(value, "");
        for (int i = 0; i < dropdown.getItemCount(); i++) {
            OptionItem item = dropdown.getItemAt(i);
            if (item != null && item.value.equalsIgnoreCase(safeValue)) {
                dropdown.setSelectedIndex(i);
                return;
            }
        }
        dropdown.setSelectedIndex(0);
    }

    private String getSelectedOptionValue(JComboBox<OptionItem> dropdown) {
        OptionItem item = (OptionItem) dropdown.getSelectedItem();
        if (item == null) {
            return PROGRESSION_BY_CLASS;
        }
        return item.value;
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private String resolveCategoryLabel(String categoryKey, SkillCategories registry) {
        String safeKey = Objects.toString(categoryKey, "").trim();
        if (safeKey.isEmpty()) {
            return "";
        }
        SkillCategories safeRegistry = Objects.requireNonNullElseGet(registry, SkillCategories::new);
        SkillCategory category = safeRegistry.get(safeKey);
        if (category != null) {
            return Objects.toString(category.getDisplayName(), safeKey);
        }
        List<SkillCategory> categories = safeRegistry.getAll();
        for (SkillCategory entry : categories) {
            if (entry == null) {
                continue;
            }
            String name = Objects.toString(entry.getName(), "");
            String displayName = Objects.toString(entry.getDisplayName(), "");
            if (safeKey.equalsIgnoreCase(name) || safeKey.equalsIgnoreCase(displayName)) {
                return displayName.isEmpty() ? name : displayName;
            }
        }
        return safeKey;
    }

    @Override
    public StageId getStageId() {
        return StageId.SKILLS;
    }

    @Override
    public Game getGame() {
        return game;
    }

    private record OptionItem(String value, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

    private record SkillPointsEntry(int level, int points) {
    }
}
