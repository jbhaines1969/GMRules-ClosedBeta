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
import com.gamemaker.gmrules.GameElements.Spell;

/**
 * Stage for defining spells.
 */
public class SpellsStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "spells";

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<Spell> spellListModel = new DefaultListModel<>();
    private final JList<Spell> spellList = new JList<>(spellListModel);
    private final JScrollPane spellScroll = new JScrollPane(spellList);
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
    public SpellsStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshSpells();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        introArea.setText(Localization.get("spells.intro"));
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        addButton.setText(Localization.get("spells.add"));
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
        spellScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        spellList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        spellList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String level = String.valueOf(value.getLevel());
            String school = value.getSchool();
            if (school == null || school.isEmpty()) {
                label.setText(value.getDisplayName() + " (L" + level + ")");
            } else {
                label.setText(value.getDisplayName() + " (" + school + " L" + level + ")");
            }
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

        listPanel.add(spellScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addSpell());
        editButton.addActionListener(event -> editSelectedSpell());
        removeButton.addActionListener(event -> removeSelectedSpell());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new RacesStage(mainStage, game));
        });
    }

    private void addSpell() {
        editSpell(new Spell(""));
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void editSelectedSpell() {
        int selectedIndex = spellList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Spell selected = spellListModel.getElementAt(selectedIndex);
        editSpell(selected);
    }

    private void removeSelectedSpell() {
        int selectedIndex = spellList.getSelectedIndex();
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
        Spell selected = spellListModel.getElementAt(selectedIndex);
        ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
        registry.remove(selected);
        refreshSpells();
        saveGame();
    }

    private void refreshSpells() {
        spellListModel.clear();
        ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
        List<Spell> spells = registry.getAll();
        spells.sort(Comparator.comparing(
            spell -> Objects.toString(spell.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Spell spell : spells) {
            spellListModel.addElement(spell);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("spells.title");
    }

    private void editSpell(Spell selected) {
        Spell safeSpell = Objects.requireNonNullElse(selected, new Spell(""));
        SpellEditPanel editPanel = new SpellEditPanel(mainStage, game, safeSpell);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("spells.edit.title"),
            safeSpell.getName(),
            safeSpell.getDescription(),
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
        ElementRegistry<Spell> registry = game.getElementRegistry(ElementRegistryKey.SPELLS);
        String previousName = safeSpell.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeSpell);
            safeSpell.setName(name);
            safeSpell.setDescription(result.getDescription());
            editPanel.applyToSpell(safeSpell);
            registry.add(safeSpell);
        } else {
            safeSpell.setDescription(result.getDescription());
            editPanel.applyToSpell(safeSpell);
        }
        refreshSpells();
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
        return StageId.SPELLS;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
