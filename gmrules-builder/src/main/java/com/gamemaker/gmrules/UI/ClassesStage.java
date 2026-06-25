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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.CharacterElements.CharacterClass;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for defining character classes.
 */
public class ClassesStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 24;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final String SYSTEM_NAME_KEY = "classes";

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton();
    private final DefaultListModel<CharacterClass> classListModel = new DefaultListModel<>();
    private final JList<CharacterClass> classList = new JList<>(classListModel);
    private final JScrollPane classScroll = new JScrollPane(classList);
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
    public ClassesStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshClasses();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        introArea.setText(Localization.get("classes.intro"));
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        addButton.setText(Localization.get("classes.add"));
        editButton.setText(Localization.get("common.edit"));
        removeButton.setText(Localization.get("common.remove.selected"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.done"));
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
        classScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        classList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        classList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
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

        listPanel.add(classScroll, BorderLayout.CENTER);
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
        addButton.addActionListener(event -> addClass());
        editButton.addActionListener(event -> editSelectedClass());
        removeButton.addActionListener(event -> removeSelectedClass());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> finishAndDownload());
    }

    private void addClass() {
        editClass(new CharacterClass(""));
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void editSelectedClass() {
        int selectedIndex = classList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        CharacterClass selected = classListModel.getElementAt(selectedIndex);
        editClass(selected);
    }

    private void editClass(CharacterClass selected) {
        CharacterClass safeClass = Objects.requireNonNullElse(selected, new CharacterClass(""));
        ClassEditPanel editPanel = new ClassEditPanel(mainStage, game, safeClass);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("classes.edit.title"),
            safeClass.getName(),
            safeClass.getDescription(),
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
        ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
        String previousName = safeClass.getName();
        if (!previousName.equalsIgnoreCase(name) && registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        if (!previousName.equalsIgnoreCase(name)) {
            registry.remove(safeClass);
            safeClass.setName(name);
            safeClass.setDescription(result.getDescription());
            editPanel.applyToClass(safeClass);
            registry.add(safeClass);
        } else {
            safeClass.setDescription(result.getDescription());
            editPanel.applyToClass(safeClass);
        }
        refreshClasses();
        saveGame();
    }

    private void removeSelectedClass() {
        int selectedIndex = classList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("classes.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        CharacterClass selected = classListModel.getElementAt(selectedIndex);
        ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
        registry.remove(selected);
        refreshClasses();
        saveGame();
    }

    private void refreshClasses() {
        classListModel.clear();
        ElementRegistry<CharacterClass> registry = game.getElementRegistry(ElementRegistryKey.CHARACTER_CLASSES);
        List<CharacterClass> classes = registry.getAll();
        classes.sort(Comparator.comparing(
            characterClass -> Objects.toString(characterClass.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (CharacterClass characterClass : classes) {
            classListModel.addElement(characterClass);
        }
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("classes.title");
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private void finishAndDownload() {
        Path defaultPath = resolveDefaultSavePath();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(Localization.get("common.done"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("GMRules Files (*.gmrf)", "gmrf"));
        chooser.setSelectedFile(defaultPath.toFile());

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path selectedPath = chooser.getSelectedFile().toPath();
        Path normalizedPath = normalizeSavePath(selectedPath);
        if (Files.exists(normalizedPath)) {
            boolean overwrite = ConfirmPopup.show(
                mainStage,
                Localization.get("setup.game.duplicate.confirm"),
                Localization.get("common.overwrite")
            );
            if (!overwrite) {
                return;
            }
        }
        try {
            Path savedPath = gameSaveIO.saveToPath(game, normalizedPath);
            mainStage.setCurrentSavePath(savedPath);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private Path resolveDefaultSavePath() {
        if (mainStage.hasCurrentSavePath()) {
            return mainStage.getCurrentSavePath();
        }
        return gameSaveIO.getUserHomeSavePath(game);
    }

    private Path normalizeSavePath(Path path) {
        Path safePath = Objects.requireNonNullElseGet(path, () -> Paths.get(""));
        String filename = Objects.toString(safePath.getFileName(), "").trim();
        if (filename.toLowerCase().endsWith(".gmrf")) {
            return safePath;
        }
        if (filename.isEmpty()) {
            return safePath;
        }
        return safePath.resolveSibling(filename + ".gmrf");
    }

    @Override
    public StageId getStageId() {
        return StageId.CLASSES;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
