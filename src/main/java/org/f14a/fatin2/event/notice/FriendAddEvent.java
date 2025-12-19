package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.FriendAddOnebotNotice;

public class FriendAddEvent extends Event {
    private final FriendAddOnebotNotice notice;
    public FriendAddEvent(FriendAddOnebotNotice notice) {
        this.notice = notice;
    }
    public FriendAddOnebotNotice getNotice() {
        return this.notice;
    }
    public long getUserId() {
        return this.notice.userId();
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
