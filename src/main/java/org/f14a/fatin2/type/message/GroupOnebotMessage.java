package org.f14a.fatin2.type.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;


public class GroupOnebotMessage extends OnebotMessage {
    // Group id
    @SerializedName("group_id")
    private Long groupId;

    // Getter
    public Long getGroupId() {
        return this.groupId;
    }

    public GroupOnebotMessage(Long groupId, JsonObject ... message) {
        this.groupId = groupId;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{\"group_id\":\"" + this.groupId + "\",\"message\":" + String.join(",", Arrays.toString(this.message)) + "}";
                // Or (String[]) Arrays.stream(this.message).map(JsonElement::toString).toArray()
    }
}
