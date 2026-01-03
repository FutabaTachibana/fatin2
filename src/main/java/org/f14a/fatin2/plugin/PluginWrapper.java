package org.f14a.fatin2.plugin;

import lombok.Getter;
import lombok.Setter;
import org.f14a.fatin2.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;

public class PluginWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginWrapper.class);
    public static final String INTEGRATED_JAR_PATH = "Integrated";

    @Getter
    private final Fatin2Plugin plugin;
    @Getter
    private final URLClassLoader classLoader;
    @Getter
    private final String jarPath;
    @Getter
    private boolean enabled = false;

    public PluginWrapper(Fatin2Plugin plugin, URLClassLoader classLoader, String jarPath) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.jarPath = jarPath;
    }

    // Setters
    public void enable() {
        if (this.enabled) {
            return;
        }
        this.plugin.onEnable();
        this.enabled = true;
    }

    public void disable() {
        if (!this.enabled) {
            // Still make sure we are not registered in EventBus
            EventBus.getInstance().unregister(this.plugin);
            return;
        }
        try {
            // Call onDisable
            this.plugin.onDisable();
        } catch (Exception e) {
            LOGGER.error("Error while disabling plugin {}", this.plugin != null ? this.plugin.getName() : "null", e);
        } finally {
            // Always remove from eventbus even if onDisable throws
            EventBus.getInstance().unregister(this.plugin);
            this.enabled = false;
        }
    }
}
