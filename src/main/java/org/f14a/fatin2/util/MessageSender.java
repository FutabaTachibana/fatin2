package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Send a private forward message.
     * <p>
     * Fake forward messages are unavailable yet,
     * and some fields like "news", "prompt", "summary", "source" are not implemented yet.
     * @param userId the user id to send the message to.
     * @param userIdOfSender the user id of the sender.
     * @param nicknameOfSender the nickname of the sender.
     * @param messages the messages to be forwarded.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendPrivateForward(long userId, long userIdOfSender, String nicknameOfSender, JsonArray messages) {
        JsonArray node = MessageGenerator.builder().node(
                NodeGenerator.builder().userId(userIdOfSender).nickname(nicknameOfSender).content(messages).build()
        ).build();
        JsonObject jsonObject = RequestGenerator.builder().userId(userId).messages(node)
//                .add("news", GSON.toJsonTree(List.of(Map.of("text", "news")))).add("prompt", "prompt")
//                .add("summary", "summary").add("source", "source")
                .build();
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
    /**
     * Send a private forward message.
     * <p>
     * Fake forward messages are unavailable yet,
     * and some fields like "news", "prompt", "summary", "source" are not implemented yet.
     * @param groupId the group id to send the message to.
     * @param userIdOfSender the user id of the sender.
     * @param nicknameOfSender the nickname of the sender.
     * @param messages the messages to be forwarded.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendGroupForward(long groupId, long userIdOfSender, String nicknameOfSender, JsonArray messages){
        JsonArray node = MessageGenerator.builder().node(
                NodeGenerator.builder().userId(userIdOfSender).nickname(nicknameOfSender).content(messages).build()
        ).build();
        JsonObject jsonObject = RequestGenerator.builder().groupId(groupId).messages(node)
//                .add("news", GSON.toJsonTree(List.of(Map.of("text", "news")))).add("prompt", "prompt")
//                .add("summary", "summary").add("source", "source")
                .build();
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
    public static int replyPrivateMessage(long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).at(userId).build();
        newArray.addAll(messages);
        return MessageSender.sendPrivate(userId, newArray);
    }
    public static int replyPrivateMessage(long userId, int messageId, JsonObject ... messages) {
        return replyPrivateMessage(userId, messageId, MessageGenerator.create(messages));
    }
    public static int replyPrivateMessageWithoutAt(long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).build();
        newArray.addAll(messages);
        return MessageSender.sendPrivate(userId, newArray);
    }
    public static int replyPrivateMessageWithoutAt(long userId, int messageId, JsonObject ... messages) {
        return replyPrivateMessageWithoutAt(userId, messageId, MessageGenerator.create(messages));
    }
    public static int replyGroupMessage(long groupId, long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).at(userId).build();
        newArray.addAll(messages);
        return MessageSender.sendGroup(groupId, newArray);
    }
    public static int replyGroupMessage(long groupId, long userId, int messageId, JsonObject ... messages) {
        return replyGroupMessage(groupId, userId, messageId, MessageGenerator.create(messages));
    }
    public static int replyGroupMessageWithoutAt(long groupId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).build();
        newArray.addAll(messages);
        return MessageSender.sendGroup(groupId, newArray);
    }
    public static int replyGroupMessageWithoutAt(long groupId, int messageId, JsonObject ... messages) {
        return replyGroupMessageWithoutAt(groupId, messageId, MessageGenerator.create(messages));
    }
}
