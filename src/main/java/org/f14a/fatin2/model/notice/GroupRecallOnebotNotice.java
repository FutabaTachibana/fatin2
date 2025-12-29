package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 好友撤回消息通知。
 * @param groupId 事件发生的群 ID
 * @param userId 撤回消息的好友 ID
 * @param messageId 被撤回的消息 ID
 * @see AbstractOnebotNotice
 */
public record GroupRecallOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId,
        @SerializedName("operator_id") long   operatorId,
        @SerializedName("message_id")  int    messageId
) implements AbstractOnebotNotice { }
