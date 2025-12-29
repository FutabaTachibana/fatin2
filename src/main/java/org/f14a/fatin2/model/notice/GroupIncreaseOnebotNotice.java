package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 群成员增加通知。
 * @param subType 子类型，取值为 "approve" "invite"
 * @param groupId 事件发生的群 ID
 * @param operatorId 操作者的用户 ID
 * @param userId 事件对应的用户 ID
 * @see AbstractOnebotNotice
 */
public record GroupIncreaseOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type")    String subType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("operator_id") long   operatorId,
        @SerializedName("user_id")     long   userId
) implements AbstractOnebotNotice { }
