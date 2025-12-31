package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.api.generator.MessageGenerator;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.message.Message;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.model.message.OnebotMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 这个接口定义了消息事件的基本功能。
 * 具体的实现位于 {@link MessageEvent} 类中。
 */
public interface IMessageEvent extends Event {
    /**
     * @return 获取消息类型，即消息发生的域，是一个 {@link MessageType}
     */
    MessageType getMessageType();

    /**
     * @return 获取当前消息对象
     */
    OnebotMessage getMessage();

    /**
     * 简单发送消息。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int send(JsonArray messages);

    /**
     * 简单发送消息。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int send(JsonObject... messages);

    /**
     * 发送消息并指定响应回调函数。
     * @param onFinish 接收一个 {@link Response} 对象作为参数的响应回调函数
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int send(Consumer<Response> onFinish, JsonArray messages);

    /**
     * 发送消息并指定响应回调函数。
     * @param onFinish 接收一个 {@link Response} 对象作为参数的响应回调函数
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int send(Consumer<Response> onFinish, JsonObject... messages);

    /**
     * 发送消息后等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @param prompt 通过 {@link MessageGenerator} 构建的消息内容，发送后等待用户回复
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    @Nullable List<Message> wait(JsonArray prompt);

    /**
     * 发送消息后等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @param prompt 通过 {@link MessageGenerator} 构建的消息内容，发送后等待用户回复
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    @Nullable List<Message> wait(JsonObject... prompt);

    /**
     * 等待用户回复，它会创建一个会话并挂起当前事件监听器。
     * <p>
     * 这个方法会阻塞执行事件监听器的线程，直到用户回复消息或会话超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * <p>
     * 注意: 在事件监听器结束后必须使用 {@link #finish()} 或其重载方法来结束会话。
     * @return 用户回复的内容，如果超时或发生错误则返回 null
     */
    @Nullable List<Message> waitSilent();

    /**
     * 发送消息并结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int finish(JsonArray messages);

    /**
     * 发送消息并结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    int finish(JsonObject... messages);

    /**
     * 结束会话，用于在 {@link #wait(JsonArray prompt)} 或其重载方法之后调用。
     */
    void finish();

    /**
     * 发送信息并返回获取响应的 {@link CompletableFuture} 对象。
     * <p>
     * 相比较于使用 {@link #sendAndWait(JsonArray)} 方法同步等待响应，这个方法不会阻塞当前线程。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 一个 {@link CompletableFuture} 对象，当响应到达时会被完成
     */
    @NotNull CompletableFuture<Response> sendFuture(JsonArray messages);

    /**
     * 发送信息并返回获取响应的 {@link CompletableFuture} 对象。
     * <p>
     * 相比较于使用 {@link #sendAndWait(JsonObject... messages)} 方法同步等待响应，这个方法不会阻塞当前线程。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 一个 {@link CompletableFuture} 对象，当响应到达时会被完成
     */
    @NotNull CompletableFuture<Response> sendFuture(JsonObject... messages);

    /**
     * 发送信息并同步等待响应。
     * <p>
     * 相比较于使用 {@link #sendFuture(JsonArray)} 方法异步获取响应，
     * 这个方法会阻塞当前线程直到响应到达或超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 消息对应的 {@link Response} 对象，包括状态和消息 ID
     */
    @Nullable Response sendAndWait(JsonArray messages);

    /**
     * 发送信息并同步等待响应。
     * <p>
     * 相比较于使用 {@link #sendFuture(JsonObject... messages)} 方法异步获取响应，
     * 这个方法会阻塞当前线程直到响应到达或超时，因此只能在标记了 {@code @Coroutines} 注解的处理器中使用。
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 消息对应的 {@link Response} 对象，包括状态和消息 ID
     */
    @Nullable Response sendAndWait(JsonObject... messages);

    /**
     * 设置会话超时时间。
     * @param seconds 超时时间，单位为秒。
     */
    void setTimeOut(int seconds);

    /**
     * 设置会话超时回调函数。
     * @param callback 回调函数，接受上一个消息事件作为参数。
     */
    void setOnTimeout(Consumer<MessageEvent> callback);

    /**
     * 设置会话结束回调函数。
     * @param callback 回调函数，接受上一个消息事件作为参数。
     */
    void setOnFinish(Consumer<MessageEvent> callback);

    /**
     * 获取当前会话的 {@link  MessageEvent} 对象。
     */
    MessageEvent getCurrentEvent();

    /**
     * 设置是否将消息作为转发消息发送。
     * <p>
     * 适用于较长的消息内容，可以将消息包装为转发消息发送。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    MessageEvent setSendForward(boolean sendForward);

    /**
     * 设置是否将消息作为回复发送。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    MessageEvent setSendReply(boolean sendReply);

    /**
     * 设置是否在发送消息时 @ 触发事件的用户。
     * <p>
     * 仅适用于下一次发送的消息，发送后会自动重置为默认值。
     */
    MessageEvent setSendAt(boolean sendAt);

    /**
     * @return 是否将消息作为转发消息发送
     * @see #setSendForward(boolean sendForward)
     */
    boolean isSendForward();

    /**
     * @return 是否将消息作为回复发送。
     * @see #setSendReply(boolean sendReply)
     */
    boolean isSendReply();

    /**
     * @return 是否在发送消息时 @ 触发事件的用户。
     * @see #setSendAt(boolean sendAt)
     */
    boolean isSendAt();

    /**
     * 重置发送选项为默认值。
     */
    void resetOptions();
}
