package org.f14a.fatin2.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.websocket.Client;
import org.f14a.fatin2.model.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 这个类是发送请求的工具类，提供了 同意/拒绝 好友、撤回消息、戳一戳、贴表情等操作。
 */
public final class RequestSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSender.class);
    private static final Gson GSON = new Gson();

    /**
     * 处理加好友申请。
     * @param flag 申请的 {@code falg} 属性，对应一个申请
     * @param approve 同意 or 拒绝
     * @param remark 好友备注
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int approveFriend(String flag, boolean approve, String remark) {
        JsonObject jsonObject = RequestGenerator.builder()
                .flag(flag).approve(approve).remark(remark)
                .build();
        return sendRequest("set_friend_add_request", jsonObject);
    }

    /**
     * 处理加好友申请。
     * @param flag 申请的 {@code falg} 属性，对应一个申请
     * @param approve 同意 or 拒绝
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int approveFriend(String flag, boolean approve) {
        return approveFriend(flag, approve, "");
    }

    /**
     * 撤回消息。
     * @param messageId 要撤回的消息 ID
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int deleteMessage(int messageId) {
        JsonObject jsonObject = RequestGenerator.builder().messageId(messageId).build();
        return sendRequest("delete_msg", jsonObject);
    }

    /**
     * 戳一戳。
     * @param userId 要戳的用户 ID
     * @param groupId 要戳的群 ID，如果是私聊就设为 0
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int poke(long userId, long groupId) {
        RequestGenerator.RequestBuilder builder = RequestGenerator.builder().userId(userId);
        if (groupId != 0) {
            builder.groupId(groupId);
        }
        return sendRequest("send_poke", builder.build());
    }

    /**
     * 贴表情。
     * @param messageId 对应的消息 ID
     * @param faces 要贴的表情，是一个 {@link Faces} 的值
     * @param set 贴表情/取消贴表情
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int setEmojiLike(int messageId, Faces faces, boolean set) {
        JsonObject jsonObject = RequestGenerator.builder()
                .messageId(messageId).emojiId(faces).set(set)
                .build();
        return sendRequest("set_msg_emoji_like", jsonObject);
    }



    /**
     * 为指定的 {@code action} 设定 {@code  params}。
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int sendRequest(String action, JsonObject params) {
        int echo = params.hashCode();
        String request = GSON.toJson(Map.of(
                "action", action,
                "params", params,
                "echo", Integer.toString(echo)
        ));
        LOGGER.debug("Sending request {}: {}", action, request);
        Client.getInstance().send(request);
        return echo;
    }
}
