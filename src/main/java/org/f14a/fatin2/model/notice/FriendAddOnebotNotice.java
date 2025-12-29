package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 好友添加通知。
 * @param userId 添加的好友的用户 ID
 * @see AbstractOnebotNotice
 */
public record FriendAddOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("user_id")     long   userId
) implements AbstractOnebotNotice { }
