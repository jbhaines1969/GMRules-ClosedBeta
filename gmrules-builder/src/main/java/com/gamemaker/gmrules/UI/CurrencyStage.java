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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import com.gamemaker.gmrules.Game;
import com.gamemaker.gmrules.GameElements.Currency;
import com.gamemaker.gmrules.GameSaveIO;

/**
 * Stage for defining currency systems.
 */
public class CurrencyStage extends JPanel implements StageView {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 20;
    private static final int LIST_VISIBLE_ROWS = 8;
    private static final double VALUE_MIN = 0.01;
    private static final double VALUE_MAX = 100000.0;
    private static final String SYSTEM_NAME_KEY = "currencies";

    private final JLabel titleLabel = new JLabel();
    private final JLabel systemNameLabel = new JLabel();
    private final PlaceholderTextField systemNameField = new PlaceholderTextField();
    private final JButton systemNameButton = new JButton();
    private final JTextArea introArea = new JTextArea();
    private final JLabel currencyNameLabel = new JLabel();
    private final PlaceholderTextField currencyNameField = new PlaceholderTextField();
    private final JLabel baseDenominationLabel = new JLabel();
    private final PlaceholderTextField baseDenominationField = new PlaceholderTextField();
    private final JButton addCurrencyButton = new JButton();
    private final DefaultListModel<Currency> currencyListModel = new DefaultListModel<>();
    private final JList<Currency> currencyList = new JList<>(currencyListModel);
    private final JScrollPane currencyScroll = new JScrollPane(currencyList);
    private final JButton removeCurrencyButton = new JButton();

    private final JLabel denominationTitleLabel = new JLabel();
    private final JLabel denominationNameLabel = new JLabel();
    private final PlaceholderTextField denominationNameField = new PlaceholderTextField();
    private final JLabel denominationValueLabel = new JLabel();
    private final JSpinner denominationValueSpinner =
            new JSpinner(new SpinnerNumberModel(1.0, VALUE_MIN, VALUE_MAX, 0.01));
    private final JButton addDenominationButton = new JButton();
    private final DefaultListModel<DenominationEntry> denominationListModel = new DefaultListModel<>();
    private final JList<DenominationEntry> denominationList = new JList<>(denominationListModel);
    private final JScrollPane denominationScroll = new JScrollPane(denominationList);
    private final JButton removeDenominationButton = new JButton();
    private final JLabel startingMoneyTitleLabel = new JLabel();
    private final JLabel startingMoneyMethodLabel = new JLabel();
    private final JComboBox<OptionItem> startingMoneyMethodDropdown = new JComboBox<>();
    private final JLabel startingMoneyBaseLabel = new JLabel();
    private final JSpinner startingMoneyBaseSpinner =
        new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JLabel startingMoneyCurrencyLabel = new JLabel();
    private final JComboBox<Currency> startingMoneyCurrencyDropdown = new JComboBox<>();

    private final JButton backButton = new JButton();
    private final JButton continueButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel systemNamePanel = new JPanel();
    private final JPanel currencyInputPanel = new JPanel();
    private final JPanel denominationInputPanel = new JPanel();
    private final JPanel listPanel = new JPanel(new BorderLayout());
    private final JPanel denominationPanel = new JPanel(new BorderLayout());
    private final JPanel startingMoneyPanel = new JPanel();
    private final MainStage mainStage;
    private final Game game;
    private final GameSaveIO gameSaveIO = new GameSaveIO();
    private boolean loading = false;

    // *** CONSTRUCTORS ***
    public CurrencyStage(MainStage mainStage, Game game) {
        this.mainStage = Objects.requireNonNullElseGet(mainStage, MainStage::getInstance);
        this.game = Objects.requireNonNullElseGet(game, () -> new Game(""));
        configureText();
        configureInputs();
        buildLayout();
        refreshCurrencies();
        registerActions();
    }

    // *** METHODS ***
    private void configureText() {
        titleLabel.setText(resolveTitle());
        systemNameLabel.setText(Localization.get("common.system_name.label"));
        systemNameButton.setText(Localization.get("common.system_name.save"));
        systemNameField.setPlaceholder(Localization.get("common.system_name.placeholder"));
        systemNameField.setText(game.getSystemName(SYSTEM_NAME_KEY));
        introArea.setText(Localization.get("currency.intro"));
        currencyNameLabel.setText(Localization.get("currency.name"));
        baseDenominationLabel.setText(Localization.get("currency.base_denom"));
        addCurrencyButton.setText(Localization.get("currency.add"));
        removeCurrencyButton.setText(Localization.get("common.remove.selected"));
        denominationTitleLabel.setText(Localization.get("currency.denom.section"));
        denominationNameLabel.setText(Localization.get("currency.denom.name"));
        denominationValueLabel.setText(Localization.get("currency.denom.value"));
        addDenominationButton.setText(Localization.get("currency.denom.add"));
        removeDenominationButton.setText(Localization.get("common.remove.selected"));
        startingMoneyTitleLabel.setText(Localization.get("money.starting.title"));
        startingMoneyMethodLabel.setText(Localization.get("money.starting.method"));
        startingMoneyBaseLabel.setText(Localization.get("money.starting.base"));
        startingMoneyCurrencyLabel.setText(Localization.get("money.starting.currency"));
        backButton.setText(Localization.get("setup.back"));
        continueButton.setText(Localization.get("common.continue"));
        currencyNameField.setPlaceholder(Localization.get("currency.name.placeholder"));
        baseDenominationField.setPlaceholder(Localization.get("currency.base_denom.placeholder"));
        denominationNameField.setPlaceholder(Localization.get("currency.denom.placeholder"));
    }

