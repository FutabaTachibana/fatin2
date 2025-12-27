package org.f14a.fatin2.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.exception.OnebotProtocolException;

import java.util.Map;

public record Message(
        @SerializedName("type") String type,
        @SerializedName("data") Map<String, Object> data
) {
    private static final Gson GSON = new Gson();
    public String parse() {
        try {
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
        } catch (RuntimeException e) {
            throw new OnebotProtocolException("Malformed message data for type: " + this.type + ",data: " + this.data, e);
        }
    }
    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
