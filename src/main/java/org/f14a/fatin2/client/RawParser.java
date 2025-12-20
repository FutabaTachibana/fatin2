package org.f14a.fatin2.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.f14a.fatin2.event.response.ResponseEvent;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;
import org.f14a.fatin2.type.meta.OnebotHeartbeat;
import org.f14a.fatin2.type.meta.OnebotLifecycle;
import org.f14a.fatin2.type.notice.*;
import org.f14a.fatin2.type.request.AddOnebotRequest;
import org.f14a.fatin2.type.request.FriendOnebotRequest;
import org.f14a.fatin2.type.request.InviteOnebotRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
* A class contains a static method to parse raw JSON messages into Event objects.
*/
final class RawParser {
    private static final Gson gson = new Gson();
    private static final Map<String, Function<JsonObject, Event>> ROUTES = new HashMap<>();
    static {
        ROUTES.put("message:private", obj -> PrivateCommandEvent.getCommandOrBasic(gson.fromJson(obj, PrivateOnebotMessage.class)));
        ROUTES.put("message:group", obj -> GroupCommandEvent.getCommandOrBasic(gson.fromJson(obj, GroupOnebotMessage.class)));
        ROUTES.put("meta_event:heartbeat", obj -> new HeartbeatEvent(gson.fromJson(obj, OnebotHeartbeat.class)));
        ROUTES.put("meta_event:lifecycle", obj -> new LifecycleEvent(gson.fromJson(obj, OnebotLifecycle.class)));
        ROUTES.put("notice:group_upload", obj -> new GroupUploadEvent(gson.fromJson(obj, GroupUploadOnebotNotice.class)));
        ROUTES.put("notice:group_admin", obj -> new GroupAdminEvent(gson.fromJson(obj, GroupAdminOnebotNotice.class)));
        ROUTES.put("notice:group_decrease", obj -> new GroupDecreaseEvent(gson.fromJson(obj, GroupDecreaseOnebotNotice.class)));
        ROUTES.put("notice:group_increase", obj -> new GroupIncreaseEvent(gson.fromJson(obj, GroupIncreaseOnebotNotice.class)));
        ROUTES.put("notice:group_ban", obj -> new GroupBanEvent(gson.fromJson(obj, GroupBanOnebotNotice.class)));
        ROUTES.put("notice:friend_add", obj -> new FriendAddEvent(gson.fromJson(obj, FriendAddOnebotNotice.class)));
        ROUTES.put("notice:friend_recall", obj -> new FriendRecallEvent(gson.fromJson(obj, FriendRecallOnebotNotice.class)));
        ROUTES.put("notice:group_recall", obj -> new GroupRecallEvent(gson.fromJson(obj, GroupRecallOnebotNotice.class)));
        ROUTES.put("notice:poke", obj -> new PokeEvent(gson.fromJson(obj, PokeOnebotNotify.class)));
        ROUTES.put("notice:lucky_king", obj -> new LuckyKingEvent(gson.fromJson(obj, LuckyKingOnebotNotify.class)));
        ROUTES.put("notice:honor", obj -> new HonorEvent(gson.fromJson(obj, HonorOnebotNotify.class)));
        ROUTES.put("request:friend", obj -> new FriendRequestEvent(gson.fromJson(obj, FriendOnebotRequest.class)));
        ROUTES.put("request:group:add", obj -> new AddRequestEvent(gson.fromJson(obj, AddOnebotRequest.class)));
        ROUTES.put("request:group:invite", obj -> new InviteRequestEvent(gson.fromJson(obj, InviteOnebotRequest.class)));
        ROUTES.put("response", obj -> new ResponseEvent(gson.fromJson(obj, Response.class)));
    }
    public static Event parse(String message) {
        JsonObject object = JsonParser.parseString(message).getAsJsonObject();
        String routeKey = RouteKeyResolver.resolve(object);
        Function<JsonObject, Event> parser = ROUTES.get(routeKey);
        if (parser == null) {
            throw new UnknownMessageTypeException("No parser found for route key: " + routeKey);
        }
        return parser.apply(object);
    }

    @Deprecated
    public static Event parseRaw(String message) {
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
            return new ResponseEvent(gson.fromJson(message, Response.class));
        }
        return null;
    }
}
