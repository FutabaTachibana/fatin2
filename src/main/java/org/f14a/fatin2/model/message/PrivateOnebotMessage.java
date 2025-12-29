package org.f14a.fatin2.model.message;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * 一个 Onebot 私聊消息对象，表示接收到的一条私聊消息。
 * @param time 消息发生的时间戳
 * @param postType 报文类型，固定为 "message"
 * @param selfId 机器人自身的 ID
 * @param messageType 消息的域，固定为 "private"
 * @param subType 消息的子类型，如 "friend" "group" "normal"
 * @param messageId 消息的唯一 ID
 * @param userId 发送消息的用户 ID
 * @param messages 消息内容的列表
 * @param font 消息使用的字体
 * @param sender 发送者的信息，是一个 {@link Sender} 对象
 * @see OnebotMessage
 */
public record PrivateOnebotMessage(
        @SerializedName("time")         long          time,
        @SerializedName("post_type")    String        postType,
        @SerializedName("self_id")      long          selfId,
        @SerializedName("message_type") String        messageType,
        @SerializedName("sub_type")     String        subType,
        @SerializedName("message_id")   int           messageId,
        @SerializedName("user_id")      long          userId,
        @SerializedName("message")      List<Message> messages,
        @SerializedName("font")         int           font,
        @SerializedName("sender")       Sender        sender
) implements OnebotMessage {
    @Override
    public List<Message> messages() {
        return Collections.unmodifiableList(messages);
    }
}
