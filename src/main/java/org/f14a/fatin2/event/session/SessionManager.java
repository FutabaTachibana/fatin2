package org.f14a.fatin2.event.session;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;

import java.util.Map;
import java.util.concurrent.*;

public class SessionManager {
    private final Map<String, SessionContext<?>> sessions;
    private final Map<String, String> userSessions;

    private final ScheduledExecutorService cleanupExecutor;

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r, "Session-Cleanup-Thread");
            thread.setDaemon(true);
            return thread;
        });
        this.cleanupExecutor.scheduleAtFixedRate(
                this::cleanupExpiredSessions, 30, 30, TimeUnit.SECONDS
        );

        EventBus.LOGGER.info("SessionManager initialized");
    }

    public <T extends Event> SessionContext<T> createSession(String userId, String scope) {
        String userKey = userId + "@" + scope;
        if (this.userSessions.containsKey(userKey)) {
            String oldSessionId = this.userSessions.get(userKey);
            endSession(oldSessionId);
        }
        String sessionId = ("session" + ThreadLocalRandom.current().nextInt(99999) + "_" + userKey);
        SessionContext<T> context = new SessionContext<>(sessionId, userId, scope);
        this.sessions.put(sessionId, context);
        this.userSessions.put(userKey, sessionId);
        EventBus.LOGGER.debug("Created new session {} for user {}", sessionId, userKey);
        return context;
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> SessionContext<T> getSession(String sessionId) {
        return (SessionContext<T>) this.sessions.get(sessionId);
    }
    @SuppressWarnings("unchecked")
    public <T extends Event> SessionContext<T> getSession(String useId, String scope) {
        String sessionId = this.userSessions.get(useId + "@" + scope);
        if(sessionId != null) {
            SessionContext<T> context = (SessionContext<T>) this.sessions.get(sessionId);
            if (context != null && context.isActive()) {
                return context;
            }
        }
        return null;
    }
    public boolean hasActiveSession(String useId, String scope) {
        return this.getSession(useId, scope) != null;
    }
    public void endSession(String sessionId) {
        SessionContext<?> context = this.sessions.remove(sessionId);
        if (context != null) {
            context.finish();
            String userKey = context.getUserId() + "@" + context.getScope();
            this.userSessions.remove(userKey);
            userSessions.remove(userKey);
            EventBus.LOGGER.debug("Ended session {}", sessionId);
        }
    }
    public void receiveInput(String useId, String scope, String input) {
        SessionContext<?> context = getSession(useId, scope);
        if (context != null) {
            context.receiveInput(input);
        }
    }
    // Cleanup of expired sessions lazily called every fixed interval
    public void cleanupExpiredSessions() {
        int clean = 0;
        for (Map.Entry<String, SessionContext<?>> entry : sessions.entrySet()) {
            SessionContext<?> context = entry.getValue();
            if (!context.isActive() || context.isExpired()) {
                endSession(entry.getKey());
                clean++;
            }
        }
        if (clean > 0) {
            EventBus.LOGGER.info("Cleaned up {} expired sessions", clean);
        }
    }
    public void shutdown() {
        EventBus.LOGGER.info("Shutting down SessionManager...");
        this.cleanupExecutor.shutdown();
        this.sessions.keySet().forEach(this::endSession);
        try {
            if (!this.cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                EventBus.LOGGER.warn("Cleanup executor did not terminate in the specified time.");
                this.cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            EventBus.LOGGER.error("Interrupted while waiting for cleanup executor to terminate", e);
            this.cleanupExecutor.shutdownNow();
        }
        EventBus.LOGGER.debug("SessionManager shut down complete.");
    }
}
