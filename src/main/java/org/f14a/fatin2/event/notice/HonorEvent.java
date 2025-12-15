package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.HonorOnebotNotify;

public class HonorEvent extends Event {
    private final HonorOnebotNotify notify;
    public HonorEvent(HonorOnebotNotify notify) {
        this.notify = notify;
    }
    public HonorOnebotNotify getNotify() {
        return this.notify;
    }
    public String getRawHonorType() {
        return this.notify.honorType();
    }
    public long getGroupId() {
        return this.notify.groupId() != null ? this.notify.groupId() : 0L;
    }
    public long getUserId() {
        return this.notify.userId() != null ? this.notify.userId() : 0L;
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
