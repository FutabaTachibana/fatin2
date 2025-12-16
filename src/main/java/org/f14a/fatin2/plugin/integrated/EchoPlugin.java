package org.f14a.fatin2.plugin.integrated;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.RequestGenerator;

public class EchoPlugin implements Fatin2Plugin {
    @Override
    public void onLoad() {
        EventBus.getInstance().register(this, this);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "Echo";
    }

    @Override
    public String getAuthor() {
        return "Fatin2";
    }

    @OnCommand(command = "echo")
    public void onEcho(CommandEvent event) {
        event.reply(event.getMessage().rawMessage().substring(6).trim());
    }
}
