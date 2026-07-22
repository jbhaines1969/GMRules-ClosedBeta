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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameMechanics.ArmorClassMethod;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring armor class calculation methods.
 */
public class ArmorClassStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel baseLabel = new JLabel();
    private final javax.swing.JSpinner baseSpinner = new javax.swing.JSpinner(
        new javax.swing.SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 1)
    );
    private final JLabel abilityAttributeLabel = new JLabel();
    private final JComboBox<Attribute> abilityAttributeDropdown = new JComboBox<>();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final ArmorClassMethod method;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public ArmorClassStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        this.method = this.game.getArmorClassMethod();
        configureText();
        configureInputs();
        buildLayout();
        loadFromMethod();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("armorclass.title"));
        introArea.setText(Localization.get("armorclass.intro"));
        baseLabel.setText(Localization.get("armorclass.base"));
        abilityAttributeLabel.setText(Localization.get("armorclass.ability.attribute"));
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
        baseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        baseSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        abilityAttributeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        abilityAttributeDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(baseLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(baseSpinner);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(abilityAttributeLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(abilityAttributeDropdown);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFromMethod() {
        loading = true;
        baseSpinner.setValue(Math.max(0, method.getBaseArmorClass()));
        populateAbilityAttributes();
        selectAbilityAttribute(method.getAcAbilityAttributeId());
        loading = false;
    }

    private void registerActions() {
        baseSpinner.addChangeListener(event -> updateSelection());
        abilityAttributeDropdown.addActionListener(event -> updateSelection());

        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.navigateToStage(StageId.CURRENCY);
        });
    }

    private void updateSelection() {
        if (loading) {
            return;
        }
        method.setBaseArmorClass((Integer) baseSpinner.getValue());
        Attribute selected = (Attribute) abilityAttributeDropdown.getSelectedItem();
        String attributeId = selected == null ? "" : Objects.toString(selected.getId(), "").trim();
        method.setAcAbilityAttributeId(attributeId);
        saveGame();
    }

    private void populateAbilityAttributes() {
        abilityAttributeDropdown.removeAllItems();
        Attribute placeholder = new Attribute("");
        placeholder.setName(Localization.get("common.none"));
        abilityAttributeDropdown.addItem(placeholder);
        for (Attribute attribute : game.getElementRegistry(ElementRegistryKey.ATTRIBUTES).getAll()) {
            if (attribute != null) {
                abilityAttributeDropdown.addItem(attribute);
            }
        }
    }

    private void selectAbilityAttribute(String attributeId) {
        String safeId = Objects.toString(attributeId, "").trim();
        if (safeId.isEmpty()) {
            abilityAttributeDropdown.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < abilityAttributeDropdown.getItemCount(); i++) {
            Attribute attribute = abilityAttributeDropdown.getItemAt(i);
            if (attribute != null && safeId.equals(Objects.toString(attribute.getId(), "").trim())) {
                abilityAttributeDropdown.setSelectedIndex(i);
                return;
            }
        }
        abilityAttributeDropdown.setSelectedIndex(0);
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
        return StageId.ARMOR_CLASS;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
