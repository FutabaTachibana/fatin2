package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.message.GroupOnebotMessage;

public class GroupMessageEvent extends Event {
    private final GroupOnebotMessage message;
    public GroupMessageEvent(GroupOnebotMessage message) {
        this.message = message;
    }

    public GroupOnebotMessage getMessage() {
        return message;
    }
    public long getGroupId() {
        return message.groupId() != null ? message.groupId() : null;
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
        MessageSender.sendGroup(this.message.groupId(), sendMessages);
    }
}
