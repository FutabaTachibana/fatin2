package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.MessageSender;

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
        int echo = 0;
        if (isSendForward()) {
            echo = MessageSender.sendPrivateForward(this.message.userId(), this.message.selfId(), "bot", messages);
        }if (isSendReply()) {
            echo = MessageSender.replyPrivateMessage(this.message.userId(), this.message.messageId(), messages);
        } else if (isSendAt()) {
            JsonArray newArray = MessageGenerator.builder().at(this.message.userId()).build();
            newArray.addAll(messages);
            echo = MessageSender.sendPrivate(this.message.userId(), newArray);
        } else {
            echo = MessageSender.sendPrivate(this.message.userId(), messages);
        }
        resetOptions();
        return echo;
    }

}
