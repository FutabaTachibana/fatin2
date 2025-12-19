package org.f14a.fatin2.type;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record Message(
        @SerializedName("type") String type,
        @SerializedName("data") Map<String, Object> data
) {
    private static final Gson gson = new Gson();
    public String parse() {
        return switch (this.type) {
            case "text" -> (String) this.data.get("text");
            case "at" -> " @" + this.data.get("qq") + " ";
            case "face" -> " [face:" + Faces.meaningOf(Integer.parseInt((String) this.data.get("id"))) + "] ";
            case "record" -> " [voice] ";
            case "image" -> " [image] ";
            case "video" -> " [video] ";
            case "file" -> " [file] ";
            case "reply" -> " [reply] ";
            default -> " [unknown message type] ";
        };
    }
    @Override
    public String toString() {
        return "{\"type\":\"" + this.type + "\",\"data\":" + gson.toJson(this.data) + "}";
    }
}
