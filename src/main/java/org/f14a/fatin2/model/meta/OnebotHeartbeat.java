package org.f14a.fatin2.model.meta;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.model.AbstractOnebotMessage;

/**
 * 心跳元事件。
 * @param time 事件发生的时间戳
 * @param postType 报文类型，固定为 "meta_event"
 * @param selfId 机器人自身的 ID
 * @param metaEventType 元事件类型，固定为 "heartbeat"
 * @param status 状态信息
 * @param interval 到下次心跳的时间间隔，单位为毫秒
 */
public record OnebotHeartbeat (
        @SerializedName("time")            long   time,
        @SerializedName("post_type")       String postType,
        @SerializedName("self_id")         long   selfId,
        @SerializedName("meta_event_type") String metaEventType,
        @SerializedName("status")          Status status,
        @SerializedName("interval")        long   interval
) implements AbstractOnebotMessage {
    /**
     * 表示心跳事件的状态信息。
     * @param online 机器人是否在线
     * @param good 状态符合预期
     */
    public record Status(
            @SerializedName("online") boolean online,
            @SerializedName("good")   boolean good
    ) { }
}
