package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.Main;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.dispatcher.MessageDispatcher;
import org.f14a.fatin2.type.Exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.AbstractOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class Client extends WebSocketClient {
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
        Main.LOGGER.info("Connected to {}", Config.getConfig().getWebSocketUrl());
        Main.LOGGER.debug("Server status: {}", handshake.getHttpStatusMessage());
        this.closed = false;
    }

    @Override
    public void onMessage(String message) {
        try {
            Main.LOGGER.debug("Received message: {}", message);

            // Parse
            Map<?, ?> raw = gson.fromJson(message, Map.class);
            String postType = (String) raw.get("post_type");

            AbstractOnebotMessage parsedMessage;
            switch (postType) {
                case "message" -> parsedMessage = gson.fromJson(message, OnebotMessage.class);

                default -> throw new UnknownMessageTypeException("Unknown type of message: " + postType);
            }
            // Dispatch
            dispatcher.dispatch(parsedMessage);

        } catch (Exception e) {
            Main.LOGGER.error("Failed to processing message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(closed) {
            Main.LOGGER.info("Connection closed.");
        }
        else {
            Main.LOGGER.warn("Connection lost. Reason: {} - {}, remote = {}", code, reason, remote);
            // Attempt to reconnect
        }
    }

    @Override
    public void onError(Exception e) {
        Main.LOGGER.error("WebSocket error occurred", e);
    }

    @Override
    public void close() {
        this.closed = true;
        if (reconnectExecutor != null && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdownNow();
        }
        super.close();
        Main.LOGGER.info("WebSocket connection closed.");
    }

    // TODO: Reconnect logic

}
