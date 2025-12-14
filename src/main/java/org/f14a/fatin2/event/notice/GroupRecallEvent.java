package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupRecallOnebotNotice;

public class GroupRecallEvent extends Event {
    public final GroupRecallOnebotNotice groupRecallOnebotNotice;
    public GroupRecallEvent(GroupRecallOnebotNotice groupRecallOnebotNotice) {
        this.groupRecallOnebotNotice = groupRecallOnebotNotice;
    }
    public GroupRecallOnebotNotice getGroupRecallNotice() {
        return this.groupRecallOnebotNotice;
    }
    public long getGroupId() {
        return this.groupRecallOnebotNotice.groupId() != null ? this.groupRecallOnebotNotice.groupId() : 0L;
    }
    public long getUserId() {
        return this.groupRecallOnebotNotice.userId() != null ? this.groupRecallOnebotNotice.userId() : 0L;
    }
    public long getOperatorId() {
        return this.groupRecallOnebotNotice.operatorId() != null ? this.groupRecallOnebotNotice.operatorId() : 0L;
    }
    public long getMessageId() {
        return this.groupRecallOnebotNotice.messageId() != null ? this.groupRecallOnebotNotice.messageId() : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    @Override
    public void fire() {
        new RecallEvent(this.groupRecallOnebotNotice, false,
                this.groupRecallOnebotNotice.groupId(), this.groupRecallOnebotNotice.userId(),
                this.groupRecallOnebotNotice.operatorId(), this.groupRecallOnebotNotice.messageId()).fire();
        super.fire();
    }
}
