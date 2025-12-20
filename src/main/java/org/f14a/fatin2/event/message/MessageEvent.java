package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.session.SessionContext;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class MessageEvent extends Event {
    private final OnebotMessage message;

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

    // Exactly abstract method to send message subclasses must override.
    abstract protected int sendOnly(JsonArray messages);
    protected int sendOnly(JsonObject ... messages) {
        return sendOnly(MessageGenerator.create(messages));
    }
    /**
     * A simple method to send messages, it cannot end the handler but can finish the session.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(JsonArray messages) {
        finishSession();
        return sendOnly(messages);
    }
    /**
     * A simple method to send messages, it cannot end the handler but can finish the session.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(JsonObject ... messages) {
        finishSession();
        return sendOnly(MessageGenerator.create(messages));
    }
    /**
     * Send messages and register a callback to handle the response.
     * @param onFinish the callback to handle the response.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(Consumer<Response> onFinish, JsonArray messages) {
        int echo = send(messages);
        CompletableFuture<Response> future = EventBus.getResponseManager().registerFuture(Integer.toString(echo), 30);
        future.thenAccept(onFinish);
        return echo;
    }
    /**
     * Send messages and register a callback to handle the response.
     * @param onFinish the callback to handle the response.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(Consumer<Response> onFinish, JsonObject ... messages) {
        return send(onFinish, MessageGenerator.create(messages));
    }
    /**
     * Wait for user input after sending a message, it will create a session and hang the handler until user reply or timeout.
     * It can be used only in coroutine handlers.
     * @param prompt the message created by MessageGenerator to send before waiting.
     * @return the user input message content, or null if timeout or error occurs.
     */
    public Message[] wait(JsonArray prompt) {
        if (prompt != null) {
            sendOnly(prompt);
        }
        createSessionContext();
        CompletableFuture<Message[]> future = this.sessionContext.waitForInput();
        Message[] reply;
        try {
            reply = future.get();
        } catch (Exception e) {
            return null;
        }
        return reply;
    }
    /**
     * Wait for user input after sending a message, it will create a session and hang the handler until user reply or timeout.
     * It can be used only in coroutine handlers.
     * @param prompt the message created by MessageGenerator to send before waiting.
     * @return the user input message content, or null if timeout or error occurs.
     */
    public Message[] wait(JsonObject ... prompt) {
        return wait(MessageGenerator.create(prompt));
    }
    /**
     * Wait for user input without sending any message.
     * @return the user input message content, or null if timeout or error occurs.
     */
    public Message[] waitSilent() {
        return wait((JsonArray) null);
    }
    // TODO: add wait(Consumer<Response> onFinish, JsonArray prompt) to track the message sent.
    /**
     * Send a message and get a CompletableFuture for the response.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return a CompletableFuture that will be completed with the Response.
     */
    public CompletableFuture<Response> sendFuture(JsonArray messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }
    /**
     * Send a message and get a CompletableFuture for the response.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return a CompletableFuture that will be completed with the Response.
     */
    public CompletableFuture<Response> sendFuture(JsonObject ... messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }
    /**
     * Send a message and wait for the response synchronously.
     * It will block the current thread until the response is received or timeout occurs, so use it with @Coroutines annotation.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return the response received, including status and message id.
     */
    public Response sendAndWait(JsonArray messages) {
        CompletableFuture<Response> future = sendFuture(messages);
        try {
            return future.get();
        } catch (Exception e) {
            EventBus.LOGGER.error("Error while waiting for response", e);
            return null;
        }
    }
    /**
     * Send a message and wait for the response synchronously.
     * It will block the current thread until the response is received or timeout occurs, so use it with @Coroutines annotation.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return the response received, including status and message id.
     */
    public Response sendAndWait(JsonObject ... messages) {
        return sendAndWait(MessageGenerator.create(messages));
    }

    /**
     * Finish the current session if exists.
     * Commonly it is called automatically after send() methods, but if you use wait
     */
    public void finishSession() {
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
            context.receiveInput(message.messages());
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
