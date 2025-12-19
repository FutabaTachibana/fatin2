package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.PokeOnebotNotify;

public class PokeEvent extends Event {
    private final PokeOnebotNotify notify;
    public PokeEvent(PokeOnebotNotify notify) {
        this.notify = notify;
    }
    public PokeOnebotNotify getNotify() {
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
