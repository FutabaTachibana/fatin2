package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.message.OnebotMessage;

public interface MessageHandler {
    boolean canHandle(OnebotMessage message);

    void handle(OnebotMessage message);
}
