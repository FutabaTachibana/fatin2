package org.f14a.fatin2.event.request;

import org.f14a.fatin2.api.GroupRequestSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.request.InviteOnebotRequest;

/**
 * Called when someone invite you to a group.
 */
public class InviteRequestEvent implements Event {
    private final InviteOnebotRequest request;
    public InviteRequestEvent(InviteOnebotRequest request) {
        this.request = request;
    }
    public InviteOnebotRequest getRequest() {
        return this.request;
    }
    public long getUserId() {
        return this.request.userId();
    }
    public long getGroupId() {
        return this.request.groupId();
    }
    public void agree() {
        GroupRequestSender.approveGroup(this.request.flag(), true);
    }
    public void decline() {
        GroupRequestSender.approveGroup(this.request.flag(), false);
    }
    public void decline(String reason) {
        GroupRequestSender.approveGroup(this.request.flag(), false, reason);
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
