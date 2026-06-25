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
import java.awt.FlowLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameIO;

/**
 * Initial display stage panel for the application.
 */
public class DisplayStage extends JPanel {

    // *** MEMBERS ***
    private final JLabel titleLabel = new JLabel();
    private final JTextArea descriptionArea = new JTextArea();
    private final JButton continueButton = new JButton();
    private final JButton loadButton = new JButton();
    private final JLabel languageLabel = new JLabel();
    private final JComboBox<String> languageDropdown =
            new JComboBox<>(new String[] { "English", "Fran\u00e7ais" });
    private final JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    private final JPanel contentPanel = new JPanel();
    private final MainStage mainStage;
    private String selectedLocaleTag = "en";

    // *** CONSTRUCTORS ***
    public DisplayStage(MainStage mainStage) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        configureText();
        configureInputs();
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionArea.setFont(UIManager.getFont("Label.font"));
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshText();
    }

    private void configureInputs() {
        Locale defaultLocale = Locale.getDefault();
        setLanguageSelection(defaultLocale);
    }

    private void refreshText() {
        titleLabel.setText(Localization.get("splash.title"));
        descriptionArea.setText(Localization.get("splash.description"));
        continueButton.setText(Localization.get("common.continue"));
        loadButton.setText(Localization.get("splash.load"));
        languageLabel.setText(Localization.get("splash.language"));
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(descriptionArea);
        contentPanel.add(Box.createVerticalStrut(20));
        languagePanel.add(languageLabel);
        languagePanel.add(languageDropdown);
        contentPanel.add(languagePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loadButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        languageDropdown.addActionListener(event -> applyLanguageSelection());
        loadButton.addActionListener(event -> loadSavedGame());
        continueButton.addActionListener(event -> {
            Game game = new Game("");
            game.setUiLocale(selectedLocaleTag);
            mainStage.clearCurrentSavePath();
            mainStage.showContent(new GameSetupStage(mainStage, game));
        });
    }

    private void applyLanguageSelection() {
        Locale locale = resolveSelectedLocale();
        selectedLocaleTag = locale.toLanguageTag();
        Localization.setLocale(locale);
        mainStage.setTitle(Localization.get("app.title"));
        mainStage.refreshSidebar();
        refreshText();
    }

    private void loadSavedGame() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(Localization.get("splash.load"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("GMRules Rulesets (*.gmrf)", "gmrf"));

        Path defaultDirectory = resolveDefaultLoadDirectory();
        if (Files.isDirectory(defaultDirectory)) {
            chooser.setCurrentDirectory(defaultDirectory.toFile());
        }

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path selectedPath = chooser.getSelectedFile().toPath();
        try {
            Game loaded = new GameIO().loadGame(selectedPath);
            applyLocaleFromGame(loaded);
            mainStage.setCurrentSavePath(selectedPath);
            mainStage.showContent(new GameSetupStage(mainStage, loaded));
        } catch (java.io.IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game file: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                Localization.get("splash.load.error.message"),
                Localization.get("splash.load.error.title"),
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Path resolveDefaultLoadDirectory() {
        String userHome = Objects.toString(System.getProperty("user.home"), "").trim();
        if (userHome.isEmpty()) {
            return Paths.get("");
        }
        return Paths.get(userHome, "GameMakerFiles");
    }

    private Locale resolveSelectedLocale() {
        int selectedIndex = languageDropdown.getSelectedIndex();
        return selectedIndex == 1 ? Locale.FRENCH : Locale.ENGLISH;
    }

    private void setLanguageSelection(Locale locale) {
        Locale resolvedLocale = Objects.requireNonNullElseGet(locale, Locale::getDefault);
        boolean isFrench = Locale.FRENCH.getLanguage().equals(resolvedLocale.getLanguage());
        languageDropdown.setSelectedIndex(isFrench ? 1 : 0);
        selectedLocaleTag = resolvedLocale.toLanguageTag();
    }

    private void applyLocaleFromGame(Game game) {
        Game safeGame = Objects.requireNonNullElseGet(game, () -> new Game(""));
        String localeTag = Objects.toString(safeGame.getUiLocale(), "").trim();
        if (localeTag.isEmpty()) {
            return;
        }
        Locale locale = Locale.forLanguageTag(localeTag);
        setLanguageSelection(locale);
        Localization.setLocale(locale);
        mainStage.setTitle(Localization.get("app.title"));
        mainStage.refreshSidebar();
        refreshText();
    }
}
