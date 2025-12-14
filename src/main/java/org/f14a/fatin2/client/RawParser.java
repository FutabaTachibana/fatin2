package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.event.meta.HeartbeatEvent;
import org.f14a.fatin2.event.meta.LifecycleEvent;
import org.f14a.fatin2.event.notice.BanEvent;
import org.f14a.fatin2.event.notice.FriendRecallEvent;
import org.f14a.fatin2.event.notice.GroupRecallEvent;
import org.f14a.fatin2.event.notice.PokeEvent;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;
import org.f14a.fatin2.type.meta.OnebotHeartbeat;
import org.f14a.fatin2.type.meta.OnebotLifecycle;
import org.f14a.fatin2.type.notice.GroupBanOnebotNotice;
import org.f14a.fatin2.type.notice.FriendRecallOnebotNotice;
import org.f14a.fatin2.type.notice.GroupRecallOnebotNotice;
import org.f14a.fatin2.type.notice.PokeOnebotNotify;

import java.util.Map;

public class RawParser {
    public static Event parseRaw(String message) {
        Gson gson = new Gson();
        Map<?, ?> raw = gson.fromJson(message, Map.class);
        // Event type:
        // message | meta_event | notice | request
        if (raw.containsKey("post_type")) {
            String postType = (String) raw.get("post_type");
            switch (postType) {
                case "message" -> {
                    String messageType = (String) raw.get("message_type");
                    if ("private".equals(messageType)) {
                        return new PrivateMessageEvent(gson.fromJson(message, PrivateOnebotMessage.class));
                    } else if ("group".equals(messageType)) {
                        return new GroupMessageEvent(gson.fromJson(message, GroupOnebotMessage.class));
                    } else {
                        throw new UnknownMessageTypeException("Unknown message_type: " + messageType);
                    }
                }
                case "meta_event" -> {
                    String metaEventType = (String) raw.get("meta_event_type");
                    if("heartbeat".equals(metaEventType)) {
                        return new HeartbeatEvent(gson.fromJson(message, OnebotHeartbeat.class));
                    }
                    else if ("lifecycle".equals(metaEventType)) {
                        return new LifecycleEvent(gson.fromJson(message, OnebotLifecycle.class));
                    }
                    else {
                        throw new UnknownMessageTypeException("Unknown meta_event_type: " + metaEventType);
                    }
                }
                case "notice" -> {
                    String noticeType = (String) raw.get("notice_type");
                    return switch (noticeType) {
                        case "group_upload" -> null;
                        case "group_admin" -> null;
                        case "group_decrease" -> null;
                        case "group_increase" -> null;
                        case "group_ban" ->  new BanEvent(gson.fromJson(message, GroupBanOnebotNotice.class));
                        case "friend_add" -> null;
                        case "friend_recall" -> new FriendRecallEvent(gson.fromJson(message, FriendRecallOnebotNotice.class));
                        case "group_recall" -> new GroupRecallEvent(gson.fromJson(message, GroupRecallOnebotNotice.class));
                        case "poke" -> new PokeEvent(gson.fromJson(message, PokeOnebotNotify.class));
                        case "lucky_king" -> null;
                        case "honor" -> null;
                        case "group_msg_emoji_like" -> null;
                        case "essence" -> null;
                        case "group_card" -> null;
                        default -> throw new UnknownMessageTypeException("Unexpected notice_type: " + noticeType);
                    };
                }
                default -> throw new UnknownMessageTypeException("Unknown post_type: " + postType);
            }
        }
        return null;
    }
}
