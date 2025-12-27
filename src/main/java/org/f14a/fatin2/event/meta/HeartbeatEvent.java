package org.f14a.fatin2.event.meta;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.meta.OnebotHeartbeat;

public class HeartbeatEvent extends Event {
    private final OnebotHeartbeat heartbeat;
    public HeartbeatEvent(OnebotHeartbeat heartbeat) {
        this.heartbeat = heartbeat;
    }
    public OnebotHeartbeat getHeartbeat() {
        return heartbeat;
    }
    public boolean isOnline() {
        return heartbeat.status().online();
    }
    public boolean isGood() {
        return heartbeat.status().good();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
