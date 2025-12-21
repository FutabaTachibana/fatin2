package org.f14a.fatin2.event.session;

import org.f14a.fatin2.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class SessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
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

        LOGGER.info("SessionManager initialized");
    }

    /**
     * Create a new session for a user in a specific scope.
     * @param userId ID of the user triggering the session.
     * @param scope Scope of the session, is chosen from group ID and user ID.
     * @return The created SessionContext.
     * @param <T> The type of event associated with the session.
     */
    public <T extends Event> SessionContext<T> createSession(long userId, long scope) {
        String userKey = userId + "@" + scope;
        if (this.userSessions.containsKey(userKey)) {
            String oldSessionId = this.userSessions.get(userKey);
            endSession(oldSessionId);
        }
        String sessionId = ("session" + ThreadLocalRandom.current().nextInt(99999) + "_" + userKey);
        SessionContext<T> context = new SessionContext<>(sessionId, userId, scope);
        this.sessions.put(sessionId, context);
        this.userSessions.put(userKey, sessionId);
        LOGGER.debug("Created new session {} for user {}", sessionId, userKey);
        return context;
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> SessionContext<T> getSession(String sessionId) {
        SessionContext<T> context = (SessionContext<T>) this.sessions.get(sessionId);
        if (context != null && context.isActive()) {
            return context;
        }
        return null;
    }
    public <T extends Event> SessionContext<T> getSession(long useId, long scope) {
        String sessionId = this.userSessions.get(useId + "@" + scope);
        if(sessionId != null) {
            return getSession(sessionId);
        }
        return null;
    }
    public void endSession(String sessionId) {
        SessionContext<?> context = this.sessions.remove(sessionId);
        if (context != null) {
            context.finish();
            String userKey = context.getUserId() + "@" + context.getScope();
            this.userSessions.remove(userKey);
            userSessions.remove(userKey);
            LOGGER.debug("Ended session {}", sessionId);
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
            LOGGER.info("Cleaned up {} expired sessions", clean);
        }
    }
    public void shutdown() {
        LOGGER.info("Shutting down SessionManager...");
        this.cleanupExecutor.shutdown();
        this.sessions.keySet().forEach(this::endSession);
        try {
            if (!this.cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.warn("Cleanup executor did not terminate in the specified time.");
                this.cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for cleanup executor to terminate", e);
            this.cleanupExecutor.shutdownNow();
        }
        LOGGER.debug("SessionManager shut down complete.");
    }
}
