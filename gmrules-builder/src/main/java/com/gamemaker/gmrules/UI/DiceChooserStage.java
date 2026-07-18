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
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for selecting dice options.
 */
public class DiceChooserStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int CUSTOM_RANGE_MIN = 1;
    private static final int CUSTOM_RANGE_MAX = 1000;
    private static final int CUSTOM_RANGE_STEP = 1;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel standardDiceLabel = new JLabel();
    private final JCheckBox d4Check = new JCheckBox();
    private final JCheckBox d2Check = new JCheckBox();
    private final JCheckBox d6Check = new JCheckBox();
    private final JCheckBox d8Check = new JCheckBox();
    private final JCheckBox d10Check = new JCheckBox();
    private final JCheckBox d12Check = new JCheckBox();
    private final JCheckBox d20Check = new JCheckBox();
    private final JCheckBox d100Check = new JCheckBox();
    private final JPanel standardDicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));

    private final JLabel customDiceLabel = new JLabel();
    private final JLabel customMinLabel = new JLabel();
    private final JLabel customMaxLabel = new JLabel();
    private final JSpinner customMinSpinner =
            new JSpinner(new SpinnerNumberModel(1, CUSTOM_RANGE_MIN, CUSTOM_RANGE_MAX, CUSTOM_RANGE_STEP));
    private final JSpinner customMaxSpinner =
            new JSpinner(new SpinnerNumberModel(6, CUSTOM_RANGE_MIN, CUSTOM_RANGE_MAX, CUSTOM_RANGE_STEP));
    private final JButton addRangeButton = new JButton();
    private final JButton removeRangeButton = new JButton();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final DefaultListModel<Game.DiceRange> customRangeModel = new DefaultListModel<>();
    private final JList<Game.DiceRange> customRangeList = new JList<>(customRangeModel);
    private final JScrollPane customRangeScroll = new JScrollPane(customRangeList);
    private final JPanel customInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel customListPanel = new JPanel();

    private final JPanel contentPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public DiceChooserStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        loadFromGame();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("dice.title"));
        introArea.setText(Localization.get("dice.intro"));
        standardDiceLabel.setText(Localization.get("dice.standard"));
        customDiceLabel.setText(Localization.get("dice.custom"));
        customMinLabel.setText(Localization.get("dice.min"));
        customMaxLabel.setText(Localization.get("dice.max"));
        addRangeButton.setText(Localization.get("dice.add"));
        removeRangeButton.setText(Localization.get("common.remove.selected"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));

        d4Check.setText("d4");
        d2Check.setText("d2");
        d6Check.setText("d6");
        d8Check.setText("d8");
        d10Check.setText("d10");
        d12Check.setText("d12");
        d20Check.setText("d20");
        d100Check.setText("d100");
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
        standardDiceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        standardDicePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customDiceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customRangeScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        customRangeList.setVisibleRowCount(6);
        customRangeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(standardDiceLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        standardDicePanel.add(d2Check);
        standardDicePanel.add(d4Check);
        standardDicePanel.add(d6Check);
        standardDicePanel.add(d8Check);
        standardDicePanel.add(d10Check);
        standardDicePanel.add(d12Check);
        standardDicePanel.add(d20Check);
        standardDicePanel.add(d100Check);
        contentPanel.add(standardDicePanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(customDiceLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        customInputPanel.add(customMinLabel);
        customInputPanel.add(customMinSpinner);
        customInputPanel.add(customMaxLabel);
        customInputPanel.add(customMaxSpinner);
        customInputPanel.add(addRangeButton);
        contentPanel.add(customInputPanel);
        contentPanel.add(Box.createVerticalStrut(8));

        customListPanel.setLayout(new BorderLayout());
        customListPanel.add(customRangeScroll, BorderLayout.CENTER);
        customListPanel.add(removeRangeButton, BorderLayout.SOUTH);
        customListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(customListPanel);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        d4Check.addActionListener(event -> updateStandardDice(4, d4Check.isSelected()));
        d2Check.addActionListener(event -> updateStandardDice(2, d2Check.isSelected()));
        d6Check.addActionListener(event -> updateStandardDice(6, d6Check.isSelected()));
        d8Check.addActionListener(event -> updateStandardDice(8, d8Check.isSelected()));
        d10Check.addActionListener(event -> updateStandardDice(10, d10Check.isSelected()));
        d12Check.addActionListener(event -> updateStandardDice(12, d12Check.isSelected()));
        d20Check.addActionListener(event -> updateStandardDice(20, d20Check.isSelected()));
        d100Check.addActionListener(event -> updateStandardDice(100, d100Check.isSelected()));
        addRangeButton.addActionListener(event -> addCustomRange());
        removeRangeButton.addActionListener(event -> removeSelectedRange());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> saveAndContinue());
    }

    private void loadFromGame() {
        updateStandardDiceSelections();
        refreshCustomRanges();
    }

    private void updateStandardDiceSelections() {
        java.util.List<Integer> diceUsed = game.getDiceUsed();
        d4Check.setSelected(diceUsed.contains(4));
        d2Check.setSelected(diceUsed.contains(2));
        d6Check.setSelected(diceUsed.contains(6));
        d8Check.setSelected(diceUsed.contains(8));
        d10Check.setSelected(diceUsed.contains(10));
        d12Check.setSelected(diceUsed.contains(12));
        d20Check.setSelected(diceUsed.contains(20));
        d100Check.setSelected(diceUsed.contains(100));
    }

    private void updateStandardDice(int diceSides, boolean selected) {
        if (selected) {
            game.addDiceUsed(diceSides);
            return;
        }
        game.removeDiceUsed(diceSides);
    }

    private void addCustomRange() {
        int minValue = (Integer) customMinSpinner.getValue();
        int maxValue = (Integer) customMaxSpinner.getValue();
        int safeMin = Math.min(minValue, maxValue);
        int safeMax = Math.max(minValue, maxValue);
        game.addCustomDiceRange(safeMin, safeMax);
        refreshCustomRanges();
    }

    private void removeSelectedRange() {
        Game.DiceRange selectedRange = customRangeList.getSelectedValue();
        if (selectedRange != null) {
            if (!ConfirmPopup.show(
                mainStage,
                Localization.get("common.remove.confirm"),
                Localization.get("common.remove")
            )) {
                return;
            }
            game.removeCustomDiceRange(selectedRange.getMinValue(), selectedRange.getMaxValue());
            refreshCustomRanges();
            saveGame();
        }
    }

    private void refreshCustomRanges() {
        customRangeModel.clear();
        for (Game.DiceRange range : game.getCustomDiceRanges()) {
            customRangeModel.addElement(range);
        }
    }

    private void saveAndContinue() {
        saveGame();
        mainStage.showContent(new AttributeGenerationStage(mainStage, game));
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
        return StageId.DICE;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
