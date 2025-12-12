package org.f14a.fatin2.plugin;

import org.f14a.fatin2.handler.MessageHandler;
import org.f14a.fatin2.type.message.AbstractOnebotMessage;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginWrapper {
    private final Fatin2Plugin plugin;
    private final URLClassLoader classLoader;
    private final String jarPath;
    private boolean enabled = false;
    private final List<AbstractOnebotMessage> handlers = new ArrayList<>();

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
    public List<AbstractOnebotMessage> getHandlers() {
        return this.handlers;
    }
    // Setters
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void addHandler(AbstractOnebotMessage handler) {
        this.handlers.add(handler);
    }
}
