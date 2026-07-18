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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.AttributeType;
import com.gamemaker.gmrules.AtomicElements.AttributeTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for defining core attributes.
 */
public class AttributesStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "attributes";

    private final JLabel titleLabel = new JLabel();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JTextArea introArea = new JTextArea();
    private final AttributeType emptyType = new AttributeType("", "", "", true);
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Attribute> attributeListModel = new DefaultListModel<>();
    private final JList<Attribute> attributeList = new JList<>(attributeListModel);
    private final JScrollPane attributeScroll = new JScrollPane(attributeList);
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
    private String lastAttributeTypeKey = "";

    // *** CONSTRUCTORS ***
    public AttributesStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshAttributes();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        introArea.setText(Localization.get("attributes.intro"));
        emptyType.setKey("");
        emptyType.setName(Localization.get("attrtypes.none"));
        emptyType.setCustom(true);
        addButton.setText(Localization.get("attributes.add"));
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
        attributeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        attributeList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        attributeList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String typeLabel = resolveTypeDisplayName(value.getType());
            label.setText(value.getDisplayName() + " : " + typeLabel);
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

        listPanel.add(attributeScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addAttribute());
        editButton.addActionListener(event -> editSelectedAttribute());
        removeButton.addActionListener(event -> removeSelectedAttribute());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            StageId nextStage = AttributeGenerationStage.getNextGenerationStage(
                game.getAttributeGenerationMethod(),
                StageId.ATTRIBUTES
            );
            mainStage.navigateToStage(nextStage);
        });
        attributeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    editSelectedAttributeType();
                }
            }
        });
    }

    private void editSelectedAttribute() {
        int selectedIndex = attributeList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Attribute selected = attributeListModel.getElementAt(selectedIndex);
        editAttribute(selected);
    }

    private void addAttribute() {
        Attribute attribute = new Attribute("");
        if (!lastAttributeTypeKey.isEmpty()) {
            attribute.setType(lastAttributeTypeKey);
        }
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        AttributeEditPanel editPanel = new AttributeEditPanel(
            mainStage,
            registry.getAll(),
            Localization.get("attrtypes.none"),
            attribute
        );
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("attributes.edit.title"),
            "",
            "",
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
        int minValue = editPanel.getMinValue();
        int maxValue = editPanel.getMaxValue();
        if (minValue > maxValue) {
            PopupAlert.show(mainStage, Localization.get("attributes.edit.range.invalid"));
            return;
        }
        attribute.setName(name);
        attribute.setDescription(result.getDescription());
        attribute.setType(editPanel.getSelectedTypeKey());
        attribute.setMinValue(minValue);
        attribute.setMaxValue(maxValue);
        attribute.setModifierMap(editPanel.getModifierMap());
        attribute.setScoreBonuses(editPanel.getScoreBonuses());
        lastAttributeTypeKey = attribute.getType();
        boolean added = game.addElement("attributes", attribute);
        if (!added) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        refreshAttributes();
        saveGame();
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void removeSelectedAttribute() {
        int selectedIndex = attributeList.getSelectedIndex();
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
        Attribute selected = attributeListModel.getElementAt(selectedIndex);
        game.removeElement("attributes", selected);
        refreshAttributes();
        saveGame();
    }

    private void refreshAttributes() {
        attributeListModel.clear();
        List<Attribute> attributes = game.getObjectArray("attributes");
        for (Attribute attribute : attributes) {
            attributeListModel.addElement(attribute);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("attributes.title");
    }

    private void editAttribute(Attribute selected) {
        Attribute safeAttribute = Objects.requireNonNullElse(selected, new Attribute(""));
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        AttributeEditPanel editPanel = new AttributeEditPanel(
            mainStage,
            registry.getAll(),
            Localization.get("attrtypes.none"),
            safeAttribute
        );
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("attributes.edit.title"),
            safeAttribute.getName(),
            safeAttribute.getDescription(),
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
        int minValue = editPanel.getMinValue();
        int maxValue = editPanel.getMaxValue();
        if (minValue > maxValue) {
            PopupAlert.show(mainStage, Localization.get("attributes.edit.range.invalid"));
            return;
        }
        safeAttribute.setName(name);
        safeAttribute.setDescription(result.getDescription());
        safeAttribute.setType(editPanel.getSelectedTypeKey());
        safeAttribute.setMinValue(minValue);
        safeAttribute.setMaxValue(maxValue);
        safeAttribute.setModifierMap(editPanel.getModifierMap());
        safeAttribute.setScoreBonuses(editPanel.getScoreBonuses());
        lastAttributeTypeKey = safeAttribute.getType();
        refreshAttributes();
        saveGame();
    }

    private void editSelectedAttributeType() {
        int selectedIndex = attributeList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Attribute selected = attributeListModel.getElementAt(selectedIndex);
        AttributeType currentType = resolveTypeByKey(selected.getType());
        JComboBox<AttributeType> typeSelector = buildTypeSelector(currentType);
        int result = JOptionPane.showConfirmDialog(
            this,
            typeSelector,
            Localization.get("attrtypes.select"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        AttributeType chosen = (AttributeType) typeSelector.getSelectedItem();
        AttributeType safeType = Objects.requireNonNullElse(chosen, emptyType);
        selected.setType(safeType.getKey());
        lastAttributeTypeKey = selected.getType();
        refreshAttributes();
        saveGame();
    }

    private JComboBox<AttributeType> buildTypeSelector(AttributeType selectedType) {
        JComboBox<AttributeType> selector = new JComboBox<>();
        selector.addItem(emptyType);
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        for (AttributeType type : registry.getAll()) {
            selector.addItem(type);
        }
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        selector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            AttributeType type = (AttributeType) value;
            label.setText(type == null ? "" : type.getDisplayName());
            return label;
        });
        selector.setSelectedItem(selectedType);
        return selector;
    }

    private AttributeType resolveTypeByKey(String key) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return emptyType;
        }
        AttributeTypes registry = game.getRegistry(RegistryKey.ATTRIBUTE_TYPES);
        AttributeType type = registry.get(safeKey);
        return Objects.requireNonNullElse(type, emptyType);
    }

    private String resolveTypeDisplayName(String key) {
        AttributeType type = resolveTypeByKey(key);
        if (type == emptyType) {
            return Localization.get("attrtypes.none");
        }
        String name = Objects.toString(type.getDisplayName(), "").trim();
        if (name.isEmpty()) {
            return Localization.get("attrtypes.none");
        }
        return name;
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
        return StageId.ATTRIBUTES;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
