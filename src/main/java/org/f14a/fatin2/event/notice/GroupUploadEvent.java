package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.notice.GroupUploadOnebotNotice;

public class GroupUploadEvent extends Event {
    private final GroupUploadOnebotNotice notice;
    public GroupUploadEvent(GroupUploadOnebotNotice notice) {
        this.notice = notice;
    }
    public GroupUploadOnebotNotice getNotice() {
        return this.notice;
    }
    public long getGroupId() {
        return this.notice.groupId() != null ? this.notice.groupId() : 0L;
    }
    public long getUserId() {
        return this.notice.userId() != null ? this.notice.userId() : 0L;
    }
    public GroupUploadOnebotNotice.File getFile() {
        return this.notice.file();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
