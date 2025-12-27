package org.f14a.fatin2.event.notice;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.model.MessageType;

public abstract class RecallEvent extends Event {
    @Override
    public boolean isAsync() {
        return true;
    }

    public abstract MessageType getRecallType();
    public abstract long getUserId();
    public abstract long getMessageId();
}
