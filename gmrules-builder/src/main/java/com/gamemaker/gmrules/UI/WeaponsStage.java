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
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.GameElements.Weapon;

/**
 * Stage for defining weapons.
 */
public class WeaponsStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Weapon> weaponListModel = new DefaultListModel<>();
    private final JList<Weapon> weaponList = new JList<>(weaponListModel);
    private final JScrollPane weaponScroll = new JScrollPane(weaponList);
    private final JButton removeButton = new JButton();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel listPanel = new JPanel(new BorderLayout());
    private final JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public WeaponsStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshWeapons();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("weapons.title"));
        introArea.setText(Localization.get("weapons.intro"));
        addButton.setText(Localization.get("weapons.add"));
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
        weaponScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        weaponList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        weaponList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        weaponList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
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
        contentPanel.add(Box.createVerticalStrut(12));

        listPanel.add(weaponScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addWeapon());
        editButton.addActionListener(event -> editSelectedWeapon());
        removeButton.addActionListener(event -> removeSelectedWeapon());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new SkillsStage(mainStage, game));
        });
    }

    private void addWeapon() {
        editWeapon(new Weapon(""));
    }

    private void editSelectedWeapon() {
        int selectedIndex = weaponList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Weapon selected = weaponListModel.getElementAt(selectedIndex);
        editWeapon(selected);
    }

    private void removeSelectedWeapon() {
        int selectedIndex = weaponList.getSelectedIndex();
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
        Weapon selected = weaponListModel.getElementAt(selectedIndex);
        ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
        registry.remove(selected);
        refreshWeapons();
        saveGame();
    }

    private void refreshWeapons() {
        weaponListModel.clear();
        ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
        List<Weapon> weapons = registry.getAll();
        weapons.sort(Comparator.comparing(
            weapon -> Objects.toString(weapon.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Weapon weapon : weapons) {
            weaponListModel.addElement(weapon);
        }
    }

    private void editWeapon(Weapon selected) {
        Weapon safeWeapon = Objects.requireNonNullElse(selected, new Weapon(""));
        WeaponEditPanel editPanel = new WeaponEditPanel(mainStage, game, safeWeapon);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("weapons.edit.title"),
            safeWeapon.getName(),
            safeWeapon.getDescription(),
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
        ElementRegistry<Weapon> registry = game.getElementRegistry(ElementRegistryKey.WEAPONS);
        String previousName = safeWeapon.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeWeapon);
            safeWeapon.setName(name);
            safeWeapon.setDescription(result.getDescription());
            editPanel.applyToWeapon(safeWeapon);
            registry.add(safeWeapon);
        } else {
            safeWeapon.setDescription(result.getDescription());
            editPanel.applyToWeapon(safeWeapon);
        }
        refreshWeapons();
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
        return StageId.WEAPONS;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
