package org.f14a.fatin2.client.sender;

import org.f14a.fatin2.client.Client;

public class PrivateMessageSender {
    public static void send(PrivateMessageSender message){
        Client.getInstance().send(message.toString());
    }
}
