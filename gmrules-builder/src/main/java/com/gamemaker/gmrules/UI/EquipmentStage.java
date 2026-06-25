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
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.GameElements.Equipment;

/**
 * Stage for defining equipment.
 */
public class EquipmentStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "equipment";

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Equipment> equipmentListModel = new DefaultListModel<>();
    private final JList<Equipment> equipmentList = new JList<>(equipmentListModel);
    private final JScrollPane equipmentScroll = new JScrollPane(equipmentList);
    private final JButton removeButton = new JButton();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel systemNamePanel = new JPanel();
    private final JPanel listPanel = new JPanel(new BorderLayout());
    private final JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public EquipmentStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshEquipment();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        introArea.setText(Localization.get("equipment.intro"));
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        addButton.setText(Localization.get("equipment.add"));
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
        equipmentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        equipmentList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        equipmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        equipmentList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value.getDisplayName());
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

        listPanel.add(equipmentScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addEquipment());
        editButton.addActionListener(event -> editSelectedEquipment());
        removeButton.addActionListener(event -> removeSelectedEquipment());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new WeaponsStage(mainStage, game));
        });
    }

    private void addEquipment() {
        editEquipment(new Equipment(""));
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void editSelectedEquipment() {
        int selectedIndex = equipmentList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Equipment selected = equipmentListModel.getElementAt(selectedIndex);
        editEquipment(selected);
    }

    private void removeSelectedEquipment() {
        int selectedIndex = equipmentList.getSelectedIndex();
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
        Equipment selected = equipmentListModel.getElementAt(selectedIndex);
        ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
        registry.remove(selected);
        refreshEquipment();
        saveGame();
    }

    private void refreshEquipment() {
        equipmentListModel.clear();
        ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
        List<Equipment> equipment = registry.getAll();
        equipment.sort(Comparator.comparing(
            item -> Objects.toString(item.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Equipment item : equipment) {
            equipmentListModel.addElement(item);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("equipment.title");
    }

    private void editEquipment(Equipment selected) {
        Equipment safeEquipment = Objects.requireNonNullElse(selected, new Equipment(""));
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        weightSpinner.setValue(Math.max(0, (int) Math.round(safeEquipment.getWeight())));
        JComboBox<String> weightUnitDropdown = new JComboBox<>();
        populateWeightUnits(weightUnitDropdown, safeEquipment.getWeightUnit());
        JPanel weightPanel = buildWeightPanel(weightSpinner, weightUnitDropdown);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("equipment.edit.title"),
            safeEquipment.getName(),
            safeEquipment.getDescription(),
            weightPanel
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        ElementRegistry<Equipment> registry = game.getElementRegistry(ElementRegistryKey.EQUIPMENT);
        String previousName = safeEquipment.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeEquipment);
            safeEquipment.setName(name);
            safeEquipment.setDescription(result.getDescription());
            registry.add(safeEquipment);
        } else {
            safeEquipment.setDescription(result.getDescription());
        }
        safeEquipment.setWeight(Math.max(0, (Integer) weightSpinner.getValue()));
        safeEquipment.setWeightUnit(resolveWeightUnitSelection(weightUnitDropdown));
        refreshEquipment();
        saveGame();
    }

    private JPanel buildWeightPanel(JSpinner weightSpinner, JComboBox<String> weightUnitDropdown) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel weightLabel = new JLabel(Localization.get("common.weight"));
        weightLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension spinnerSize = weightSpinner.getPreferredSize();
        weightSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinnerSize.height));

        JLabel unitLabel = new JLabel(Localization.get("common.weight.unit"));
        unitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightUnitDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension unitSize = weightUnitDropdown.getPreferredSize();
        weightUnitDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, unitSize.height));

        panel.add(weightLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(weightSpinner);
        panel.add(Box.createVerticalStrut(12));
        panel.add(unitLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(weightUnitDropdown);
        return panel;
    }

    private void populateWeightUnits(JComboBox<String> dropdown, String selectedUnit) {
        dropdown.removeAllItems();
        dropdown.addItem("");
        List<String> units = getWeightUnits();
        for (String unit : units) {
            dropdown.addItem(unit);
        }
        applyWeightUnitRenderer(dropdown);
        String safeUnit = Objects.toString(selectedUnit, "").trim();
        if (!safeUnit.isEmpty() && !units.contains(safeUnit)) {
            dropdown.addItem(safeUnit);
        }
        dropdown.setSelectedItem(safeUnit.isEmpty() ? "" : safeUnit);
    }

    private List<String> getWeightUnits() {
        String system = Objects.toString(game.getWeightSystem(), "").trim().toLowerCase();
        String arrayName = system.equals("english") ? "weightUnitsEnglish" : "weightUnitsMetric";
        List<String> units = game.getArray(arrayName);
        return units == null ? List.of() : new java.util.ArrayList<>(units);
    }

    private void applyWeightUnitRenderer(JComboBox<String> dropdown) {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        dropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String unit = Objects.toString(value, "");
            if (unit.isEmpty()) {
                label.setText(Localization.get("common.none"));
            } else {
                label.setText(unit);
            }
            return label;
        });
    }

    private String resolveWeightUnitSelection(JComboBox<String> dropdown) {
        String unit = Objects.toString(dropdown.getSelectedItem(), "").trim();
        return unit.isEmpty() ? "" : unit;
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    @Override
    public StageId getStageId() {
        return StageId.EQUIPMENT;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
