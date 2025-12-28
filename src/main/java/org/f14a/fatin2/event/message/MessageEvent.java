package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.session.SessionContext;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.model.Message;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.f14a.fatin2.model.message.OnebotMessage;
import org.f14a.fatin2.api.MessageGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 当客户端接收到消息时触发的事件基类。
 * <p>
 * 这个类提供了发送消息、等待用户输入以及会话管理的功能，继承这个类必须实现 {@link #send(JsonArray)} 方法来发送消息。
 */
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

    // 需要子类实现的抽象方法
    /**
     * 简单发送消息。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    abstract public int send(JsonArray messages);

    /**
     * 简单发送消息。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public int send(JsonObject ... messages) {
        return send(MessageGenerator.create(messages));
    }

    /**
     * 发送消息并指定响应回调函数。
     * @param onFinish 接收一个 {@link Response} 对象作为参数的响应回调函数
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public int send(Consumer<Response> onFinish, JsonArray messages) {
        int echo = send(messages);
        CompletableFuture<Response> future = EventBus.getResponseManager().registerFuture(Integer.toString(echo), 30);
        future.thenAccept(onFinish);
        return echo;
    }

    /**
     * 发送消息并指定响应回调函数。
     * @param onFinish 接收一个 {@link Response} 对象作为参数的响应回调函数
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public int send(Consumer<Response> onFinish, JsonObject ... messages) {
        return send(onFinish, MessageGenerator.create(messages));
    }

    /**
     * 发送消息后等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @param prompt 通过 {@link MessageGenerator} 构建的消息内容，发送后等待用户回复
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    public @Nullable Message[] wait(JsonArray prompt) {
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
     * 发送消息后等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @param prompt 通过 {@link MessageGenerator} 构建的消息内容，发送后等待用户回复
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    public @Nullable Message[] wait(JsonObject ... prompt) {
        return wait(MessageGenerator.create(prompt));
    }

    /**
     * 等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    public @Nullable Message[] waitSilent() {
        return wait((JsonArray) null);
    }

    /**
     * 发送消息并结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public int finish(JsonArray messages) {
        finish();
        return send(messages);
    }

    /**
     * 发送消息并结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public int finish(JsonObject ... messages) {
        finish();
        return send(messages);
    }

    /**
     * 结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     */
    public void finish() {
        if (this.sessionContext != null) {
            EventBus.getSessionManager().endSession(this.sessionContext.getSessionId());
            this.sessionContext = null;
        }
    }

    /**
     * 发送信息并返回获取响应的 {@link CompletableFuture} 对象。
     * <p>
     * 相比较于使用 {@link #sendAndWait(JsonArray)} 方法同步等待响应，这个方法不会阻塞当前线程。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 一个 {@link CompletableFuture} 对象，当响应到达时会被完成
     */
    public @NotNull CompletableFuture<Response> sendFuture(JsonArray messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }

    /**
     * 发送信息并返回获取响应的 {@link CompletableFuture} 对象。
     * <p>
     * 相比较于使用 {@link #sendAndWait(JsonObject... messages)} 方法同步等待响应，这个方法不会阻塞当前线程。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 一个 {@link CompletableFuture} 对象，当响应到达时会被完成
     */
    public @NotNull CompletableFuture<Response> sendFuture(JsonObject ... messages) {
        return EventBus.getResponseManager().registerFuture(String.valueOf(send(messages)), 30);
    }

    /**
     * 发送信息并同步等待响应。
     * <p>
     * 相比较于使用 {@link #sendFuture(JsonArray)} 方法异步获取响应，
     * 这个方法会阻塞当前线程直到响应到达或超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 消息对应的 {@link Response} 对象，包括状态和消息 ID
     */
    public @Nullable Response sendAndWait(JsonArray messages) {
        CompletableFuture<Response> future = sendFuture(messages);
        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.error("Error while waiting for response", e);
            return null;
        }
    }

    /**
     * 发送信息并同步等待响应。
     * <p>
     * 相比较于使用 {@link #sendFuture(JsonObject... messages)} 方法异步获取响应，
     * 这个方法会阻塞当前线程直到响应到达或超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 消息对应的 {@link Response} 对象，包括状态和消息 ID
     */
    public @Nullable Response sendAndWait(JsonObject ... messages) {
        return sendAndWait(MessageGenerator.create(messages));
    }

    /**
     * 设置会话超时时间。
     * @param seconds 超时时间，单位为秒。
     */
    public void setTimeOut(int seconds) {
        createSessionContext();
        this.sessionContext.setTimeout(seconds);
    }

    /**
     * 设置会话超时回调函数。
     * @param callback 回调函数，接受上一个消息事件作为参数。
     */
    public void setOnTimeout(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnTimeout(callback);
    }

    /**
     * 设置会话结束回调函数。
     * @param callback 回调函数，接受上一个消息事件作为参数。
     */
    public void setOnFinish(Consumer<MessageEvent> callback) {
        createSessionContext();
        this.sessionContext.setOnFinish(callback);
    }

    /**
     * 获取当前会话的 {@link  MessageEvent} 对象。
     */
    public MessageEvent getCurrentEvent() {
        createSessionContext();
        return this.sessionContext.getCurrentEvent();
    }

    /**
     * 设置是否将消息作为转发消息发送。
     * <p>
     * 适用于较长的消息内容，可以将消息包装为转发消息发送。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    public MessageEvent setSendForward(boolean sendForward) {
        this.sendForward = sendForward;
        return this;
    }

    /**
     * 设置是否将消息作为回复发送。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    public MessageEvent setSendReply(boolean sendReply) {
        this.sendReply = sendReply;
        return this;
    }

    /**
     * 设置是否在发送消息时 @ 触发事件的用户。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    public MessageEvent setSendAt(boolean sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    /**
     * @return 是否将消息作为转发消息发送
     * @see #setSendForward(boolean sendForward)
     */
    public boolean isSendForward() {
        return sendForward;
    }

    /**
     * @return 是否将消息作为回复发送。
     * @see #setSendReply(boolean sendReply)
     */
    public boolean isSendReply() {
        return sendReply;
    }

    /**
     * @return 是否在发送消息时 @ 触发事件的用户。
     * @see #setSendAt(boolean sendAt)
     */
    public boolean isSendAt() {
        return sendAt;
    }

    /**
     * 重置发送选项为默认值。
     */
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
