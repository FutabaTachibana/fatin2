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
        return this.notify.groupId() != null ? this.notify.groupId() : 0L;
    }
    public long getUserId() {
        return this.notify.userId() != null ? this.notify.userId() : 0L;
    }
    public long getTargetId() {
        return this.notify.targetId() != null ? this.notify.targetId() : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
