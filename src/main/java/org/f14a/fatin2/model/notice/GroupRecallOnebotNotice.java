package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

public record GroupRecallOnebotNotice(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id") long groupId,
        @SerializedName("user_id") long userId,
        @SerializedName("operator_id") long operatorId,
        @SerializedName("message_id") long messageId
) implements AbstractOnebotNotice { }
