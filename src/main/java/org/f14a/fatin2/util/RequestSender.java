package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.type.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A utility class for applying request.
 */
public class RequestSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSender.class);
    private static final Gson GSON = new Gson();
    /**
     * Handle group request.
     * @param flag the request flag, which sources from AddRequestEvent or InviteRequestEvent.
     * @param approve whether to approve the request.
     * @param reason the reason for declining the request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int approveGroup(String flag, boolean approve, String reason) {
        JsonObject jsonObject = RequestGenerator.builder()
                .flag(flag).approve(approve).reason(reason)
                .build();
        return sendRequest("set_group_add_request", jsonObject);
    }
    /**
     * Handle group request, reason is set to null.
     * @param flag the request flag, which sources from AddRequestEvent or InviteRequestEvent.
     * @param approve whether to approve the request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int approveGroup(String flag, boolean approve){
        return approveGroup(flag, approve, "");
    }

    /**
     * Handle group request.
     * @param flag the request flag, which sources from FriendRequestEvent.
     * @param approve whether to approve the request.
     * @param remark the remark of the new friend.
     * @return the echo of the request, for tracking the message status.
     */
    public static int approveFriend(String flag, boolean approve, String remark) {
        JsonObject jsonObject = RequestGenerator.builder()
                .flag(flag).approve(approve).remark(remark)
                .build();
        return sendRequest("set_friend_add_request", jsonObject);
    }
    /**
     * Handle group request, remark is set to null.
     * @param flag the request flag, which sources from FriendRequestEvent.
     * @param approve whether to approve the request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int approveFriend(String flag, boolean approve) {
        return approveFriend(flag, approve, "");
    }

    /**
     * Delete a message.
     * @param messageId the message ID to delete.
     * @return the echo of the request, for tracking the message status.
     */
    public static int deleteMessage(int messageId) {
        JsonObject jsonObject = RequestGenerator.builder().messageId(messageId).build();
        return sendRequest("delete_msg", jsonObject);
    }

    // Group management requests
    /**
     * Set special title for a user in a group, bot must be owner of the group.
     * @param userId user id to set special title
     * @param groupId group id where the user is located
     * @param specialTitle the special title to set
     * @return the echo of the request, for tracking the message status.
     */
    public static int setSpecialTitle(long userId, long groupId, String specialTitle) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).specialTitle(specialTitle)
                .build();
        return sendRequest("set_group_special_title", jsonObject);
    }
    /**
     * Kick a member from a group.
     * @param userId user id to kick
     * @param groupId group id where the user is located
     * @param rejectAddRequest whether to reject the add request
     * @return the echo of the request, for tracking the message status.
     */
    public static int kickGroupMember(long userId, long groupId, boolean rejectAddRequest) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).rejectAddRequest(rejectAddRequest)
                .build();
        return sendRequest("set_group_kick", jsonObject);
    }
    /**
     * Ban a member in a group.
     * @param userId user id to ban
     * @param groupId group id where the user is located
     * @param durationSeconds duration in seconds to ban the user
     * @return the echo of the request, for tracking the message status.
     */
    public static int banGroupMember(long userId, long groupId, int durationSeconds) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).duration(durationSeconds)
                .build();
        return sendRequest("set_group_ban", jsonObject);
    }
    /**
     * Ban or unban the whole group.
     * @param groupId group id to ban or unban
     * @param enable whether to enable the whole group ban
     * @return the echo of the request, for tracking the message status.
     */
    public static int banWholeGroup(long groupId, boolean enable) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).enable(enable)
                .build();
        return sendRequest("set_group_whole_ban", jsonObject);
    }
    /**
     * Set group name.
     * @param groupId group id to set name
     * @param groupName the new group name
     * @return the echo of the request, for tracking the message status.
     */
    public static int setGroupName(long groupId, String groupName) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).groupName(groupName)
                .build();
        return sendRequest("set_group_name", jsonObject);
    }
    /**
     * Set group card for a user in a group.
     * @param groupId group id where the user is located
     * @param userId user id to set card
     * @param card the new group card
     * @return the echo of the request, for tracking the message status.
     */
    public static int setCard(long groupId, long userId, String card) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).userId(userId).card(card)
                .build();
        return sendRequest("set_group_card", jsonObject);
    }

    /**
     * Send a poke to a user in a group.
     * @param userId user id to poke
     * @param groupId group id where the user is located (0 for private poke)
     * @return the echo of the request, for tracking the message status.
     */
    public static int poke(long userId, long groupId) {
        RequestGenerator.RequestBuilder builder = RequestGenerator.builder().userId(userId);
        if (groupId != 0) {
            builder.groupId(groupId);
        }
        return sendRequest("send_poke", builder.build());
    }

    /**
     * Like or unlike a message with emoji.
     * @param messageId the message ID to like or unlike
     * @param faces the emoji to use
     * @param set true to like, false to unlike
     * @return the echo of the request, for tracking the message status.
     */
    public static int setEmojiLike(int messageId, Faces faces, boolean set) {
        JsonObject jsonObject = RequestGenerator.builder()
                .messageId(messageId).emojiId(faces).set(set)
                .build();
        return sendRequest("set_msg_emoji_like", jsonObject);
    }



    /**
     * Send a request with given action and parameters.
     * @param action the action to perform
     * @param params the parameters for the action
     * @return the echo of the request, for tracking the message status.
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
