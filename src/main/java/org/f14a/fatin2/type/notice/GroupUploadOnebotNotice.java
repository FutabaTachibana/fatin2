package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupUploadOnebotNotice(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id") Long groupId,
        @SerializedName("user_id") Long userId,
        @SerializedName("file") File file
) implements AbstractOnebotNotice {
    public record File(
            @SerializedName("id") String id,
            @SerializedName("name") String name,
            @SerializedName("size") Long size,
            @SerializedName("busid") Long busid
    ) { }
}
