package org.f14a.fatin2.event;

import java.lang.reflect.Method;
import java.util.List;

public abstract class Event {
    public abstract boolean isAsync();
    public  void fire() {
        EventBus.getInstance().post(this);
    }
}
