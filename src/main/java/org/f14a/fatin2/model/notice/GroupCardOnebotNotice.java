package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 表示群名片变更通知的通知。
 * @param groupId 发生的群 ID
 * @param userId 群名片变更的用户 ID
 * @param cardNew 新的群名片
 * @param cardOld 旧的群名片
 * @see AbstractOnebotNotice
 */
public record GroupCardOnebotNotice(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId,
        @SerializedName("card_new")    String cardNew,
        @SerializedName("card_old")    String cardOld
) implements AbstractOnebotNotice { }
