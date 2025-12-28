package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.Event;

/**
 * 当权限被拒绝时触发
 */
public class PermissionDeniedEvent implements Event {
    private final CommandEvent currentEvent;
    private final CommandEventListener listener;

    public PermissionDeniedEvent(CommandEvent currentEvent, CommandEventListener listener) {
        this.currentEvent = currentEvent;
        this.listener = listener;
    }
    public CommandEvent getCurrentEvent() {
        return currentEvent;
    }
    public CommandEventListener getListener() {
        return listener;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
