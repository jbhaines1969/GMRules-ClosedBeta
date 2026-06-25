/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules;

import javax.swing.SwingUtilities;

import com.gamemaker.gmrules.UI.AppWindow;

/**
 * Main application entry point for GMRules.
 * Placeholder while the UI and CLI are refactored.
 */
public class Main {

    // *** MEMBERS ***
    private final AppWindow appWindow = new AppWindow();

    // *** CONSTRUCTORS ***
    public Main() {
    }

    // *** METHODS ***
    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().start());
    }

    public void start() {
        appWindow.show();
    }
}
