package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.event.Event;
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
    public void send(String message) {
        MessageSender.sendPrivate(this.message.userId(), message);
        finishSession();
    }

}
