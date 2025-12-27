package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.notice.GroupBanOnebotNotice;

public class GroupBanEvent extends Event {
    private final GroupBanOnebotNotice notice;
    public GroupBanEvent(GroupBanOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupBanOnebotNotice getNotice() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId();
    }
    public long getOperatorId() {
        return this.notice.operatorId();
    }
    public long getUserId() {
        return this.notice.userId();
    }
    public int getDuration() {
        return this.notice.duration();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
