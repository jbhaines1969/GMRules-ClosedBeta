/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.util.Objects;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Shared helpers for resolving text input values safely.
 */
public final class EntryInputHandler {

// *** MEMBERS ***
    // (none)

// *** CONSTRUCTORS ***
    private EntryInputHandler() {
    }

// *** METHODS ***
    public static void selectAllOnFocus(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                SwingUtilities.invokeLater(() -> {
                    String text = field.getText();
                    if (text != null && !text.isEmpty()) {
                        field.selectAll();
                    }
                });
            }
        });
    }

    public static String resolveText(JTextField field) {
        return resolveText(field, "");
    }

    public static String resolveText(PlaceholderTextField field) {
        return resolveText(field, field.getPlaceholder());
    }

    public static String resolveText(JTextField field, String placeholder) {
        String value = Objects.toString(field.getText(), "").trim();
        if (value.isEmpty()) {
            return "";
        }
        String safePlaceholder = Objects.toString(placeholder, "").trim();
        if (!safePlaceholder.isEmpty() && value.equalsIgnoreCase(safePlaceholder)) {
            return "";
        }
        return value;
    }
}
