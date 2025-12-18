package org.f14a.fatin2.util;

import com.google.gson.JsonElement;
import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;

/**
 * A utility class for sending messages.
 */
public class MessageSender {
    /**
     * Send a group message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     */
    public static void sendGroup(Long groupId, JsonElement message){
        String string = RequestGenerator.builder().groupId(groupId).message(message).build();
        Main.LOGGER.debug("Sending group message: {}", string);
        Client.getInstance().send("{\"action\":\"send_group_msg\",\"params\":" + string + "}");
    }
    /**
     * Send a private message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     */
    public static void sendPrivate(Long userId, JsonElement message){
        String string = RequestGenerator.builder().userId(userId).message(message).build();
        Main.LOGGER.debug("Sending private message: {}", string);
        Client.getInstance().send("{\"action\":\"send_private_msg\",\"params\":" + string + "}");
    }
}
