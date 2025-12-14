package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.FriendRecallOnebotNotice;

public class FriendRecallEvent extends Event {
    private final FriendRecallOnebotNotice friendRecallOnebotNotice;
    public FriendRecallEvent(FriendRecallOnebotNotice friendRecallOnebotNotice) {
        this.friendRecallOnebotNotice = friendRecallOnebotNotice;
    }
    public FriendRecallOnebotNotice getFriendRecallNotice() {
        return this.friendRecallOnebotNotice;
    }
    public long getUserId() {
        return this.friendRecallOnebotNotice.userId() != null ? this.friendRecallOnebotNotice.userId() : 0L;
    }
    public long getMessageId() {
        return this.friendRecallOnebotNotice.messageId() != null ? this.friendRecallOnebotNotice.messageId() : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    @Override
    public void fire() {
        new RecallEvent(this.friendRecallOnebotNotice, true,
                null, this.friendRecallOnebotNotice.userId(),
                null, this.friendRecallOnebotNotice.messageId()).fire();
        super.fire();
    }
}
