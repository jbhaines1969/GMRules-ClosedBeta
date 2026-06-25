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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring point buy rules.
 */
public class PointsBuyStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int VALUE_MIN = -1000;
    private static final int VALUE_MAX = 1000;
    private static final int POINTS_MAX = 10000;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel basePointsLabel = new JLabel();
    private final JSpinner basePointsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, POINTS_MAX, 1));
    private final JLabel minValueLabel = new JLabel();
    private final JSpinner minValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel maxValueLabel = new JLabel();
    private final JSpinner maxValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel maxPostRacialLabel = new JLabel();
    private final JSpinner maxPostRacialSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel minPointsToSpendLabel = new JLabel();
    private final JSpinner minPointsToSpendSpinner = new JSpinner(new SpinnerNumberModel(0, 0, POINTS_MAX, 1));
    private final JCheckBox allowNegativeCheck = new JCheckBox();

    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel pointBuyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final AttributeGenerationMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public PointsBuyStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.method = this.game.getAttributeGenerationMethod();
        configureText();
        configureInputs();
        buildLayout();
        loadFromMethod();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("attrgen.point.title"));
        introArea.setText(Localization.get("attrgen.point.intro"));
        basePointsLabel.setText(Localization.get("attrgen.point.base_points"));
        minValueLabel.setText(Localization.get("attrgen.point.min_value"));
        maxValueLabel.setText(Localization.get("attrgen.point.max_value"));
        maxPostRacialLabel.setText(Localization.get("attrgen.point.max_post_racial"));
        minPointsToSpendLabel.setText(Localization.get("attrgen.point.min_points_spend"));
        allowNegativeCheck.setText(Localization.get("attrgen.point.allow_negative"));

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
        pointBuyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        pointBuyPanel.add(basePointsLabel);
        pointBuyPanel.add(basePointsSpinner);
        pointBuyPanel.add(minValueLabel);
        pointBuyPanel.add(minValueSpinner);
        pointBuyPanel.add(maxValueLabel);
        pointBuyPanel.add(maxValueSpinner);
        pointBuyPanel.add(maxPostRacialLabel);
        pointBuyPanel.add(maxPostRacialSpinner);
        pointBuyPanel.add(minPointsToSpendLabel);
        pointBuyPanel.add(minPointsToSpendSpinner);
        contentPanel.add(pointBuyPanel);
        contentPanel.add(allowNegativeCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        basePointsSpinner.setValue(method.getBasePoints());
        minValueSpinner.setValue(method.getMinAttributeValue());
        maxValueSpinner.setValue(method.getMaxAttributeValue());
        maxPostRacialSpinner.setValue(method.getMaxAttributeValuePostRacial());
        minPointsToSpendSpinner.setValue(method.getMinimumPointsToSpend());
        allowNegativeCheck.setSelected(method.isAllowNegativeAttributes());
        loading = false;
    }

    private void registerActions() {
        basePointsSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setBasePoints((Integer) basePointsSpinner.getValue());
            saveGame();
        });

        minValueSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMinAttributeValue((Integer) minValueSpinner.getValue());
            saveGame();
        });

        maxValueSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMaxAttributeValue((Integer) maxValueSpinner.getValue());
            saveGame();
        });

        maxPostRacialSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMaxAttributeValuePostRacial((Integer) maxPostRacialSpinner.getValue());
            saveGame();
        });

        minPointsToSpendSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMinimumPointsToSpend((Integer) minPointsToSpendSpinner.getValue());
            saveGame();
        });

        allowNegativeCheck.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setAllowNegativeAttributes(allowNegativeCheck.isSelected());
            saveGame();
        });

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            StageId nextStage = AttributeGenerationStage.getNextGenerationStage(method, StageId.POINTS_BUY);
            mainStage.navigateToStage(nextStage);
        });
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
        return StageId.POINTS_BUY;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
