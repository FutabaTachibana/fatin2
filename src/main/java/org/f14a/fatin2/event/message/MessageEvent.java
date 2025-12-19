package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.session.SessionContext;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class MessageEvent extends Event {
    private final OnebotMessage message;

    public enum MessageType {
        PRIVATE,
        GROUP
    }

    private final MessageType messageType;
    private SessionContext<MessageEvent> sessionContext;

    public MessageEvent(OnebotMessage message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public OnebotMessage getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * A simple method to send messages, it cannot end the handler but can finish the session.
     * @param message the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(JsonArray message) {
        finishSession();
        return sendOnly(message);
    }
    public int send(JsonObject ... messages) {
        finishSession();
        return sendOnly(MessageGenerator.create(messages));
    }
    // Exactly abstract method to send message subclasses must override.
    abstract public int sendOnly(JsonArray message);
    public int sendOnly(JsonObject ... messages) {
        return sendOnly(MessageGenerator.create(messages));
    }
    /**
     * Wait for user input after sending a message, it will create a session and hang the handler until user reply or timeout.
     * @param prompt the message created by MessageGenerator to send before waiting.
     * @return the user input message content, or null if timeout or error occurs.
     */
    public String wait(JsonArray prompt) {
        sendOnly(prompt);
        createSessionContext();
        CompletableFuture<String> future = this.sessionContext.waitForInput();
        String reply;
        try {
            reply = future.get();
        } catch (Exception e) {
            return null;
        }
        return reply;
    }
    public String wait(JsonObject ... prompts) {
        return wait(MessageGenerator.create(prompts));
    }
    /**
     * Wait for user input without sending any message.
     * @return the user input message content, or null if timeout or error occurs.
     */
    public String waitSilent() {
        return wait((JsonArray) null);
    }
    protected void finishSession() {
        if (this.sessionContext != null) {
            EventBus.getSessionManager().endSession(this.sessionContext.getSessionId());
            this.sessionContext = null;
        }
    }
    public void setTimeOut(int seconds) {
        createSessionContext();
        this.sessionContext.setTimeout(seconds);
    }
    public void setOnTimeout(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnTimeout(callback);
    }
    public void setOnFinish(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnFinish(callback);
    }
    public MessageEvent getSessionEvent() {
        createSessionContext();
        return this.sessionContext.getCurrentEvent();
    }

    @Override
    public void fire() {
        SessionManager sessionManager = EventBus.getSessionManager();
        long scope = messageType == MessageType.PRIVATE ?
                message.userId() : ((GroupOnebotMessage) this.getMessage()).groupId();
        SessionContext<MessageEvent> context = sessionManager.getSession(message.userId(), scope);
        // Check if an active session exists
        // Do NOT dispatch to other handlers if in session
        if (context != null) {
            context.setCurrentEvent(this);
            context.receiveInput(message.parse());
        } else {
            super.fire();
        }
    }
    private void createSessionContext() {
        if (this.sessionContext == null) {
            this.sessionContext = EventBus.getSessionManager().createSession(
                    message.userId(),
                    messageType == MessageType.PRIVATE
                            ? message.userId()
                            : ((GroupOnebotMessage) this.getMessage()).groupId()
            );
        }
    }
}
