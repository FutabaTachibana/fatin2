package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.Main;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class Client extends WebSocketClient {
    private static Client instance;
    public static Client getInstance() {
        return Client.instance;
    }

    private static final int RECONNECT_INTERVAL = 10; // seconds

    private final Gson gson = new Gson();
    private final String accessToken;
    private ScheduledExecutorService reconnectExecutor;
    private volatile boolean closed = false;

    public Client(URI serverUri, String accessToken) {
        super(serverUri, createHeaders(accessToken));
        this.accessToken = accessToken;
        Client.instance = this;
    }

    private static Map<String, String> createHeaders(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        if (accessToken != null && !accessToken.isEmpty()) {
            headers.put("Authorization", "Bearer " + accessToken);
        }
        headers.put("Content-Type", "application/json");
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
            try {
                RawParser.parseRaw(message).fire();
            } catch (NullPointerException e) {
                Main.LOGGER.info("Received unsupported message");
            }



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
