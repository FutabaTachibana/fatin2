package org.f14a.fatin2.plugin;

import org.f14a.fatin2.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);
    private static PluginManager instance;
    public static PluginManager getInstance() {
        return PluginManager.instance;
    }
    private final Map<String, PluginWrapper> plugins;
    private final String pluginDir;

    private  WatchService watchService;
    private Thread watchThread;

    public PluginManager(boolean autoReload) {
        PluginManager.instance = this;
        this.plugins  = new ConcurrentHashMap<>();

        this.pluginDir = Config.getConfig().getPluginDirectory();
        File dir = new File(this.pluginDir);

        if (!dir.exists()) {
            LOGGER.info("Creating plugin directory{}", Config.getConfig().getPluginDirectory());
            if(!dir.mkdirs()) {
                LOGGER.error("Failed to create plugin directory: {}", Config.getConfig().getPluginDirectory());
                return;
            }
        }
        PluginLoader.loadAllPlugins(dir);
        if (autoReload) {
            startAutoReload();
        }
    }
    public Map<String, PluginWrapper> getPlugins() {
        return this.plugins;
    }
    public void shutdown() {
        LOGGER.info("Shutting down Plugin Manager...");
        // Close watch thread
        if (this.watchThread != null && this.watchThread.isAlive()) {
            this.watchThread.interrupt();
        }
        // Close watch service
        if (this.watchService != null) {
            try {
                this.watchService.close();
            } catch (Exception e) {
                LOGGER.error("Error closing WatchService", e);
            }
        }
        // Disable all plugins
        for (PluginWrapper wrapper : this.plugins.values()) {
            if (wrapper.isEnabled()) {
                PluginLoader.unloadPlugin(wrapper);
            }
        }
        LOGGER.debug("Plugin Manager shut down complete.");
    }

    private void startAutoReload() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(this.pluginDir);
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );
            this.watchThread = new Thread(this::watchLoop, "Plugin-AutoReload");
            this.watchThread.setDaemon(true);
            this.watchThread.start();
            LOGGER.info("Plugin auto-reload service enable");
        } catch (Exception e) {
            LOGGER.error("Failed to start plugin auto-reload service", e);
        }
    }
    private void watchLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Path fileName = ((WatchEvent<Path>) event).context();
                    String name = fileName.toString();
                    if (!name.endsWith(".jar")) {
                        continue;
                    }
                    // Sleep to wait for file writing
                    Thread.sleep(500);
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOGGER.info("Detected plugin file change: {}", name);
                        boolean found = false;
                        for (Map.Entry<String, PluginWrapper> entry : plugins.entrySet()) {
                            PluginWrapper wrapper = entry.getValue();
                            // Reload plugin already loaded from this jar
                            if (wrapper.getJarPath().endsWith(name)) {
                                LOGGER.info("Reloading plugin: {}", wrapper.getPlugin().getName());
                                found = true;
                                PluginLoader.reloadPlugin(wrapper);
                                break;
                            }
                        }
                        if (!found) {
                            // Load new plugin
                            File jarFile = new File(this.pluginDir, name);
                            LOGGER.info("Loading new plugin from file: {}", name);
                            PluginLoader.loadPlugin(jarFile);
                        }
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.info("Plugin auto-reload service interrupted, stopping...");
            }
        }
    }
}
