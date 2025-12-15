package org.f14a.fatin2.event.request;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.request.FriendOnebotRequest;

public class FriendRequestEvent extends Event {
    private final FriendOnebotRequest request;
    public FriendRequestEvent(FriendOnebotRequest request) {
        this.request = request;
    }
    public long getUserId() {
        return this.request.userId() != null ? this.request.userId() : 0L;
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
