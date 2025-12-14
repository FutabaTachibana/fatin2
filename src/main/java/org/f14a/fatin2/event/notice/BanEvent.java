package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupBanOnebotNotice;

public class BanEvent extends Event {
    private final GroupBanOnebotNotice groupBanOnebotNotice;
    public BanEvent(GroupBanOnebotNotice groupBanOnebotNotice) {
        this.groupBanOnebotNotice = groupBanOnebotNotice;
    }
    public GroupBanOnebotNotice getBanNotice() {
        return this.groupBanOnebotNotice;
    }
    public long getGroupId() {
        return this.groupBanOnebotNotice.groupId() != null ? this.groupBanOnebotNotice.groupId() : 0L;
    }
    public long getOperatorId() {
        return this.groupBanOnebotNotice.operatorId() != null ? this.groupBanOnebotNotice.operatorId() : 0L;
    }
    public long getUserId() {
        return this.groupBanOnebotNotice.userId() != null ? this.groupBanOnebotNotice.userId() : 0L;
    }
    public int getDuration() {
        return this.groupBanOnebotNotice.duration() != null ? this.groupBanOnebotNotice.duration() : 0;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
