package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record LuckyKingOnebotNotify(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") long groupId,
        // Red packet owner ID
        @SerializedName("user_id") long userId,
        // Lucky king ID
        @SerializedName("target_id") long targetId
) implements AbstractOnebotNotify { }
