package org.f14a.fatin2;

import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.config.ConfigLoader;
import org.f14a.fatin2.dispatcher.MessageDispatcher;
import org.f14a.fatin2.handler.GroupMessageHandler;
import org.f14a.fatin2.handler.PrivateMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/*
* Entry of the application
* */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Load Config
            // Choose Command Line Args fist
            String configPath = args.length > 0 ? args[0] : "config.yml";
            logger.info("Loading config from: {}", configPath);
            Config config = ConfigLoader.load(configPath);

            // Init message dispatcher
            MessageDispatcher dispatcher = new MessageDispatcher();

            // Init Handlers
            dispatcher.register(new PrivateMessageHandler());
            dispatcher.register(new GroupMessageHandler());

            URI serverUri = new URI(config.getWebSocketUrl());
            Client client = new Client(serverUri, config.getAccessToken(), dispatcher);

            logger.info("Connecting to {}...", serverUri);
            client.connect();

            Thread.currentThread().join();

        } catch (Exception e) {
            logger.error("Failed to start bot", e);
            System.exit(1);
        }
    }
}