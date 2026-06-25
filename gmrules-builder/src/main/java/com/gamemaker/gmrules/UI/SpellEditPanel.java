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

import com.gamemaker.gmrules.AtomicElements.EffectType;
import com.gamemaker.gmrules.AtomicElements.EffectTypes;
import com.gamemaker.gmrules.AtomicElements.RegistryKey;
import com.gamemaker.gmrules.ElementRegistry;
import com.gamemaker.gmrules.ElementRegistryKey;
import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;
import com.gamemaker.gmrules.GameElements.Spell;
import com.gamemaker.gmrules.SupportElements.Effect;

/**
 * Spell-specific fields for the element editor popup.
 */
public class SpellEditPanel extends JPanel {

    // *** MEMBERS ***
    private static final int LEVEL_MIN = 0;
    private static final int LEVEL_MAX = 20;

    private final JLabel schoolLabel = new JLabel();
    private final PlaceholderTextField schoolField = new PlaceholderTextField();
    private final JLabel levelLabel = new JLabel();
    private final JSpinner levelSpinner = new JSpinner(new SpinnerNumberModel(0, LEVEL_MIN, LEVEL_MAX, 1));
    private final JLabel castingTimeLabel = new JLabel();
    private final PlaceholderTextField castingTimeField = new PlaceholderTextField();
    private final JLabel rangeLabel = new JLabel();
    private final PlaceholderTextField rangeField = new PlaceholderTextField();
    private final JLabel durationLabel = new JLabel();
    private final PlaceholderTextField durationField = new PlaceholderTextField();

    private final JLabel effectsLabel = new JLabel();
    private final JComboBox<Effect> effectDropdown = new JComboBox<>();
    private final Effect effectPlaceholder = new Effect("");
    private final JButton addEffectButton = new JButton();
    private final JButton removeEffectButton = new JButton();
    private final DefaultListModel<String> effectModel = new DefaultListModel<>();
    private final JList<String> effectList = new JList<>(effectModel);
    private final JScrollPane effectScroll = new JScrollPane(effectList);
    private final EffectType effectTypePlaceholder = new EffectType("");

    private final JLabel createEffectTitle = new JLabel();
    private final JButton createEffectButton = new JButton();

    private final JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel effectsInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final JPanel effectsListPanel = new JPanel(new BorderLayout());
    private final JPanel createEffectPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();

    // *** CONSTRUCTORS ***
    public SpellEditPanel(MainStage mainStage, Game game, Spell spell) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        populateEffects();
        loadSpell(spell);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public void applyToSpell(Spell spell) {
        Spell safeSpell = Objects.requireNonNullElseGet(spell, () -> new Spell(""));
        safeSpell.setSchool(EntryInputHandler.resolveText(schoolField));
        safeSpell.setLevel((Integer) levelSpinner.getValue());
        safeSpell.setCastingTime(EntryInputHandler.resolveText(castingTimeField));
        safeSpell.setRange(EntryInputHandler.resolveText(rangeField));
        safeSpell.setDuration(EntryInputHandler.resolveText(durationField));
        List<String> effectNames = collectEffectNames();
        safeSpell.clearArray("effectNames");
        for (String effectName : effectNames) {
            safeSpell.addToArray("effectNames", effectName);
        }
        safeSpell.setEffect(effectNames.isEmpty() ? "" : effectNames.get(0));
        safeSpell.setSecondaryEffect(effectNames.size() > 1 ? effectNames.get(1) : "");
    }

    private void configureText() {
        schoolLabel.setText(Localization.get("spells.school"));
        levelLabel.setText(Localization.get("spells.level"));
        castingTimeLabel.setText(Localization.get("spells.casting_time"));
        rangeLabel.setText(Localization.get("spells.range"));
        durationLabel.setText(Localization.get("spells.duration"));
        effectsLabel.setText(Localization.get("skills.effects"));
        addEffectButton.setText(Localization.get("skills.effects.add"));
        removeEffectButton.setText(Localization.get("common.remove.selected"));
        createEffectTitle.setText(Localization.get("skills.effects.create"));
        createEffectButton.setText(Localization.get("skills.effects.create"));
        schoolField.setPlaceholder(Localization.get("spells.school.placeholder"));
        castingTimeField.setPlaceholder(Localization.get("spells.casting_time.placeholder"));
        rangeField.setPlaceholder(Localization.get("spells.range.placeholder"));
        durationField.setPlaceholder(Localization.get("spells.duration.placeholder"));
        effectPlaceholder.setName(Localization.get("skills.effects.select"));
    }

