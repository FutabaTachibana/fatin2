package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupRecallNotice(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id") Long groupId,
        @SerializedName("user_id") Long userId,
        @SerializedName("operator_id") Long operatorId,
        @SerializedName("message_id") Long messageId
) implements AbstractOnebotNotice { }
