package org.f14a.fatin2.model;

import com.google.gson.annotations.SerializedName;

public interface AbstractOnebotMessage {
    @SerializedName("time")
    long time();
    // message | message_sent | notice | request | meta_event
    @SerializedName("post_type")
    String postType();
    @SerializedName("self_id")
    long selfId();
}
