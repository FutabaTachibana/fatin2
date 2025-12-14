package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.client.sender.GroupMessageSender;
import org.f14a.fatin2.client.sender.PrivateMessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class MessageEvent extends Event {
    private final OnebotMessage message;

    public MessageEvent(OnebotMessage message) {
        this.message = message;

    }

    public OnebotMessage getMessage() {
        return message;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public void reply(JsonObject ... sendMessages) {
        if (message instanceof PrivateOnebotMessage privateMessage) {
            PrivateMessageSender.send(new PrivateOnebotMessage(privateMessage.userId(), sendMessages));
        }
        else if (message instanceof GroupOnebotMessage groupMessage) {
            GroupMessageSender.send(new GroupOnebotMessage(groupMessage.groupId(), sendMessages));
        }
    }
}
