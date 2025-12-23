package org.f14a.fatin2.type.message;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.Sender;

public record PrivateOnebotMessage(
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("message_type") String messageType,
        @SerializedName("sub_type") String subType,
        @SerializedName("message_id") int messageId,
        @SerializedName("user_id") long userId,
        @SerializedName("message") Message[] messages,
        @SerializedName("font") int font,
        @SerializedName("sender") Sender sender
) implements OnebotMessage { }
