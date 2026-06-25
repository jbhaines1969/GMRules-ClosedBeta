/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.gamemaker.gmrules.AtomicElements.Attribute;
import com.gamemaker.gmrules.AtomicElements.AttributeType;

/**
 * Attribute-specific fields for the element editor popup.
 */
public class AttributeEditPanel extends JPanel {

    // *** MEMBERS ***
    private static final int VALUE_MIN = -1000;
    private static final int VALUE_MAX = 1000;
    private static final int MODIFIER_MIN = -1000;
    private static final int MODIFIER_MAX = 1000;

    private final JLabel typeLabel = new JLabel();
    private final JComboBox<AttributeType> typeDropdown = new JComboBox<>();
    private final JLabel minValueLabel = new JLabel();
    private final JSpinner minValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel maxValueLabel = new JLabel();
    private final JSpinner maxValueSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));

    private final JLabel modifiersLabel = new JLabel();
    private final JLabel modifierScoreLabel = new JLabel();
    private final JSpinner modifierScoreSpinner = new JSpinner(new SpinnerNumberModel(0.0, VALUE_MIN, VALUE_MAX, 1.0));
    private final JLabel modifierValueLabel = new JLabel();
    private final JSpinner modifierValueSpinner = new JSpinner(new SpinnerNumberModel(0.0, MODIFIER_MIN, MODIFIER_MAX, 1.0));
    private final JButton addModifierButton = new JButton();
    private final JButton removeModifierButton = new JButton();
    private final DefaultListModel<ModifierEntry> modifierModel = new DefaultListModel<>();
    private final JList<ModifierEntry> modifierList = new JList<>(modifierModel);
    private final JScrollPane modifierScroll = new JScrollPane(modifierList);

    private final JLabel bonusesLabel = new JLabel();
    private final JLabel bonusThresholdLabel = new JLabel();
    private final JSpinner bonusThresholdSpinner = new JSpinner(new SpinnerNumberModel(0, VALUE_MIN, VALUE_MAX, 1));
    private final JLabel bonusEffectLabel = new JLabel();
    private final JTextField bonusEffectField = new JTextField();
    private final JButton addBonusButton = new JButton();
    private final JButton removeBonusButton = new JButton();
    private final DefaultListModel<BonusEntry> bonusModel = new DefaultListModel<>();
    private final JList<BonusEntry> bonusList = new JList<>(bonusModel);
    private final JScrollPane bonusScroll = new JScrollPane(bonusList);

    private final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel modifierInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel modifierListPanel = new JPanel(new BorderLayout());
    private final JPanel bonusInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel bonusListPanel = new JPanel(new BorderLayout());
    private final MainStage mainStage;

    // *** CONSTRUCTORS ***
    public AttributeEditPanel(
            MainStage mainStage,
            List<AttributeType> types,
            String noneLabel,
            Attribute attribute
    ) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        configureText(noneLabel);
        configureInputs();
        populateTypes(types, noneLabel, attribute);
        loadAttribute(attribute);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public String getSelectedTypeKey() {
        AttributeType selected = (AttributeType) typeDropdown.getSelectedItem();
        return selected == null ? "" : Objects.toString(selected.getKey(), "");
    }

    public int getMinValue() {
        return (Integer) minValueSpinner.getValue();
    }

    public int getMaxValue() {
        return (Integer) maxValueSpinner.getValue();
    }

    public Map<Float, Float> getModifierMap() {
        Map<Float, Float> map = new LinkedHashMap<>();
        for (int index = 0; index < modifierModel.getSize(); index++) {
            ModifierEntry entry = modifierModel.getElementAt(index);
            map.put(entry.score, entry.modifier);
        }
        return map;
    }

    public Map<Integer, ArrayList<String>> getScoreBonuses() {
        Map<Integer, ArrayList<String>> map = new LinkedHashMap<>();
        for (int index = 0; index < bonusModel.getSize(); index++) {
            BonusEntry entry = bonusModel.getElementAt(index);
            map.computeIfAbsent(entry.threshold, k -> new ArrayList<>()).add(entry.effect);
        }
        return map;
    }

    private void configureText(String noneLabel) {
        typeLabel.setText(Localization.get("attributes.edit.type"));
        minValueLabel.setText(Localization.get("attributes.edit.min_value"));
        maxValueLabel.setText(Localization.get("attributes.edit.max_value"));
        modifiersLabel.setText(Localization.get("attributes.edit.modifiers"));
        modifierScoreLabel.setText(Localization.get("attributes.edit.modifier.score"));
        modifierValueLabel.setText(Localization.get("attributes.edit.modifier.value"));
        addModifierButton.setText(Localization.get("attributes.edit.modifier.add"));
        removeModifierButton.setText(Localization.get("common.remove.selected"));
        bonusesLabel.setText(Localization.get("attributes.edit.bonuses"));
        bonusThresholdLabel.setText(Localization.get("attributes.edit.bonus.threshold"));
        bonusEffectLabel.setText(Localization.get("attributes.edit.bonus.effect"));
        addBonusButton.setText(Localization.get("attributes.edit.bonus.add"));
        removeBonusButton.setText(Localization.get("common.remove.selected"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        typeDropdown.setAlignmentX(LEFT_ALIGNMENT);
        typePanel.setAlignmentX(LEFT_ALIGNMENT);
        rangePanel.setAlignmentX(LEFT_ALIGNMENT);
        modifierInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        modifierListPanel.setAlignmentX(LEFT_ALIGNMENT);
        bonusInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        bonusListPanel.setAlignmentX(LEFT_ALIGNMENT);

        modifierList.setVisibleRowCount(6);
        modifierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bonusList.setVisibleRowCount(6);
        bonusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bonusEffectField.setColumns(18);
        EntryInputHandler.selectAllOnFocus(bonusEffectField);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        typeDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            AttributeType type = (AttributeType) value;
            label.setText(type == null ? "" : type.getDisplayName());
            return label;
        });

        modifierList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value.toString());
            return label;
        });

        bonusList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            label.setText(value.toString());
            return label;
        });

        Dimension typeSize = typeDropdown.getPreferredSize();
        typeDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, typeSize.height));
    }

    private void populateTypes(List<AttributeType> types, String noneLabel, Attribute attribute) {
        typeDropdown.removeAllItems();
        AttributeType placeholder = new AttributeType("", "", "", true);
        placeholder.setName(Objects.toString(noneLabel, ""));
        typeDropdown.addItem(placeholder);
        List<AttributeType> safeTypes = types == null ? List.of() : types;
        for (AttributeType type : safeTypes) {
            typeDropdown.addItem(type);
        }
        String selectedKey = Objects.toString(attribute.getType(), "");
        selectTypeByKey(selectedKey);
    }

    private void selectTypeByKey(String typeKey) {
        String safeKey = Objects.toString(typeKey, "");
        for (int index = 0; index < typeDropdown.getItemCount(); index++) {
            AttributeType type = typeDropdown.getItemAt(index);
            if (type == null) {
                continue;
            }
            if (Objects.toString(type.getKey(), "").equals(safeKey)) {
                typeDropdown.setSelectedIndex(index);
                return;
            }
        }
        typeDropdown.setSelectedIndex(0);
    }

    private void loadAttribute(Attribute attribute) {
        Attribute safeAttribute = Objects.requireNonNullElse(attribute, new Attribute(""));
        minValueSpinner.setValue(safeAttribute.getMinValue());
        maxValueSpinner.setValue(safeAttribute.getMaxValue());

        modifierModel.clear();
        for (Map.Entry<Float, Float> entry : safeAttribute.getModifierMap().entrySet()) {
            modifierModel.addElement(new ModifierEntry(entry.getKey(), entry.getValue()));
        }

        bonusModel.clear();
        for (Map.Entry<Integer, ArrayList<String>> entry : safeAttribute.getAllScoreBonuses().entrySet()) {
            int threshold = entry.getKey();
            for (String effect : entry.getValue()) {
                String safeEffect = Objects.toString(effect, "").trim();
                if (!safeEffect.isEmpty()) {
                    bonusModel.addElement(new BonusEntry(threshold, safeEffect));
                }
            }
        }
    }

    private void buildLayout() {
        typePanel.add(typeLabel);
        typePanel.add(typeDropdown);
        add(typePanel);
        add(Box.createVerticalStrut(8));

        rangePanel.add(minValueLabel);
        rangePanel.add(minValueSpinner);
        rangePanel.add(maxValueLabel);
        rangePanel.add(maxValueSpinner);
        add(rangePanel);
        add(Box.createVerticalStrut(12));

        add(modifiersLabel);
        add(Box.createVerticalStrut(6));
        modifierInputPanel.add(modifierScoreLabel);
        modifierInputPanel.add(modifierScoreSpinner);
        modifierInputPanel.add(modifierValueLabel);
        modifierInputPanel.add(modifierValueSpinner);
        modifierInputPanel.add(addModifierButton);
        add(modifierInputPanel);
        add(Box.createVerticalStrut(6));
        modifierListPanel.add(modifierScroll, BorderLayout.CENTER);
        modifierListPanel.add(removeModifierButton, BorderLayout.SOUTH);
        add(modifierListPanel);
        add(Box.createVerticalStrut(12));

        add(bonusesLabel);
        add(Box.createVerticalStrut(6));
        bonusInputPanel.add(bonusThresholdLabel);
        bonusInputPanel.add(bonusThresholdSpinner);
        bonusInputPanel.add(bonusEffectLabel);
        bonusInputPanel.add(bonusEffectField);
        bonusInputPanel.add(addBonusButton);
        add(bonusInputPanel);
        add(Box.createVerticalStrut(6));
        bonusListPanel.add(bonusScroll, BorderLayout.CENTER);
        bonusListPanel.add(removeBonusButton, BorderLayout.SOUTH);
        add(bonusListPanel);
    }

    private void registerActions() {
        addModifierButton.addActionListener(event -> addModifier());
        removeModifierButton.addActionListener(event -> removeModifier());
        addBonusButton.addActionListener(event -> addBonus());
        removeBonusButton.addActionListener(event -> removeBonus());
    }

    private void addModifier() {
        double score = ((Number) modifierScoreSpinner.getValue()).doubleValue();
        double value = ((Number) modifierValueSpinner.getValue()).doubleValue();
        modifierModel.addElement(new ModifierEntry((float) score, (float) value));
    }

    private void removeModifier() {
        int selectedIndex = modifierList.getSelectedIndex();
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
        modifierModel.remove(selectedIndex);
    }

    private void addBonus() {
        int threshold = (Integer) bonusThresholdSpinner.getValue();
        String effect = EntryInputHandler.resolveText(bonusEffectField);
        if (effect.isEmpty()) {
            return;
        }
        bonusModel.addElement(new BonusEntry(threshold, effect));
        bonusEffectField.setText("");
    }

    private void removeBonus() {
        int selectedIndex = bonusList.getSelectedIndex();
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
        bonusModel.remove(selectedIndex);
    }

    private static final class ModifierEntry {

        // *** MEMBERS ***
        private final float score;
        private final float modifier;

        // *** CONSTRUCTORS ***
        private ModifierEntry(float score, float modifier) {
            this.score = score;
            this.modifier = modifier;
        }

        // *** METHODS ***
        @Override
        public String toString() {
            return score + " -> " + modifier;
        }
    }

    private static final class BonusEntry {

        // *** MEMBERS ***
        private final int threshold;
        private final String effect;

        // *** CONSTRUCTORS ***
        private BonusEntry(int threshold, String effect) {
            this.threshold = threshold;
            this.effect = Objects.toString(effect, "");
        }

        // *** METHODS ***
        @Override
        public String toString() {
            return threshold + " : " + effect;
        }
    }
}
