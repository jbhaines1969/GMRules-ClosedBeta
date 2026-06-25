/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

/**
 * Entry point for the web UI.
 */
public class WebMain {

    // *** MEMBERS ***
    private final WebServer webServer = new WebServer();

    // *** CONSTRUCTORS ***
    public WebMain() {
    }

    // *** METHODS ***
    public static void main(String[] args) {
        new WebMain().start();
    }

    public void start() {
        webServer.start();
    }
}
