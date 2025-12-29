package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 群管理员变更通知。
 * @param subType 子类型，取值为 "set" 或 "unset"
 * @param groupId 事件发生的群 ID
 * @param userId 被设置或取消管理员的用户 ID
 * @see AbstractOnebotNotice
 */
public record GroupAdminOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type")    String subType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId
) implements AbstractOnebotNotice { }
