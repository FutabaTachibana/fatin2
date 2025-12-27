package org.f14a.fatin2.model.request;

import com.google.gson.annotations.SerializedName;

public record FriendOnebotRequest(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("request_type") String requestType,
        @SerializedName("flag") String flag,
        @SerializedName("user_id") long userId,
        @SerializedName("comment") String comment
) implements AbstractOnebotRequest { }
