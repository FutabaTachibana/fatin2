package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record FriendAddOnebotNotice(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("user_id") Long userId
) implements AbstractOnebotNotice { }
