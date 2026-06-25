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
import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * Simple modal popup with message text and an OK button.
 */
public class PopupAlert extends JDialog {

    // *** MEMBERS ***
    private final JTextArea messageArea = new JTextArea();
    private final JButton okButton = new JButton();
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));

    // *** CONSTRUCTORS ***
    public PopupAlert(JFrame owner, String message) {
        super(Objects.requireNonNullElseGet(owner, MainStage::getInstance), true);
        configureText(message);
        buildLayout();
        registerActions();
    }

    // *** METHODS ***
    public static void show(MainStage owner, String message) {
        PopupAlert dialog = new PopupAlert(owner, message);
        dialog.setVisible(true);
    }

    private void configureText(String message) {
        setTitle(Localization.get("app.title"));
        messageArea.setFont(UIManager.getFont("Label.font"));
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        messageArea.setText(Objects.toString(message, ""));
        okButton.setText(Localization.get("common.ok"));
    }

    private void buildLayout() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        contentPanel.add(messageArea, BorderLayout.CENTER);

        buttonPanel.add(okButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void registerActions() {
        okButton.addActionListener(event -> dispose());
    }
}
