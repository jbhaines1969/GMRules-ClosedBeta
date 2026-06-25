/*
 FILE CONTRACT (Non-Null):
 - Do not introduce null fields or null checks in this file.
 - All instance fields are initialized (at declaration or in constructor) and remain non-null.
 - Represent "empty" with empty/sentinel objects (e.g., "", empty lists, EMPTY instances), not null.
 - If a value may be absent at an external boundary, normalize it immediately to a non-null value.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * Starts the web UI server.
 */
public final class WebServer {

    // *** MEMBERS ***
    private final WebConfig config = WebConfig.load();
    private final SessionStore sessionStore = new SessionStore(config);
    private final AccountStore accountStore = new AccountStore(config);
    private final DraftStore draftStore = new DraftStore(config);
    private final Router router = new Router(config, sessionStore, accountStore, draftStore);
    private final StaticFileHandler staticFileHandler = new StaticFileHandler();

    // *** CONSTRUCTORS ***
    public WebServer() {
        ApiRoutes.register(router);
    }

    // *** METHODS ***
    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(config.getHost(), config.getPort()), 0);
            server.setExecutor(Executors.newFixedThreadPool(config.getThreads()));
            server.createContext("/", exchange -> {
                if (staticFileHandler.handle(exchange)) {
                    return;
                }
                router.handle(exchange);
            });
            server.start();
            logStartup();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start web server.", e);
        }
    }

    private void logStartup() {
        String url = "http://" + config.getHost() + ":" + config.getPort();
        System.out.println("Web UI running at " + url);
    }
}
