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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.gamemaker.gmrules.DiceSpec;
import com.gamemaker.gmrules.AtomicElements.EffectType;
import com.gamemaker.gmrules.AtomicElements.EffectTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.GameElements.Weapon;
import com.gamemaker.gmrules.SupportElements.Effect;

/**
 * Weapon-specific fields for the element editor popup.
 */
public class WeaponEditPanel extends JPanel {

// *** MEMBERS ***
    private static final int LIST_VISIBLE_ROWS = 6;

    private final JLabel damageLabel = new JLabel();
    private final JLabel damageCountLabel = new JLabel();
    private final JSpinner damageCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JLabel damageSidesLabel = new JLabel();
    private final JSpinner damageSidesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JLabel damageModifierLabel = new JLabel();
    private final JSpinner damageModifierSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
    private final JPanel damageDicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JLabel weightValueLabel = new JLabel();
    private final JSpinner weightValueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JLabel weightUnitLabel = new JLabel();
    private final JComboBox<String> weightUnitDropdown = new JComboBox<>();
    private final JLabel effectsLabel = new JLabel();
    private final JComboBox<Effect> effectDropdown = new JComboBox<>();
    private final Effect effectPlaceholder = new Effect("");
    private final EffectType effectTypePlaceholder = new EffectType("");
    private final JButton addEffectButton = new JButton();
    private final JButton createEffectButton = new JButton();
    private final JButton removeEffectButton = new JButton();
    private final DefaultListModel<Effect> effectModel = new DefaultListModel<>();
    private final JList<Effect> effectList = new JList<>(effectModel);
    private final JScrollPane effectScroll = new JScrollPane(effectList);

    private final JPanel effectsInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel effectsListPanel = new JPanel(new BorderLayout());
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

// *** CONSTRUCTORS ***
    public WeaponEditPanel(MainStage mainStage, Game game, Weapon weapon) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        populateEffects();
        populateWeightUnits("");
        loadWeapon(weapon);
        buildLayout();
        registerActions();
    }

