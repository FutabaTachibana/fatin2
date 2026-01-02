package org.f14a.fatin2;

import lombok.extern.slf4j.Slf4j;
import org.f14a.fatin2.config.ConfigManager;
import org.f14a.fatin2.websocket.Client;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.PluginManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
* Entry of the application
*/
@Slf4j
public class Main {
    static {
        Config config = ConfigManager.getGlobalConfig();
        System.setProperty("log.level", config.isDebug() ? "DEBUG" : "INFO");
    }

    public static void main(String[] args) {
        // Load config from working directory (config.yml)
        CountDownLatch stopLatch = new CountDownLatch(1);
        EventBus eventBus = null;
        PluginManager pluginManager = null;
        Client client = null;
        try {
            log.info("Loading config from working directory (config.yml)");
            Config config = ConfigManager.getGlobalConfig();
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
            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down Fatin2...");
                safeClose(finalPluginManager, finalEventBus, Client.getInstance());
                stopLatch.countDown();
            }, "Shutdown-Hook-Thread"));

            log.info("Connecting to {}...", serverUri);
            client.connect();
            // Wait until shutdown
            stopLatch.await();

        } catch (URISyntaxException e) {
            log.error("Invalid WebSocket URL in config: {}", e.getMessage(), e);
            safeClose(pluginManager, eventBus, client);
            System.exit(1);
        }  catch (Exception e) {
            log.error("Failed to start Fatin2 due to unexpected error", e);
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
            log.error("Error while shutting down PluginManager", e);
        }
        try {
            if (bus != null) {
                bus.shutdown();
            }
        } catch (Exception e) {
            log.error("Error while shutting down EventBus", e);
        }
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            log.error("Error while closing Client", e);
        }
    }
}