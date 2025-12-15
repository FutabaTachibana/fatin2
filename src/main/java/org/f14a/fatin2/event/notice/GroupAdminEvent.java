package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupAdminOnebotNotice;

public class GroupAdminEvent extends Event {
    private final GroupAdminOnebotNotice notice;
    public enum SubType {
        SET,
        UNSET
    }
    public GroupAdminEvent(GroupAdminOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupAdminOnebotNotice getNotice() {
        return this.notice;
    }
    public SubType getSubType() {
        return switch (this.notice.subType()) {
            case "set" -> SubType.SET;
            case "unset" -> SubType.UNSET;
            default -> null;
        };
    }
    public long getGroupId() {
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
