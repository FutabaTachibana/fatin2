package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupDecreaseOnebotNotice;

public class GroupDecreaseEvent extends Event {
    private final GroupDecreaseOnebotNotice notice;
    public enum SubType{
        LEAVE,
        KICK,
        KICK_ME
    }
    public GroupDecreaseEvent(GroupDecreaseOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupDecreaseOnebotNotice getNotice() {
        return this.notice;
    }
    public SubType getSubType(){
        return switch (this.notice.subType()){
            case "leave" -> SubType.LEAVE;
            case "kick" -> SubType.KICK;
            case "kick_me" -> SubType.KICK_ME;
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
