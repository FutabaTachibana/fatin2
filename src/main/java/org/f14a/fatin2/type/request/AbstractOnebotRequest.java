package org.f14a.fatin2.type.request;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;

public interface AbstractOnebotRequest extends AbstractOnebotMessage {
    // friend | group
    @SerializedName("request_type") String requestType();
    @SerializedName("flag") String flag();
    @SerializedName("user_id") Long userId();
    @SerializedName("comment") String comment();
}