    private void configureInputs() {
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNameField.setColumns(NAME_FIELD_COLUMNS);
        Dimension systemFieldSize = systemNameField.getPreferredSize();
        systemNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, systemFieldSize.height));
        systemNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        systemNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setFont(UIManager.getFont("Label.font"));
        introArea.setEditable(false);
        introArea.setFocusable(false);
        introArea.setLineWrap(true);
        introArea.setWrapStyleWord(true);
        introArea.setOpaque(false);
        introArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        introArea.setForeground(UIManager.getColor("Label.disabledForeground"));
        currencyNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        baseDenominationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyNameField.setColumns(NAME_FIELD_COLUMNS);
        baseDenominationField.setColumns(NAME_FIELD_COLUMNS);
        Dimension nameFieldSize = currencyNameField.getPreferredSize();
        currencyNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameFieldSize.height));
        baseDenominationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameFieldSize.height));
        currencyScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        denominationScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        denominationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        startingMoneyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        systemNamePanel.setLayout(new BoxLayout(systemNamePanel, BoxLayout.X_AXIS));
        systemNamePanel.add(systemNameField);
        systemNamePanel.add(Box.createHorizontalStrut(8));
        systemNamePanel.add(systemNameButton);

        currencyInputPanel.setLayout(new BoxLayout(currencyInputPanel, BoxLayout.X_AXIS));
        denominationInputPanel.setLayout(new BoxLayout(denominationInputPanel, BoxLayout.X_AXIS));
        currencyList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        currencyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        denominationList.setVisibleRowCount(LIST_VISIBLE_ROWS);
        denominationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        currencyList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Currency currency = (Currency) value;
            label.setText(currency == null ? "" : currency.getDisplayName());
            return label;
        });
        startingMoneyMethodDropdown.addItem(new OptionItem("base", Localization.get("money.starting.method.base")));
        startingMoneyMethodDropdown.addItem(new OptionItem("class", Localization.get("money.starting.method.class")));
        startingMoneyMethodDropdown.addItem(new OptionItem("trait", Localization.get("money.starting.method.trait")));
        startingMoneyMethodDropdown.addItem(new OptionItem("hybrid", Localization.get("money.starting.method.hybrid")));
        startingMoneyCurrencyDropdown.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
            );
            Currency currency = (Currency) value;
            label.setText(currency == null ? Localization.get("common.none") : currency.getDisplayName());
            return label;
        });
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(systemNameLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(systemNamePanel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(introArea);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(currencyNameLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        currencyInputPanel.add(currencyNameField);
        currencyInputPanel.add(Box.createHorizontalStrut(8));
        currencyInputPanel.add(baseDenominationLabel);
        currencyInputPanel.add(Box.createHorizontalStrut(6));
        currencyInputPanel.add(baseDenominationField);
        currencyInputPanel.add(Box.createHorizontalStrut(8));
        currencyInputPanel.add(addCurrencyButton);
        contentPanel.add(currencyInputPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        listPanel.add(currencyScroll, BorderLayout.CENTER);
        listPanel.add(removeCurrencyButton, BorderLayout.SOUTH);
        contentPanel.add(listPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(denominationTitleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        denominationInputPanel.add(denominationNameLabel);
        denominationInputPanel.add(Box.createHorizontalStrut(6));
        denominationInputPanel.add(denominationNameField);
        denominationInputPanel.add(Box.createHorizontalStrut(8));
        denominationInputPanel.add(denominationValueLabel);
        denominationInputPanel.add(Box.createHorizontalStrut(6));
        denominationInputPanel.add(denominationValueSpinner);
        denominationInputPanel.add(Box.createHorizontalStrut(8));
        denominationInputPanel.add(addDenominationButton);
        contentPanel.add(denominationInputPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        denominationPanel.add(denominationScroll, BorderLayout.CENTER);
        denominationPanel.add(removeDenominationButton, BorderLayout.SOUTH);
        contentPanel.add(denominationPanel);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(startingMoneyTitleLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        startingMoneyPanel.setLayout(new BoxLayout(startingMoneyPanel, BoxLayout.X_AXIS));
        startingMoneyPanel.add(startingMoneyMethodLabel);
        startingMoneyPanel.add(Box.createHorizontalStrut(6));
        startingMoneyPanel.add(startingMoneyMethodDropdown);
        startingMoneyPanel.add(Box.createHorizontalStrut(8));
        startingMoneyPanel.add(startingMoneyBaseLabel);
        startingMoneyPanel.add(Box.createHorizontalStrut(6));
        startingMoneyPanel.add(startingMoneyBaseSpinner);
        startingMoneyPanel.add(Box.createHorizontalStrut(8));
        startingMoneyPanel.add(startingMoneyCurrencyLabel);
        startingMoneyPanel.add(Box.createHorizontalStrut(6));
        startingMoneyPanel.add(startingMoneyCurrencyDropdown);
        contentPanel.add(startingMoneyPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerActions() {
        systemNameButton.addActionListener(event -> saveSystemName());
        systemNameField.addActionListener(event -> saveSystemName());
        addCurrencyButton.addActionListener(event -> addCurrency());
        currencyNameField.addActionListener(event -> addCurrency());
        removeCurrencyButton.addActionListener(event -> removeSelectedCurrency());
        addDenominationButton.addActionListener(event -> addDenomination());
        denominationNameField.addActionListener(event -> addDenomination());
        removeDenominationButton.addActionListener(event -> removeSelectedDenomination());
        currencyList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                refreshDenominations();
            }
        });
        startingMoneyMethodDropdown.addActionListener(event -> saveStartingMoneyConfig());
        startingMoneyBaseSpinner.addChangeListener(event -> saveStartingMoneyConfig());
        startingMoneyCurrencyDropdown.addActionListener(event -> saveStartingMoneyConfig());
        backButton.addActionListener(event -> mainStage.navigateBack());
        continueButton.addActionListener(event -> {
            saveGame();
            mainStage.showContent(new EffectTypesStage(mainStage, game));
        });
    }

    private void addCurrency() {
        String name = EntryInputHandler.resolveText(currencyNameField);
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        Currency currency = new Currency(name);
        String baseDenomination = EntryInputHandler.resolveText(baseDenominationField);
        if (!baseDenomination.isEmpty()) {
            currency.addDenomination(baseDenomination, 1.0f);
        }
        boolean added = game.addElement("currencies", currency);
        if (!added) {
            PopupAlert.show(mainStage, Localization.get("common.name.duplicate"));
        }
        if (added) {
            currencyNameField.setText("");
            baseDenominationField.setText("");
            saveGame();
        }
        refreshCurrencies();
        if (added) {
            String currencyId = Objects.toString(currency.getId(), "");
            for (int index = 0; index < currencyListModel.getSize(); index++) {
                Currency entry = currencyListModel.getElementAt(index);
                if (currencyId.equals(Objects.toString(entry.getId(), ""))) {
                    currencyList.setSelectedIndex(index);
                    break;
                }
            }
        }
    }

    private void saveSystemName() {
        String name = EntryInputHandler.resolveText(systemNameField);
        game.setSystemName(SYSTEM_NAME_KEY, name);
        titleLabel.setText(resolveTitle());
        mainStage.refreshSidebar();
        saveGame();
    }

    private void removeSelectedCurrency() {
        int selectedIndex = currencyList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("currency.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        Currency selected = currencyListModel.getElementAt(selectedIndex);
        game.removeElement("currencies", selected);
        saveGame();
        refreshCurrencies();
    }

    private void addDenomination() {
        int selectedIndex = currencyList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Currency currency = currencyListModel.getElementAt(selectedIndex);
        String name = EntryInputHandler.resolveText(denominationNameField);
        if (name.isEmpty()) {
            PopupAlert.show(mainStage, Localization.get("common.name.required"));
            return;
        }
        double value = ((Number) denominationValueSpinner.getValue()).doubleValue();
        if (value <= 0.0) {
            return;
        }
        currency.addDenomination(name, (float) value);
        denominationNameField.setText("");
        saveGame();
        refreshDenominations();
    }

    private void removeSelectedDenomination() {
        int selectedCurrencyIndex = currencyList.getSelectedIndex();
        if (selectedCurrencyIndex < 0) {
            return;
        }
        Currency currency = currencyListModel.getElementAt(selectedCurrencyIndex);
        int selectedDenominationIndex = denominationList.getSelectedIndex();
        if (selectedDenominationIndex < 0) {
            return;
        }
        if (!ConfirmPopup.show(
            mainStage,
            Localization.get("currency.denom.remove.confirm"),
            Localization.get("common.remove")
        )) {
            return;
        }
        DenominationEntry entry = denominationListModel.getElementAt(selectedDenominationIndex);
        currency.getDenominations().remove(entry.getName());
        saveGame();
        refreshDenominations();
    }

    private void refreshCurrencies() {
        loading = true;
        currencyListModel.clear();
        List<Currency> currencies = Objects.requireNonNullElse(game.getObjectArray("currencies"), new ArrayList<>());
        List<Currency> sorted = new ArrayList<>(currencies);
        sorted.sort(Comparator.comparing(
            currency -> Objects.toString(currency.getDisplayName(), ""),
            String.CASE_INSENSITIVE_ORDER
        ));
        for (Currency currency : sorted) {
            currencyListModel.addElement(currency);
        }
        refreshStartingMoneyCurrencyOptions(sorted);
        if (!currencyListModel.isEmpty() && currencyList.getSelectedIndex() < 0) {
            currencyList.setSelectedIndex(0);
        }
        refreshDenominations();
        loading = false;
    }

    private void refreshStartingMoneyCurrencyOptions(List<Currency> currencies) {
        startingMoneyCurrencyDropdown.removeAllItems();
        startingMoneyCurrencyDropdown.addItem(null);
        for (Currency currency : currencies) {
            startingMoneyCurrencyDropdown.addItem(currency);
        }
        setSelectedOption(startingMoneyMethodDropdown, game.getStartingMoneyMethod());
        startingMoneyBaseSpinner.setValue(game.getBaseStartingMoney());
        String selectedCurrencyId = game.getStartingMoneyCurrencyId();
        if (selectedCurrencyId.isEmpty()) {
            startingMoneyCurrencyDropdown.setSelectedItem(null);
            return;
        }
        for (int index = 0; index < startingMoneyCurrencyDropdown.getItemCount(); index++) {
            Currency currency = startingMoneyCurrencyDropdown.getItemAt(index);
            if (currency == null) {
                continue;
            }
            if (selectedCurrencyId.equals(Objects.toString(currency.getId(), ""))) {
                startingMoneyCurrencyDropdown.setSelectedIndex(index);
                return;
            }
        }
        startingMoneyCurrencyDropdown.setSelectedItem(null);
    }

    private void saveStartingMoneyConfig() {
        if (loading) {
            return;
        }
        OptionItem selectedMethod = (OptionItem) startingMoneyMethodDropdown.getSelectedItem();
        String method = selectedMethod == null ? "base" : selectedMethod.value;
        game.setStartingMoneyMethod(method);
        game.setBaseStartingMoney((Integer) startingMoneyBaseSpinner.getValue());
        Currency selectedCurrency = (Currency) startingMoneyCurrencyDropdown.getSelectedItem();
        if (selectedCurrency == null) {
            game.setStartingMoneyCurrencyId("");
        } else {
            game.setStartingMoneyCurrencyId(Objects.toString(selectedCurrency.getId(), ""));
        }
        saveGame();
    }

    private void setSelectedOption(JComboBox<OptionItem> dropdown, String value) {
        String safeValue = Objects.toString(value, "").trim();
        for (int index = 0; index < dropdown.getItemCount(); index++) {
            OptionItem option = dropdown.getItemAt(index);
            if (option != null && option.value.equalsIgnoreCase(safeValue)) {
                dropdown.setSelectedIndex(index);
                return;
            }
        }
        dropdown.setSelectedIndex(0);
    }

    private String resolveTitle() {
        String custom = game.getSystemName(SYSTEM_NAME_KEY);
        if (!custom.isEmpty()) {
            return custom;
        }
        return Localization.get("currency.title");
    }

    private void refreshDenominations() {
        denominationListModel.clear();
        int selectedIndex = currencyList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        Currency currency = currencyListModel.getElementAt(selectedIndex);
        Map<String, Float> denominations = currency.getDenominations();
        for (Map.Entry<String, Float> entry : denominations.entrySet()) {
            denominationListModel.addElement(new DenominationEntry(entry.getKey(), entry.getValue()));
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
        return StageId.CURRENCY;
    }

    @Override
    public Game getGame() {
        return game;
    }

    private static final class DenominationEntry {

        // *** MEMBERS ***
        private final String name;
        private final float value;

        // *** CONSTRUCTORS ***
        private DenominationEntry(String name, float value) {
            this.name = Objects.toString(name, "");
            this.value = value;
        }

        // *** METHODS ***
        private String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + " = " + value;
        }
    }

    private static final class OptionItem {

        // *** MEMBERS ***
        private final String value;
        private final String label;

        // *** CONSTRUCTORS ***
        private OptionItem(String value, String label) {
            this.value = Objects.toString(value, "");
            this.label = Objects.toString(label, "");
        }

        // *** METHODS ***
        @Override
        public String toString() {
            return label;
        }
    }
}
