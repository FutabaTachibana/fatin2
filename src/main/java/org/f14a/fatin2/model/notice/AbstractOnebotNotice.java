package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.model.AbstractOnebotMessage;

/**
 * 一个 Onebot 通知对象，表示接收到的一条通知。
 * <p>
 * <a href="https://github.com/botuniverse/onebot-11/blob/master/event/notice.md">Onebot API v11 文档</a>
 * 介绍了大部分类型的通知和这些字段的含义。
 * <p>
 * {@code notice_type} 字段表示通知的类型，可能的值包括：
 * <ul>
 *     <li>{@code group_upload}: {@link GroupUploadOnebotNotice}</li>
 *     <li>{@code group_admin}: {@link GroupAdminOnebotNotice}</li>
 *     <li>{@code group_decrease}: {@link GroupDecreaseOnebotNotice}</li>
 *     <li>{@code group_increase}: {@link GroupIncreaseOnebotNotice}</li>
 *     <li>{@code group_ban}: {@link GroupBanOnebotNotice}</li>
 *     <li>{@code friend_add}: {@link FriendAddOnebotNotice}</li>
 *     <li>{@code group_recall}: {@link GroupRecallOnebotNotice}</li>
 *     <li>{@code friend_recall}: {@link FriendRecallOnebotNotice}</li>
 *     <li>{@code notify}: {@link AbstractOnebotNotify}</li>
 *     <li>{@code group_msg_emoji_like}: 暂不可用</li>
 *     <li>{@code essence}: 暂不可用</li>
 *     <li>{@code group_card}: {@link GroupCardOnebotNotice}</li>
 *     <li>{@code notify}: {@link AbstractOnebotNotify}</li>
 * </ul>
 */
public interface AbstractOnebotNotice extends AbstractOnebotMessage {
    @SerializedName("notice_type") String noticeType();
}
