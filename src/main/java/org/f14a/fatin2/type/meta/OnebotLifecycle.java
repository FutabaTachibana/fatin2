package org.f14a.fatin2.type.meta;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;

public record OnebotLifecycle(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("meta_event_type") String metaEventType,
        @SerializedName("sub_type") String subType
) implements AbstractOnebotMessage { }
