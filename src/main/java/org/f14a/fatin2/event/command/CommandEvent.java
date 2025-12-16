package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.type.message.OnebotMessage;

public class CommandEvent extends MessageEvent {
    public CommandEvent(OnebotMessage message) {
        super(message);
    }
}
