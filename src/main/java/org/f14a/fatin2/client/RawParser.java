package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.PrivateCommandEvent;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.event.meta.HeartbeatEvent;
import org.f14a.fatin2.event.meta.LifecycleEvent;
import org.f14a.fatin2.event.notice.*;
import org.f14a.fatin2.event.request.AddRequestEvent;
import org.f14a.fatin2.event.request.FriendRequestEvent;
import org.f14a.fatin2.event.request.InviteRequestEvent;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;
import org.f14a.fatin2.type.meta.OnebotHeartbeat;
import org.f14a.fatin2.type.meta.OnebotLifecycle;
import org.f14a.fatin2.type.notice.*;
import org.f14a.fatin2.type.request.AddOnebotRequest;
import org.f14a.fatin2.type.request.FriendOnebotRequest;
import org.f14a.fatin2.type.request.InviteOnebotRequest;

import java.util.Map;

/**
* A class contains a static method to parse raw JSON messages into Event objects.
* */
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
                        return PrivateCommandEvent.getCommandOrBasic(gson.fromJson(message, PrivateOnebotMessage.class));
                    } else if ("group".equals(messageType)) {
                        return GroupCommandEvent.getCommandOrBasic(gson.fromJson(message, GroupOnebotMessage.class));
                    } else {
                        throw new UnknownMessageTypeException("Unknown message_type: " + messageType);
                    }
                }
                case "meta_event" -> {
                    String metaEventType = (String) raw.get("meta_event_type");
                    if("heartbeat".equals(metaEventType)) {
                        return new HeartbeatEvent(gson.fromJson(message, OnebotHeartbeat.class));
                    } else if ("lifecycle".equals(metaEventType)) {
                        return new LifecycleEvent(gson.fromJson(message, OnebotLifecycle.class));
                    } else {
                        throw new UnknownMessageTypeException("Unknown meta_event_type: " + metaEventType);
                    }
                }
                case "notice" -> {
                    String noticeType = (String) raw.get("notice_type");
                    return switch (noticeType) {
                        case "group_upload" -> new GroupUploadEvent(gson.fromJson(message, GroupUploadOnebotNotice.class));
                        case "group_admin" -> new GroupAdminEvent(gson.fromJson(message, GroupAdminOnebotNotice.class));
                        case "group_decrease" -> new GroupDecreaseEvent(gson.fromJson(message, GroupDecreaseOnebotNotice.class));
                        case "group_increase" -> new GroupIncreaseEvent(gson.fromJson(message, GroupIncreaseOnebotNotice.class));
                        case "group_ban" ->  new GroupBanEvent(gson.fromJson(message, GroupBanOnebotNotice.class));
                        case "friend_add" -> new FriendAddEvent(gson.fromJson(message, FriendAddOnebotNotice.class));
                        case "friend_recall" -> new FriendRecallEvent(gson.fromJson(message, FriendRecallOnebotNotice.class));
                        case "group_recall" -> new GroupRecallEvent(gson.fromJson(message, GroupRecallOnebotNotice.class));
                        case "poke" -> new PokeEvent(gson.fromJson(message, PokeOnebotNotify.class));
                        case "lucky_king" -> new LuckyKingEvent(gson.fromJson(message, LuckyKingOnebotNotify.class));
                        case "honor" -> new HonorEvent(gson.fromJson(message, HonorOnebotNotify.class));
                        case "group_msg_emoji_like" -> null;
                        case "essence" -> null;
                        case "group_card" -> null;
                        default -> throw new UnknownMessageTypeException("Unknown notice_type: " + noticeType);
                    };
                }
                case "request" -> {
                    String requestType = (String) raw.get("request_type");
                    return switch (requestType) {
                        case "friend" -> new FriendRequestEvent(gson.fromJson(message, FriendOnebotRequest.class));
                        case "group" -> {
                            String subType = (String) raw.get("sub_type");
                            if ("add".equals(subType)) {
                                yield new AddRequestEvent(gson.fromJson(message, AddOnebotRequest.class));
                            } else if ("invite".equals(subType)) {
                                yield new InviteRequestEvent(gson.fromJson(message, InviteOnebotRequest.class));
                            } else {
                                throw new UnknownMessageTypeException("Unknown sub_type: " + subType);
                            }
                        }
                        default -> throw new UnknownMessageTypeException("Unknown request_type: " + requestType);
                    };
                }
                default -> throw new UnknownMessageTypeException("Unknown post_type: " + postType);
            }
        } else if (raw.containsKey("retcode")) {
            // TODO: handle API response events
            return null;
        }
        return null;
    }
}
