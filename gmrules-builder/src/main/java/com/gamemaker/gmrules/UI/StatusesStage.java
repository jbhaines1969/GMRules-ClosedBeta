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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.EffectType;
import com.gamemaker.gmrules.AtomicElements.EffectTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.SupportElements.Status;

/**
 * Stage for defining reusable statuses.
 */
public class StatusesStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "statuses";

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Status> statusListModel = new DefaultListModel<>();
    private final JList<Status> statusList = new JList<>(statusListModel);
    private final JScrollPane statusScroll = new JScrollPane(statusList);
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
    private final List<String> lastStatusEffectTypeKeys = new ArrayList<>();
    private final EffectType effectTypePlaceholder = new EffectType("");

    // *** CONSTRUCTORS ***
    public StatusesStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshStatuses();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        introArea.setText(Localization.get("statuses.intro"));
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        addButton.setText(Localization.get("statuses.add"));
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
        statusScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        statusList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        statusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        statusList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value == null ? "" : value.getDisplayName());
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

        listPanel.add(statusScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addStatus());
        editButton.addActionListener(event -> editSelectedStatus());
        removeButton.addActionListener(event -> removeSelectedStatus());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new EffectsStage(mainStage, game));
        });
    }

    private void addStatus() {
        Status status = new Status("");
        if (!lastStatusEffectTypeKeys.isEmpty()) {
            status.setEffectTypeKeys(new ArrayList<>(lastStatusEffectTypeKeys));
        }
        editStatus(status);
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void editSelectedStatus() {
        int selectedIndex = statusList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Status selected = statusListModel.getElementAt(selectedIndex);
        editStatus(selected);
    }

    private void removeSelectedStatus() {
        int selectedIndex = statusList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("statuses.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        Status selected = statusListModel.getElementAt(selectedIndex);
        ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
        registry.remove(selected);
        refreshStatuses();
        saveGame();
    }

    private JPanel buildEffectTypePanel(DefaultListModel<String> model) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(Localization.get("effects.type"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<EffectType> dropdown = new JComboBox<>();
        populateEffectTypeDropdown(dropdown);
        applyEffectTypeDropdownRenderer(dropdown);
        dropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addButton = new JButton(Localization.get("effects.type.add"));
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton createButton = new JButton(Localization.get("effecttypes.create"));
        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton removeButton = new JButton(Localization.get("common.remove.selected"));
        removeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        removePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        addSelectedEffectType(dropdown, model);
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
                label.setText(Objects.toString(type.getDisplayName(), name));
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
        for (int index = 0; index < model.getSize(); index++) {
            String existing = Objects.toString(model.getElementAt(index), "");
            if (existing.equalsIgnoreCase(name)) {
                dropdown.setSelectedItem(effectTypePlaceholder);
                return;
            }
        }
        model.addElement(name);
        dropdown.setSelectedItem(effectTypePlaceholder);
    }

    private void removeSelectedEffectTypes(JList<String> list, DefaultListModel<String> model) {
        int selected = list.getSelectedIndex();
        if (selected < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        model.remove(selected);
    }

    private void applyEffectTypeKeys(DefaultListModel<String> model, List<String> selectedKeys) {
        model.clear();
        if (selectedKeys == null || selectedKeys.isEmpty()) {
            return;
        }
        for (String key : selectedKeys) {
            String safeKey = Objects.toString(key, "").trim();
            if (safeKey.isEmpty()) {
                continue;
            }
            boolean exists = false;
            for (int index = 0; index < model.getSize(); index++) {
                String entry = Objects.toString(model.getElementAt(index), "");
                if (entry.equalsIgnoreCase(safeKey)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                model.addElement(safeKey);
            }
        }
    }

    private List<String> collectEffectTypeKeys(DefaultListModel<String> model) {
        List<String> selectedKeys = new java.util.ArrayList<>();
        for (int index = 0; index < model.getSize(); index++) {
            String key = Objects.toString(model.getElementAt(index), "").trim();
            if (!key.isEmpty()) {
                selectedKeys.add(key);
            }
        }
        return selectedKeys;
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

    private void refreshStatuses() {
        statusListModel.clear();
        ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
        List<Status> statuses = registry.getAll();
        statuses.sort(Comparator.comparing(
            status -> Objects.toString(status.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Status status : statuses) {
            statusListModel.addElement(status);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("statuses.title");
    }

    private void editStatus(Status selected) {
        Status safeStatus = Objects.requireNonNullElse(selected, new Status(""));
        DefaultListModel<String> typeModel = new DefaultListModel<>();
        applyEffectTypeKeys(typeModel, safeStatus.getEffectTypeKeys());
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("statuses.edit.title"),
            safeStatus.getName(),
            safeStatus.getDescription(),
            buildEffectTypePanel(typeModel)
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        ElementRegistry<Status> registry = game.getElementRegistry(ElementRegistryKey.STATUSES);
        String previousName = safeStatus.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeStatus);
            safeStatus.setName(name);
            safeStatus.setDescription(result.getDescription());
            registry.add(safeStatus);
        } else {
            safeStatus.setDescription(result.getDescription());
        }
        safeStatus.setEffectTypeKeys(collectEffectTypeKeys(typeModel));
        lastStatusEffectTypeKeys.clear();
        lastStatusEffectTypeKeys.addAll(safeStatus.getEffectTypeKeys());
        refreshStatuses();
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
        return StageId.STATUSES;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
