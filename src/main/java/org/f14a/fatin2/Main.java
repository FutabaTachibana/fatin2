package org.f14a.fatin2;

import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.config.ConfigLoader;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
* Entry of the application
*/
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Load Config
        // Choose Command Line Args first
        String configPath = args.length > 0 ? args[0] : "config.yml";
        CountDownLatch stopLatch = new CountDownLatch(1);
        EventBus eventBus =null;
        PluginManager pluginManager = null;
        Client client = null;
        try {
            LOGGER.info("Loading config from: {}", configPath);
            Config config = ConfigLoader.load(configPath);
            System.setProperty("log.level", config.isDebug() ? "DEBUG" : "INFO");
            // Init event bus
            eventBus = new EventBus();
            // Load plugins
            pluginManager = new PluginManager(config.isPluginAutoReload());
            // Init client
            URI serverUri = new URI(config.getWebSocketUrl());
            client = new Client(serverUri, config.getAccessToken());
            // Keep references for shutdown hook
            EventBus finalEventBus = eventBus;
            PluginManager finalPluginManager = pluginManager;
            Client finalClient = client;
            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down Fatin2...");
                safeClose(finalPluginManager, finalEventBus, finalClient);
                stopLatch.countDown();
            }, "Shutdown-Hook-Thread"));

            LOGGER.info("Connecting to {}...", serverUri);
            client.connect();
            // Wait until shutdown
            stopLatch.await();

        } catch (URISyntaxException e) {
            LOGGER.error("Invalid WebSocket URL in config: {}", e.getMessage(), e);
            safeClose(pluginManager, eventBus, client);
            System.exit(1);
        }  catch (Exception e) {
            LOGGER.error("Failed to start Fatin2 due to unexpected error", e);
            safeClose(pluginManager, eventBus, client);
            System.exit(1);
        }
    }
    private static void safeClose(PluginManager pm, EventBus bus, Client client) {
        try {
            if (pm != null) {
                pm.shutdown();
            }
        } catch (Exception e) {
            LOGGER.error("Error while shutting down PluginManager", e);
        }
        try {
            if (bus != null) {
                bus.shutdown();
            }
        } catch (Exception e) {
            LOGGER.error("Error while shutting down EventBus", e);
        }
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            LOGGER.error("Error while closing Client", e);
        }
    }
}