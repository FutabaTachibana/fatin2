package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupBanOnebotNotice;

public class GroupBanEvent extends Event {
    private final GroupBanOnebotNotice notice;
    public GroupBanEvent(GroupBanOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupBanOnebotNotice getNotice() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getOperatorId() {
        return this.notice.operatorId() != null ? this.notice.operatorId() : 0L;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    public int getDuration() {
        return this.notice.duration() != null ? this.notice.duration() : 0;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
