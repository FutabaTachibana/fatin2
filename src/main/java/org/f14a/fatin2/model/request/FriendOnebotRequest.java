package org.f14a.fatin2.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * 好友申请。
 * @param requestType 请求类型，固定为 "friend"
 * @param flag 请求标识符，用于处理请求
 * @param userId 申请用户的 ID
 * @param comment 备注
 * @see AbstractOnebotRequest
 */
public record FriendOnebotRequest(
        @SerializedName("time")         long   time,
        @SerializedName("post_type")    String postType,
        @SerializedName("self_id")      long   selfId,
        @SerializedName("request_type") String requestType,
        @SerializedName("flag")         String flag,
        @SerializedName("user_id")      long   userId,
        @SerializedName("comment")      String comment
) implements AbstractOnebotRequest { }
