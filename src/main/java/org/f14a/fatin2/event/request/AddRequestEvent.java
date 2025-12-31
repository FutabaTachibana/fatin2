package org.f14a.fatin2.event.request;

import org.f14a.fatin2.api.sender.GroupRequestSender;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.request.AddOnebotRequest;

/**
* Called when someone sends a group request.
*/
public class AddRequestEvent implements Event {
    private final AddOnebotRequest request;
    public AddRequestEvent(AddOnebotRequest request) {
        this.request = request;
    }
    public AddOnebotRequest getRequest() {
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
