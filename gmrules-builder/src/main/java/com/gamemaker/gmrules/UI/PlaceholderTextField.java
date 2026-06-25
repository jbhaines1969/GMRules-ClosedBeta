/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.JTextField;

/**
 * JTextField with an optional placeholder rendered when empty.
 */
public class PlaceholderTextField extends JTextField {

    // *** MEMBERS ***
    private static final Color PLACEHOLDER_COLOR = new Color(120, 120, 120);
    private String placeholder = "";

    // *** CONSTRUCTORS ***
    public PlaceholderTextField() {
        super();
        EntryInputHandler.selectAllOnFocus(this);
    }

    // *** METHODS ***
    public void setPlaceholder(String placeholder) {
        this.placeholder = Objects.toString(placeholder, "");
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!getText().isEmpty() || isFocusOwner() || placeholder.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setColor(PLACEHOLDER_COLOR);
        g2.setFont(getFont());
        Insets insets = getInsets();
        FontMetrics metrics = g2.getFontMetrics();
        int x = insets.left + 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g2.drawString(placeholder, x, y);
        g2.dispose();
    }
}
