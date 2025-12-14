package org.f14a.fatin2.type.meta;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.f14a.fatin2.type.Status;

public record OnebotHeartbeat (
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("meta_event_type") String metaEventType,
        @SerializedName("status") Status status,
        @SerializedName("interval") Integer interval
) implements AbstractOnebotMessage { }
