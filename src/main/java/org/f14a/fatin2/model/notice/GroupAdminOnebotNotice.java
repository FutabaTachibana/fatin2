package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

public record GroupAdminOnebotNotice(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        // set | unset
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") long groupId,
        @SerializedName("user_id") long userId
) implements AbstractOnebotNotice { }
