package org.f14a.fatin2.websocket;

import com.google.gson.JsonObject;
import org.f14a.fatin2.exception.UnknownMessageTypeException;

final class RouteKeyResolver {
    public static String resolve(JsonObject object) {
        if (!object.has("post_type")) {
            if (object.has("retcode")) {
                return "response";
            }
            throw  new UnknownMessageTypeException("Unknown message type: " + object);
        }
        String postType = requireString(object, "post_type");
        return switch (postType) {
            case "message" -> "message:" + requireString(object, "message_type");
            case "meta_event" -> "meta_event:" + requireString(object, "meta_event_type");
            case "notice" -> "notice:" + requireString(object, "notice_type");
            case "request" -> "request:" + resolveRequest(object);
            default -> throw new UnknownMessageTypeException("Unknown post_type: " + postType);
        };
    }
    private static String resolveRequest(JsonObject object) {
        String requestType = requireString(object, "request_type");
        if ("group".equals(requestType)) {
            return requestType + ":" + requireString(object, "sub_type");
        } else {
            return requestType;
        }
    }
    private static String requireString(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            throw  new UnknownMessageTypeException("Missing required key: " + key);
        }
        return object.get(key).getAsString();
    }
}
