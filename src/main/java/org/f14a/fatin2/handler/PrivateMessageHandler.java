package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.message.OnebotMessage;

public class PrivateMessageHandler implements MessageHandler{
    @Override
    public boolean canHandle(OnebotMessage message) {
        return ("message".equals(message.getPostType()) ||
                "message_sent".equals(message.getPostType()))
                && "private".equals(message.getMessageType());
    }

    @Override
    public void handle(OnebotMessage message) {
        // Handle private message
        System.out.println("Handling private message: " + message.getRawMessage());


    }
}
