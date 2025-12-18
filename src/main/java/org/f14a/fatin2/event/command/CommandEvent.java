package org.f14a.fatin2.event.command;

import com.google.gson.JsonElement;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.util.function.Consumer;

public interface CommandEvent {
    String getCommand();
    String[] getArgs();
    OnebotMessage getMessage();
    MessageEvent.MessageType getMessageType();
    String wait(JsonElement message);
    String waitSilent();
    void send(JsonElement message);
    void setTimeOut(int seconds);
    void setOnTimeout(Consumer<MessageEvent> callback);
    void setOnFinish(Consumer<MessageEvent> callback);
    MessageEvent getSessionEvent();

}
