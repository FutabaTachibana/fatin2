package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupIncreaseOnebotNotice;

public class GroupIncreaseEvent extends Event {
    private final GroupIncreaseOnebotNotice notice;
    public enum SubType{
        APPROVE,
        INVITE
    }
    public GroupIncreaseEvent(GroupIncreaseOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupIncreaseOnebotNotice getNotice() {
        return this.notice;
    }
    public SubType getSubType(){
        return switch (this.notice.subType()){
            case "approve" -> SubType.APPROVE;
            case "invite" -> SubType.INVITE;
            default -> null;
        };
    }
    public long getGroupId(){
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getOperatorId(){
        return this.notice.operatorId() != null ? this.notice.operatorId() : 0L;
    }
    public long getUserId(){
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
