package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.message.AbstractOnebotMessage;
import org.f14a.fatin2.type.message.OnebotMessage;

public class GroupMessageHandler implements MessageHandler {
    @Override
    public boolean canHandle(AbstractOnebotMessage message) {
        if (message instanceof OnebotMessage onebotMessage) {
            return "group".equals(onebotMessage.getMessageType());
        }
        return false;
    }

    @Override
    public void handle(AbstractOnebotMessage abstract_message) {
        // Handle group message



    }
}
