package org.f14a.fatin2.event.request;

import org.f14a.fatin2.api.GroupRequestGenerator;
import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.request.AddOnebotRequest;
import org.f14a.fatin2.api.RequestSender;

/**
* Called when someone sends a group request.
*/
public class AddRequestEvent extends Event {
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
        GroupRequestGenerator.approveGroup(this.request.flag(), true);
    }
    public void decline() {
        GroupRequestGenerator.approveGroup(this.request.flag(), false);
    }
    public void decline(String reason) {
        GroupRequestGenerator.approveGroup(this.request.flag(), false, reason);
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
