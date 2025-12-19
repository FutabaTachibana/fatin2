package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.type.message.GroupOnebotMessage;

public class GroupMessageEvent extends MessageEvent {
    private final GroupOnebotMessage message;
    public GroupMessageEvent(GroupOnebotMessage message) {
        super(message, MessageType.GROUP);
        this.message = message;
    }

    public GroupOnebotMessage getMessage() {
        return message;
    }
    public long getGroupId() {
        return message.groupId();
    }

    @Override
    public int sendOnly(JsonArray message) {
        return MessageSender.sendGroup(this.message.groupId(), message);
    }
}
