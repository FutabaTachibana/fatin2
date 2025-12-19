package org.f14a.fatin2.event.request;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.request.InviteOnebotRequest;
import org.f14a.fatin2.util.RequestSender;

/**
 * Called when someone invite you to a group.
 */
public class InviteRequestEvent extends Event {
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
        RequestSender.approveGroup(this.request.flag(), true);
    }
    public void decline() {
        RequestSender.approveGroup(this.request.flag(), false);
    }
    public void decline(String reason) {
        RequestSender.approveGroup(this.request.flag(), false, reason);
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
