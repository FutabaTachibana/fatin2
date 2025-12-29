package org.f14a.fatin2.model.meta;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.model.AbstractOnebotMessage;

/**
 * 生命周期元事件。
 * @param time 事件发生的时间戳
 * @param postType 报文类型，固定为 "meta_event"
 * @param selfId 机器人自身的 ID
 * @param metaEventType 元事件类型，固定为 "lifecycle"
 * @param subType 子类型，在 websocket 中只有 "connect"
 */
public record OnebotLifecycle(
        @SerializedName("time")            long   time,
        @SerializedName("post_type")       String postType,
        @SerializedName("self_id")         long   selfId,
        @SerializedName("meta_event_type") String metaEventType,
        @SerializedName("sub_type")        String subType
) implements AbstractOnebotMessage { }
