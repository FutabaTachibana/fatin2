package org.f14a.fatin2.type.message;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractOnebotMessage {
    // The time the message was received
    @SerializedName("time")
    protected Long time;

    // The type of the event
    // message | message_sent
    @SerializedName("post_type")
    protected String postType;

    // The id of the self bot
    @SerializedName("self_id")
    protected String selfId;

    // Getters
    public Long getTime() {
        return time;
    }
    public String getPostType() {
        return postType;
    }
    public String getSelfId() {
        return selfId;
    }
}
