package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;

import java.util.Map;

/**
 * A utility class for applying request.
 */
public class RequestSender {
    private static final Gson gson = new Gson();
    /**
     * Handle group request.
     * @param flag the request flag, which sources from AddRequestEvent or InviteRequestEvent.
     * @param approve whether to approve the request.
     * @param reason the reason for declining the request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int approveGroup(String flag, boolean approve, String reason) {
        JsonObject jsonObject = RequestGenerator.builder().flag(flag).approve(approve).reason(reason).build();
        int echo = jsonObject.hashCode();
        String request = gson.toJson(Map.of(
                "action", "set_group_add_request",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        Main.LOGGER.debug("Handling group request: {}", request);
        Client.getInstance().send(request);
        return echo;
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
        JsonObject jsonObject = RequestGenerator.builder().flag(flag).approve(approve).remark(remark).build();
        int echo = jsonObject.hashCode();
        String request = gson.toJson(Map.of(
                "action", "set_friend_add_request",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        Main.LOGGER.debug("Handling friend request: {}", request);
        Client.getInstance().send(request);
        return echo;
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
    public static int deleteMessage(long messageId) {
        JsonObject jsonObject = RequestGenerator.builder().messageId(messageId).build();
        int echo = jsonObject.hashCode();
        String request = gson.toJson(Map.of(
                "action", "delete_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        Main.LOGGER.debug("Deleting message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
}
