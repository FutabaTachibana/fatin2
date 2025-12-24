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
import java.util.concurrent.atomic.AtomicInteger;

public class Client extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static volatile Client instance;

    /*
     * Global reconnect loop state.
     * <p>
     * Why static?
     * WebSocketClient is not reusable, so we create a new Client per attempt. If reconnect state lives on instance,
     * it gets reset on every new object, and old instances may accidentally keep scheduling their own loops.
     */
    private static final AtomicBoolean RECONNECTING = new AtomicBoolean(false);
    private static final AtomicInteger RECONNECT_ATTEMPTS = new AtomicInteger(0);
    private static ScheduledExecutorService RECONNECT_EXECUTOR;
    private static ScheduledFuture<?> RECONNECT_FUTURE;

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
                // Mark old client as intentionally closed to prevent it from starting a new reconnect loop.
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
            return;
        }

        // Only the current active client is allowed to drive the global reconnect loop.
        if (Client.getInstance() != this) {
            LOGGER.debug("Ignoring onClose from non-active client (code={}, reason={}, remote={})", code, reason, remote);
            return;
        }

        LOGGER.warn("Connection lost. Reason: {} - {}, remote = {}", code, reason, remote);
        startReconnectLoop();
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

    private static void startReconnectLoop() {
        if (!RECONNECTING.compareAndSet(false, true)) {
            return; // already reconnecting
        }
        // create executor if needed
        if (RECONNECT_EXECUTOR == null || RECONNECT_EXECUTOR.isShutdown()) {
            RECONNECT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ws-reconnect");
                t.setDaemon(true);
                return t;
            });
        }
        // schedule first attempt immediately
        scheduleNextReconnect(0);
    }

    private static void stopReconnectLoop() {
        RECONNECTING.set(false);
        RECONNECT_ATTEMPTS.set(0);
        if (RECONNECT_FUTURE != null) {
            RECONNECT_FUTURE.cancel(true);
            RECONNECT_FUTURE = null;
        }
        if (RECONNECT_EXECUTOR != null && !RECONNECT_EXECUTOR.isShutdown()) {
            RECONNECT_EXECUTOR.shutdownNow();
        }
    }

    /**
     * Calculate the next backoff interval in seconds, it will increase exponentially with jitter.
     * <p>
     * [capped - jitter, capped + jitter], where capped = min(BASE_RECONNECT_INTERVAL_SECONDS * 2^exp, MAX_RECONNECT_INTERVAL_SECONDS),
     * <p>
     * exp = min(reconnectAttempts, 30), jitter = 20% of capped
     *
     * @return the seconds to wait before the next reconnect attempt
     */
    private static int nextBackoffSeconds() {
        // exponential backoff with jitter
        int exp = (int) Math.min(30, RECONNECT_ATTEMPTS.get()); // cap exponent
        long base = (long) BASE_RECONNECT_INTERVAL_SECONDS * (1L << Math.min(exp, 10)); // cap shift
        long capped = Math.min(base, MAX_RECONNECT_INTERVAL_SECONDS);
        // jitter +/-20%
        long jitter = (long) (capped * 0.2);
        long min = Math.max(0, capped - jitter);
        long max = capped + jitter;
        return (int) ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    private static void scheduleNextReconnect(int delaySeconds) {
        if (RECONNECT_EXECUTOR == null || RECONNECT_EXECUTOR.isShutdown()) {
            RECONNECTING.set(false);
            return;
        }
        RECONNECT_FUTURE = RECONNECT_EXECUTOR.schedule(Client::doReconnectAttempt, delaySeconds, TimeUnit.SECONDS);
    }

    private static void doReconnectAttempt() {
        Client current = Client.getInstance();
        if (current == null) {
            stopReconnectLoop();
            return;
        }
        if (current.closed) {
            stopReconnectLoop();
            return;
        }

        LOGGER.info("Attempting to reconnect... (attempt #{})", RECONNECT_ATTEMPTS.get() + 1);
        try {
            // IMPORTANT: WebSocketClient objects are not reusable.
            // Create a fresh client per attempt. Its constructor swaps the global instance.
            URI uri = current.getURI();
            Client newClient = new Client(uri, current.accessToken);
            newClient.closed = false;
            newClient.connect();

            // Note: success will be detected by onOpen() and stopReconnectLoop()
            RECONNECT_ATTEMPTS.incrementAndGet();
            int delay = nextBackoffSeconds();
            scheduleNextReconnect(delay);
        } catch (Exception e) {
            RECONNECT_ATTEMPTS.incrementAndGet();
            LOGGER.error("Reconnection attempt failed", e);
            int delay = nextBackoffSeconds();
            scheduleNextReconnect(delay);
        }
    }
}
