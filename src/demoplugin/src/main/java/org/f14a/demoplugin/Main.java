package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.Fatin2Plugin;

public class Main implements Fatin2Plugin {
    // This method is called when the plugin is loaded
    @Override
    public void onLoad() {

    }
    // This method is called when the plugin is enabled
    @Override
    public void onEnable() {
        EventBus.getInstance().register(new EventListener(), this);
    }
    // This method is called when the plugin is disabled, including during shutdown
    @Override
    public void onDisable() {

    }
    // What do you name for this plugin
    // It should be unique among all plugins
    @Override
    public String getName() {
        return "demo-plugin";
    }
    // Display name of your plugin
    @Override
    public String getDisplayName() {
        return "Demo Plugin";
    }
    // The name of yours
    @Override
    public String getAuthor() {
        return "Futaba_Tachibana";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "示例插件，展示如何开发一个 Fatin2 插件。";
    }
}
