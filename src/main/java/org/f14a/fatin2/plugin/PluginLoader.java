package org.f14a.fatin2.plugin;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.integrated.EchoPlugin;
import org.f14a.fatin2.type.exception.IllegalPluginException;
import org.f14a.fatin2.type.exception.MainClassNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginLoader {
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

    public static void loadAllPlugins(File dir) {
        // Load integrated plugins
        loadIntegratedPlugins();

        // Load external plugins
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if(files == null || files.length == 0) {
            LOGGER.info("No plugins found in directory: {}", Config.getConfig().getPluginDirectory());
            return;
        }
        // Load each plugin
        for(File file : files) {
            loadPlugin(file);
        }
    }

    public static void loadPlugin(File jarFile) {
        try {
            // Load JAR file
            LOGGER.debug("Loading plugin from: {}", jarFile.getName());
            URL[] urls = { jarFile.toURI().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls, PluginLoader.class.getClassLoader());

            // Read main class
            String mainClass = getMainClass(jarFile);
            LOGGER.debug("Main class of plugin {}: {}", jarFile.getName(), mainClass);
            Class<?> pluginClass = classLoader.loadClass(mainClass);

            // Check if it implements Fatin2Plugin
            if(!Fatin2Plugin.class.isAssignableFrom(pluginClass)) {
                classLoader.close();
                throw new IllegalPluginException("It is NOT a valid Fatin2 plugin: " + jarFile.getName());
            }

            Fatin2Plugin plugin = (Fatin2Plugin) pluginClass.getDeclaredConstructor().newInstance();
            if(plugin.getName() == null || plugin.getName().isEmpty()) {
                classLoader.close();
                throw new IllegalPluginException("Plugin name is empty: " + jarFile.getName());
            }
            if (PluginManager.getInstance().getPlugins().containsKey(plugin.getName())) {
                classLoader.close();
                throw new IllegalPluginException("Plugin with the same name already loaded: " + plugin.getName());
            }
            // Instantiate plugin
            PluginWrapper wrapper = new PluginWrapper(plugin, classLoader, jarFile.getAbsolutePath());
            // Start lifecycle
            plugin.onLoad();
            wrapper.enable();
            LOGGER.info("Plugin loaded: {} v{} by {}", plugin.getName(), plugin.getVersion(), plugin.getAuthor());

        } catch (Exception e) {
            LOGGER.error("Failed to load plugin from: {}", jarFile.getName(), e);
        }
    }
    private static void loadIntegratedPlugins() {
        EchoPlugin echoPlugin = new EchoPlugin();
        PluginWrapper wrapper = new PluginWrapper(echoPlugin, null, "Integrated");
        echoPlugin.onLoad();
        wrapper.enable();
        LOGGER.info("Integrated Plugin loaded: {} v{} by {}", echoPlugin.getName(), echoPlugin.getVersion(), echoPlugin.getAuthor());
    }

    public static void reloadPlugin(PluginWrapper wrapper) {
        try {
            File jarFile = new File(wrapper.getJarPath());
            if (!jarFile.exists()) {
                LOGGER.warn("Plugin JAR file not found for reload: {}", wrapper.getJarPath());
                return;
            }
            unloadPlugin(wrapper);
            // Load plugin again
            loadPlugin(jarFile);
        } catch (Exception e) {
            LOGGER.error("Failed to reload plugin: {}", wrapper.getPlugin().getName(), e);
        }
    }
    public static void unloadPlugin(PluginWrapper wrapper) {
        try {
            // Remove and get wrapper from plugin manager
            PluginManager.getInstance().getPlugins().remove(wrapper.getPlugin().getName());
            if (!wrapper.isEnabled()) {
                LOGGER.warn("Plugin {} is already disabled", wrapper.getPlugin().getName());
                return;
            }
            // Unload current plugin
            wrapper.disable();
            // Close classloader
            if (wrapper.getClassLoader() != null) {
                wrapper.getClassLoader().close();
            }
            LOGGER.info("Plugin unloaded: {}", wrapper.getPlugin().getName());
        } catch (Exception e) {
            LOGGER.error("Failed to unload plugin: {}", wrapper.getPlugin().getName(), e);
        }
    }

    // Read main class from MANIFEST.MF
    private static String getMainClass(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                throw new MainClassNotFoundException("MANIFEST.MF of {} do NOT exist");
            }
            Attributes attributes = manifest.getMainAttributes();
            String mainClass = attributes.getValue("Main-Class");
            if (mainClass == null || mainClass.isEmpty()) {
                throw new MainClassNotFoundException("Main-Class not found in MANIFEST.MF of {}");
            }
            return mainClass;
        }
    }
}
