package org.f14a.fatin2.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * 邀请入群请求。
 * @param requestType 请求类型，固定为 "group"
 * @param flag 请求标识符，用于处理请求
 * @param userId 申请用户的 ID
 * @param comment 备注
 * @param subType 子类型，固定为 "invite"
 * @param groupId 目标的群 ID
 * @see AbstractOnebotRequest
 */
public record InviteOnebotRequest(
        @SerializedName("time")         long   time,
        @SerializedName("post_type")    String postType,
        @SerializedName("self_id")      long   selfId,
        @SerializedName("request_type") String requestType,
        @SerializedName("flag")         String flag,
        @SerializedName("user_id")      long   userId,
        @SerializedName("comment")      String comment,
        @SerializedName("sub_type")     String subType,
        @SerializedName("group_id")     long   groupId
) implements AbstractOnebotRequest { }
