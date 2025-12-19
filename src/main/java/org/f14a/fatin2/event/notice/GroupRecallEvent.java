package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.type.MessageType;
import org.f14a.fatin2.type.notice.GroupRecallOnebotNotice;

public class GroupRecallEvent extends RecallEvent {
    private final GroupRecallOnebotNotice notice;
    public GroupRecallEvent(GroupRecallOnebotNotice groupRecallOnebotNotice) {
        this.notice = groupRecallOnebotNotice;
    }
    public GroupRecallOnebotNotice getNotice() {
        return notice;
    }
    @Override
    public MessageType getRecallType() {
        return MessageType.GROUP;
    }
    public long getGroupId() {
        return this.notice.groupId();
    }
    @Override
    public long getUserId() {
        return this.notice.userId();
    }
    public long getOperatorId() {
        return this.notice.operatorId();
    }
    @Override
    public long getMessageId() {
        return this.notice.messageId();
    }


}
