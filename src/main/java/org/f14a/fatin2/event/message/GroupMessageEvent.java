package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.client.sender.GroupMessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.type.message.GroupOnebotMessage;

public class GroupMessageEvent extends Event {
    private final GroupOnebotMessage message;
    public GroupMessageEvent(GroupOnebotMessage message) {
        this.message = message;
    }

    public GroupOnebotMessage getMessage() {
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

    public void reply(JsonObject ... sendMessages) {
        GroupMessageSender.send(new GroupOnebotMessage(this.message.groupId(), sendMessages));
    }
}
