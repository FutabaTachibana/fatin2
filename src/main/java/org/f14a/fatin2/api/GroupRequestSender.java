package org.f14a.fatin2.api;

import com.google.gson.JsonObject;

import static org.f14a.fatin2.api.RequestSender.sendRequest;

/**
 * 这个类是发送请求的工具类，提供了 同意/拒绝 群申请、禁言、设置群名片等操作。
 */
public final class GroupRequestSender {
    /**
     * 处理加群/拉群申请。
     * @param flag 申请的 {@code falg} 属性，对应一个申请
     * @param approve 同意 or 拒绝
     * @param reason 拒绝原因
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int approveGroup(String flag, boolean approve, String reason) {
        JsonObject jsonObject = RequestGenerator.builder()
                .flag(flag).approve(approve).reason(reason)
                .build();
        return sendRequest("set_group_add_request", jsonObject);
    }

    /**
     * 处理加群/拉群申请。
     * @param flag 申请的 {@code falg} 属性，对应一个申请
     * @param approve 同意 or 拒绝
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int approveGroup(String flag, boolean approve){
        return approveGroup(flag, approve, "");
    }

    /**
     * 为群里的某个成员设置群头衔，bot 必须是群主才能完成这个操作。
     * @param userId 要设置头衔的用户 ID
     * @param groupId 对应的群 ID
     * @param specialTitle 设置的头衔，注意字数限制
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int setSpecialTitle(long userId, long groupId, String specialTitle) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).specialTitle(specialTitle)
                .build();
        return sendRequest("set_group_special_title", jsonObject);
    }

    /**
     * 踢出成员。
     * @param userId 要踢出的用户 ID
     * @param groupId 对应的群 ID
     * @param rejectAddRequest 是否拉黑
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int kickGroupMember(long userId, long groupId, boolean rejectAddRequest) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).rejectAddRequest(rejectAddRequest)
                .build();
        return sendRequest("set_group_kick", jsonObject);
    }

    /**
     * 禁言成员。
     * @param userId 要禁言的用户 ID
     * @param groupId 对应的群 ID
     * @param durationSeconds 禁言时长，单位是秒，0 为解除禁言
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int banGroupMember(long userId, long groupId, int durationSeconds) {
        JsonObject jsonObject = RequestGenerator.builder()
                .userId(userId).groupId(groupId).duration(durationSeconds)
                .build();
        return sendRequest("set_group_ban", jsonObject);
    }

    /**
     * 全体禁言。
     * @param groupId 对应的群 ID
     * @param enable 开/关
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int banWholeGroup(long groupId, boolean enable) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).enable(enable)
                .build();
        return sendRequest("set_group_whole_ban", jsonObject);
    }

    /**
     * 设置群名。
     * @param groupId 对应的群 ID
     * @param groupName 新的群名。
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int setGroupName(long groupId, String groupName) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).groupName(groupName)
                .build();
        return sendRequest("set_group_name", jsonObject);
    }

    /**
     * 设置群名片。
     * @param groupId 对应的群 ID
     * @param userId 要设置群名片的用户 ID
     * @param card 新的群名片。
     * @return 请求的 {@code echo} 值，用于追踪响应
     */
    public static int setCard(long groupId, long userId, String card) {
        JsonObject jsonObject = RequestGenerator.builder()
                .groupId(groupId).userId(userId).card(card)
                .build();
        return sendRequest("set_group_card", jsonObject);
    }
}
