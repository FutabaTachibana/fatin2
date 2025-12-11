package org.f14a.fatin2.type;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Message {
    // Message type
    @SerializedName("type")
    private String type;

    // Message data
    @SerializedName("data")
    private Map<String, Object>[] data;

    // Getters
    public String getType() {
        return type;
    }
    public Map<String, Object>[] getData() {
        return data;
    }
}
