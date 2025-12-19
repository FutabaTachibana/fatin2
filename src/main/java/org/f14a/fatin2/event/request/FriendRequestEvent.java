package org.f14a.fatin2.event.request;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.type.request.FriendOnebotRequest;
import org.f14a.fatin2.util.RequestSender;

/**
 * Called when someone sends a friend request.
 */
public class FriendRequestEvent extends Event {
    private final FriendOnebotRequest request;
    public FriendRequestEvent(FriendOnebotRequest request) {
        this.request = request;
    }
    public long getUserId() {
        return this.request.userId();
    }
    public void agree() {
        RequestSender.approveFriend(this.request.flag(), true);
    }
    public void decline() {
        RequestSender.approveFriend(this.request.flag(), false);
    }
    public void decline(String remark) {
        RequestSender.approveFriend(this.request.flag(), false, remark);
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
