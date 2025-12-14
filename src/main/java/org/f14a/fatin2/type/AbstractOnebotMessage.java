package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

public interface AbstractOnebotMessage {
    @SerializedName("time")
    Long time();
    // message | message_sent | notice | request | meta_event
    @SerializedName("post_type")
    String postType();
    @SerializedName("self_id")
    Long selfId();
}
