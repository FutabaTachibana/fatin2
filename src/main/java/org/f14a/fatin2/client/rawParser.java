package org.f14a.fatin2.client;

import com.google.gson.Gson;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.event.message.PrivateMessageEvent;
import org.f14a.fatin2.type.exception.UnknownMessageTypeException;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

import java.util.Map;

public class rawParser {
    public static Event parseRaw(String message) {
        Gson gson = new Gson();
        Map<?, ?> raw = gson.fromJson(message, Map.class);
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
                        return null;
                    }
                    else if ("lifecycle".equals(metaEventType)) {
                        return null;
                    }
                    else {
                        throw new UnknownMessageTypeException("Unknown meta_event_type: " + metaEventType);
                    }
                }
                default -> throw new UnknownMessageTypeException("Unknown post_type: " + postType);
            }
        }
        return null;
    }
}
