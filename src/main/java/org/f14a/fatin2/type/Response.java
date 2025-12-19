package org.f14a.fatin2.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public record Response(
    @SerializedName("status") String status,
    @SerializedName("retcode") int retcode,
    @SerializedName("data") JsonElement data,
    @SerializedName("message") String message,
    @SerializedName("wording") String wording,
    @SerializedName("echo") String echo
) {
    public boolean isSuccess() {
        return "ok".equalsIgnoreCase(this.status) && this.retcode == 0;
    }
    public long getMessageId() {
        if (this.data instanceof JsonObject dataObject && dataObject.has("message_id")) {
            return dataObject.get("message_id").getAsLong();
        }
        return 0L;
    }
}