    private void configureInputs() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        topRow.setAlignmentX(LEFT_ALIGNMENT);
        effectsInputPanel.setAlignmentX(LEFT_ALIGNMENT);
        effectsListPanel.setAlignmentX(LEFT_ALIGNMENT);
        createEffectPanel.setAlignmentX(LEFT_ALIGNMENT);

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
            label.setText(Objects.toString(value, ""));
            return label;
        });

        Dimension fieldSize = schoolField.getPreferredSize();
        castingTimeField.setColumns(18);
        rangeField.setColumns(18);
        durationField.setColumns(18);
        schoolField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldSize.height));
        castingTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldSize.height));
        rangeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldSize.height));
        durationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldSize.height));
        effectDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldSize.height));
        effectList.setVisibleRowCount(4);
        effectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

    private void loadSpell(Spell spell) {
        Spell safeSpell = Objects.requireNonNullElseGet(spell, () -> new Spell(""));
        schoolField.setText(safeSpell.getSchool());
        levelSpinner.setValue(safeSpell.getLevel());
        castingTimeField.setText(safeSpell.getCastingTime());
        rangeField.setText(safeSpell.getRange());
        durationField.setText(safeSpell.getDuration());
        loadEffectNames(safeSpell);
    }

    private void buildLayout() {
        add(schoolLabel);
        add(Box.createVerticalStrut(6));
        topRow.add(schoolField);
        topRow.add(levelLabel);
        topRow.add(levelSpinner);
        add(topRow);
        add(Box.createVerticalStrut(12));

        add(castingTimeLabel);
        add(Box.createVerticalStrut(6));
        add(castingTimeField);
        add(Box.createVerticalStrut(12));
        add(rangeLabel);
        add(Box.createVerticalStrut(6));
        add(rangeField);
        add(Box.createVerticalStrut(12));
        add(durationLabel);
        add(Box.createVerticalStrut(6));
        add(durationField);
        add(Box.createVerticalStrut(12));

        add(effectsLabel);
        add(Box.createVerticalStrut(6));
        effectsInputPanel.add(effectDropdown);
        effectsInputPanel.add(addEffectButton);
        add(effectsInputPanel);
        effectsListPanel.add(effectScroll, BorderLayout.CENTER);
        effectsListPanel.add(removeEffectButton, BorderLayout.SOUTH);
        add(effectsListPanel);
        add(Box.createVerticalStrut(12));

        createEffectPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        createEffectPanel.add(createEffectTitle);
        createEffectPanel.add(createEffectButton);
        add(createEffectPanel);
    }

    private void registerActions() {
        addEffectButton.addActionListener(event -> addSelectedEffect());
        removeEffectButton.addActionListener(event -> removeSelectedEffect());
        createEffectButton.addActionListener(event -> createEffect());
    }

    private void addSelectedEffect() {
        Effect selected = (Effect) effectDropdown.getSelectedItem();
        if (selected == null || selected == effectPlaceholder) {
            return;
        }
        String name = Objects.toString(selected.getName(), "").trim();
        if (name.isEmpty()) {
            return;
        }
        if (containsEffectName(name)) {
            return;
        }
        effectModel.addElement(name);
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

    private void loadEffectNames(Spell spell) {
        effectModel.clear();
        List<String> effectNames = spell.getArray("effectNames");
        if (effectNames != null && !effectNames.isEmpty()) {
            for (String effectName : effectNames) {
                String safeName = Objects.toString(effectName, "").trim();
                if (!safeName.isEmpty() && !containsEffectName(safeName)) {
                    effectModel.addElement(safeName);
                }
            }
            return;
        }
        String primary = Objects.toString(spell.getEffect(), "").trim();
        if (!primary.isEmpty()) {
            effectModel.addElement(primary);
        }
        String secondary = Objects.toString(spell.getSecondaryEffect(), "").trim();
        if (!secondary.isEmpty() && !containsEffectName(secondary)) {
            effectModel.addElement(secondary);
        }
    }

    private boolean containsEffectName(String name) {
        String safeName = Objects.toString(name, "").trim();
        if (safeName.isEmpty()) {
            return false;
        }
        for (int index = 0; index < effectModel.getSize(); index++) {
            String entry = Objects.toString(effectModel.getElementAt(index), "");
            if (entry.equalsIgnoreCase(safeName)) {
                return true;
            }
        }
        return false;
    }

    private List<String> collectEffectNames() {
        List<String> effects = new ArrayList<>();
        for (int index = 0; index < effectModel.getSize(); index++) {
            String name = Objects.toString(effectModel.getElementAt(index), "").trim();
            if (!name.isEmpty()) {
                effects.add(name);
            }
        }
        return effects;
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
        if (!containsEffectName(name)) {
            effectModel.addElement(name);
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
}
