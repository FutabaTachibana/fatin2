package org.f14a.fatin2.plugin;

import org.f14a.fatin2.event.EventBus;

import java.net.URLClassLoader;

public class PluginWrapper {
    private final Fatin2Plugin plugin;
    private final URLClassLoader classLoader;
    private final String jarPath;
    private boolean enabled = false;

    public PluginWrapper(Fatin2Plugin plugin, URLClassLoader classLoader, String jarPath) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.jarPath = jarPath;
    }

    // Getters
    public Fatin2Plugin getPlugin() {
        return this.plugin;
    }
    public URLClassLoader getClassLoader() {
        return this.classLoader;
    }
    public String getJarPath() {
        return this.jarPath;
    }
    public boolean isEnabled() {
        return this.enabled;
    }
    // Setters
    public void enable() {
        this.plugin.onEnable();
        this.enabled = true;
    }
    public void disable() {
        // Call onDisable
        this.plugin.onDisable();
        // Remove from eventbus
        EventBus.getInstance().unregister(this.plugin);
        this.enabled = false;
    }
}
