package org.f14a.fatin2.model.message;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.exception.IllegalTypeException;
import org.f14a.fatin2.model.AbstractOnebotMessage;
import org.f14a.fatin2.model.Sender;

import java.util.List;

/**
 * 一个 Onebot 消息对象，表示接收到的一条消息。
 * <p>
 * 要查阅每个字段的具体含义，请参考
 * <a href="https://github.com/botuniverse/onebot-11/blob/master/event/message.md">Onebot API v11 文档</a>
 * 或 <a href="https://napneko.github.io/onebot/basic_event">NapCat文档</a>。
 * @see PrivateOnebotMessage
 * @see GroupOnebotMessage
 */
public sealed interface OnebotMessage extends AbstractOnebotMessage permits GroupOnebotMessage, PrivateOnebotMessage {
    // private | group
    @SerializedName("message_type")
    String messageType();
    // friend | normal | anonymous | notice
    @SerializedName("sub_type")
    String subType();
    @SerializedName("message_id")
    int messageId();
    @SerializedName("user_id")
    long userId();
    @SerializedName("message")
    List<Message> messages();
//    @Deprecated
//    @SerializedName("raw_message")
//    String rawMessage();
    @SerializedName("font")
    int font();
    @SerializedName("sender")
    Sender sender();

    default String parse(){
        StringBuilder sb = new StringBuilder();
        for(Message msg : this.messages()) {
            sb.append(msg.parse());
        }
        return sb.toString();
    }

    default GroupOnebotMessage getAsGroupMessage(){
        if(this instanceof GroupOnebotMessage){
            return (GroupOnebotMessage)this;
        }else{
            throw new IllegalTypeException("Expected GroupOnebotMessage but got " + this.getClass().getSimpleName());
        }
    }

    default PrivateOnebotMessage getAsPrivateMessage(){
        if(this instanceof PrivateOnebotMessage){
            return (PrivateOnebotMessage)this;
        }else{
            throw new IllegalTypeException("Expected PrivateOnebotMessage but got " + this.getClass().getSimpleName());
        }
    }
}
