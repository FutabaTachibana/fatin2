package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.FriendRecallOnebotNotice;

public class FriendRecallEvent extends Event {
    private final FriendRecallOnebotNotice notice;
    public FriendRecallEvent(FriendRecallOnebotNotice notice) {
        this.notice = notice;
    }
    public FriendRecallOnebotNotice getNotice() {
        return this.notice;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    public long getMessageId() {
        return this.notice.messageId() != null ? this.notice.messageId() : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    @Override
    public void fire() {
        new RecallEvent(this.notice, RecallEvent.RecallType.PRIVATE,
                null, this.notice.userId(),
                null, this.notice.messageId()).fire();
        super.fire();
    }
}
