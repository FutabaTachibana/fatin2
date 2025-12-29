package org.f14a.fatin2.model.request;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.model.AbstractOnebotMessage;

/**
 * 一个 Onebot 请求对象，表示接收到的好友或群申请。
 * <p>
 * <a href="https://github.com/botuniverse/onebot-11/blob/master/event/notice.md">Onebot API v11 文档</a>
 * 介绍了所有类型的消息和这些字段的含义。
 *  <p>
 * {@code request_type} 字段表示请求的类型，可能的值包括：
 * <ul>
 *     <li>{@code friend}: {@link FriendOnebotRequest}</li>
 *     <li>{@code group}: {@link AddOnebotRequest} 和 {@link InviteOnebotRequest}</li>
 * </ul>
 */
public interface AbstractOnebotRequest extends AbstractOnebotMessage {
    @SerializedName("request_type") String requestType();
    @SerializedName("flag")         String flag();
    @SerializedName("user_id")      long   userId();
    @SerializedName("comment")      String comment();
}
