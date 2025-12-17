package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.util.Faces;

import java.util.Map;

public record Message(
        @SerializedName("type") String type,
        @SerializedName("data") Map<String, Object> data
) {
    public String parse() {
        return switch (this.type) {
            case "text" -> (String) this.data.get("text");
            case "at" -> " @" + this.data.get("qq") + " ";
            case "face" -> " [face:" + Faces.meaningOf((int) this.data.get("id")) + " ]";
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
        return "{\"type\":\"" + this.type + "\",\"data\":" + this.data.toString() + "}";
    }
}
