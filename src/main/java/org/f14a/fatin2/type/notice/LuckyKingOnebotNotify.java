package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record LuckyKingOnebotNotify(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") Long groupId,
        @SerializedName("user_id") Long userId,
        @SerializedName("target_id") Long targetId
) implements AbstractOnebotNotify { }
