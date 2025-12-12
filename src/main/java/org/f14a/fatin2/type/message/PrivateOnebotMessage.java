package org.f14a.fatin2.type.message;

import java.util.Arrays;

public class PrivateOnebotMessage extends OnebotMessage{
    @Override
    public String toString() {
        return "{\"group_id\":\"" + this.userId + "\",\"message\":" + String.join(",", Arrays.toString(this.message)) + "}";
        // Or (String[]) Arrays.stream(this.message).map(JsonElement::toString).toArray()
    }
}
