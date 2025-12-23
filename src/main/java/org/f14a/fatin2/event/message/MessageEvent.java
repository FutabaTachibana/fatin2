package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.session.SessionContext;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.f14a.fatin2.util.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class MessageEvent extends Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEvent.class);

    private final OnebotMessage message;
    private final MessageType messageType;

    private SessionContext<MessageEvent> sessionContext;
    private boolean sendForward = false;
    private boolean sendReply = false;
    private boolean sendAt = false;

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
    /**
     * A simple method to send messages.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    abstract public int send(JsonArray messages);
    /**
     * A simple method to send messages.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int send(JsonObject ... messages) {
        return send(MessageGenerator.create(messages));
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
            send(prompt);
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
    /**
     * Send messages and finish the session. Use it after wait().
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int finish(JsonArray messages) {
        finish();
        return send(messages);
    }
    /**
     * Send messages and finish the session. Use it after wait().
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    public int finish(JsonObject ... messages) {
        finish();
        return send(messages);
    }
    /**
     * Finish the current session if exists.
     */
    public void finish() {
        if (this.sessionContext != null) {
            EventBus.getSessionManager().endSession(this.sessionContext.getSessionId());
            this.sessionContext = null;
        }
    }
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
            LOGGER.error("Error while waiting for response", e);
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
     * Set the timeout for the session in seconds.
     * @param seconds the timeout in seconds.
     */
    public void setTimeOut(int seconds) {
        createSessionContext();
        this.sessionContext.setTimeout(seconds);
    }
    /**
     * Set the callback function when the session times out.
     * @param callback the callback function.
     */
    public void setOnTimeout(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnTimeout(callback);
    }
    /**
     * Set the callback function when the session finishes.
     * @param callback the callback function.
     */
    public void setOnFinish(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnFinish(callback);
    }
    /**
     * Get the MessageEvent of the current session, it differs to this event in coroutine handlers.
     * @return the MessageEvent of the current session.
     */
    public MessageEvent getCurrentEvent() {
        createSessionContext();
        return this.sessionContext.getCurrentEvent();
    }
    /**
     * Set whether the message will be sent as a forward message, it is suitable for long messages.
     * @param sendForward true to send as a forward message, false otherwise.
     */
    public void setSendForward(boolean sendForward) {
        this.sendForward = sendForward;
    }
    /**
     * Set whether the message will be sent as a reply.
     * @param sendReply true to send as a reply, false otherwise.
     */
    public void setSendReply(boolean sendReply) {
        this.sendReply = sendReply;
    }
    /**
     * Set whether the message will be sent at sender.
     * @param sendAt true to send at sender, false otherwise.
     */
    public void setSendAt(boolean sendAt) {
        this.sendAt = sendAt;
    }
    /**
     * Return true if the message will be sent as a forward message.
     * @return true if the message will be sent as a forward message.
     */
    public boolean isSendForward() {
        return sendForward;
    }
    /**
     * Return true if the message wille be sent as a reply.
     * @return true if the message will be sent as a reply.
     */
    public boolean isSendReply() {
        return sendReply;
    }
    /**
     * Return true if the message will be sent at sender.
     * @return true if the message will be sent at sender.
     */
    public boolean isSendAt() {
        return sendAt;
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
