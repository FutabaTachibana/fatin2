package org.f14a.fatin2.client.sender;

import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;

public class PrivateMessageSender {
    public static void send(PrivateOnebotMessage message){
        String string = message.toString();
        Main.LOGGER.debug("Sending private message: {}", string);
        Client.getInstance().send("{\"action\":\"send_private_msg\",\"params\":" + string + "}");
    }
}
