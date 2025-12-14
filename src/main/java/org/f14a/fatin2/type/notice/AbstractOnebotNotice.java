package org.f14a.fatin2.type.notice;

import com.google.gson.annotations.SerializedName;
import org.f14a.fatin2.type.AbstractOnebotMessage;

public interface AbstractOnebotNotice extends AbstractOnebotMessage {
    // group_upload | group_admin | group_decrease | group_increase | group_ban | friend_add
    // group_recall | friend_recall | poke | lucky_king | honor | group_msg_emoji_like
    // essence | group_card
    @SerializedName("notice_type")
    String noticeType();
    // TODO:
}
