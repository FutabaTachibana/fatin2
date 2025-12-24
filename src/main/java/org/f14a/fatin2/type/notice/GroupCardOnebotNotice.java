package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;

public record GroupCardOnebotNotice (
        @SerializedName("time") long time,
        @SerializedName("post_type") String postType,
        @SerializedName("self_id") long selfId,
        @SerializedName("notice_type") String noticeType,
        @SerializedName("group_id") long groupId,
        @SerializedName("user_id") long userId,
        @SerializedName("card_new") String cardNew,
        @SerializedName("card_old") String cardOld
) implements AbstractOnebotNotice { }
