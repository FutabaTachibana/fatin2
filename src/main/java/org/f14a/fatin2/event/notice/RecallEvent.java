package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.AbstractOnebotNotice;

public class RecallEvent extends Event {
    private final AbstractOnebotNotice notice;
    public enum RecallType {
        PRIVATE,
        GROUP
    }
    private final RecallType recallType;
    private final long groupId;    // May be 0L if recalled in private chat
    private final long userId;
    private final long operatorId; // May be 0L if recalled in private chat
    private final long messageId;


    public RecallEvent(AbstractOnebotNotice notice, RecallType recallType, Long groupId, Long userId, Long operatorId, Long messageId) {
        this.notice = notice;
        this.recallType = recallType;

        this.groupId = groupId != null ? groupId : 0L;
        this.userId = userId != null ? userId : 0L;
        this.operatorId = operatorId != null ? operatorId : 0L;
        this.messageId = messageId != null ? messageId : 0L;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public AbstractOnebotNotice getNotice() {
        return this.notice;
    }
    public RecallType getRecallType() {
        return this.recallType;
    }
    public long getUserId() {
        return this.userId;
    }
    public long getMessageId() {
        return this.messageId;
    }
    public long getGroupId() {
        return this.groupId;
    }
    public long getOperatorId() {
        return this.operatorId;
    }
}
