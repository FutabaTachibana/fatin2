package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class PrivateMessageEvent extends MessageEvent {
    private final PrivateOnebotMessage message;
    public PrivateMessageEvent(PrivateOnebotMessage message) {
        super(message, MessageType.PRIVATE);
        this.message = message;
    }

    public PrivateOnebotMessage getMessage() {
        return message;
    }

    @Override
    public int send(JsonArray messages) {
        return MessageSender.sendPrivate(this.message.userId(), messages);
    }

}
