package org.f14a.fatin2.model.notice;

import com.google.gson.annotations.SerializedName;

/**
 * 戳一戳通知。
 * @param subType 固定为 "poke"
 * @param groupId 事件发生的群 ID，如果是私聊则为 0
 * @param userId 戳一戳发起者的用户 ID
 * @param targetId 被戳一戳的用户 ID
 * @see AbstractOnebotNotify
 */
public record PokeOnebotNotify(
        @SerializedName("time")        long   time,
        @SerializedName("post_type")   String postType,
        @SerializedName("self_id")     long   selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("sub_type")    String subType,
        @SerializedName("group_id")    long   groupId,
        @SerializedName("user_id")     long   userId,
        @SerializedName("target_id")   long   targetId
) implements AbstractOnebotNotify { }
