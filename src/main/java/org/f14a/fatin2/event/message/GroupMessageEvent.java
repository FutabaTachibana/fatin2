package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.MessageSender;

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
    public int send(JsonArray messages) {
        if (isSendForward()) {
            return MessageSender.sendGroupForward(this.message.groupId(), messages);
        } else if (isSendReply()) {
            return MessageSender.replyGroupMessage(this.message.groupId(), this.message.userId(), this.message.messageId(), messages);
        } else if (isSendAt()) {
            JsonArray newArray = MessageGenerator.builder().at(this.message.userId()).build();
            newArray.addAll(messages);
            return MessageSender.sendGroup(this.message.groupId(), newArray);
        } else {
            return MessageSender.sendGroup(this.message.groupId(), messages);
        }
    }
}
