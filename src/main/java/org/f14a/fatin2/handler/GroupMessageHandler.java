package org.f14a.fatin2.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.client.sender.GroupMessageSender;
import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.f14a.fatin2.type.message.GroupOnebotMessage;

@Deprecated
public class GroupMessageHandler implements MessageHandler {
    @Override
    public boolean canHandle(AbstractOnebotMessage message) {
        if (message instanceof GroupOnebotMessage groupOnebotMessage) {
            return "group".equals(groupOnebotMessage.messageType());
        }
        return false;
    }

    @Override
    public void handle(AbstractOnebotMessage abstract_message) {
        // Handle group message
        if (abstract_message instanceof GroupOnebotMessage message) {
            if(message.rawMessage().startsWith("/echo")) {
                String reply = message.rawMessage().substring(5).trim();
                String raw_reply = "{\"type\":\"text\",\"data\":{\"text\":\"" + reply + "\"}}";
                Gson gson = new Gson();
                GroupOnebotMessage response = new GroupOnebotMessage(message.groupId(), gson.fromJson(raw_reply, JsonObject.class));
                GroupMessageSender.send(response);
            }
        }

    }
}
