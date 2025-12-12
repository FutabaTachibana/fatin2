package org.f14a.fatin2.plugin;

import org.f14a.fatin2.type.Exception.MainClassNotFoundException;
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


    public static void loadPlugins(File jarFile) {
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
                throw new IllegalArgumentException("It is NOT a valid Fatin2 plugin: " + jarFile.getName());
            }

            Fatin2Plugin plugin = (Fatin2Plugin) pluginClass.getDeclaredConstructor().newInstance();

            // TODO: Check if it already loaded

            // Instantiate plugin
            PluginWrapper wrapper = new PluginWrapper(plugin, classLoader, jarFile.getAbsolutePath());

            // Start lifecycle
            plugin.onLoad();
            plugin.onEnable();
            wrapper.setEnabled(true);

            LOGGER.info("Plugin loaded: {} v{} by{}", plugin.getName(), plugin.getVersion(), plugin.getAuthor());

        } catch (Exception e) {
            LOGGER.error("Failed to load plugin from: {}", jarFile.getName(), e);
        }

    }

    // Read main class from MANIFEST.MF
    private static String getMainClass(File jarFile) throws Exception {
        JarFile jar = new JarFile(jarFile);
        Manifest manifest = jar.getManifest();
        if (manifest == null) {
            throw new MainClassNotFoundException("MANIFEST.MF of {} do NOT exist");
        }
        Attributes attributes = manifest.getMainAttributes();
        String mainClass = attributes.getValue("Main-Class");
        if (mainClass == null || mainClass.isEmpty()) {
            throw new MainClassNotFoundException("Main-Class not found in MANIFEST.MF of {}");
        }
        jar.close();
        return mainClass;
    }
}
