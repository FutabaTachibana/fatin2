package org.f14a.fatin2.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.dispatcher.MessageDispatcher;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final int RECONNECT_INTERVAL = 10; // seconds

    private final Gson gson = new Gson();
    private final String accessToken;
    private final MessageDispatcher dispatcher;
    private ScheduledExecutorService reconnectExecutor;
    private volatile boolean closed = false;

    public Client(URI serverUri, String accessToken, MessageDispatcher dispatcher) {
        super(serverUri, createHeaders(accessToken));
        this.accessToken = accessToken;
        this.dispatcher = dispatcher;
    }

    private static Map<String, String> createHeaders(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        if (accessToken != null && !accessToken.isEmpty()) {
            headers.put("Authorization", "Bearer " + accessToken);
        }
        return headers;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("Connected to {}", Config.getConfig().getWebSocketUrl());
        logger.info("Server status: {}", handshake.getHttpStatusMessage());
        this.closed = false;
    }

    @Override
    public void onMessage(String message) {
        try {
            logger.debug("Received message: {}", message);

            // Parse
            OnebotMessage onebotMessage = gson.fromJson(message, OnebotMessage.class);

            // Dispatch
            dispatcher.dispatch(onebotMessage);

        } catch (Exception e) {
            logger.error("Failed to processing message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(closed) {
            logger.info("Connection closed.");
        }
        else {
            logger.warn("Connection lost. Reason: {} - {}, remote = {}", code, reason, remote);
            // Attempt to reconnect
        }
    }

    @Override
    public void onError(Exception e) {
        logger.info("WebSocket error occurred", e);
    }

    @Override
    public void close() {
        this.closed = true;
        if (reconnectExecutor != null && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdownNow();
        }
        super.close();
        logger.info("WebSocket connection closed by client.");
    }

}