// *** METHODS ***
    public void applyToWeapon(Weapon weapon) {
        Weapon safeWeapon = Objects.requireNonNullElse(weapon, new Weapon(""));
        safeWeapon.setDamageDiceCount((Integer) damageCountSpinner.getValue());
        safeWeapon.setDamageDiceSides((Integer) damageSidesSpinner.getValue());
        safeWeapon.setDamageDiceModifier((Integer) damageModifierSpinner.getValue());
        safeWeapon.setWeight(Math.max(0, (Integer) weightValueSpinner.getValue()));
        safeWeapon.setWeightUnit(resolveWeightUnitSelection());
        safeWeapon.clearArray("effects");
        for (int index = 0; index < effectModel.getSize(); index++) {
            Effect effect = effectModel.getElementAt(index);
            if (effect != null) {
                safeWeapon.addToArray("effects", effect);
            }
        }
    }

    private void configureText() {
        damageLabel.setText(Localization.get("weapons.damage"));
        damageCountLabel.setText(Localization.get("attrgen.dice.count"));
        damageSidesLabel.setText(Localization.get("attrgen.dice.sides"));
        damageModifierLabel.setText(Localization.get("common.modifier"));
        weightValueLabel.setText(Localization.get("common.weight"));
        weightUnitLabel.setText(Localization.get("common.weight.unit"));
        effectsLabel.setText(Localization.get("skills.effects"));
        addEffectButton.setText(Localization.get("skills.effects.add"));
        createEffectButton.setText(Localization.get("skills.effects.create"));
        removeEffectButton.setText(Localization.get("common.remove.selected"));
        effectPlaceholder.setName(Localization.get("skills.effects.select"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        effectsInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        effectsListPanel.setAlignmentX(LEFT_ALIGNMENT);

        damageLabel.setAlignmentX(LEFT_ALIGNMENT);
        damageDicePanel.setAlignmentX(LEFT_ALIGNMENT);
        Dimension spinnerSize = damageCountSpinner.getPreferredSize();
        damageCountSpinner.setMaximumSize(new Dimension(120, spinnerSize.height));
        damageSidesSpinner.setMaximumSize(new Dimension(120, spinnerSize.height));
        damageModifierSpinner.setMaximumSize(new Dimension(120, spinnerSize.height));

        weightValueLabel.setAlignmentX(LEFT_ALIGNMENT);
        weightValueSpinner.setAlignmentX(LEFT_ALIGNMENT);
        Dimension weightSize = weightValueSpinner.getPreferredSize();
        weightValueSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, weightSize.height));
        weightUnitLabel.setAlignmentX(LEFT_ALIGNMENT);
        weightUnitDropdown.setAlignmentX(LEFT_ALIGNMENT);
        Dimension unitSize = weightUnitDropdown.getPreferredSize();
        weightUnitDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, unitSize.height));

        effectList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        effectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        effectDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Effect effect = (Effect) value;
            label.setText(effect == null ? "" : effect.getDisplayName());
            return label;
        });
        effectList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Effect effect = (Effect) value;
            label.setText(effect == null ? "" : effect.getDisplayName());
            return label;
        });
    }

    private void populateEffects() {
        effectDropdown.removeAllItems();
        effectDropdown.addItem(effectPlaceholder);
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        List<Effect> effects = registry.getAll();
        effects.sort(Comparator.comparing(
            effect -> Objects.toString(effect.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Effect effect : effects) {
            effectDropdown.addItem(effect);
        }
    }

    private void loadWeapon(Weapon weapon) {
        Weapon safeWeapon = Objects.requireNonNullElse(weapon, new Weapon(""));
        int count = Math.max(0, safeWeapon.getDamageDiceCount());
        int sides = Math.max(0, safeWeapon.getDamageDiceSides());
        int modifier = safeWeapon.getDamageDiceModifier();
        if (count <= 0 || sides <= 0) {
            DiceSpec parsed = DiceSpec.parseSimpleNotation(safeWeapon.getDamageRoll());
            if (parsed.isDefined()) {
                count = parsed.getCount();
                sides = parsed.getSides();
                modifier = parsed.getModifier();
            }
        }
        damageCountSpinner.setValue(count);
        damageSidesSpinner.setValue(sides);
        damageModifierSpinner.setValue(modifier);
        weightValueSpinner.setValue(Math.max(0, (int) Math.round(safeWeapon.getWeight())));
        populateWeightUnits(safeWeapon.getWeightUnit());
        effectModel.clear();
        List<Effect> effects = safeWeapon.getObjectArray("effects");
        if (effects == null) {
            return;
        }
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        for (Effect effect : effects) {
            Effect resolved = resolveEffect(effect, registry);
            if (resolved != null && !containsEffect(resolved)) {
                effectModel.addElement(resolved);
            }
        }
    }

    private Effect resolveEffect(Effect effect, ElementRegistry<Effect> registry) {
        if (effect == null) {
            return null;
        }
        String id = Objects.toString(effect.getId(), "");
        if (!id.isEmpty()) {
            Effect resolved = registry.getById(id);
            if (resolved != null) {
                return resolved;
            }
        }
        return effect;
    }

    private boolean containsEffect(Effect effect) {
        String id = Objects.toString(effect.getId(), "");
        for (int index = 0; index < effectModel.getSize(); index++) {
            Effect existing = effectModel.getElementAt(index);
            if (existing == null) {
                continue;
            }
            if (!id.isEmpty() && id.equals(existing.getId())) {
                return true;
            }
            if (id.isEmpty() && existing == effect) {
                return true;
            }
        }
        return false;
    }

    private void buildLayout() {
        add(damageLabel);
        add(Box.createVerticalStrut(6));
        damageDicePanel.add(damageCountLabel);
        damageDicePanel.add(damageCountSpinner);
        damageDicePanel.add(damageSidesLabel);
        damageDicePanel.add(damageSidesSpinner);
        damageDicePanel.add(damageModifierLabel);
        damageDicePanel.add(damageModifierSpinner);
        add(damageDicePanel);
        add(Box.createVerticalStrut(12));
        add(weightValueLabel);
        add(Box.createVerticalStrut(6));
        add(weightValueSpinner);
        add(Box.createVerticalStrut(12));
        add(weightUnitLabel);
        add(Box.createVerticalStrut(6));
        add(weightUnitDropdown);
        add(Box.createVerticalStrut(12));
        add(effectsLabel);
        add(Box.createVerticalStrut(6));
        effectsInputPanel.add(effectDropdown);
        effectsInputPanel.add(addEffectButton);
        effectsInputPanel.add(createEffectButton);
        add(effectsInputPanel);
        effectsListPanel.add(effectScroll, BorderLayout.CENTER);
        effectsListPanel.add(removeEffectButton, BorderLayout.SOUTH);
        add(effectsListPanel);
    }

    private void registerActions() {
        addEffectButton.addActionListener(event -> addSelectedEffect());
        createEffectButton.addActionListener(event -> createEffect());
        removeEffectButton.addActionListener(event -> removeSelectedEffect());
    }

    private void addSelectedEffect() {
        Effect selected = (Effect) effectDropdown.getSelectedItem();
        if (selected == null || selected == effectPlaceholder) {
            return;
        }
        if (!containsEffect(selected)) {
            effectModel.addElement(selected);
        }
    }

    private void removeSelectedEffect() {
        int index = effectList.getSelectedIndex();
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
        effectModel.remove(index);
    }

    private void createEffect() {
        DefaultListModel<String> typeModel = new DefaultListModel<>();
        JPanel typePanel = buildEffectTypePanel(typeModel);
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("skills.effects.create"),
            "",
            "",
            typePanel
        );
        if (!result.isConfirmed()) {
            return;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        ElementRegistry<Effect> registry = game.getElementRegistry(ElementRegistryKey.EFFECTS);
        if (registry.hasName(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return;
        }
        Effect effect = new Effect(name, result.getDescription());
        effect.setEffectTypeKeys(collectEffectTypeKeys(typeModel));
        registry.add(effect);
        populateEffects();
        if (!containsEffect(effect)) {
            effectModel.addElement(effect);
        }
        saveGame();
    }

    private JPanel buildEffectTypePanel(DefaultListModel<String> model) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(Localization.get("effects.type"));
        label.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<EffectType> dropdown = new JComboBox<>();
        populateEffectTypeDropdown(dropdown);
        applyEffectTypeDropdownRenderer(dropdown);
        dropdown.setAlignmentX(LEFT_ALIGNMENT);

        JButton addButton = new JButton(Localization.get("effects.type.add"));
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        JButton createButton = new JButton(Localization.get("effecttypes.create"));
        createButton.setAlignmentX(LEFT_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        inputPanel.setAlignmentX(LEFT_ALIGNMENT);
        inputPanel.add(dropdown);
        inputPanel.add(addButton);
        inputPanel.add(createButton);

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(4);
        DefaultListCellRenderer selectedRenderer = new DefaultListCellRenderer();
        list.setCellRenderer((valueList, value, index, isSelected, cellHasFocus) -> {
            JLabel labelCell = (JLabel) selectedRenderer.getListCellRendererComponent(
                valueList,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            labelCell.setText(resolveEffectTypeLabel(Objects.toString(value, "")));
            return labelCell;
        });

        JScrollPane scroll = new JScrollPane(list);
        Dimension scrollSize = scroll.getPreferredSize();
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, scrollSize.height + 24));
        scroll.setAlignmentX(LEFT_ALIGNMENT);

        JButton removeButton = new JButton(Localization.get("common.remove.selected"));
        removeButton.setAlignmentX(LEFT_ALIGNMENT);
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        removePanel.setAlignmentX(LEFT_ALIGNMENT);
        removePanel.add(removeButton);

        addButton.addActionListener(event -> addSelectedEffectType(dropdown, model));
        createButton.addActionListener(event -> handleCreateEffectType(dropdown, model));
        removeButton.addActionListener(event -> removeSelectedEffectTypes(list, model));

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(6));
        panel.add(removePanel);
        return panel;
    }

    private void populateEffectTypeDropdown(JComboBox<EffectType> dropdown) {
        dropdown.removeAllItems();
        dropdown.addItem(effectTypePlaceholder);
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        List<EffectType> types = registry.getAll();
        types.sort(Comparator.comparing(
            type -> Objects.toString(type.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (EffectType type : types) {
            dropdown.addItem(type);
        }
    }

    private void handleCreateEffectType(JComboBox<EffectType> dropdown, DefaultListModel<String> model) {
        EffectType created = createEffectType();
        if (created == null) {
            return;
        }
        populateEffectTypeDropdown(dropdown);
        dropdown.setSelectedItem(created);
        model.addElement(created.getName());
        dropdown.setSelectedItem(effectTypePlaceholder);
    }

    private EffectType createEffectType() {
        ElementEditPopup.Result result = ElementEditPopup.show(
            mainStage,
            Localization.get("effecttypes.create.title"),
            "",
            "",
            new JPanel()
        );
        if (!result.isConfirmed()) {
            return null;
        }
        String name = result.getName();
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return null;
        }
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        if (registry.contains(name)) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
            return null;
        }
        EffectType type = new EffectType(name, result.getDescription());
        registry.register(type);
        saveGame();
        return type;
    }

    private void applyEffectTypeDropdownRenderer(JComboBox<EffectType> dropdown) {
        DefaultListCellRenderer typeRenderer = new DefaultListCellRenderer();
        dropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) typeRenderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            EffectType type = (EffectType) value;
            String name = type == null ? "" : Objects.toString(type.getName(), "").trim();
            if (name.isEmpty()) {
                label.setText(Localization.get("effects.type.none"));
            } else {
                label.setText(name);
            }
            return label;
        });
    }

    private void addSelectedEffectType(JComboBox<EffectType> dropdown, DefaultListModel<String> model) {
        EffectType selected = (EffectType) dropdown.getSelectedItem();
        String name = selected == null ? "" : Objects.toString(selected.getName(), "").trim();
        if (name.isEmpty()) {
            return;
        }
        model.addElement(name);
        dropdown.setSelectedItem(effectTypePlaceholder);
    }

    private void removeSelectedEffectTypes(JList<String> list, DefaultListModel<String> model) {
        int[] selected = list.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("common.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        for (int index = selected.length - 1; index >= 0; index--) {
            model.remove(selected[index]);
        }
    }

    private List<String> collectEffectTypeKeys(DefaultListModel<String> model) {
        List<String> keys = new ArrayList<>();
        for (int index = 0; index < model.getSize(); index++) {
            String key = Objects.toString(model.getElementAt(index), "").trim();
            if (!key.isEmpty()) {
                keys.add(key);
            }
        }
        return keys;
    }

    private String resolveEffectTypeLabel(String key) {
        String safeKey = Objects.toString(key, "").trim();
        if (safeKey.isEmpty()) {
            return Localization.get("effects.type.none");
        }
        EffectTypes registry = game.getRegistry(RegistryKey.EFFECT_TYPES);
        EffectType type = registry.get(safeKey);
        if (type == null) {
            String lower = safeKey.toLowerCase();
            for (EffectType candidate : registry.getAll()) {
                String name = Objects.toString(candidate.getName(), "").trim().toLowerCase();
                String display = Objects.toString(candidate.getDisplayName(), "").trim().toLowerCase();
                if (name.equals(lower) || display.equals(lower)) {
                    type = candidate;
                    break;
                }
            }
        }
        if (type != null) {
            String label = Objects.toString(type.getDisplayName(), "").trim();
            if (!label.isEmpty()) {
                return label;
            }
            String name = Objects.toString(type.getName(), "").trim();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return safeKey;
    }

    private void saveGame() {
        try {
            gameSaveIO.saveToUserHome(game);
        } catch (java.io.IOException e) {
            System.err.println("Failed to save game file: " + e.getMessage());
        }
    }

    private void populateWeightUnits(String selectedUnit) {
        weightUnitDropdown.removeAllItems();
        weightUnitDropdown.addItem("");
        List<String> units = getWeightUnits();
        for (String unit : units) {
            weightUnitDropdown.addItem(unit);
        }
        applyWeightUnitRenderer();
        String safeUnit = Objects.toString(selectedUnit, "").trim();
        if (!safeUnit.isEmpty() && !units.contains(safeUnit)) {
            weightUnitDropdown.addItem(safeUnit);
        }
        weightUnitDropdown.setSelectedItem(safeUnit.isEmpty() ? "" : safeUnit);
    }

    private List<String> getWeightUnits() {
        String system = Objects.toString(game.getWeightSystem(), "").trim().toLowerCase();
        String arrayName = system.equals("english") ? "weightUnitsEnglish" : "weightUnitsMetric";
        List<String> units = game.getArray(arrayName);
        return units == null ? List.of() : new java.util.ArrayList<>(units);
    }

    private void applyWeightUnitRenderer() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        weightUnitDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            String unit = Objects.toString(value, "");
            if (unit.isEmpty()) {
                label.setText(Localization.get("common.none"));
            } else {
                label.setText(unit);
            }
            return label;
        });
    }

    private String resolveWeightUnitSelection() {
        String unit = Objects.toString(weightUnitDropdown.getSelectedItem(), "").trim();
        return unit.isEmpty() ? "" : unit;
    }
}
