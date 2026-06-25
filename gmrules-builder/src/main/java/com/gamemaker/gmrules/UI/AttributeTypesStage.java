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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.AttributeType;
import com.gamemaker.gmrules.AtomicElements.AttributeTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for defining attribute types.
 */
public class AttributeTypesStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "attribute-types";

    private final JLabel titleLabel = new JLabel();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JTextArea introArea = new JTextArea();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<AttributeType> typeListModel = new DefaultListModel<>();
    private final JList<AttributeType> typeList = new JList<>(typeListModel);
    private final JScrollPane typeScroll = new JScrollPane(typeList);
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
    public AttributeTypesStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshTypes();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        introArea.setText(Localization.get("attrtypes.intro"));
        addButton.setText(Localization.get("attrtypes.add"));
        editButton.setText(Localization.get("common.edit"));
        removeButton.setText(Localization.get("common.remove.selected"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));
    }

    private void configureInputs() {
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNameField.setColumns(NAME_FIELD_COLUMNS);
        Dimension systemFieldSize = systemNameField.getPreferredSize();
        systemNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, systemFieldSize.height));
        systemNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setFont(UIManager.getFont("Label.font"));
        introArea.setEditable(false);
        introArea.setFocusable(false);
        introArea.setLineWrap(true);
        introArea.setWrapStyleWord(true);
        introArea.setOpaque(false);
        introArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setForeground(UIManager.getColor("Label.disabledForeground"));
        typeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        typeList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        typeList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
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
        contentPanel.add(systemNameLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(systemNamePanel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(12));

        listPanel.add(typeScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addAttributeType());
        editButton.addActionListener(event -> editSelectedType());
        removeButton.addActionListener(event -> removeSelectedType());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new AttributesStage(mainStage, game));
        });
    }

    private void editSelectedType() {
        int selectedIndex = typeList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        AttributeType selected = typeListModel.getElementAt(selectedIndex);
        editType(selected);
    }

    private void addAttributeType() {
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("attrtypes.edit.title"),
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
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        if (registry.contains(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        AttributeType attributeType = new AttributeType(name, name, result.getDescription(), true);
        registry.register(attributeType);
        refreshTypes();
        saveGame();
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void removeSelectedType() {
        int selectedIndex = typeList.getSelectedIndex();
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
        AttributeType selected = typeListModel.getElementAt(selectedIndex);
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        registry.remove(selected.getKey());
        refreshTypes();
        saveGame();
    }

    private void refreshTypes() {
        typeListModel.clear();
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        List<AttributeType> types = registry.getAll();
        types.sort(Comparator.comparing(
            type -> Objects.toString(type.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (AttributeType type : types) {
            typeListModel.addElement(type);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("attrtypes.title");
    }

    private void editType(AttributeType selected) {
        AttributeType safeType = Objects.requireNonNullElse(selected, new AttributeType("", "", "", true));
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("attrtypes.edit.title"),
            safeType.getName(),
            safeType.getDescription(),
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
        safeType.setName(name);
        safeType.setDescription(result.getDescription());
        refreshTypes();
        saveGame();
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
        return StageId.ATTRIBUTE_TYPES;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
