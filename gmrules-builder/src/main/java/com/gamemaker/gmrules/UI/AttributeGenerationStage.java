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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.AttributeGenerationMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for selecting attribute generation method.
 */
public class AttributeGenerationStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final String TYPE_DICE = "dice";
    private static final String TYPE_POINT_BUY = "point_buy";
    private static final String TYPE_STANDARD_ARRAY = "standard_array";
    private static final String TYPE_HYBRID = "hybrid";
    private static final String ARRAY_HYBRID = "hybridStages";
    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel methodLabel = new JLabel();
    private final JCheckBox diceCheck = new JCheckBox();
    private final JCheckBox pointBuyCheck = new JCheckBox();
    private final JCheckBox standardArrayCheck = new JCheckBox();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel optionPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final AttributeGenerationMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public AttributeGenerationStage(MainStage mainStage, Game game) {
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
        titleLabel.setText(Localization.get("attrgen.title"));
        introArea.setText(Localization.get("attrgen.intro"));
        methodLabel.setText(Localization.get("attrgen.type"));
        diceCheck.setText(Localization.get("attrgen.type.dice"));
        pointBuyCheck.setText(Localization.get("attrgen.type.point_buy"));
        standardArrayCheck.setText(Localization.get("attrgen.type.standard_array"));
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
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diceCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        pointBuyCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        standardArrayCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(methodLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.add(standardArrayCheck);
        optionPanel.add(diceCheck);
        optionPanel.add(pointBuyCheck);
        contentPanel.add(optionPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        selectGenerationType(method.getGenerationType());
        loading = false;
    }

    private void registerActions() {
        diceCheck.addActionListener(event -> updateSelectionFromCheckboxes());
        pointBuyCheck.addActionListener(event -> updateSelectionFromCheckboxes());
        standardArrayCheck.addActionListener(event -> updateSelectionFromCheckboxes());

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            StageId nextStage = getNextGenerationStage(method, StageId.ATTRIBUTE_GENERATION);
            mainStage.navigateToStage(nextStage);
        });
    }

    public static List<StageId> buildGenerationStages(AttributeGenerationMethod method) {
        AttributeGenerationMethod safeMethod = Objects.requireNonNullElseGet(
            method,
            () -> new AttributeGenerationMethod("Attribute Generation")
        );
        String safeValue = Objects.toString(safeMethod.getGenerationType(), "").trim().toLowerCase();
        List<String> hybridStages = getHybridStageKeys(safeMethod);
        boolean isHybrid = TYPE_HYBRID.equals(safeValue);
        List<StageId> stages = new ArrayList<>();
        if (TYPE_STANDARD_ARRAY.equals(safeValue) || (isHybrid && includeHybridStage(hybridStages, TYPE_STANDARD_ARRAY))) {
            stages.add(StageId.STANDARD_ARRAY);
        }
        if (TYPE_DICE.equals(safeValue) || (isHybrid && includeHybridStage(hybridStages, TYPE_DICE))) {
            stages.add(StageId.DICE_ROLLING);
        }
        if (TYPE_POINT_BUY.equals(safeValue) || (isHybrid && includeHybridStage(hybridStages, TYPE_POINT_BUY))) {
            stages.add(StageId.POINTS_BUY);
        }
        return stages;
    }

    public static StageId getNextGenerationStage(AttributeGenerationMethod method, StageId currentStage) {
        List<StageId> stages = buildGenerationStages(method);
        if (stages.isEmpty()) {
            return StageId.HIT_POINTS;
        }
        StageId safeStage = Objects.requireNonNullElse(currentStage, StageId.ATTRIBUTE_GENERATION);
        int currentIndex = stages.indexOf(safeStage);
        if (currentIndex < 0) {
            return stages.get(0);
        }
        if (currentIndex + 1 < stages.size()) {
            return stages.get(currentIndex + 1);
        }
        return StageId.HIT_POINTS;
    }

    public static StageId getPreviousGenerationStage(AttributeGenerationMethod method, StageId currentStage) {
        List<StageId> stages = buildGenerationStages(method);
        if (stages.isEmpty()) {
            return StageId.ATTRIBUTE_GENERATION;
        }
        StageId safeStage = Objects.requireNonNullElse(currentStage, StageId.ATTRIBUTE_GENERATION);
        if (safeStage == StageId.HIT_POINTS) {
            return stages.get(stages.size() - 1);
        }
        int currentIndex = stages.indexOf(safeStage);
        if (currentIndex <= 0) {
            return StageId.ATTRIBUTE_GENERATION;
        }
        return stages.get(currentIndex - 1);
    }

    private void updateSelectionFromCheckboxes() {
        if (loading) {
            return;
        }
        List<String> selected = new ArrayList<>();
        if (standardArrayCheck.isSelected()) {
            selected.add(TYPE_STANDARD_ARRAY);
        }
        if (diceCheck.isSelected()) {
            selected.add(TYPE_DICE);
        }
        if (pointBuyCheck.isSelected()) {
            selected.add(TYPE_POINT_BUY);
        }
        int selectedCount = selected.size();
        if (selectedCount == 0) {
            selectGenerationType(method.getGenerationType());
            return;
        }
        if (selectedCount == 1) {
            method.setGenerationType(selected.get(0));
            method.clearArray(ARRAY_HYBRID);
            saveGame();
            return;
        }
        method.setGenerationType(TYPE_HYBRID);
        method.clearArray(ARRAY_HYBRID);
        for (String entry : selected) {
            method.addToArray(ARRAY_HYBRID, entry);
        }
        saveGame();
    }

    private void selectGenerationType(String value) {
        String safeValue = Objects.toString(value, "").trim().toLowerCase();
        if (TYPE_HYBRID.equalsIgnoreCase(safeValue)) {
            List<String> hybridStages = getHybridStageKeys(method);
            boolean includeAll = hybridStages.isEmpty();
            diceCheck.setSelected(includeAll || hybridStages.contains(TYPE_DICE));
            pointBuyCheck.setSelected(includeAll || hybridStages.contains(TYPE_POINT_BUY));
            standardArrayCheck.setSelected(includeAll || hybridStages.contains(TYPE_STANDARD_ARRAY));
            return;
        }
        diceCheck.setSelected(TYPE_DICE.equalsIgnoreCase(safeValue));
        pointBuyCheck.setSelected(TYPE_POINT_BUY.equalsIgnoreCase(safeValue));
        standardArrayCheck.setSelected(TYPE_STANDARD_ARRAY.equalsIgnoreCase(safeValue));
    }

    private static List<String> getHybridStageKeys(AttributeGenerationMethod method) {
        AttributeGenerationMethod safeMethod = Objects.requireNonNullElseGet(
            method,
            () -> new AttributeGenerationMethod("Attribute Generation")
        );
        List<String> rawStages = safeMethod.getArray(ARRAY_HYBRID);
        List<String> resolved = new ArrayList<>();
        for (String stage : rawStages) {
            String normalized = Objects.toString(stage, "").trim().toLowerCase();
            if (normalized.isEmpty()) {
                continue;
            }
            if (!resolved.contains(normalized)) {
                resolved.add(normalized);
            }
        }
        return resolved;
    }

    private static boolean includeHybridStage(List<String> stages, String key) {
        if (stages.isEmpty()) {
            return true;
        }
        return stages.contains(key);
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
        return StageId.ATTRIBUTE_GENERATION;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
