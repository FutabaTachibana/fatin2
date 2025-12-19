package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupDecreaseOnebotNotice(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        // leave | kick | kick_me
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") long groupId,
        @SerializedName("operator_id") long operatorId,
        @SerializedName("user_id") long userId
) implements AbstractOnebotNotice{ }
