package org.f14a.fatin2.event.session;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Session context records a state of user session
 * @param <T> the type of event
 */
public class SessionContext<T extends Event> {
    private final String sessionId;
    private final long userId;
    private final long scope;
    private final long createTime;
    private long lastActiveTime;
    private int timeout; // in seconds

    private CompletableFuture<String> waitingFuture;
    private boolean active;

    // Callbacks
    private Consumer<T> onTimeout;
    private Consumer<T> onFinish;
    private T currentEvent;

    public SessionContext(String sessionId, long userId, long scope) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.scope = scope;
        this.createTime = System.currentTimeMillis();
        this.lastActiveTime = this.createTime;
        this.timeout = 60;
        this.active = true;
    }
    public CompletableFuture<String> waitForInput() {
        this.lastActiveTime = System.currentTimeMillis();
        this.waitingFuture = new CompletableFuture<>();

        this.waitingFuture.orTimeout(this.timeout, TimeUnit.SECONDS).exceptionally(ex -> {
            if (ex instanceof TimeoutException) {
                EventBus.LOGGER.info("Session {} timed out", this.sessionId);
                handleTimeout();
            }
            return null;
        });
        return this.waitingFuture;
    }
    public void receiveInput(String input) {
        this.lastActiveTime = System.currentTimeMillis();
        if (this.waitingFuture != null && !this.waitingFuture.isDone()) {
            this.waitingFuture.complete(input);
        }
    }
    public void finish() {
        this.active = false;
        if (this.waitingFuture != null && !this.waitingFuture.isDone()) {
            this.waitingFuture.completeExceptionally(new IllegalStateException("Session finished"));
        }
        if (this.onFinish != null && this.currentEvent != null) {
            this.onFinish.accept(this.currentEvent);
        }
    }

    private void handleTimeout() {
        this.active = false;
        if (this.onTimeout != null && this.currentEvent != null) {
            this.onTimeout.accept(this.currentEvent);
        }
    }
    // Getters and Setters
    public String getSessionId() {
        return this.sessionId;
    }
    public long getUserId() {
        return this.userId;
    }
    public long getScope() {
        return this.scope;
    }
    public boolean isActive() {
        return this.active;
    }
    public boolean isExpired() {
        return System.currentTimeMillis() - this.lastActiveTime > this.timeout * 1000L;
    }
    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }
    public int getTimeout() {
        return this.timeout;
    }
    public void setOnTimeout(Consumer<T> onTimeout) {
        this.onTimeout = onTimeout;
    }
    public void setOnFinish(Consumer<T> onFinish) {
        this.onFinish = onFinish;
    }
    public void setCurrentEvent(T event) {
        this.currentEvent = event;
    }
    public T getCurrentEvent(){
        return this.currentEvent;
    }
    public long getCreateTime() {
        return this.createTime;
    }
    public long getLastActiveTime() {
        return this.lastActiveTime;
    }
}
