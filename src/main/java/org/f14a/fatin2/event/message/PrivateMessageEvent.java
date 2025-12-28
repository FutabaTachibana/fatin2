package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.message.PrivateOnebotMessage;
import org.f14a.fatin2.api.MessageGenerator;
import org.f14a.fatin2.api.MessageSender;

/**
 * 当客户端接收到<b>私聊</b>消息时触发的事件基类。
 * @see MessageEvent
 */
public class PrivateMessageEvent extends MessageEvent {
    private final PrivateOnebotMessage message;

    public PrivateMessageEvent(PrivateOnebotMessage message) {
        super(message, MessageType.PRIVATE);
        this.message = message;
    }

    /**
     * @return 获取私聊消息对象
     * @see PrivateOnebotMessage
     */
    public PrivateOnebotMessage getMessage() {
        return message;
    }

    /**
     * @return 获取触发事件的用户 ID
     */
    public long getUserId() {
        return message.userId();
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
