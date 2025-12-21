package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A utility class for sending messages.
 */
public class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    private static final Gson GSON = new Gson();
    /**
     * Send a private message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendPrivate(long userId, JsonArray message){
        JsonObject jsonObject = RequestGenerator.builder().userId(userId).message(message).build();
        int echo = jsonObject.hashCode();
        String request = GSON.toJson(Map.of(
                "action", "send_private_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        LOGGER.debug("Sending private message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
    /**
     * Send a group message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendGroup(long groupId, JsonArray message){
        JsonObject jsonObject = RequestGenerator.builder().groupId(groupId).message(message).build();
        int echo = jsonObject.hashCode();
        String request = GSON.toJson(Map.of(
                "action", "send_group_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        LOGGER.debug("Sending group message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
    // Unavailable in OneBot v11
    public static int sendPrivateForward(long userId, JsonArray messages){
        JsonObject jsonObject = RequestGenerator.builder().userId(userId).messages(messages).build();
        int echo = jsonObject.hashCode();
        String request = GSON.toJson(Map.of(
                "action", "send_private_forward_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        LOGGER.debug("Sending private forward message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
    // Unavailable now
    public static int sendGroupForward(long groupId, JsonArray messages){
        JsonObject jsonObject = RequestGenerator.builder().groupId(groupId).messages(messages)
                .add("news", GSON.toJsonTree(List.of(Map.of("text", "news")))).add("prompt", "prompt")
                .add("summary", "summary").add("source", "source").build();
        int echo = jsonObject.hashCode();
        String request = GSON.toJson(Map.of(
                "action", "send_group_forward_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        LOGGER.debug("Sending group forward message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
    public static int replyGroupMessage(long groupId, long messageId, JsonArray messages) {
        MessageGenerator.MessageBuilder mb = MessageGenerator.builder().reply(messageId);
        messages.forEach(msg -> mb.addSegment(msg.getAsJsonObject()));
        return MessageSender.sendGroup(groupId, mb.build());
    }
    public static int replyGroupMessage(long groupId, long messageId, JsonObject ... messages) {
        return replyGroupMessage(groupId, messageId, MessageGenerator.create(messages));
    }
    public static int replyPrivateMessage(long userId, long messageId, JsonArray messages) {
        MessageGenerator.MessageBuilder mb = MessageGenerator.builder().reply(messageId);
        messages.forEach(msg -> mb.addSegment(msg.getAsJsonObject()));
        return MessageSender.sendPrivate(userId, mb.build());
    }
    public static int replyPrivateMessage(long userId, long messageId, JsonObject ... messages) {
        return replyPrivateMessage(userId, messageId, MessageGenerator.create(messages));
    }
}
