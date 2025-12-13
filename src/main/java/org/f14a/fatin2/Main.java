package org.f14a.fatin2;

import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.config.ConfigLoader;
import org.f14a.fatin2.dispatcher.MessageDispatcher;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.handler.GroupMessageHandler;
import org.f14a.fatin2.handler.PrivateMessageHandler;
import org.f14a.fatin2.plugin.PluginLoader;
import org.f14a.fatin2.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/*
* Entry of the application
* */
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Load Config
            // Choose Command Line Args fist
            String configPath = args.length > 0 ? args[0] : "config.yml";
            LOGGER.info("Loading config from: {}", configPath);
            Config config = ConfigLoader.load(configPath);

            System.setProperty("log.level", config.isDebug() ? "DEBUG" : "INFO");

            // Load plugins
            PluginManager pluginManager = new PluginManager(config.isPluginAutoReload());

            // Init message dispatcher
            MessageDispatcher dispatcher = new MessageDispatcher();

            // Init event bus
            new EventBus();

//            // Init Handlers
//            dispatcher.register(new PrivateMessageHandler());
//            dispatcher.register(new GroupMessageHandler());

            URI serverUri = new URI(config.getWebSocketUrl());
            Client client = new Client(serverUri, config.getAccessToken(), dispatcher);

            // Register closure hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down Fatin2...");
                // pluginManager.shutdown();
                client.close();
            }));

            LOGGER.info("Connecting to {}...", serverUri);
            client.connect();

            Thread.currentThread().join();

        } catch (Exception e) {
            LOGGER.error("Failed to start fatin2", e);
            System.exit(1);
        }
    }
}