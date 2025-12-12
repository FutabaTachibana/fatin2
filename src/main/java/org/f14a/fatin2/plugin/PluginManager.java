package org.f14a.fatin2.plugin;


import org.f14a.fatin2.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PluginManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);
    private final File pluginDir;
    private final boolean autoReload;


    public PluginManager(boolean autoReload) {
        this.autoReload = autoReload;

        File dir = new File(Config.getConfig().getPluginDirectory());
        this.pluginDir = dir;

        if (!dir.exists()) {
            LOGGER.info("Creating plugin directory{}", Config.getConfig().getPluginDirectory());
            if(!dir.mkdirs()) {
                LOGGER.error("Failed to create plugin directory: {}", Config.getConfig().getPluginDirectory());
                return;
            }
        }
        PluginLoader.loadAllPlugins(dir);
        if (autoReload) {
            // TODO: Implement auto-reload functionality
        }
    }
}
