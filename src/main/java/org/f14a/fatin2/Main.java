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

/*
* Entry of the application
* */
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Load Config
            // Choose Command Line Args first
            String configPath = args.length > 0 ? args[0] : "config.yml";
            LOGGER.info("Loading config from: {}", configPath);
            Config config = ConfigLoader.load(configPath);

            System.setProperty("log.level", config.isDebug() ? "DEBUG" : "INFO");

            // Init event bus
            new EventBus();

            // Load plugins
            new PluginManager(config.isPluginAutoReload());


            URI serverUri = new URI(config.getWebSocketUrl());
            new Client(serverUri, config.getAccessToken());

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down Fatin2...");
                PluginManager.getInstance().shutdown();
                EventBus.getInstance().shutdown();
                Client.getInstance().close();
            }, "Shutdown-Hook-Thread"));

            LOGGER.info("Connecting to {}...", serverUri);
            Client.getInstance().connect();


            Thread.currentThread().join();

        } catch (URISyntaxException e) {
            LOGGER.error("Invalid WebSocket URL in config: {}", e.getMessage(), e);
            System.exit(1);
        } catch (InterruptedException e) {
            LOGGER.error("Main thread was interrupted", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception e) {
            LOGGER.error("Failed to start Fatin2 due to unexpected error", e);
            System.exit(1);
        }
    }
}