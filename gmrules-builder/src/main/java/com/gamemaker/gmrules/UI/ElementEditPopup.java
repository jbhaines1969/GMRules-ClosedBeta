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
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Reusable popup for editing common element fields.
 */
public class ElementEditPopup extends JDialog {

    // *** MEMBERS ***
    private static final int NAME_FIELD_COLUMNS = 26;
    private static final int DESCRIPTION_ROWS = 6;

    private final JLabel nameLabel = new JLabel();
    private final JTextField nameField = new JTextField();
    private final JLabel descriptionLabel = new JLabel();
    private final JTextArea descriptionArea = new JTextArea();
    private final JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
    private final JButton cancelButton = new JButton();
    private final JButton okButton = new JButton();
    private final JPanel contentPanel = new JPanel();
    private final JPanel formPanel = new JPanel();
    private final JScrollPane formScroll = new JScrollPane(formPanel);
    private final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
    private final JComponent extraPanel;
    private final Result result = new Result();

    // *** CONSTRUCTORS ***
    public ElementEditPopup(JFrame owner, String title, String name, String description, JComponent extraPanel) {
        super(Objects.requireNonNullElseGet(owner, MainStage::getInstance), true);
        this.extraPanel = Objects.requireNonNullElseGet(extraPanel, JPanel::new);
        configureText(title, name, description);
        configureInputs();
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public static Result show(MainStage owner, String title, String name, String description, JComponent extraPanel) {
        ElementEditPopup dialog = new ElementEditPopup(owner, title, name, description, extraPanel);
        dialog.setVisible(true);
        return dialog.result;
    }

    private void configureText(String title, String name, String description) {
        setTitle(Objects.toString(title, Localization.get("app.title")));
        nameLabel.setText(Localization.get("common.name"));
        descriptionLabel.setText(Localization.get("common.description"));
        nameField.setText(Objects.toString(name, ""));
        descriptionArea.setText(Objects.toString(description, ""));
        cancelButton.setText(Localization.get("common.cancel"));
        okButton.setText(Localization.get("common.ok"));
    }

    private void configureInputs() {
        nameField.setColumns(NAME_FIELD_COLUMNS);
        EntryInputHandler.selectAllOnFocus(nameField);
        Dimension nameSize = nameField.getPreferredSize();
        nameField.setMaximumSize(new Dimension(nameSize.width, nameSize.height));
        descriptionArea.setFont(UIManager.getFont("Label.font"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setColumns(32);
        descriptionArea.setRows(DESCRIPTION_ROWS);
        Dimension descriptionSize = descriptionArea.getPreferredSize();
        descriptionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, descriptionSize.height + 24));
    }

    private void buildLayout() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(descriptionLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(descriptionScroll);
        if (extraPanel.getComponentCount() > 0) {
            formPanel.add(Box.createVerticalStrut(16));
            formPanel.add(extraPanel);
        }

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setPreferredSize(new Dimension(520, 420));

        contentPanel.add(formScroll, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void registerActions() {
        cancelButton.addActionListener(event -> dispose());
        okButton.addActionListener(event -> {
            result.confirmed = true;
            result.name = Objects.toString(nameField.getText(), "").trim();
            result.description = Objects.toString(descriptionArea.getText(), "").trim();
            dispose();
        });
    }

    public static final class Result {

        // *** MEMBERS ***
        private boolean confirmed = false;
        private String name = "";
        private String description = "";

        // *** CONSTRUCTORS ***
        private Result() {
        }

        // *** METHODS ***
        public boolean isConfirmed() {
            return confirmed;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
