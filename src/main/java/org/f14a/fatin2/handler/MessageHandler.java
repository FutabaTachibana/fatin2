package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.message.AbstractOnebotMessage;

@Deprecated
public interface MessageHandler {
    boolean canHandle(AbstractOnebotMessage message);

    void handle(AbstractOnebotMessage message);
}
