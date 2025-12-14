package org.f14a.fatin2.type.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.f14a.fatin2.type.Sender;

/*
* To learn Onebot message structure see:
* https://napneko.github.io/onebot/basic_event
* */

public interface OnebotMessage extends AbstractOnebotMessage {
    // private | group
    @SerializedName("message_type")
    String messageType();
    // friend | normal | anonymous | notice
    @SerializedName("sub_type")
    String subType();
    @SerializedName("message_id")
    String messageId();
    @SerializedName("user_id")
    Long userId();
    @SerializedName("message")
    JsonObject[] message();
    @SerializedName("raw_message")
    String rawMessage();
    @SerializedName("font")
    Integer font();
    @SerializedName("sender")
    Sender sender();
}
