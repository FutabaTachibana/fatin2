package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.session.SessionContext;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.model.message.Message;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.f14a.fatin2.model.message.OnebotMessage;
import org.f14a.fatin2.api.MessageGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 当客户端接收到消息时触发的事件基类。
 * <p>
 * 这个类提供了发送消息、等待用户输入以及会话管理的功能，继承这个类必须实现 {@link #send(JsonArray)} 方法来发送消息。
 */
public abstract class MessageEvent implements IMessageEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEvent.class);

    private final OnebotMessage message;

    private SessionContext<MessageEvent> sessionContext;
    private boolean sendForward = false;
    private boolean sendReply = false;
    private boolean sendAt = false;

    @Deprecated
    public MessageEvent(OnebotMessage message, MessageType messageType) {
        this.message = message;
    }

    public MessageEvent(OnebotMessage message) {
        this.message = message;
    }

//    @Override
//    public abstract MessageType getMessageType();

    @Override
    public OnebotMessage getMessage() {
        return message;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    // 需要子类实现的抽象方法
//    @Override
//    public abstract int send(JsonArray messages);

    @Override
    public int send(JsonObject... messages) {
        return send(MessageGenerator.create(messages));
    }

    @Override
    public int send(Consumer<Response> onFinish, JsonArray messages) {
        int echo = send(messages);
        CompletableFuture<Response> future = EventBus.getResponseManager().registerFuture(Integer.toString(echo), 30);
        future.thenAccept(onFinish);
        return echo;
    }

    @Override
    public int send(Consumer<Response> onFinish, JsonObject... messages) {
        return send(onFinish, MessageGenerator.create(messages));
    }

    @Override
    public @Nullable List<Message> wait(JsonArray prompt) {
        if (prompt != null) {
            send(prompt);
        }
        createSessionContext();
        CompletableFuture<List<Message>> future = this.sessionContext.waitForInput();
        List<Message> reply;
        try {
            reply = future.get();
        } catch (Exception e) {
            return null;
        }
        return reply;
    }

    @Override
    public @Nullable List<Message> wait(JsonObject... prompt) {
        return wait(MessageGenerator.create(prompt));
    }

    @Override
    public @Nullable List<Message> waitSilent() {
        return wait((JsonArray) null);
    }

    @Override
    public int finish(JsonArray messages) {
        finish();
        return send(messages);
    }

    @Override
    public int finish(JsonObject... messages) {
        finish();
        return send(messages);
    }

    @Override
    public void finish() {
        if (this.sessionContext != null) {
            EventBus.getSessionManager().endSession(this.sessionContext.getSessionId());
            this.sessionContext = null;
        }
    }

    @Override
    public @NotNull CompletableFuture<Response> sendFuture(JsonArray messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }

    @Override
    public @NotNull CompletableFuture<Response> sendFuture(JsonObject... messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }

    @Override
    public @Nullable Response sendAndWait(JsonArray messages) {
        CompletableFuture<Response> future = sendFuture(messages);
        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.error("Error while waiting for response", e);
            return null;
        }
    }

    @Override
    public @Nullable Response sendAndWait(JsonObject... messages) {
        return sendAndWait(MessageGenerator.create(messages));
    }

    @Override
    public void setTimeOut(int seconds) {
        createSessionContext();
        this.sessionContext.setTimeout(seconds);
    }

    @Override
    public void setOnTimeout(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnTimeout(callback);
    }

    @Override
    public void setOnFinish(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnFinish(callback);
    }

    @Override
    public MessageEvent getCurrentEvent() {
        createSessionContext();
        return this.sessionContext.getCurrentEvent();
    }

    @Override
    public MessageEvent setSendForward(boolean sendForward) {
        this.sendForward = sendForward;
        return this;
    }

    @Override
    public MessageEvent setSendReply(boolean sendReply) {
        this.sendReply = sendReply;
        return this;
    }

    @Override
    public MessageEvent setSendAt(boolean sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    @Override
    public boolean isSendForward() {
        return sendForward;
    }

    @Override
    public boolean isSendReply() {
        return sendReply;
    }

    @Override
    public boolean isSendAt() {
        return sendAt;
    }

    @Override
    public void resetOptions() {
        this.sendAt = false;
        this.sendForward = false;
        this.sendReply = false;
    }

    /**
     * 在触发事件前，检查是否存在活动会话，
     * 如果存在，则不将事件分发至 {@link EventBus} ，而是将事件传递给 {@link SessionContext} 。
     */
    @Override
    public void fire() {
        SessionManager sessionManager = EventBus.getSessionManager();
        long scope = this.getMessageType() == MessageType.PRIVATE ?
                message.userId() : ((GroupOnebotMessage) this.getMessage()).groupId();
        SessionContext<MessageEvent> context = sessionManager.getSession(message.userId(), scope);
        // Check if an active session exists
        // Do NOT dispatch to other handlers if in session
        if (context != null) {
            context.setCurrentEvent(this);
            context.receiveInput(message.messages());
        } else {
            IMessageEvent.super.fire();
        }
    }

    private void createSessionContext() {
        if (this.sessionContext == null) {
            this.sessionContext = EventBus.getSessionManager().createSession(
                    message.userId(),
                    this.getMessageType() == MessageType.PRIVATE
                            ? message.userId()
                            : ((GroupOnebotMessage) this.getMessage()).groupId()
            );
        }
    }
}
