package org.f14a.fatin2.type.request;

import com.google.gson.annotations.SerializedName;

public record InviteOnebotRequest(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("request_type") String requestType,
        @SerializedName("flag") String flag,
        @SerializedName("user_id") Long userId,
        @SerializedName("comment") String comment
) implements AbstractOnebotRequest { }
