package org.f14a.fatin2.event;

public abstract class Event {
    public abstract boolean isAsync();
    public void fire() {
        EventBus.getInstance().post(this);
    }
}
