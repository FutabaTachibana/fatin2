package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.PokeOnebotNotify;

public class PokeEvent extends Event {
    private final PokeOnebotNotify notice;
    public PokeEvent(PokeOnebotNotify notice) {
        this.notice = notice;
    }
    public PokeOnebotNotify getPokeNotify() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    public long getTargetId() {
        return this.notice.targetId() != null ? this.notice.targetId() : 0L;
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
