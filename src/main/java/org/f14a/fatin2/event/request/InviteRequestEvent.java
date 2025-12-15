package org.f14a.fatin2.event.request;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.request.InviteOnebotRequest;

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
        return this.request.userId() != null ? this.request.userId() : 0L;
    }
    public long getGroupId() {
        return this.request.groupId() != null ? this.request.groupId() : 0L;
    }
    // TODO: Implement agree and decline logic here
    public void agree() {

    }
    public void decline() {

    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
