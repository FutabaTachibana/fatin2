package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

public record HonorOnebotNotify(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") long groupId,
        // talkative | performer | emotion
        @SerializedName("honor_type") String honorType,
        @SerializedName("user_id") long userId
) implements AbstractOnebotNotify { }
