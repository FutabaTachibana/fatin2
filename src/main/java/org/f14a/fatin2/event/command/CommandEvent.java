package org.f14a.fatin2.event.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.command.parse.CommandParseResult;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.model.Message;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.model.message.OnebotMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface CommandEvent {
    String getCommand();
    CommandParseResult getResult();
    String[] getArgs();
    OnebotMessage getMessage();
    boolean isAtBot();
    boolean hasReply();
    MessageType getMessageType();

    /**
     * @see MessageEvent#send(JsonArray messages)
     */
    int send(JsonArray messages);

    /**
     * @see MessageEvent#send(JsonObject... messages)
     */
    int send(JsonObject... messages);

    /**
     * @see MessageEvent#send(Consumer onFinish, JsonArray messages)
     */
    int send(Consumer<Response> onFinish, JsonArray messages);

    /**
     * @see MessageEvent#send(Consumer onFinish, JsonObject... messages)
     */
    int send(Consumer<Response> onFinish, JsonObject... messages);

    /**
     * @see MessageEvent#wait(JsonArray prompt)
     */
    Message[] wait(JsonArray prompt);

    /**
     * @see MessageEvent#wait(JsonObject... prompt)
     */
    Message[] wait(JsonObject... prompt);

    /**
     * @see MessageEvent#waitSilent()
     */
    Message[] waitSilent();

    /**
     * @see MessageEvent#finish(JsonArray messages)
     */
    int finish(JsonArray messages);

    /**
     * @see MessageEvent#finish(JsonObject... messages)
     */
    int finish(JsonObject... messages);

    /**
     * @see MessageEvent#finish()
     */
    void finish();

    /**
     * @see MessageEvent#sendFuture(JsonArray messages) ()
     */
    CompletableFuture<Response> sendFuture(JsonArray messages);

    /**
     * @see MessageEvent#sendFuture(JsonObject... messages) ()
     */
    CompletableFuture<Response> sendFuture(JsonObject... messages);

    /**
     * @see MessageEvent#sendAndWait(JsonArray messages)
     */
    Response sendAndWait(JsonArray messages);

    /**
     * @see MessageEvent#sendAndWait(JsonObject... messages)
     */
    Response sendAndWait(JsonObject... messages);

    /**
     * @see MessageEvent#setTimeOut(int seconds)
     */
    void setTimeOut(int seconds);

    /**
     * @see MessageEvent#setOnTimeout(Consumer callback)
     */
    void setOnTimeout(Consumer<MessageEvent> callback);

    /**
     * @see MessageEvent#setOnFinish(Consumer callback)
     */
    void setOnFinish(Consumer<MessageEvent> callback);

    /**
     * @see MessageEvent#getCurrentEvent()
     */
    MessageEvent getCurrentEvent();

    /**
     * @see MessageEvent#setSendForward(boolean sendForward)
     */
    MessageEvent setSendForward(boolean sendForward);

    /**
     * @see MessageEvent#setSendReply(boolean sendReply)
     */
    MessageEvent setSendReply(boolean sendReply);

    /**
     * @see MessageEvent#setSendAt(boolean sendAt)
     */
    MessageEvent setSendAt(boolean sendAt);

    /**
     * @see MessageEvent#isSendForward()
     */
    boolean isSendForward();

    /**
     * @see MessageEvent#isSendReply()
     */
    boolean isSendReply();

    /**
     * @see MessageEvent#isSendAt()
     */
    boolean isSendAt();

    /**
     * @see MessageEvent#resetOptions()
     */
    void resetOptions();

}
