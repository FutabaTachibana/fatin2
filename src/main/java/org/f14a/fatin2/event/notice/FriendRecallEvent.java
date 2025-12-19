package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.notice.FriendRecallOnebotNotice;

public class FriendRecallEvent extends RecallEvent {
    private final FriendRecallOnebotNotice notice;
    public FriendRecallEvent(FriendRecallOnebotNotice notice) {
        this.notice = notice;
    }
    public FriendRecallOnebotNotice getNotice() {
        return this.notice;
    }
    @Override
    public MessageType getRecallType() {
        return MessageType.PRIVATE;
    }
    @Override
    public long getUserId() {
        return this.notice.userId();
    }
    @Override
    public long getMessageId() {
        return this.notice.messageId();
    }
}
