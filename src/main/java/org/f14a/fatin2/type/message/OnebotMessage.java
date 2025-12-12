package org.f14a.fatin2.type.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.Sender;

/*
* To learn Onebot message structure see:
* https://napneko.github.io/onebot/basic_event
* */
public abstract class OnebotMessage extends AbstractOnebotMessage{
    // The type of the message
    // private | group
    @SerializedName("message_type")
    protected String messageType;

    // Subtype of the message
    // friend | normal | anonymous | notice
    @SerializedName("sub_type")
    protected String text;

    // Message ID
    @SerializedName("message_id")
    protected String messageId;

    // User ID of the sender
    @SerializedName("user_id")
    protected String userId;

    // Message
    @SerializedName("message")
    protected JsonObject[] message;

    // Raw message
    @SerializedName("raw_message")
    protected String rawMessage;

    // Font
    @SerializedName("font")
    protected int font;

    // Sender
    @SerializedName("sender")
    protected Sender sender;

    // Getters
    public String getMessageType() {
        return this.messageType;
    }
    public String getSubType() {
        return this.text;
    }
    public String getMessageId() {
        return this.messageId;
    }
    public String getUserId() {
        return this.userId;
    }
    public JsonObject[] getMessage() {
        return this.message;
    }
    public String getRawMessage() {
        return this.rawMessage;
    }
    public int getFont() {
        return this.font;
    }
    public Sender getSender() {
        return this.sender;
    }
}
