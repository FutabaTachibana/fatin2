package org.f14a.fatin2.handler;

import org.f14a.fatin2.type.message.OnebotMessage;

public class GroupMessageHandler implements MessageHandler {
    @Override
    public boolean canHandle(OnebotMessage message) {
        return ("message".equals(message.getPostType()) ||
                "message_sent".equals(message.getPostType()))
                && "group".equals(message.getMessageType());
    }

    @Override
    public void handle(OnebotMessage message) {
        // Handle group message
        System.out.println("Handling group message: " + message.getRawMessage());


    }
}
