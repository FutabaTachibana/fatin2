package org.f14a.fatin2.type.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.Sender;

import java.util.Arrays;

public record GroupOnebotMessage(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("message_type") String messageType,
        @SerializedName("sub_type") String subType,
        @SerializedName("message_id") String messageId,
        @SerializedName("user_id") Long userId,
        @SerializedName("message") JsonObject[] message,
        @SerializedName("raw_message") String rawMessage,
        @SerializedName("font") Integer font,
        @SerializedName("sender") Sender sender,
        @SerializedName("group_id") Long groupId
) implements OnebotMessage {
    // new GroupOnebotMessage(groupId, message...)
    public GroupOnebotMessage(Long groupId, JsonObject... message) {
        this(null, null, null, null, null, null, null, message, null, null, null, groupId);
    }
    @Override
    public String toString() {
        return "{\"group_id\":\"" + this.groupId + "\",\"message\":" + String.join(",", Arrays.toString(this.message)) + "}";
    }
}
