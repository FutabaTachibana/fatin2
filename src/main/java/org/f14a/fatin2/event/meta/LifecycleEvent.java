package org.f14a.fatin2.event.meta;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.meta.OnebotLifecycle;

public class LifecycleEvent extends Event {
    private final OnebotLifecycle lifecycle;
    public LifecycleEvent(OnebotLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }
    public OnebotLifecycle getLifecycle() {
        return lifecycle;
    }
    public boolean isConnected() {
        return "connect".equals(this.lifecycle.subType());
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
