package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupUploadOnebotNotice(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id") long groupId,
        @SerializedName("user_id") long userId,
        @SerializedName("file") File file
) implements AbstractOnebotNotice {
    public record File(
            @SerializedName("id") String id,
            @SerializedName("name") String name,
            @SerializedName("size") long size,
            @SerializedName("busid") long busid
    ) { }
}
