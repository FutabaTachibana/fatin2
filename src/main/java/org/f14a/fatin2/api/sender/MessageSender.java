package org.f14a.fatin2.api.sender;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.api.generator.MessageGenerator;
import org.f14a.fatin2.api.generator.NodeGenerator;
import org.f14a.fatin2.api.generator.RequestGenerator;
import org.f14a.fatin2.websocket.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 这个类是一个消息发送的工具类，提供了发送私聊消息、群消息以及转发消息的功能。
 */
public final class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);
    private static final Gson GSON = new Gson();

    /**
     * 私聊发送消息。
     * @param message 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
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
     * 群聊发送消息。
     * @param message message 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
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
     * 私聊发送伪造转发聊天记录。
     * <p>
     * 伪造转发消息部分功能可能受客户端影响变得不可用。
     * @param userId 要发送消息的用户 ID
     * @param userIdOfSender 聊天记录的发送者 ID
     * @param nicknameOfSender 聊天记录的发送者昵称
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
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
     * 群聊送伪造转发聊天记录。
     * <p>
     * 伪造转发消息部分功能可能受客户端影响变得不可用。
     * @param groupId 要发送消息的群 ID
     * @param userIdOfSender 聊天记录的发送者 ID
     * @param nicknameOfSender 聊天记录的发送者昵称
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
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

    /**
     * 私聊回复消息并@用户。
     * @param userId 要发送消息的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyPrivateMessage(long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).at(userId).build();
        newArray.addAll(messages);
        return MessageSender.sendPrivate(userId, newArray);
    }

    /**
     * 私聊回复消息并@用户。
     * @param userId 要发送消息的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyPrivateMessage(long userId, int messageId, JsonObject ... messages) {
        return replyPrivateMessage(userId, messageId, MessageGenerator.create(messages));
    }

    /**
     * 私聊回复消息。
     * @param userId 要发送消息的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyPrivateMessageWithoutAt(long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).build();
        newArray.addAll(messages);
        return MessageSender.sendPrivate(userId, newArray);
    }

    /**
     * 私聊回复消息。
     * @param userId 要发送消息的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyPrivateMessageWithoutAt(long userId, int messageId, JsonObject ... messages) {
        return replyPrivateMessageWithoutAt(userId, messageId, MessageGenerator.create(messages));
    }

    /**
     * 群聊回复消息并@用户。
     * @param groupId 要发送消息的群 ID
     * @param userId 要艾特的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyGroupMessage(long groupId, long userId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).at(userId).build();
        newArray.addAll(messages);
        return MessageSender.sendGroup(groupId, newArray);
    }

    /**
     * 群聊回复消息并@用户。
     * @param groupId 要发送消息的群 ID
     * @param userId 要艾特的用户 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyGroupMessage(long groupId, long userId, int messageId, JsonObject ... messages) {
        return replyGroupMessage(groupId, userId, messageId, MessageGenerator.create(messages));
    }

    /**
     * 群聊回复消息。
     * @param groupId 要发送消息的群 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyGroupMessageWithoutAt(long groupId, int messageId, JsonArray messages) {
        JsonArray newArray = MessageGenerator.builder().reply(messageId).build();
        newArray.addAll(messages);
        return MessageSender.sendGroup(groupId, newArray);
    }

    /**
     * 群聊回复消息。
     * @param groupId 要发送消息的群 ID
     * @param messageId 要回复的消息 ID
     * @param messages 通过 {@link MessageGenerator} 构建的消息内容
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int replyGroupMessageWithoutAt(long groupId, int messageId, JsonObject ... messages) {
        return replyGroupMessageWithoutAt(groupId, messageId, MessageGenerator.create(messages));
    }
}
