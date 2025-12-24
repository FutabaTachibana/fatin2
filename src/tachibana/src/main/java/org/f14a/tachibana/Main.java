package org.f14a.tachibana;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.Fatin2Plugin;

public class Main implements Fatin2Plugin {
    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        EventBus.getInstance().register(new EventListener(), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "tachibana";
    }

    @Override
    public String getDisplayName() {
        return "Tachibana Plugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Futaba_Tachibana";
    }

    @Override
    public String getDescription() {
        return "这个插件没有提供任何 API 和泛用性的功能，仅实现了一些特定场景的功能。";
    }
}
