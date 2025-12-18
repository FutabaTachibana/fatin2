package org.f14a.fatin2.util;

import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;

/**
 * A utility class for applying request.
 */
public class RequestSender {
    /**
     * Handle group request.
     * @param flag the request flag, which sources from AddRequestEvent or InviteRequestEvent.
     * @param approve whether to approve the request.
     * @param reason the reason for declining the request.
     */
    public static void approveGroup(String flag, boolean approve, String reason) {
        String string = RequestGenerator.builder().flag(flag).approve(approve).reason(reason).build();
        Main.LOGGER.debug("Handling group request: {}", string);
        Client.getInstance().send("{\"action\":\"set_group_add_request\",\"params\":" + string + "}");
    }
    /**
     * Handle group request, reason is set to null.
     * @param flag the request flag, which sources from AddRequestEvent or InviteRequestEvent.
     * @param approve whether to approve the request.
     */
    public static void approveGroup(String flag, boolean approve){
        approveGroup(flag, approve, "");
    }
    /**
     * Handle group request.
     * @param flag the request flag, which sources from FriendRequestEvent.
     * @param approve whether to approve the request.
     * @param remark the remark of the new friend.
     */
    public static void approveFriend(String flag, boolean approve, String remark) {
        String string = RequestGenerator.builder().flag(flag).approve(approve).remark(remark).build();
        Main.LOGGER.debug("Handling friend request: {}", string);
        Client.getInstance().send("{\"action\":\"set_friend_add_request\",\"params\":" + string + "}");
    }
    /**
     * Handle group request, remark is set to null.
     * @param flag the request flag, which sources from FriendRequestEvent.
     * @param approve whether to approve the request.
     */
    public static void approveFriend(String flag, boolean approve) {
        approveFriend(flag, approve, "");
    }
}
