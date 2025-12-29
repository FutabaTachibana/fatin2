package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 运气王通知。
 * @param subType 固定为 "lucky_king"
 * @param groupId 事件发生的群 ID
 * @param userId 红包发送者的用户 ID
 * @param targetId 获得运气王的用户 ID
 * @see AbstractOnebotNotify
 */
public record LuckyKingOnebotNotify(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type")    String subType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId,
        @SerializedName("target_id")   long   targetId
) implements AbstractOnebotNotify { }
