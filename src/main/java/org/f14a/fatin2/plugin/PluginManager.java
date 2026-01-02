package org.f14a.fatin2.plugin;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.config.ConfigManager;
import org.f14a.fatin2.event.lifecycle.PluginsLoadDoneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.*;
import java.util.Collections;
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

    private final Object pluginOpLock = new Object();

    public PluginManager(boolean autoReload) {
        PluginManager.instance = this;
        this.plugins  = new ConcurrentHashMap<>();
        String pluginDirectory = ConfigManager.getGlobalConfig().getPluginDirectory();

        this.pluginDir = pluginDirectory;
        File dir = new File(this.pluginDir);

        if (!dir.exists()) {
            LOGGER.info("Creating plugin directory{}", pluginDirectory);
            if(!dir.mkdirs()) {
                LOGGER.error("Failed to create plugin directory: {}", pluginDirectory);
                return;
            }
        }
        PluginLoader.loadAllPlugins(dir);
        new PluginsLoadDoneEvent().fire();
        if (autoReload) {
            startAutoReload();
        }
    }
    public Map<String, PluginWrapper> getPlugins() {
        return Collections.unmodifiableMap(this.plugins);
    }
    public PluginWrapper getPluginWrapper(Fatin2Plugin plugin) {
        for (PluginWrapper wrapper : this.plugins.values()) {
            if (wrapper.getPlugin() == plugin) {
                return wrapper;
            }
        }
        return null;
    }
    public PluginWrapper getPluginWrapper(String pluginName) {
        for (PluginWrapper wrapper : this.plugins.values()) {
            Fatin2Plugin plugin = wrapper.getPlugin();
            if (plugin.getDisplayName().equals(pluginName) || plugin.getName().equals(pluginName)) {
                return wrapper;
            }
        }
        return null;
    }

    /**
     * Unified entrypoints for plugin operations.
     * <p>
     * PluginManager owns the global plugin registry (the map). PluginLoader only does the per-plugin
     * execution details (classloading, lifecycle, unloading).
     */
    public void loadExternalPlugin(File jarFile) {
        synchronized (pluginOpLock) {
            PluginWrapper wrapper = PluginLoader.createWrapperFromJar(jarFile);
            if (wrapper == null) {
                return;
            }
            String name = wrapper.getPlugin().getName();
            if (plugins.containsKey(name)) {
                // Already loaded (maybe another watcher event raced). Unload the newly created one.
                PluginLoader.unloadWrapper(wrapper);
                LOGGER.warn("Plugin with the same name already loaded: {}", name);
                return;
            }
            plugins.put(name, wrapper);
            LOGGER.info("Plugin loaded (registered): {}", name);
        }
    }

    public void loadIntegratedPlugin(Fatin2Plugin plugin) {
        synchronized (pluginOpLock) {
            PluginWrapper wrapper = PluginLoader.createIntegratedWrapper(plugin);
            String name = wrapper.getPlugin().getName();
            if (plugins.containsKey(name)) {
                // Should not happen normally, but protect against accidental duplicate integrated load.
                PluginLoader.unloadWrapper(wrapper);
                LOGGER.warn("Integrated plugin with the same name already loaded: {}", name);
                return;
            }
            plugins.put(name, wrapper);
            LOGGER.info("Integrated plugin loaded (registered): {}", name);
        }
    }

    public void unloadPlugin(PluginWrapper wrapper) {
        synchronized (pluginOpLock) {
            if (wrapper == null) {
                return;
            }
            String name = wrapper.getPlugin().getName();
            // Remove first so we don't expose a half-unloaded plugin to other threads.
            PluginWrapper removed = plugins.remove(name);
            if (removed == null) {
                return;
            }
            PluginLoader.unloadWrapper(removed);
            LOGGER.info("Plugin unloaded (unregistered): {}", name);
        }
    }

    public void reloadPlugin(PluginWrapper wrapper) {
        synchronized (pluginOpLock) {
            if (wrapper == null) {
                return;
            }
            String oldName = wrapper.getPlugin().getName();

            // Remove old first to avoid duplicate name conflicts during reload.
            PluginWrapper removed = plugins.remove(oldName);
            if (removed != null) {
                PluginWrapper newWrapper = PluginLoader.reloadWrapper(removed);
                if (newWrapper == null) {
                    // Reload failed: keep it unloaded.
                    LOGGER.warn("Reload failed, plugin remains unloaded: {}", oldName);
                    return;
                }

                String newName = newWrapper.getPlugin().getName();
                if (plugins.containsKey(newName)) {
                    // Another plugin with the same name exists. Unload the new one and do not register it.
                    PluginLoader.unloadWrapper(newWrapper);
                    LOGGER.warn("Reload produced a plugin with duplicate name {}, not registering.", newName);
                    return;
                }
                plugins.put(newName, newWrapper);
                LOGGER.info("Plugin reloaded: {} -> {}", oldName, newName);
            }
        }
    }

    /**
     * Wait until a jar becomes readable.
     * <p>
     * On Windows, copying/overwriting a jar can take a little time, and attempting to open the jar
     * too early can fail (partially written / locked by another process). This method retries for
     * a short period.
     * <p>
     * Package-visible so PluginLoader can reuse it (avoid duplicated logic).
     */
    void waitJarReady(Path jarPath) {
        // On Windows, overwrite/copy may take a moment; retry opening jar a few times.
        for (int i = 0; i < 10; i++) {
            try (java.util.jar.JarFile ignored = new java.util.jar.JarFile(jarPath.toFile())) {
                return;
            } catch (Exception e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
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

        // Unload all plugins under the same global lock.
        // We iterate on a snapshot to avoid surprises while unloading removes from the map.
        synchronized (pluginOpLock) {
            for (PluginWrapper wrapper : this.plugins.values().toArray(new PluginWrapper[0])) {
                // Use the public unload method to ensure consistent "remove + unload" behavior.
                unloadPlugin(wrapper);
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

                    // Get current file name
                    @SuppressWarnings("unchecked")
                    Path fileName = ((WatchEvent<Path>) event).context();
                    String name = fileName.toString();
                    if (!name.endsWith(".jar")) {
                        continue;
                    }

                    // File deleted -> unload loaded plugin
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        LOGGER.info("Detected plugin file delete: {}", name);
                        PluginWrapper wrapperToUnload = null;
                        // Find the wrapper by jar path
                        for (PluginWrapper wrapper : plugins.values()) {
                            if (wrapper.getJarPath() != null && wrapper.getJarPath().endsWith(name)) {
                                wrapperToUnload = wrapper;
                                break;
                            }
                        }
                        if (wrapperToUnload != null) {
                            LOGGER.info("Unloading plugin due to delete: {}", wrapperToUnload.getPlugin().getName());
                            unloadPlugin(wrapperToUnload);
                        }
                        continue;
                    }

                    Path fullPath = Paths.get(this.pluginDir).resolve(name);
                    waitJarReady(fullPath);

                    // File created or modified -> load or reload plugin
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        LOGGER.info("Detected plugin file change: {}", name);

                        PluginWrapper foundWrapper = null;
                        for (PluginWrapper wrapper : plugins.values()) {
                            if (wrapper.getJarPath() != null && wrapper.getJarPath().endsWith(name)) {
                                foundWrapper = wrapper;
                                break;
                            }
                        }

                        if (foundWrapper != null) {
                            // Not found, load a new plugin
                            LOGGER.info("Reloading plugin: {}", foundWrapper.getPlugin().getName());
                            reloadPlugin(foundWrapper);
                        } else {
                            // Found, reload existing plugin
                            File jarFile = new File(this.pluginDir, name);
                            LOGGER.info("Loading new plugin from file: {}", name);
                            loadExternalPlugin(jarFile);
                        }
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.info("Plugin auto-reload service interrupted, stopping...");
            } catch (ClosedWatchServiceException e) {
                LOGGER.info("WatchService closed, stopping plugin auto-reload service...");
                return;
            } catch (Exception e) {
                LOGGER.error("Error in plugin auto-reload watch loop", e);
            }
        }
    }
}
