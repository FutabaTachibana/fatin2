package org.f14a.fatin2.type.message;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractOnebotMessage {
    // The time the message was received
    @SerializedName("time")
    protected Long time;

    // Post type
    // message | message_sent | notice | request | meta_event
    @SerializedName("post_type")
    protected String postType;

    // The id of the self bot
    @SerializedName("self_id")
    protected Long selfId;

    // Getters
    public Long getTime() {
        return this.time;
    }
    public String getPostType() {
        return this.postType;
    }
    public Long getSelfId() {
        return this.selfId;
    }
}
