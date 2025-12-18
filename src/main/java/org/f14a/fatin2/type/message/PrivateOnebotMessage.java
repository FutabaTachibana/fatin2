package org.f14a.fatin2.type.message;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.Sender;

public record PrivateOnebotMessage(
        @SerializedName("time") Long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") Long selfId,
        @SerializedName("message_type") String messageType,
        @SerializedName("sub_type") String subType,
        @SerializedName("message_id") String messageId,
        @SerializedName("user_id") Long userId,
        @SerializedName("message") Message[] message,
        @SerializedName("font") Integer font,
        @SerializedName("sender") Sender sender
) implements OnebotMessage { }
