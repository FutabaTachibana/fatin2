package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 群文件上传通知。
 * @param groupId 事件发生的群 ID
 * @param userId 上传文件的用户 ID
 * @param file 文件信息，是一个 {@link File} 对象
 * @see AbstractOnebotNotice
 */
public record GroupUploadOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId,
        @SerializedName("file")        File   file
) implements AbstractOnebotNotice {
    /**
     * 文件信息。
     * @param id 文件 ID
     * @param name 文件名称
     * @param size 文件大小，单位为字节
     * @param busid 目前不清楚有什么用
     * @see GroupUploadOnebotNotice
     */
    public record File(
            @SerializedName("id")    String id,
            @SerializedName("name")  String name,
            @SerializedName("size")  long   size,
            @SerializedName("busid") long   busid
    ) { }
}
