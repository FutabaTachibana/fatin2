package org.f14a.fatin2.event.message;

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
        return message.groupId() != null ? message.groupId() : 0L;
    }

    @Override
    public void send(String message) {
        MessageSender.sendGroup(this.message.groupId(), message);
        finishSession();
    }
}
