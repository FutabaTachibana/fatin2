package org.f14a.fatin2.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.f14a.fatin2.Main;
import org.f14a.fatin2.client.Client;

import java.util.Map;

/**
 * A utility class for sending messages.
 */
public class MessageSender {
    private static final Gson gson = new Gson();
    /**
     * Send a group message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendGroup(long groupId, JsonArray message){
        JsonObject jsonObject = RequestGenerator.builder().groupId(groupId).message(message).build();
        int echo = jsonObject.hashCode();
        String request = gson.toJson(Map.of(
                "action", "send_group_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        Main.LOGGER.debug("Sending group message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
    /**
     * Send a private message.
     * @param message the original json message.
     *                You must use RequestGenerator to build the message send request.
     * @return the echo of the request, for tracking the message status.
     */
    public static int sendPrivate(long userId, JsonArray message){
        JsonObject jsonObject = RequestGenerator.builder().userId(userId).message(message).build();
        int echo = jsonObject.hashCode();
        String request = gson.toJson(Map.of(
                "action", "send_private_msg",
                "params", jsonObject,
                "echo", Integer.toString(echo)
        ));
        Main.LOGGER.debug("Sending private message: {}", request);
        Client.getInstance().send(request);
        return echo;
    }
}
