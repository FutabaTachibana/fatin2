package org.f14a.fatin2.util;

import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;
import org.f14a.fatin2.type.message.GroupOnebotMessage;
import org.f14a.fatin2.type.message.PrivateOnebotMessage;


public class MessageSender {
    /**
     * Send a group message.
     * @param message the original json message.
     */
    public static void sendGroup(Long groupId, String message){
        String string = RequestGenerator.builder().groupId(groupId).message(message).build();
        Main.LOGGER.debug("Sending group message: {}", string);
        Client.getInstance().send("{\"action\":\"send_group_msg\",\"params\":" + string + "}");
    }
    /**
     * Send a private message.
     * @param message the original json message.
     */
    public static void sendPrivate(Long userId, String message){
        String string = RequestGenerator.builder().userId(userId).message(message).build();
        Main.LOGGER.debug("Sending private message: {}", string);
        Client.getInstance().send("{\"action\":\"send_private_msg\",\"params\":" + string + "}");
    }
}
