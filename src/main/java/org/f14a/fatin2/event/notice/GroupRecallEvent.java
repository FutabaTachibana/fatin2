package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupRecallOnebotNotice;

public class GroupRecallEvent extends Event {
    public final GroupRecallOnebotNotice notice;
    public GroupRecallEvent(GroupRecallOnebotNotice groupRecallOnebotNotice) {
        this.notice = groupRecallOnebotNotice;
    }
    public GroupRecallOnebotNotice getNotice() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    public long getOperatorId() {
        return this.notice.operatorId() != null ? this.notice.operatorId() : 0L;
    }
    public long getMessageId() {
        return this.notice.messageId() != null ? this.notice.messageId() : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    @Override
    public void fire() {
        new RecallEvent(this.notice, RecallEvent.RecallType.GROUP,
                this.notice.groupId(), this.notice.userId(),
                this.notice.operatorId(), this.notice.messageId()).fire();
        super.fire();
    }
}
