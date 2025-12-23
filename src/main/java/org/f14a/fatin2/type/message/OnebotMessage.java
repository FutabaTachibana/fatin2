package org.f14a.fatin2.type.message;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.f14a.fatin2.type.Message;
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
    int messageId();
    @SerializedName("user_id")
    long userId();
    @SerializedName("message")
    Message[] messages();
//    @Deprecated
//    @SerializedName("raw_message")
//    String rawMessage();
    @SerializedName("font")
    int font();
    @SerializedName("sender")
    Sender sender();

    default String parse(){
        StringBuilder sb = new StringBuilder();
        for(Message msg : this.messages()) {
            sb.append(msg.parse());
        }
        return sb.toString();
    }
}
