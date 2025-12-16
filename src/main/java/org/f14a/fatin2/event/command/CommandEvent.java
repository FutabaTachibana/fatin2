package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.type.message.OnebotMessage;

public interface CommandEvent {
    String getCommand();
    String[] getArgs();
    OnebotMessage getMessage();
    MessageEvent.MessageType getMessageType();
    String wait(String message);
    String waitSilent();
    void send(String message);
}
