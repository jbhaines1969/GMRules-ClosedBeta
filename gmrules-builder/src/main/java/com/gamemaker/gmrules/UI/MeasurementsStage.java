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
import java.util.Comparator;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for configuring measurement systems and time units.
 */
public class MeasurementsStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 18;
    private static final int DURATION_FIELD_COLUMNS = 8;
    private static final int LIST_VISIBLE_ROWS = 8;

    private final JLabel titleLabel = new JLabel();
    private final JTextArea introArea = new JTextArea();
    private final JLabel weightSystemLabel = new JLabel();
    private final JComboBox<String> weightSystemDropdown = new JComboBox<>();
    private final JLabel timeUnitsLabel = new JLabel();
    private final JLabel timeUnitNameLabel = new JLabel();
    private final PlaceholderTextField timeUnitNameField = new PlaceholderTextField();
    private final JLabel timeUnitDurationLabel = new JLabel();
    private final JTextField timeUnitDurationField = new JTextField();
    private final JLabel timeUnitBaseLabel = new JLabel();
    private final JComboBox<String> timeUnitBaseDropdown = new JComboBox<>();
    private final JButton addTimeUnitButton = new JButton();
    private final DefaultListModel<TimeUnitEntry> timeUnitListModel = new DefaultListModel<>();
    private final JList<TimeUnitEntry> timeUnitList = new JList<>(timeUnitListModel);
    private final JScrollPane timeUnitScroll = new JScrollPane(timeUnitList);
    private final JButton removeTimeUnitButton = new JButton();
    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel timeUnitInputPanel = new JPanel();
    private final JPanel timeUnitButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private String metricLabel = "";
    private String englishLabel = "";
    private String secondsSuffix = "";
    private String minutesSuffix = "";
    private String hoursSuffix = "";
    private boolean isLoading = false;

    // *** CONSTRUCTORS ***
    public MeasurementsStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        isLoading = true;
        loadFromGame();
        isLoading = false;
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(Localization.get("measurements.title"));
        introArea.setText(Localization.get("measurements.intro"));
        weightSystemLabel.setText(Localization.get("measurements.weight.label"));
        timeUnitsLabel.setText(Localization.get("measurements.timeUnits.title"));
        timeUnitNameLabel.setText(Localization.get("measurements.timeUnits.name"));
        timeUnitDurationLabel.setText(Localization.get("measurements.timeUnits.duration"));
        timeUnitBaseLabel.setText(Localization.get("measurements.timeUnits.base"));
        addTimeUnitButton.setText(Localization.get("measurements.timeUnits.add"));
        removeTimeUnitButton.setText(Localization.get("common.remove.selected"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));
        metricLabel = Localization.get("measurements.weight.metric");
        englishLabel = Localization.get("measurements.weight.english");
        secondsSuffix = Localization.get("measurements.timeUnits.seconds");
        minutesSuffix = Localization.get("measurements.timeUnits.minutes");
        hoursSuffix = Localization.get("measurements.timeUnits.hours");
        timeUnitNameField.setPlaceholder(Localization.get("measurements.timeUnits.name.placeholder"));
        timeUnitDurationField.setText("");
        populateTimeUnitBases();
        populateWeightSystems();
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
        weightSystemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        weightSystemDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeUnitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeUnitNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeUnitDurationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeUnitBaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeUnitNameField.setColumns(NAME_FIELD_COLUMNS);
        timeUnitDurationField.setColumns(DURATION_FIELD_COLUMNS);
        EntryInputHandler.selectAllOnFocus(timeUnitDurationField);
        timeUnitInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeUnitInputPanel.setLayout(new BoxLayout(timeUnitInputPanel, BoxLayout.X_AXIS));
        timeUnitList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        timeUnitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        timeUnitList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            if (value == null) {
                label.setText("");
            } else {
                label.setText(String.format(
                    "%s (%d %s)",
                    value.getName(),
                    value.getDisplayAmount(),
                    value.getDisplayUnit()
                ));
            }
            return label;
        });

        Dimension durationSize = timeUnitDurationField.getPreferredSize();
        timeUnitDurationField.setMaximumSize(new Dimension(120, durationSize.height));
    }

    private void loadFromGame() {
        String weightSystem = Objects.toString(game.getWeightSystem(), "");
        if (weightSystem.equalsIgnoreCase("english")) {
            weightSystemDropdown.setSelectedItem(englishLabel);
        } else {
            weightSystemDropdown.setSelectedItem(metricLabel);
        }
        refreshTimeUnits();
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(weightSystemLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(weightSystemDropdown);
        contentPanel.add(Box.createVerticalStrut(18));

        contentPanel.add(timeUnitsLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(timeUnitNameLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        timeUnitInputPanel.add(timeUnitNameField);
        timeUnitInputPanel.add(Box.createHorizontalStrut(8));
        timeUnitInputPanel.add(timeUnitDurationLabel);
        timeUnitInputPanel.add(Box.createHorizontalStrut(6));
        timeUnitInputPanel.add(timeUnitDurationField);
        timeUnitInputPanel.add(Box.createHorizontalStrut(8));
        timeUnitInputPanel.add(timeUnitBaseLabel);
        timeUnitInputPanel.add(Box.createHorizontalStrut(6));
        timeUnitInputPanel.add(timeUnitBaseDropdown);
        timeUnitInputPanel.add(Box.createHorizontalStrut(8));
        timeUnitInputPanel.add(addTimeUnitButton);
        contentPanel.add(timeUnitInputPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        timeUnitScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(timeUnitScroll);

        timeUnitButtonPanel.add(removeTimeUnitButton);
        contentPanel.add(timeUnitButtonPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            applyWeightSystemToGame();
            saveGame();
            mainStage.showContent(new DiceChooserStage(mainStage, game));
        });
        addTimeUnitButton.addActionListener(event -> addTimeUnit());
        timeUnitNameField.addActionListener(event -> addTimeUnit());
        timeUnitDurationField.addActionListener(event -> addTimeUnit());
        removeTimeUnitButton.addActionListener(event -> removeSelectedTimeUnit());
        weightSystemDropdown.addActionListener(event -> {
            if (isLoading) {
                return;
            }
            applyWeightSystemToGame();
            saveGame();
        });
    }

    private void populateWeightSystems() {
        weightSystemDropdown.removeAllItems();
        weightSystemDropdown.addItem(metricLabel);
        weightSystemDropdown.addItem(englishLabel);
    }

    private void populateTimeUnitBases() {
        timeUnitBaseDropdown.removeAllItems();
        timeUnitBaseDropdown.addItem(secondsSuffix);
        timeUnitBaseDropdown.addItem(minutesSuffix);
        timeUnitBaseDropdown.addItem(hoursSuffix);
    }

    private void applyWeightSystemToGame() {
        String selection = Objects.toString(weightSystemDropdown.getSelectedItem(), "");
        if (selection.equalsIgnoreCase(englishLabel)) {
            game.setWeightSystem("english");
            return;
        }
        game.setWeightSystem("metric");
    }

    private void refreshTimeUnits() {
        timeUnitListModel.clear();
        Map<String, Integer> units = game.getTimeUnits();
        List<TimeUnitEntry> entries = buildTimeUnitEntries(units);
        for (TimeUnitEntry entry : entries) {
            timeUnitListModel.addElement(entry);
        }
    }

    private List<TimeUnitEntry> buildTimeUnitEntries(Map<String, Integer> units) {
        List<TimeUnitEntry> baseEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : units.entrySet()) {
            baseEntries.add(new TimeUnitEntry(entry.getKey(), entry.getValue()));
        }
        baseEntries.sort(
            Comparator
                .comparingInt(TimeUnitEntry::getDuration)
                .thenComparing(TimeUnitEntry::getName, String.CASE_INSENSITIVE_ORDER)
        );
        List<TimeUnitEntry> displayEntries = new ArrayList<>();
        for (int index = 0; index < baseEntries.size(); index++) {
            TimeUnitEntry current = baseEntries.get(index);
            int duration = current.getDuration();
            int displayAmount = duration;
            String displayUnit = secondsSuffix;
            for (int lowerIndex = index - 1; lowerIndex >= 0; lowerIndex--) {
                TimeUnitEntry lower = baseEntries.get(lowerIndex);
                int lowerDuration = lower.getDuration();
                if (lowerDuration <= 0 || lowerDuration >= duration) {
                    continue;
                }
                if (duration % lowerDuration == 0) {
                    displayAmount = duration / lowerDuration;
                    displayUnit = lower.getName();
                    break;
                }
            }
            displayEntries.add(new TimeUnitEntry(current.getName(), duration, displayAmount, displayUnit));
        }
        return displayEntries;
    }

    private void addTimeUnit() {
        String name = EntryInputHandler.resolveText(timeUnitNameField);
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        Integer duration;
        try {
            duration = resolveDurationInSeconds();
        } catch (ArithmeticException ex) {
            duration = null;
        }
        if (duration == null || duration <= 0) {
            PopupAlert.show(mainStage, Localization.get("measurements.timeUnits.duration.invalid"));
            return;
        }
        game.addTimeUnit(name, duration);
        timeUnitNameField.setText("");
        timeUnitDurationField.setText("");
        refreshTimeUnits();
        saveGame();
    }

    private void removeSelectedTimeUnit() {
        int index = timeUnitList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("measurements.timeUnits.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        TimeUnitEntry selected = timeUnitListModel.getElementAt(index);
        game.removeTimeUnit(selected.getName());
        refreshTimeUnits();
        saveGame();
    }

    private Integer resolveDuration() {
        String raw = Objects.toString(timeUnitDurationField.getText(), "").trim();
        if (raw.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer resolveDurationInSeconds() {
        Integer baseDuration = resolveDuration();
        if (baseDuration == null) {
            return null;
        }
        String base = Objects.toString(timeUnitBaseDropdown.getSelectedItem(), "");
        if (base.equalsIgnoreCase(minutesSuffix)) {
            return Math.multiplyExact(baseDuration, 60);
        }
        if (base.equalsIgnoreCase(hoursSuffix)) {
            return Math.multiplyExact(baseDuration, 3600);
        }
        return baseDuration;
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
        return StageId.MEASUREMENTS;
    }

    @Override
    public Game getGame() {
        return game;
    }

    private static final class TimeUnitEntry {

        // *** MEMBERS ***
        private final String name;
        private final int duration;
        private final int displayAmount;
        private final String displayUnit;

        // *** CONSTRUCTORS ***
        private TimeUnitEntry(String name, int duration) {
            this(name, duration, duration, "");
        }

        private TimeUnitEntry(String name, int duration, int displayAmount, String displayUnit) {
            this.name = Objects.toString(name, "");
            this.duration = Math.max(0, duration);
            this.displayAmount = Math.max(0, displayAmount);
            this.displayUnit = Objects.toString(displayUnit, "");
        }

        // *** METHODS ***
        private String getName() { return name; }
        private int getDuration() { return duration; }
        private int getDisplayAmount() { return displayAmount; }
        private String getDisplayUnit() { return displayUnit; }
    }
}
