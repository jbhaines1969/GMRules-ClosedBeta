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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.HPMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring hit point generation rules.
 */
public class HitPointsStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int VALUE_MIN = -1000;
    private static final int VALUE_MAX = 1000;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel methodLabel = new JLabel();
    private final JComboBox<OptionItem> methodDropdown = new JComboBox<>();
    private final JLabel fixedPerLevelLabel = new JLabel();
    private final JSpinner fixedPerLevelSpinner = new JSpinner(new SpinnerNumberModel(5, 0, VALUE_MAX, 1));
    private final JLabel roundingLabel = new JLabel();
    private final JComboBox<OptionItem> roundingDropdown = new JComboBox<>();
    private final JCheckBox conModifierCheck = new JCheckBox();
    private final JCheckBox allowNegativeConCheck = new JCheckBox();
    private final JLabel minPerLevelLabel = new JLabel();
    private final JSpinner minPerLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 0, VALUE_MAX, 1));
    private final JCheckBox firstLevelMaxCheck = new JCheckBox();
    private final JLabel firstLevelBonusLabel = new JLabel();
    private final JSpinner firstLevelBonusSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));

    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel modifierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel firstLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final HPMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public HitPointsStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.method = this.game.getHpMethod();
        configureText();
        configureInputs();
        buildLayout();
        loadFromMethod();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("hp.title"));
        introArea.setText(Localization.get("hp.intro"));
        methodLabel.setText(Localization.get("hp.method.label"));
        fixedPerLevelLabel.setText(Localization.get("hp.fixed_per_level"));
        roundingLabel.setText(Localization.get("hp.average.rounding"));
        conModifierCheck.setText(Localization.get("hp.modifier.apply_con"));
        allowNegativeConCheck.setText(Localization.get("hp.modifier.allow_negative_con"));
        minPerLevelLabel.setText(Localization.get("hp.minimum_per_level"));
        firstLevelMaxCheck.setText(Localization.get("hp.first_level.max"));
        firstLevelBonusLabel.setText(Localization.get("hp.first_level.bonus"));

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

        methodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        modifierPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstLevelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        methodDropdown.addItem(new OptionItem("rolled", Localization.get("hp.method.rolled")));
        methodDropdown.addItem(new OptionItem("average", Localization.get("hp.method.average")));
        methodDropdown.addItem(new OptionItem("fixed", Localization.get("hp.method.fixed")));

        roundingDropdown.addItem(new OptionItem("up", Localization.get("hp.average.rounding.up")));
        roundingDropdown.addItem(new OptionItem("down", Localization.get("hp.average.rounding.down")));
        roundingDropdown.addItem(new OptionItem("nearest", Localization.get("hp.average.rounding.nearest")));
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        methodPanel.add(methodLabel);
        methodPanel.add(methodDropdown);
        methodPanel.add(fixedPerLevelLabel);
        methodPanel.add(fixedPerLevelSpinner);
        methodPanel.add(roundingLabel);
        methodPanel.add(roundingDropdown);
        contentPanel.add(methodPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        modifierPanel.add(conModifierCheck);
        modifierPanel.add(allowNegativeConCheck);
        modifierPanel.add(minPerLevelLabel);
        modifierPanel.add(minPerLevelSpinner);
        contentPanel.add(modifierPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        firstLevelPanel.add(firstLevelMaxCheck);
        firstLevelPanel.add(firstLevelBonusLabel);
        firstLevelPanel.add(firstLevelBonusSpinner);
        contentPanel.add(firstLevelPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        selectOption(methodDropdown, method.getHpGainMethod());
        fixedPerLevelSpinner.setValue(method.getFixedHPPerLevel());
        selectOption(roundingDropdown, method.getAverageRoundingMethod());
        conModifierCheck.setSelected(method.isAppliesConstitutionModifier());
        allowNegativeConCheck.setSelected(method.isAllowNegativeConModifier());
        minPerLevelSpinner.setValue(method.getMinimumHPPerLevel());
        firstLevelMaxCheck.setSelected(method.isFirstLevelMaxHP());
        firstLevelBonusSpinner.setValue(method.getFirstLevelBonusHP());
        updateMethodControls();
        loading = false;
    }

    private void registerActions() {
        methodDropdown.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setHpGainMethod(getSelectedValue(methodDropdown));
            updateMethodControls();
            saveGame();
        });
        fixedPerLevelSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setFixedHPPerLevel((Integer) fixedPerLevelSpinner.getValue());
            saveGame();
        });
        roundingDropdown.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setAverageRoundingMethod(getSelectedValue(roundingDropdown));
            saveGame();
        });
        conModifierCheck.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setAppliesConstitutionModifier(conModifierCheck.isSelected());
            saveGame();
        });
        allowNegativeConCheck.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setAllowNegativeConModifier(allowNegativeConCheck.isSelected());
            saveGame();
        });
        minPerLevelSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setMinimumHPPerLevel((Integer) minPerLevelSpinner.getValue());
            saveGame();
        });
        firstLevelMaxCheck.addActionListener(event -> {
            if (loading) {
                return;
            }
            method.setFirstLevelMaxHP(firstLevelMaxCheck.isSelected());
            saveGame();
        });
        firstLevelBonusSpinner.addChangeListener(event -> {
            if (loading) {
                return;
            }
            method.setFirstLevelBonusHP((Integer) firstLevelBonusSpinner.getValue());
            saveGame();
        });

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.navigateToStage(StageId.ARMOR_CLASS);
        });
    }

    private void updateMethodControls() {
        String methodValue = getSelectedValue(methodDropdown);
        boolean isFixed = "fixed".equals(methodValue);
        boolean isAverage = "average".equals(methodValue);
        fixedPerLevelLabel.setEnabled(isFixed);
        fixedPerLevelSpinner.setEnabled(isFixed);
        roundingLabel.setEnabled(isAverage);
        roundingDropdown.setEnabled(isAverage);
    }

    private void selectOption(JComboBox<OptionItem> combo, String value) {
        String safeValue = Objects.toString(value, "").trim().toLowerCase();
        for (int index = 0; index < combo.getItemCount(); index++) {
            OptionItem item = combo.getItemAt(index);
            if (item.matches(safeValue)) {
                combo.setSelectedIndex(index);
                return;
            }
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private String getSelectedValue(JComboBox<OptionItem> combo) {
        OptionItem item = (OptionItem) combo.getSelectedItem();
        return item.value();
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
        return StageId.HIT_POINTS;
    }

    @Override
    public Game getGame() {
        return game;
    }

    private static final class OptionItem {
        private final String value;
        private final String label;

        private OptionItem(String value, String label) {
            this.value = Objects.toString(value, "").trim().toLowerCase();
            this.label = Objects.toString(label, "");
        }

        private String value() {
            return value;
        }

        private boolean matches(String candidate) {
            return value.equalsIgnoreCase(Objects.toString(candidate, "").trim());
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
