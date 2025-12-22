package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.type.exception.OnebotProtocolException;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static volatile Client instance;
    public static Client getInstance() {
        return Client.instance;
    }

    /**
     * Atomically swap the global active client.
     * Old client will be closed best-effort.
     */
    private static void setInstance(Client newInstance) {
        Client old = Client.instance;
        Client.instance = newInstance;
        if (old != null && old != newInstance) {
            try {
                // Avoid triggering reconnect loop for old client
                old.closed = true;
                old.closeConnection(1000, "Replaced by a new client");
            } catch (Exception e) {
                LOGGER.debug("Failed to close previous websocket client", e);
            }
        }
    }

    private static final int BASE_RECONNECT_INTERVAL_SECONDS = 10;
    private static final int MAX_RECONNECT_INTERVAL_SECONDS = 300;

    private final Gson GSON = new Gson();
    private final String accessToken;

    private ScheduledExecutorService reconnectExecutor;
    private ScheduledFuture<?> reconnectFuture;
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private volatile int reconnectAttempts = 0;

    /** True if close() was called intentionally. */
    private volatile boolean closed = false;

    public Client(URI serverUri, String accessToken) {
        super(serverUri, createHeaders(accessToken));
        this.accessToken = accessToken;
        Client.setInstance(this);
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
        LOGGER.info("Connected to {}", Config.getConfig().getWebSocketUrl());
        LOGGER.debug("Server status: {}", handshake.getHttpStatusMessage());
        this.closed = false;
        stopReconnectLoop();
    }

    @Override
    public void onMessage(String message) {
        try {
            LOGGER.debug("Received message: {}", message);
            // Parse
            try {
                RawParser.parse(message).fire();
            } catch (UnknownMessageTypeException e) {
                LOGGER.warn("Received unsupported message", e);
            } catch (OnebotProtocolException e) {
                LOGGER.error("Failed to parse message due to protocol error", e);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to processing message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (closed) {
            LOGGER.info("Connection closed.");
            stopReconnectLoop();
        } else {
            LOGGER.warn("Connection lost. Reason: {} - {}, remote = {}", code, reason, remote);
            startReconnectLoop();
        }
    }

    @Override
    public void onError(Exception e) {
        // InterruptedException is common when the library aborts close/reset during reconnect attempts
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            LOGGER.warn("WebSocket operation interrupted", e);
            return;
        }
        LOGGER.error("WebSocket error occurred", e);
    }

    @Override
    public void close() {
        this.closed = true;
        stopReconnectLoop();
        super.close();
    }

    private void startReconnectLoop() {
        if (!reconnecting.compareAndSet(false, true)) {
            return; // already reconnecting
        }
        // create executor if needed
        if (this.reconnectExecutor == null || this.reconnectExecutor.isShutdown()) {
            this.reconnectExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ws-reconnect");
                t.setDaemon(true);
                return t;
            });
        }
        // schedule first attempt immediately
        scheduleNextReconnect(0);
    }

    private void stopReconnectLoop() {
        reconnecting.set(false);
        reconnectAttempts = 0;
        if (reconnectFuture != null) {
            reconnectFuture.cancel(true);
            reconnectFuture = null;
        }
        if (reconnectExecutor != null && !reconnectExecutor.isShutdown()) {
            reconnectExecutor.shutdownNow();
        }
    }

    /**
     * Calculate the next backoff interval in seconds, it will increase exponentially with jitter.
     * <p>
     * [capped - jitter, capped + jitter], where capped = min(BASE_RECONNECT_INTERVAL_SECONDS * 2^exp, MAX_RECONNECT_INTERVAL_SECONDS),
     * <p>
     * exp = min(reconnectAttempts, 30), jitter = 20% of capped
     * @return the seconds to wait before the next reconnect attempt
     */
    private int nextBackoffSeconds() {
        // exponential backoff with jitter
        int exp = (int) Math.min(30, reconnectAttempts); // cap exponent
        long base = (long) BASE_RECONNECT_INTERVAL_SECONDS * (1L << Math.min(exp, 10)); // cap shift
        long capped = Math.min(base, MAX_RECONNECT_INTERVAL_SECONDS);
        // jitter +/-20%
        long jitter = (long) (capped * 0.2);
        long min = Math.max(0, capped - jitter);
        long max = capped + jitter;
        return (int) ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    private void scheduleNextReconnect(int delaySeconds) {
        if (reconnectExecutor == null || reconnectExecutor.isShutdown()) {
            reconnecting.set(false);
            return;
        }
        reconnectFuture = reconnectExecutor.schedule(this::doReconnectAttempt, delaySeconds, TimeUnit.SECONDS);
    }

    private void doReconnectAttempt() {
        if (closed) {
            stopReconnectLoop();
            return;
        }
        LOGGER.info("Attempting to reconnect... (attempt #{})", reconnectAttempts + 1);
        try {
            // IMPORTANT: WebSocketClient objects are not reusable.
            // Create a fresh client per attempt.
            URI uri = getURI();
            Client newClient = new Client(uri, this.accessToken);
            newClient.closed = false;
            newClient.connect();
            // success will be detected by onOpen() and stopReconnectLoop()
            reconnectAttempts++;
            int delay = nextBackoffSeconds();
            scheduleNextReconnect(delay);
        } catch (Exception e) {
            reconnectAttempts++;
            LOGGER.error("Reconnection attempt failed", e);
            int delay = nextBackoffSeconds();
            scheduleNextReconnect(delay);
        }
    }
}
