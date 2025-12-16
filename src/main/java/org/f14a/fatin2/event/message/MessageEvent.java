package org.f14a.fatin2.event.message;

import com.google.gson.JsonObject;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class MessageEvent extends Event {
    private final OnebotMessage message;
    public enum MessageType {
        PRIVATE,
        GROUP
    }
    private final MessageType messageType;
    public MessageEvent(OnebotMessage message) {
        this.message = message;
        this.messageType = switch (message.messageType()){
            case "private" -> MessageType.PRIVATE;
            case "group" -> MessageType.GROUP;
            default -> null;
        };
    }

    public OnebotMessage getMessage() {
        return message;
    }
    public MessageType getMessageType() {
        return this.messageType;
    }
    @Override
    public boolean isAsync() {
        return true;
    }

    public void reply(String sendMessages) {
        if (this.messageType == MessageType.PRIVATE) {
            MessageSender.sendPrivate(this.message.userId(), sendMessages);
        }
        else if (this.messageType == MessageType.GROUP) {
            MessageSender.sendGroup(((GroupOnebotMessage) message).groupId(), sendMessages);
        }
    }
}
