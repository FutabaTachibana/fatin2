package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.f14a.fatin2.api.MessageGenerator;
import org.f14a.fatin2.api.MessageSender;

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
        int echo = 0;
        if (isSendForward()) {
            echo = MessageSender.sendGroupForward(this.message.groupId(), this.message.selfId(), "bot", messages);
        } else if (isSendReply()) {
            echo = MessageSender.replyGroupMessage(this.message.groupId(), this.message.userId(), this.message.messageId(), messages);
        } else if (isSendAt()) {
            JsonArray newArray = MessageGenerator.builder().at(this.message.userId()).build();
            newArray.addAll(messages);
            echo = MessageSender.sendGroup(this.message.groupId(), newArray);
        } else {
            echo = MessageSender.sendGroup(this.message.groupId(), messages);
        }
        resetOptions();
        return echo;
    }
}
