package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.notice.GroupIncreaseOnebotNotice;

public class GroupIncreaseEvent implements Event {
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
        return this.notice.groupId();
    }
    public long getOperatorId(){
        return this.notice.operatorId();
    }
    public long getUserId(){
        return this.notice.userId();
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
