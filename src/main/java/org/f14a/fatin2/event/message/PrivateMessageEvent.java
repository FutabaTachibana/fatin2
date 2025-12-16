package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class PrivateMessageEvent extends Event {
    private final PrivateOnebotMessage message;
    public PrivateMessageEvent(PrivateOnebotMessage message) {
        this.message = message;
    }

    public PrivateOnebotMessage getMessage() {
        return message;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void fire() {
        new MessageEvent(this.message).fire();
        super.fire();
    }

    public void reply(String sendMessages) {
        MessageSender.sendPrivate(this.message.userId(), sendMessages);
    }
}
