package org.f14a.fatin2.model.message;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.exception.OnebotProtocolException;
import org.f14a.fatin2.model.Faces;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Onebot 消息中的单个消息段。
 * @param type 消息段类型
 * @param data 消息段数据，通常为一个 {@code Map<String, Object>} 对象，
 *             Object 的类型为 Integer 或者 String，具体取决于消息段的类型
 * @see OnebotMessage
 */
public record Message(
        @SerializedName("type") String              type,
        @SerializedName("data") Map<String, Object> data
) {
    private static final Gson GSON = new Gson();

    public String parse() {
        try {
            return switch (this.type) {
                case "text"   -> this.data.get("text").toString();
                case "at"     -> " @" + this.data.get("qq") + " ";
                case "face"   -> " [face:" + Faces.meaningOf(Integer.parseInt(this.data.get("id").toString())) + "] ";
                case "record" -> " [voice] ";
                case "image"  -> " [image] ";
                case "video"  -> " [video] ";
                case "file"   -> " [file] ";
                case "reply"  -> " [reply] ";
                default       -> " [unknown message type] ";
            };
        } catch (RuntimeException e) {
            throw new OnebotProtocolException("Malformed message data for type: " + this.type + ",data: " + this.data, e);
        }
    }
    @Override
    public @NotNull String toString() {
        return GSON.toJson(this);
    }
}
