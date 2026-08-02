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
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring dice rolling generation.
 */
public class DiceRollingStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int LIST_VISIBLE_ROWS = 6;
    private static final int DICE_COUNT_MIN = 1;
    private static final int DICE_COUNT_MAX = 100;
    private static final int DICE_SIDES_MAX = 1000;
    private static final int SETS_MAX = 100;
    private static final int REROLL_RESULT_MIN = 0;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel setsLabel = new JLabel();
    private final JLabel setsCountLabel = new JLabel();
    private final JSpinner setsCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, SETS_MAX, 1));

    private final JLabel diceSectionLabel = new JLabel();
    private final JLabel substitutionSectionLabel = new JLabel();
    private final JCheckBox allowSubstitutionCheck = new JCheckBox();
    private final JLabel substitutionValueLabel = new JLabel();
    private final JSpinner substitutionValueSpinner = new JSpinner(new SpinnerNumberModel(14, 0, 100, 1));
    private final JLabel substitutionCountLabel = new JLabel();
    private final JSpinner substitutionCountSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    private final JLabel diceCountLabel = new JLabel();
    private final JLabel diceSidesLabel = new JLabel();
    private final JLabel diceRerollLabel = new JLabel();
    private final JSpinner diceCountSpinner =
            new JSpinner(new SpinnerNumberModel(3, DICE_COUNT_MIN, DICE_COUNT_MAX, 1));
    private final JComboBox<Integer> diceSidesDropdown = new JComboBox<>();
    private final Integer diceSidesPlaceholder = 0;
    private final JSpinner diceRerollSpinner =
            new JSpinner(new SpinnerNumberModel(0, REROLL_RESULT_MIN, DICE_SIDES_MAX, 1));
    private final JCheckBox diceDropLowestCheck = new JCheckBox();
    private final JButton addDiceTermButton = new JButton();
    private final DefaultListModel<AttributeGenerationMethod.DiceTerm> diceTermModel = new DefaultListModel<>();
    private final JList<AttributeGenerationMethod.DiceTerm> diceTermList = new JList<>(diceTermModel);
    private final JScrollPane diceTermScroll = new JScrollPane(diceTermList);
    private final JButton removeDiceTermButton = new JButton();

    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel setsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel diceInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel substitutionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel diceListPanel = new JPanel(new BorderLayout());
    private final MainStage mainStage;
    private final Game game;
    private final AttributeGenerationMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public DiceRollingStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.method = this.game.getAttributeGenerationMethod();
        configureText();
        configureInputs();
        populateDiceSides();
        buildLayout();
        loadFromMethod();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("attrgen.dice.title"));
        introArea.setText(Localization.get("attrgen.dice.intro"));
        setsLabel.setText(Localization.get("attrgen.sets.section"));
        setsCountLabel.setText(Localization.get("attrgen.sets.count"));

        diceSectionLabel.setText(Localization.get("attrgen.dice.section"));
        substitutionSectionLabel.setText(Localization.get("attrgen.dice.substitution.section"));
        allowSubstitutionCheck.setText(Localization.get("attrgen.dice.substitution.enable"));
        substitutionValueLabel.setText(Localization.get("attrgen.dice.substitution.value"));
        substitutionCountLabel.setText(Localization.get("attrgen.dice.substitution.count"));
        diceCountLabel.setText(Localization.get("attrgen.dice.count"));
        diceSidesLabel.setText(Localization.get("attrgen.dice.sides"));
        diceRerollLabel.setText(Localization.get("attrgen.dice.reroll"));
        diceDropLowestCheck.setText(Localization.get("attrgen.dice.drop_lowest"));
        addDiceTermButton.setText(Localization.get("attrgen.dice.add"));
        removeDiceTermButton.setText(Localization.get("common.remove.selected"));

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
        setsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diceSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        setsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        substitutionSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        substitutionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diceInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diceListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diceDropLowestCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        diceTermList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        diceTermList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        diceTermList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value.getNotation());
            return label;
        });

        diceSidesDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            int sides = value == null ? 0 : (Integer) value;
            if (sides <= 0) {
                label.setText(Localization.get("common.die.select"));
            } else {
                label.setText("d" + sides);
            }
            return label;
        });

        Dimension diceSize = diceSidesDropdown.getPreferredSize();
        diceSidesDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, diceSize.height));
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(setsLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        setsPanel.add(setsCountLabel);
        setsPanel.add(setsCountSpinner);
        contentPanel.add(setsPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(substitutionSectionLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        substitutionPanel.add(allowSubstitutionCheck);
        substitutionPanel.add(substitutionValueLabel);
        substitutionPanel.add(substitutionValueSpinner);
        substitutionPanel.add(substitutionCountLabel);
        substitutionPanel.add(substitutionCountSpinner);
        contentPanel.add(substitutionPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(diceSectionLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        diceInputPanel.add(diceCountLabel);
        diceInputPanel.add(diceCountSpinner);
        diceInputPanel.add(diceSidesLabel);
        diceInputPanel.add(diceSidesDropdown);
        diceInputPanel.add(diceRerollLabel);
        diceInputPanel.add(diceRerollSpinner);
        diceInputPanel.add(diceDropLowestCheck);
        diceInputPanel.add(addDiceTermButton);
        contentPanel.add(diceInputPanel);
        contentPanel.add(Box.createVerticalStrut(6));

        diceListPanel.add(diceTermScroll, BorderLayout.CENTER);
        diceListPanel.add(removeDiceTermButton, BorderLayout.SOUTH);
        contentPanel.add(diceListPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        setsCountSpinner.setValue(method.getNumberOfSets());
        allowSubstitutionCheck.setSelected(method.isAllowDiceSubstitution());
        substitutionValueSpinner.setValue(method.getDiceSubstitutionValue());
        substitutionCountSpinner.setValue(method.getMaxDiceSubstitutions());
        refreshDiceTerms();
        loading = false;
    }

    private void registerActions() {
        setsCountSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setNumberOfSets((Integer) setsCountSpinner.getValue());
            saveGame();
        });

        allowSubstitutionCheck.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setAllowDiceSubstitution(allowSubstitutionCheck.isSelected());
            saveGame();
        });
        substitutionValueSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setDiceSubstitutionValue((Integer) substitutionValueSpinner.getValue());
            saveGame();
        });
        substitutionCountSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMaxDiceSubstitutions((Integer) substitutionCountSpinner.getValue());
            saveGame();
        });

        addDiceTermButton.addActionListener(event -> addDiceTerm());
        removeDiceTermButton.addActionListener(event -> removeSelectedDiceTerm());
        diceSidesDropdown.addActionListener(event -> updateRerollMaximum());

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            StageId nextStage = AttributeGenerationStage.getNextGenerationStage(method, StageId.DICE_ROLLING);
            mainStage.navigateToStage(nextStage);
        });
    }

    private void addDiceTerm() {
        int count = (Integer) diceCountSpinner.getValue();
        int sides = resolveDiceSides();
        if (sides <= 0) {
            PopupAlert.show(mainStage, Localization.get("common.die.required"));
            return;
        }
        AttributeGenerationMethod.DiceTerm term = new AttributeGenerationMethod.DiceTerm(count, sides);
        int rerollResult = ((Number) diceRerollSpinner.getValue()).intValue();
        if (diceDropLowestCheck.isSelected()) {
            term.setDropLowest(1);
        }
        int maxFace = Math.min(rerollResult - 1, sides);
        if (maxFace > 0) {
            ArrayList<Integer> ignoredFaces = new ArrayList<>();
            for (int face = 1; face <= maxFace; face++) {
                ignoredFaces.add(face);
            }
            term.setIgnoredFaces(ignoredFaces);
        }
        method.addDiceTerm(term);
        refreshDiceTerms();
        saveGame();
    }

    private void removeSelectedDiceTerm() {
        int index = diceTermList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        List<AttributeGenerationMethod.DiceTerm> terms = method.getDiceTerms();
        if (index >= terms.size()) {
            return;
        }
        terms.remove(index);
        method.setDiceTerms(terms);
        refreshDiceTerms();
        saveGame();
    }

    private void refreshDiceTerms() {
        diceTermModel.clear();
        for (AttributeGenerationMethod.DiceTerm term : method.getDiceTerms()) {
            diceTermModel.addElement(term);
        }
    }

    private void populateDiceSides() {
        diceSidesDropdown.removeAllItems();
        diceSidesDropdown.addItem(diceSidesPlaceholder);
        List<Integer> diceUsed = game.getDiceUsed();
        List<Integer> sorted = new ArrayList<>(diceUsed);
        sorted.sort(Integer::compareTo);
        for (Integer sides : sorted) {
            if (sides != null && sides > 0) {
                diceSidesDropdown.addItem(sides);
            }
        }
        selectDiceSides(6);
        updateRerollMaximum();
    }

    private void selectDiceSides(int sides) {
        if (sides <= 0) {
            diceSidesDropdown.setSelectedIndex(0);
            return;
        }
        for (int index = 0; index < diceSidesDropdown.getItemCount(); index++) {
            Integer value = diceSidesDropdown.getItemAt(index);
            if (value != null && value == sides) {
                diceSidesDropdown.setSelectedIndex(index);
                return;
            }
        }
        if (diceSidesDropdown.getItemCount() > 1) {
            diceSidesDropdown.setSelectedIndex(1);
        } else {
            diceSidesDropdown.setSelectedIndex(0);
        }
    }

    private int resolveDiceSides() {
        Integer sides = (Integer) diceSidesDropdown.getSelectedItem();
        return sides == null ? 0 : sides;
    }

    private void updateRerollMaximum() {
        SpinnerNumberModel model = (SpinnerNumberModel) diceRerollSpinner.getModel();
        int max = resolveDiceSides();
        if (max <= 0) {
            max = DICE_SIDES_MAX;
        }
        model.setMaximum(max);
        int current = ((Number) model.getNumber()).intValue();
        if (current > max) {
            model.setValue(max);
        }
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
        return StageId.DICE_ROLLING;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
