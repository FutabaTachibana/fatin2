package org.f14a.fatin2.event.lifecycle;

import org.f14a.fatin2.event.Event;

public class PluginsLoadDoneEvent implements Event {
    @Override
    public boolean isAsync() {
        return false;
    }
}
