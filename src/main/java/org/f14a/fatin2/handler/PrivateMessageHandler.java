package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

@Deprecated
public class PrivateMessageHandler implements MessageHandler{
    @Override
    public boolean canHandle(AbstractOnebotMessage message) {
        if (message instanceof PrivateOnebotMessage privateOnebotMessage) {
            return "private".equals(privateOnebotMessage.messageType());
        }
        return false;
    }

    @Override
    public void handle(AbstractOnebotMessage abstract_message) {
        // Handle private message



    }
}
