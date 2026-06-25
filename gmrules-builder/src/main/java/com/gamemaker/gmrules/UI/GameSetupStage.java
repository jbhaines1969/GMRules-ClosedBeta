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
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Game setup screen for initial game metadata.
 */
public class GameSetupStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private final JLabel gameNameLabel = new JLabel();
    private final JTextField gameNameField = new JTextField();
    private final JTextArea gameNameHintArea = new JTextArea();
    private final JLabel gameDescriptionLabel = new JLabel();
    private final JTextArea gameDescriptionArea = new JTextArea();
    private final JScrollPane gameDescriptionScroll = new JScrollPane(gameDescriptionArea);
    private final JLabel gameTypeLabel = new JLabel();
    private final JComboBox<String> gameTypeDropdown = new JComboBox<>();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private String gameTypePlaceholder = "";

    // *** CONSTRUCTORS ***
    public GameSetupStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        loadFromGame();
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        gameNameLabel.setText(Localization.get("setup.game.name"));
        gameNameHintArea.setText(Localization.get("setup.game.name.hint"));
        gameDescriptionLabel.setText(Localization.get("setup.game.description"));
        gameTypeLabel.setText(Localization.get("setup.game.type"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));
        gameTypePlaceholder = Localization.get("setup.game.type.placeholder");
        populateGameTypes();
    }

    private void configureInputs() {
        gameNameField.setColumns(24);
        EntryInputHandler.selectAllOnFocus(gameNameField);
        Dimension nameFieldSize = gameNameField.getPreferredSize();
        gameNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameFieldSize.height));
        gameNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        gameNameHintArea.setFont(UIManager.getFont("Label.font"));
        gameNameHintArea.setEditable(false);
        gameNameHintArea.setFocusable(false);
        gameNameHintArea.setLineWrap(true);
        gameNameHintArea.setWrapStyleWord(true);
        gameNameHintArea.setOpaque(false);
        gameNameHintArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameNameHintArea.setForeground(UIManager.getColor("Label.disabledForeground"));
        gameNameHintArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        gameDescriptionArea.setRows(6);
        gameDescriptionArea.setLineWrap(true);
        gameDescriptionArea.setWrapStyleWord(true);
        gameDescriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameDescriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        gameTypeDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void loadFromGame() {
        gameNameField.setText(Objects.toString(game.getName(), ""));
        gameDescriptionArea.setText(Objects.toString(game.getDescription(), ""));
        String gameType = Objects.toString(game.getGameType(), "");
        if (!gameType.isEmpty()) {
            gameTypeDropdown.setSelectedItem(gameType);
            return;
        }
        gameTypeDropdown.setSelectedIndex(0);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        gameNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(gameNameLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(gameNameField);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(gameNameHintArea);
        contentPanel.add(Box.createVerticalStrut(16));

        gameDescriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(gameDescriptionLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(gameDescriptionScroll);
        contentPanel.add(Box.createVerticalStrut(16));

        gameTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(gameTypeLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(gameTypeDropdown);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> handleContinue());
    }

    private void handleContinue() {
        String resolvedName = EntryInputHandler.resolveText(gameNameField);
        if (resolvedName.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            gameNameField.requestFocusInWindow();
            return;
        }
        String originalName = Objects.toString(game.getName(), "");
        boolean isNewGame = originalName.isEmpty();
        boolean hasExistingSave = mainStage.hasCurrentSavePath();
        Path existingSavePath = hasExistingSave ? mainStage.getCurrentSavePath() : null;

        applyInputsToGame();

        String updatedName = Objects.toString(game.getName(), "");
        boolean nameChanged = !originalName.equals(updatedName);
        Path targetPath = resolveTargetSavePath(hasExistingSave, nameChanged, existingSavePath);

        if (isNewGame && Files.exists(targetPath)) {
            boolean overwrite = ConfirmPopup.show(
                mainStage,
                Localization.get("setup.game.duplicate.confirm"),
                Localization.get("common.overwrite")
            );
            if (!overwrite) {
                return;
            }
        }

        boolean renameExisting = false;
        if (!isNewGame && hasExistingSave && nameChanged && existingSavePath != null && Files.exists(existingSavePath)) {
            renameExisting = ConfirmPopup.show(
                mainStage,
                Localization.get("setup.game.rename.confirm"),
                Localization.get("common.rename"),
                Localization.get("common.create.new")
            );
        }

        if (!isNewGame && hasExistingSave && !targetPath.equals(existingSavePath) && Files.exists(targetPath)) {
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
            Path savedPath = gameSaveIO.saveToPath(game, targetPath);
            mainStage.setCurrentSavePath(savedPath);
            if (renameExisting && existingSavePath != null && !existingSavePath.equals(savedPath)) {
                Files.deleteIfExists(existingSavePath);
            }
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }

        mainStage.showContent(new MeasurementsStage(mainStage, game));
    }

    private Path resolveTargetSavePath(boolean hasExistingSave, boolean nameChanged, Path existingSavePath) {
        if (hasExistingSave && existingSavePath != null) {
            if (!nameChanged) {
                return existingSavePath;
            }
            Path parent = Objects.requireNonNullElse(existingSavePath.getParent(), Paths.get(""));
            String filename = gameSaveIO.buildFilename(game);
            return parent.resolve(filename);
        }
        return gameSaveIO.getUserHomeSavePath(game);
    }

    private void applyInputsToGame() {
        game.setName(EntryInputHandler.resolveText(gameNameField));
        game.setDescription(gameDescriptionArea.getText());
        String selection = Objects.toString(gameTypeDropdown.getSelectedItem(), "");
        String resolved = selection.equals(gameTypePlaceholder) ? "" : selection;
        game.setGameType(resolved);
    }

    private void populateGameTypes() {
        gameTypeDropdown.removeAllItems();
        gameTypeDropdown.addItem(gameTypePlaceholder);
        for (String type : Game.getGameTypes()) {
            gameTypeDropdown.addItem(type);
        }
    }

    @Override
    public StageId getStageId() {
        return StageId.SETUP;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
