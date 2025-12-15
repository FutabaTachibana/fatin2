package org.f14a.fatin2.event.request;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.request.AddOnebotRequest;

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
