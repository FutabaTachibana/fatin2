package org.f14a.fatin2.type.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.Sender;

/*
* To learn Onebot message structure see:
* https://napneko.github.io/onebot/basic_event
* */
public class OnebotMessage extends AbstractOnebotMessage{
    // The type of the message
    // private | group
    @SerializedName("message_type")
    private String messageType;

    // Subtype of the message
    // friend | normal | anonymous | notice
    @SerializedName("sub_type")
    private String text;

    // Message ID
    @SerializedName("message_id")
    private String messageId;

    // User ID of the sender
    @SerializedName("user_id")
    private String userId;

    // Message
    @SerializedName("message")
    private JsonObject[] message;

    // Raw message
    @SerializedName("raw_message")
    private String rawMessage;

    // Font
    @SerializedName("font")
    private int font;

    // Sender
    @SerializedName("sender")
    private Sender sender;

    // Getters
    public String getMessageType() {
        return messageType;
    }
    public String getSubType() {
        return text;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getUserId() {
        return userId;
    }
    public JsonObject[] getMessage() {
        return message;
    }
    public String getRawMessage() {
        return rawMessage;
    }
    public int getFont() {
        return font;
    }
    public Sender getSender() {
        return sender;
    }
}
