package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupAdminOnebotNotice(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        // set | unset
        @SerializedName("sub_type") String subType,
        @SerializedName("group_id") Long groupId,
        @SerializedName("user_id") Long userId
) implements AbstractOnebotNotice { }
