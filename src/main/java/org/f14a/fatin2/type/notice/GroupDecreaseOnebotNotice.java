package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupDecreaseOnebotNotice(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        // leave | kick | kick_me
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") Long groupId,
        @SerializedName("operator_id") Long operatorId,
        @SerializedName("user_id") Long userId
) implements AbstractOnebotNotice{ }
