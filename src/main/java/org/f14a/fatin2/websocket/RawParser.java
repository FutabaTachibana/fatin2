package org.f14a.fatin2.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.PrivateCommandEvent;
import org.f14a.fatin2.event.meta.HeartbeatEvent;
import org.f14a.fatin2.event.meta.LifecycleEvent;
import org.f14a.fatin2.event.notice.*;
import org.f14a.fatin2.event.request.AddRequestEvent;
import org.f14a.fatin2.event.request.FriendRequestEvent;
import org.f14a.fatin2.event.request.InviteRequestEvent;
import org.f14a.fatin2.event.response.ResponseEvent;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.exception.UnknownMessageTypeException;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.f14a.fatin2.model.message.PrivateOnebotMessage;
import org.f14a.fatin2.model.meta.OnebotHeartbeat;
import org.f14a.fatin2.model.meta.OnebotLifecycle;
import org.f14a.fatin2.model.notice.*;
import org.f14a.fatin2.model.request.AddOnebotRequest;
import org.f14a.fatin2.model.request.FriendOnebotRequest;
import org.f14a.fatin2.model.request.InviteOnebotRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
* A class contains a static method to parse raw JSON messages into Event objects.
*/
final class RawParser {
    private static final Gson GSON = new Gson();
    private static final Map<String, Function<JsonObject, Event>> ROUTES = new HashMap<>();
    static {
        ROUTES.put("message:private", obj -> PrivateCommandEvent.getCommandOrBasic(GSON.fromJson(obj, PrivateOnebotMessage.class)));
        ROUTES.put("message:group", obj -> GroupCommandEvent.getCommandOrBasic(GSON.fromJson(obj, GroupOnebotMessage.class)));
        ROUTES.put("meta_event:heartbeat", obj -> new HeartbeatEvent(GSON.fromJson(obj, OnebotHeartbeat.class)));
        ROUTES.put("meta_event:lifecycle", obj -> new LifecycleEvent(GSON.fromJson(obj, OnebotLifecycle.class)));
        ROUTES.put("notice:group_upload", obj -> new GroupUploadEvent(GSON.fromJson(obj, GroupUploadOnebotNotice.class)));
        ROUTES.put("notice:group_admin", obj -> new GroupAdminEvent(GSON.fromJson(obj, GroupAdminOnebotNotice.class)));
        ROUTES.put("notice:group_decrease", obj -> new GroupDecreaseEvent(GSON.fromJson(obj, GroupDecreaseOnebotNotice.class)));
        ROUTES.put("notice:group_increase", obj -> new GroupIncreaseEvent(GSON.fromJson(obj, GroupIncreaseOnebotNotice.class)));
        ROUTES.put("notice:group_ban", obj -> new GroupBanEvent(GSON.fromJson(obj, GroupBanOnebotNotice.class)));
        ROUTES.put("notice:friend_add", obj -> new FriendAddEvent(GSON.fromJson(obj, FriendAddOnebotNotice.class)));
        ROUTES.put("notice:friend_recall", obj -> new FriendRecallEvent(GSON.fromJson(obj, FriendRecallOnebotNotice.class)));
        ROUTES.put("notice:group_recall", obj -> new GroupRecallEvent(GSON.fromJson(obj, GroupRecallOnebotNotice.class)));
        ROUTES.put("notice:poke", obj -> new PokeEvent(GSON.fromJson(obj, PokeOnebotNotify.class)));
        ROUTES.put("notice:lucky_king", obj -> new LuckyKingEvent(GSON.fromJson(obj, LuckyKingOnebotNotify.class)));
        ROUTES.put("notice:honor", obj -> new HonorEvent(GSON.fromJson(obj, HonorOnebotNotify.class)));
        ROUTES.put("notice:group_card", obj -> new GroupCardEvent(GSON.fromJson(obj, GroupCardOnebotNotice.class)));
        ROUTES.put("request:friend", obj -> new FriendRequestEvent(GSON.fromJson(obj, FriendOnebotRequest.class)));
        ROUTES.put("request:group:add", obj -> new AddRequestEvent(GSON.fromJson(obj, AddOnebotRequest.class)));
        ROUTES.put("request:group:invite", obj -> new InviteRequestEvent(GSON.fromJson(obj, InviteOnebotRequest.class)));
        ROUTES.put("response", obj -> new ResponseEvent(GSON.fromJson(obj, Response.class)));
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
        Map<?, ?> raw = GSON.fromJson(message, Map.class);
        // Event type:
        // message | meta_event | notice | request
        if (raw.containsKey("post_type")) {
            String postType = (String) raw.get("post_type");
            switch (postType) {
                case "message" -> {
                    String messageType = (String) raw.get("message_type");
                    if ("private".equals(messageType)) {
                        return PrivateCommandEvent.getCommandOrBasic(GSON.fromJson(message, PrivateOnebotMessage.class));
                    } else if ("group".equals(messageType)) {
                        return GroupCommandEvent.getCommandOrBasic(GSON.fromJson(message, GroupOnebotMessage.class));
                    } else {
                        throw new UnknownMessageTypeException("Unknown message_type: " + messageType);
                    }
                }
                case "meta_event" -> {
                    String metaEventType = (String) raw.get("meta_event_type");
                    if("heartbeat".equals(metaEventType)) {
                        return new HeartbeatEvent(GSON.fromJson(message, OnebotHeartbeat.class));
                    } else if ("lifecycle".equals(metaEventType)) {
                        return new LifecycleEvent(GSON.fromJson(message, OnebotLifecycle.class));
                    } else {
                        throw new UnknownMessageTypeException("Unknown meta_event_type: " + metaEventType);
                    }
                }
                case "notice" -> {
                    String noticeType = (String) raw.get("notice_type");
                    return switch (noticeType) {
                        case "group_upload" -> new GroupUploadEvent(GSON.fromJson(message, GroupUploadOnebotNotice.class));
                        case "group_admin" -> new GroupAdminEvent(GSON.fromJson(message, GroupAdminOnebotNotice.class));
                        case "group_decrease" -> new GroupDecreaseEvent(GSON.fromJson(message, GroupDecreaseOnebotNotice.class));
                        case "group_increase" -> new GroupIncreaseEvent(GSON.fromJson(message, GroupIncreaseOnebotNotice.class));
                        case "group_ban" ->  new GroupBanEvent(GSON.fromJson(message, GroupBanOnebotNotice.class));
                        case "friend_add" -> new FriendAddEvent(GSON.fromJson(message, FriendAddOnebotNotice.class));
                        case "friend_recall" -> new FriendRecallEvent(GSON.fromJson(message, FriendRecallOnebotNotice.class));
                        case "group_recall" -> new GroupRecallEvent(GSON.fromJson(message, GroupRecallOnebotNotice.class));
                        case "poke" -> new PokeEvent(GSON.fromJson(message, PokeOnebotNotify.class));
                        case "lucky_king" -> new LuckyKingEvent(GSON.fromJson(message, LuckyKingOnebotNotify.class));
                        case "honor" -> new HonorEvent(GSON.fromJson(message, HonorOnebotNotify.class));
                        case "group_msg_emoji_like" -> null;
                        case "essence" -> null;
                        case "group_card" -> null;
                        default -> throw new UnknownMessageTypeException("Unknown notice_type: " + noticeType);
                    };
                }
                case "request" -> {
                    String requestType = (String) raw.get("request_type");
                    return switch (requestType) {
                        case "friend" -> new FriendRequestEvent(GSON.fromJson(message, FriendOnebotRequest.class));
                        case "group" -> {
                            String subType = (String) raw.get("sub_type");
                            if ("add".equals(subType)) {
                                yield new AddRequestEvent(GSON.fromJson(message, AddOnebotRequest.class));
                            } else if ("invite".equals(subType)) {
                                yield new InviteRequestEvent(GSON.fromJson(message, InviteOnebotRequest.class));
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
            return new ResponseEvent(GSON.fromJson(message, Response.class));
        }
        return null;
    }
}
