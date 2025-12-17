package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.Fatin2Plugin;

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
