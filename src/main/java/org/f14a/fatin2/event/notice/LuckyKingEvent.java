package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.LuckyKingOnebotNotify;

public class LuckyKingEvent extends Event {
    private final LuckyKingOnebotNotify notify;
    public LuckyKingEvent(LuckyKingOnebotNotify notify) {
        this.notify = notify;
    }
    public LuckyKingOnebotNotify getNotify() {
        return this.notify;
    }
    public long getGroupId() {
        return this.notify.groupId();
    }
    public long getUserId() {
        return this.notify.userId();
    }
    public long getTargetId() {
        return this.notify.targetId();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
