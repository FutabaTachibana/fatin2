package org.f14a.fatin2.type.message.notice;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.message.AbstractOnebotMessage;

public abstract class AbstractOnebotNotice extends AbstractOnebotMessage {
    // Notice Type
    // group_upload | group_admin | group_decrease | group_increase | group_ban | friend_add
    // group_recall | friend_recall | poke | lucky_king | honor | group_msg_emoji_like
    // essence | group_card
    @SerializedName("notice_type")
    private String noticeType;

    // Getter
    public String getNoticeType() {
        return this.noticeType;
    }
}
