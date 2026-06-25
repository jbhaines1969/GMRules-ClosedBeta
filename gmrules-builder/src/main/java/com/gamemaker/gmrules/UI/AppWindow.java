/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.UI;

import javax.swing.JFrame;

/**
 * Application window that hosts the main UI stage.
 */
public class AppWindow {

    // *** MEMBERS ***
    private final MainStage mainStage = MainStage.getInstance();

    // *** CONSTRUCTORS ***
    public AppWindow() {
        mainStage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showSplash();
    }

    // *** METHODS ***
    public void show() {
        mainStage.setVisible(true);
    }

    public final void showSplash() {
        mainStage.showContent(new DisplayStage(mainStage));
    }
}
