package org.f14a.fatin2.event.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface CommandEvent {
    String getCommand();
    CommandParser.Result getResult();
    String[] getArgs();
    OnebotMessage getMessage();
    boolean isAtBot();
    boolean hasReply();
    MessageType getMessageType();

    /**
     * A simple method to send messages.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int send(JsonArray messages);
    /**
     * A simple method to send messages.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int send(JsonObject... messages);
    /**
     * Send messages and register a callback to handle the response.
     * @param onFinish the callback to handle the response.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int send(Consumer<Response> onFinish, JsonArray messages);
    /**
     * Send messages and register a callback to handle the response.
     * @param onFinish the callback to handle the response.
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int send(Consumer<Response> onFinish, JsonObject... messages);
    /**
     * Wait for user input after sending a message, it will create a session and hang the handler until user reply or timeout.
     * It can be used only in coroutine handlers.
     * @param prompt the message created by MessageGenerator to send before waiting.
     * @return the user input message content, or null if timeout or error occurs.
     */
    Message[] wait(JsonArray prompt);
    /**
     * Wait for user input after sending a message, it will create a session and hang the handler until user reply or timeout.
     * It can be used only in coroutine handlers.
     * @param prompt the message created by MessageGenerator to send before waiting.
     * @return the user input message content, or null if timeout or error occurs.
     */
    Message[] wait(JsonObject... prompt);
    /**
     * Wait for user input without sending any message.
     * @return the user input message content, or null if timeout or error occurs.
     */
    Message[] waitSilent();
    /**
     * Send messages and finish the session. Use it after wait().
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int finish(JsonArray messages);
    /**
     * Send messages and finish the session. Use it after wait().
     * @param messages the message created by MessageGenerator to send.
     * @return the echo id of the message has sent, you can use it to track the message status.
     */
    int finish(JsonObject... messages);
    /**
     * Finish the current session if exists.
     */
    void finish();
    /**
     * Send a message and get a CompletableFuture for the response.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return a CompletableFuture that will be completed with the Response.
     */
    CompletableFuture<Response> sendFuture(JsonArray messages);
    /**
     * Send a message and get a CompletableFuture for the response.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return a CompletableFuture that will be completed with the Response.
     */
    CompletableFuture<Response> sendFuture(JsonObject... messages);
    /**
     * Send a message and wait for the response synchronously.
     * It will block the current thread until the response is received or timeout occurs, so use it with @Coroutines annotation.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return the response received, including status and message id.
     */
    Response sendAndWait(JsonArray messages);
    /**
     * Send a message and wait for the response synchronously.
     * It will block the current thread until the response is received or timeout occurs, so use it with @Coroutines annotation.
     * @param messages the message created by MessageGenerator to send before waiting.
     * @return the response received, including status and message id.
     */
    Response sendAndWait(JsonObject... messages);

    /**
     * Set the timeout for the session in seconds.
     * @param seconds the timeout in seconds.
     */
    void setTimeOut(int seconds);
    /**
     * Set the callback function when the session times out.
     * @param callback the callback function.
     */
    void setOnTimeout(Consumer<MessageEvent> callback);
    /**
     * Set the callback function when the session finishes.
     * @param callback the callback function.
     */
    void setOnFinish(Consumer<MessageEvent> callback);
    /**
     * Get the MessageEvent of the current session, it differs to this event in coroutine handlers.
     * @return the MessageEvent of the current session.
     */
    MessageEvent getCurrentEvent();

}
