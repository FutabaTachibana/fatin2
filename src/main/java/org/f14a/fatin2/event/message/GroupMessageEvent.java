package org.f14a.fatin2.event.message;

import com.google.gson.JsonArray;
import org.f14a.fatin2.model.MessageType;
import org.f14a.fatin2.model.message.GroupOnebotMessage;
import org.f14a.fatin2.api.MessageGenerator;
import org.f14a.fatin2.api.MessageSender;

/**
 * 当客户端接收到<b>群聊</b>消息时触发的事件基类。
 * @see MessageEvent
 */
public class GroupMessageEvent extends MessageEvent {
    private final GroupOnebotMessage message;

    public GroupMessageEvent(GroupOnebotMessage message) {
        super(message);
        this.message = message;
    }

    /**
     * @return 获取群聊消息对象
     * @see GroupOnebotMessage
     */
    @Override
    public GroupOnebotMessage getMessage() {
        return message;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GROUP;
    }

    /**
     * @return 获取事件发生的群 ID
     */
    public long getGroupId() {
        return message.groupId();
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
