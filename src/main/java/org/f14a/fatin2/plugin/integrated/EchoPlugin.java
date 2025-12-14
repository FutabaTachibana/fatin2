package org.f14a.fatin2.plugin.integrated;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.message.GroupMessageEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;

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

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        if (event.getMessage().rawMessage().startsWith("/echo ")) {
            String reply = event.getMessage().rawMessage().substring(5);
            String raw_reply = "{\"type\":\"text\",\"data\":{\"text\":\"" + reply + "\"}}";
            Gson gson = new Gson();
            event.reply(gson.fromJson(raw_reply, JsonObject.class));
        }
    }
}
