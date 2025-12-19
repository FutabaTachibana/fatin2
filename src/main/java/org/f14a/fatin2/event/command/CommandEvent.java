package org.f14a.fatin2.event.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.util.function.Consumer;

public interface CommandEvent {
    String getCommand();
    String[] getArgs();
    OnebotMessage getMessage();
    MessageEvent.MessageType getMessageType();
    String wait(JsonArray message);
    String wait(JsonObject... message);
    String waitSilent();
    int send(JsonArray message);
    int send(JsonObject... message);
    int sendOnly(JsonArray message);
    int sendOnly(JsonObject... message);
    void setTimeOut(int seconds);
    void setOnTimeout(Consumer<MessageEvent> callback);
    void setOnFinish(Consumer<MessageEvent> callback);
    MessageEvent getSessionEvent();

}
