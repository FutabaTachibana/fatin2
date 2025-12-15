package org.f14a.fatin2.client.sender;

import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.type.message.GroupOnebotMessage;


public class GroupMessageSender {
    /**
     * Send a group message.
     * @param message the original json message.
     */
    public static void send(GroupOnebotMessage message){
        String string = message.toString();
        Main.LOGGER.debug("Sending group message: {}", string);
        Client.getInstance().send("{\"action\":\"send_group_msg\",\"params\":" + string + "}");
    }
}
