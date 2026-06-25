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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring standard and elite arrays.
 */
public class StandardArrayStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int VALUE_MIN = -1000;
    private static final int VALUE_MAX = 1000;
    private static final String ARRAY_STANDARD = "standardArrays";
    private static final String ARRAY_ELITE = "eliteArrays";
    private static final String[] DEFAULT_ARRAY_VALUES = {
        "standard",
        "elite"
    };

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel standardArraySectionLabel = new JLabel();
    private final JLabel standardArrayAttributeLabel = new JLabel();
    private final JLabel standardArrayValueLabel = new JLabel();
    private final JComboBox<Attribute> standardAttributeDropdown = new JComboBox<>();
    private final JSpinner standardValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JButton addStandardArrayButton = new JButton();
    private final DefaultListModel<String> standardArrayModel = new DefaultListModel<>();
    private final JList<String> standardArrayList = new JList<>(standardArrayModel);
    private final JScrollPane standardArrayScroll = new JScrollPane(standardArrayList);
    private final JButton removeStandardArrayButton = new JButton();

    private final JLabel eliteArraySectionLabel = new JLabel();
    private final JLabel eliteArrayAttributeLabel = new JLabel();
    private final JLabel eliteArrayValueLabel = new JLabel();
    private final JComboBox<Attribute> eliteAttributeDropdown = new JComboBox<>();
    private final JSpinner eliteValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JButton addEliteArrayButton = new JButton();
    private final DefaultListModel<String> eliteArrayModel = new DefaultListModel<>();
    private final JList<String> eliteArrayList = new JList<>(eliteArrayModel);
    private final JScrollPane eliteArrayScroll = new JScrollPane(eliteArrayList);
    private final JButton removeEliteArrayButton = new JButton();

    private final JLabel defaultArrayLabel = new JLabel();
    private final JComboBox<String> defaultArrayDropdown = new JComboBox<>();

    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel standardArrayInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel standardArrayListPanel = new JPanel(new BorderLayout());
    private final JPanel eliteArrayInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel eliteArrayListPanel = new JPanel(new BorderLayout());
    private final JPanel defaultArrayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final AttributeGenerationMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public StandardArrayStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.method = this.game.getAttributeGenerationMethod();
        configureText();
        configureInputs();
        buildLayout();
        loadFromMethod();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("attrgen.standard.title"));
        introArea.setText(Localization.get("attrgen.standard.intro"));
        standardArraySectionLabel.setText(Localization.get("attrgen.arrays.section"));
        standardArrayAttributeLabel.setText(Localization.get("attrgen.arrays.attribute"));
        standardArrayValueLabel.setText(Localization.get("attrgen.arrays.value"));
        addStandardArrayButton.setText(Localization.get("attrgen.arrays.add"));
        removeStandardArrayButton.setText(Localization.get("common.remove.selected"));

        eliteArraySectionLabel.setText(Localization.get("attrgen.arrays.elite.section"));
        eliteArrayAttributeLabel.setText(Localization.get("attrgen.arrays.attribute"));
        eliteArrayValueLabel.setText(Localization.get("attrgen.arrays.value"));
        addEliteArrayButton.setText(Localization.get("attrgen.arrays.add"));
        removeEliteArrayButton.setText(Localization.get("common.remove.selected"));

        defaultArrayLabel.setText(Localization.get("attrgen.default_array"));
        defaultArrayDropdown.addItem(Localization.get("attrgen.default_array.standard"));
        defaultArrayDropdown.addItem(Localization.get("attrgen.default_array.elite"));

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
        standardArraySectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eliteArraySectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        defaultArrayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        standardArrayInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        standardArrayListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eliteArrayInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eliteArrayListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        defaultArrayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        standardArrayList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        standardArrayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eliteArrayList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        eliteArrayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Dimension standardFieldSize = standardArrayValueLabel.getPreferredSize();
        standardAttributeDropdown.setMaximumSize(new Dimension(NAME_FIELD_COLUMNS * 12, standardFieldSize.height));

        Dimension eliteFieldSize = eliteArrayValueLabel.getPreferredSize();
        eliteAttributeDropdown.setMaximumSize(new Dimension(NAME_FIELD_COLUMNS * 12, eliteFieldSize.height));

        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        standardArrayList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) listRenderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(formatArrayEntry(value));
            return label;
        });

        eliteArrayList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) listRenderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(formatArrayEntry(value));
            return label;
        });

        DefaultListCellRenderer attributeRenderer = new DefaultListCellRenderer();
        standardAttributeDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) attributeRenderer.getListCellRendererComponent(
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

        eliteAttributeDropdown.setRenderer(standardAttributeDropdown.getRenderer());
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(standardArraySectionLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        standardArrayInputPanel.add(standardArrayAttributeLabel);
        standardArrayInputPanel.add(standardAttributeDropdown);
        standardArrayInputPanel.add(standardArrayValueLabel);
        standardArrayInputPanel.add(standardValueSpinner);
        standardArrayInputPanel.add(addStandardArrayButton);
        contentPanel.add(standardArrayInputPanel);
        contentPanel.add(Box.createVerticalStrut(6));
        standardArrayListPanel.add(standardArrayScroll, BorderLayout.CENTER);
        standardArrayListPanel.add(removeStandardArrayButton, BorderLayout.SOUTH);
        contentPanel.add(standardArrayListPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(eliteArraySectionLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        eliteArrayInputPanel.add(eliteArrayAttributeLabel);
        eliteArrayInputPanel.add(eliteAttributeDropdown);
        eliteArrayInputPanel.add(eliteArrayValueLabel);
        eliteArrayInputPanel.add(eliteValueSpinner);
        eliteArrayInputPanel.add(addEliteArrayButton);
        contentPanel.add(eliteArrayInputPanel);
        contentPanel.add(Box.createVerticalStrut(6));
        eliteArrayListPanel.add(eliteArrayScroll, BorderLayout.CENTER);
        eliteArrayListPanel.add(removeEliteArrayButton, BorderLayout.SOUTH);
        contentPanel.add(eliteArrayListPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(defaultArrayLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        defaultArrayPanel.add(defaultArrayDropdown);
        contentPanel.add(defaultArrayPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        selectDefaultArrayType(method.getDefaultArrayType());
        refreshAttributeOptions();
        refreshStandardArrays();
        refreshEliteArrays();
        loading = false;
    }

    private void registerActions() {
        addStandardArrayButton.addActionListener(event -> addStandardArray());
        removeStandardArrayButton.addActionListener(event -> removeSelectedStandardArray());
        addEliteArrayButton.addActionListener(event -> addEliteArray());
        removeEliteArrayButton.addActionListener(event -> removeSelectedEliteArray());

        defaultArrayDropdown.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setDefaultArrayType(getSelectedDefaultArrayType());
            saveGame();
        });

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            StageId nextStage = AttributeGenerationStage.getNextGenerationStage(method, StageId.STANDARD_ARRAY);
            mainStage.navigateToStage(nextStage);
        });
    }

    private void addStandardArray() {
        String entry = buildArrayEntry(standardAttributeDropdown, standardValueSpinner);
        if (entry.isEmpty()) {
            return;
        }
        method.addToArray(ARRAY_STANDARD, entry);
        refreshStandardArrays();
        saveGame();
    }

    private void removeSelectedStandardArray() {
        String value = standardArrayList.getSelectedValue();
        if (value == null) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        method.removeFromArray(ARRAY_STANDARD, value);
        refreshStandardArrays();
        saveGame();
    }

    private void addEliteArray() {
        String entry = buildArrayEntry(eliteAttributeDropdown, eliteValueSpinner);
        if (entry.isEmpty()) {
            return;
        }
        method.addToArray(ARRAY_ELITE, entry);
        refreshEliteArrays();
        saveGame();
    }

    private void removeSelectedEliteArray() {
        String value = eliteArrayList.getSelectedValue();
        if (value == null) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        method.removeFromArray(ARRAY_ELITE, value);
        refreshEliteArrays();
        saveGame();
    }

    private void refreshStandardArrays() {
        standardArrayModel.clear();
        List<String> values = method.getArray(ARRAY_STANDARD);
        for (String value : values) {
            standardArrayModel.addElement(value);
        }
    }

    private void refreshEliteArrays() {
        eliteArrayModel.clear();
        List<String> values = method.getArray(ARRAY_ELITE);
        for (String value : values) {
            eliteArrayModel.addElement(value);
        }
    }

    private void refreshAttributeOptions() {
        standardAttributeDropdown.removeAllItems();
        eliteAttributeDropdown.removeAllItems();
        List<Attribute> attributes = game.getObjectArray("attributes");
        for (Attribute attribute : attributes) {
            standardAttributeDropdown.addItem(attribute);
            eliteAttributeDropdown.addItem(attribute);
        }
    }

    private void selectDefaultArrayType(String value) {
        int index = indexOf(value, DEFAULT_ARRAY_VALUES);
        defaultArrayDropdown.setSelectedIndex(index >= 0 ? index : 0);
    }

    private String getSelectedDefaultArrayType() {
        int index = defaultArrayDropdown.getSelectedIndex();
        if (index < 0 || index >= DEFAULT_ARRAY_VALUES.length) {
            return DEFAULT_ARRAY_VALUES[0];
        }
        return DEFAULT_ARRAY_VALUES[index];
    }

    private int indexOf(String value, String[] values) {
        String safeValue = Objects.toString(value, "");
        for (int i = 0; i < values.length; i++) {
            if (safeValue.equalsIgnoreCase(values[i])) {
                return i;
            }
        }
        return -1;
    }

    private String buildArrayEntry(JComboBox<Attribute> dropdown, JSpinner spinner) {
        Attribute selected = (Attribute) dropdown.getSelectedItem();
        if (selected == null) {
            return "";
        }
        String name = Objects.toString(selected.getName(), "").trim();
        if (name.isEmpty()) {
            return "";
        }
        int value = (Integer) spinner.getValue();
        return name + "=" + value;
    }

    private String formatArrayEntry(String entry) {
        String safeEntry = Objects.toString(entry, "").trim();
        if (safeEntry.isEmpty()) {
            return "";
        }
        int separator = safeEntry.indexOf('=');
        if (separator < 0) {
            return safeEntry;
        }
        String attributeName = safeEntry.substring(0, separator).trim();
        String value = safeEntry.substring(separator + 1).trim();
        if (attributeName.isEmpty()) {
            return safeEntry;
        }
        return attributeName + " : " + value;
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
        return StageId.STANDARD_ARRAY;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
