package org.f14a.demoplugin;

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

public class Main implements Fatin2Plugin {
    @Override
    public void onLoad() {
        EventBus.getInstance().register(new EventListener(), this);
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
}
