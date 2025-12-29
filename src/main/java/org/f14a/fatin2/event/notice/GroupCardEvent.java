package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.notice.GroupCardOnebotNotice;

public class GroupCardEvent implements Event {
    private final GroupCardOnebotNotice notice;
    public GroupCardEvent(GroupCardOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupCardOnebotNotice getNotice() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId();
    }
    public long getUserId() {
        return this.notice.userId();
    }
    public String getCardNew() {
        return this.notice.cardNew();
    }
    public String getCardOld() {
        return this.notice.cardOld();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
