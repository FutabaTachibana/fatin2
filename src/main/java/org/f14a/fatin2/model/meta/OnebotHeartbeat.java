package org.f14a.fatin2.model.meta;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.model.AbstractOnebotMessage;

public record OnebotHeartbeat (
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("meta_event_type") String metaEventType,
        @SerializedName("status") Status status,
        @SerializedName("interval") int interval
) implements AbstractOnebotMessage {
    public record Status(
            @SerializedName("online") boolean online,
            @SerializedName("good") boolean good
    ) { }
}
