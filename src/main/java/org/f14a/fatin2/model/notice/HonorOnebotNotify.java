package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 群荣誉变更通知。
 * @param subType 固定为 "honor"
 * @param groupId 事件发生的群 ID
 * @param honorType 荣誉类型，可选值包括 "talkative" "performer" "emotion"
 * @param userId 获得群荣誉的用户 ID
 * @see AbstractOnebotNotify
 */
public record HonorOnebotNotify(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type")    String subType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("honor_type")  String honorType,
        @SerializedName("user_id")     long   userId
) implements AbstractOnebotNotify { }
