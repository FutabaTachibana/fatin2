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
            case "text" -> this.data.get("text").toString();
            case "at" -> " @" + this.data.get("qq") + " ";
            case "face" -> " [face:" + Faces.meaningOf(Integer.parseInt(this.data.get("id").toString())) + "] ";
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
        return gson.toJson(this);
    }
    // TODO: Implement unformatted text parsing
}
