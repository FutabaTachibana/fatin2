package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.AbstractOnebotNotice;

public class RecallEvent extends Event {
    private final AbstractOnebotNotice notice;
    private final boolean happenedPrivately;
    private final long groupId;    // May be 0L if recalled in private chat
    private final long userId;
    private final long operatorId; // May be 0L if recalled in private chat
    private final long messageId;


    public RecallEvent(AbstractOnebotNotice notice, boolean happenedPrivately, Long groupId, Long userId, Long operatorId, Long messageId) {
        this.notice = notice;
        this.happenedPrivately = happenedPrivately;

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
    public boolean isHappenedPrivately() {
        return this.happenedPrivately;
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
