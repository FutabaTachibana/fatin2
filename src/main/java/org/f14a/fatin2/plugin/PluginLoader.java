package org.f14a.fatin2.plugin;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.plugin.integrated.IntegratedHelpGenerator;
import org.f14a.fatin2.plugin.integrated.IntegratedPermissionProvider;
import org.f14a.fatin2.type.exception.IllegalPluginException;
import org.f14a.fatin2.type.exception.MainClassNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static org.f14a.fatin2.plugin.PluginWrapper.INTEGRATED_JAR_PATH;

public class PluginLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

    /**
     * Load all plugins.
     * <p>
     * PluginManager remains the global owner of the registry, so this method will
     * call back into PluginManager to register wrappers.
     */
    public static void loadAllPlugins(File dir) {
        // Load integrated plugins
        Config config = Config.getConfig();
        if (config.isEnableHelp()) {
            PluginManager.getInstance().loadIntegratedPlugin(new IntegratedHelpGenerator());
        }
        if (config.isEnablePermission()) {
            // Something to load bot admins and banned users from config can be added here
            PluginManager.getInstance().loadIntegratedPlugin(new IntegratedPermissionProvider(Set.of(), Set.of()));
        }

        // Load external plugins
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            LOGGER.info("No plugins found in directory: {}", Config.getConfig().getPluginDirectory());
            return;
        }
        for (File file : files) {
            PluginManager.getInstance().loadExternalPlugin(file);
        }
    }

    /**
     * Create a PluginWrapper for an integrated plugin.
     * <p>
     * Integrated plugins do not have a jar or a dedicated classloader.
     */
    public static PluginWrapper createIntegratedWrapper(Fatin2Plugin plugin) {
        validatePluginName(plugin);
        PluginWrapper wrapper = new PluginWrapper(plugin, null, INTEGRATED_JAR_PATH);

        // Lifecycle: onLoad then onEnable
        plugin.onLoad();
        wrapper.enable();
        return wrapper;
    }

    /**
     * Create a PluginWrapper for an external plugin jar.
     * <p>
     * Notes about IO/classloading:
     * - We create a dedicated URLClassLoader for the jar so the plugin can load its own classes/resources.
     * - If anything fails, we close the classloader to avoid leaking handles (especially on Windows).
     * - On success we KEEP the classloader open; it will be closed in {@link #unloadWrapper(PluginWrapper)}.
     */
    public static PluginWrapper createWrapperFromJar(File jarFile) {
        URLClassLoader classLoader = null;
        boolean success = false;
        try {
            if (jarFile == null || !jarFile.isFile()) {
                LOGGER.warn("Plugin path is not a file: {}", jarFile);
                return null;
            }

            // Wait until the jar becomes readable (copy/overwrite may take time on Windows)
            PluginManager.getInstance().waitJarReady(jarFile.toPath());

            LOGGER.debug("Loading plugin from: {}", jarFile.getName());
            URL[] urls = { jarFile.toURI().toURL() };
            classLoader = new URLClassLoader(urls, PluginLoader.class.getClassLoader());

            String mainClass = getMainClass(jarFile);
            LOGGER.debug("Main class of plugin {}: {}", jarFile.getName(), mainClass);

            Class<?> pluginClass = classLoader.loadClass(mainClass);
            if (!Fatin2Plugin.class.isAssignableFrom(pluginClass)) {
                throw new IllegalPluginException("It is NOT a valid Fatin2 plugin: " + jarFile.getName());
            }

            Fatin2Plugin plugin = (Fatin2Plugin) pluginClass.getDeclaredConstructor().newInstance();
            validatePluginName(plugin);

            PluginWrapper wrapper = new PluginWrapper(plugin, classLoader, jarFile.getAbsolutePath());

            // Lifecycle: onLoad then onEnable
            plugin.onLoad();
            wrapper.enable();

            success = true;
            return wrapper;
        } catch (Exception e) {
            LOGGER.error("Failed to create plugin wrapper from: {}", jarFile != null ? jarFile.getName() : "null", e);
            return null;
        } finally {
            // If we failed, close classloader to avoid jar locking/leaks.
            if (!success && classLoader != null) {
                try {
                    classLoader.close();
                } catch (Exception ignored) {
                    // ignored
                }
            }
        }
    }

    /**
     * Reload a plugin: unload the old wrapper, then create a new wrapper from the same jar.
     * <p>
     * Returns the new wrapper, or null if reload failed.
     */
    public static PluginWrapper reloadWrapper(PluginWrapper oldWrapper) {
        if (oldWrapper == null) {
            return null;
        }
        if (INTEGRATED_JAR_PATH.equals(oldWrapper.getJarPath())) {
            LOGGER.warn("Cannot reload integrated plugin: {}", oldWrapper.getPlugin().getName());
            return null;
        }

        File jarFile = new File(oldWrapper.getJarPath());
        if (!jarFile.exists()) {
            LOGGER.warn("Plugin JAR file not found for reload: {}", oldWrapper.getJarPath());
            return null;
        }

        unloadWrapper(oldWrapper);

        // Wait until the jar becomes readable (important after overwrite).
        PluginManager.getInstance().waitJarReady(jarFile.toPath());

        return createWrapperFromJar(jarFile);
    }

    /**
     * Unload a wrapper:
     * - disable it (onDisable + EventBus unregister)
     * - close its classloader (external plugins only)
     * <p>
     * Important: this method does NOT remove it from PluginManager's registry.
     * The caller (PluginManager) owns the global state.
     */
    public static void unloadWrapper(PluginWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        try {
            wrapper.disable();

            // External plugin only. This releases jar file handles.
            if (wrapper.getClassLoader() != null) {
                wrapper.getClassLoader().close();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to unload plugin wrapper: {}", wrapper.getPlugin() != null ? wrapper.getPlugin().getName() : "null", e);
        }
    }

    private static void validatePluginName(Fatin2Plugin plugin) {
        if (plugin == null || plugin.getName() == null || plugin.getName().isEmpty() || !plugin.getName().matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalPluginException("Illegal plugin name: " + (plugin != null ? plugin.getClass().getName() : "null"));
        }
    }

    /**
     * Resolve plugin entrypoint class from jar manifest.
     * <p>
     * Resolution order:
     * 1) `Plugin-Main-Class` (preferred; avoids colliding with application Main-Class)
     * 2) `Main-Class` (fallback)
     */
    private static String getMainClass(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                throw new MainClassNotFoundException("MANIFEST.MF of " + jarFile.getAbsolutePath() + " do NOT exist");
            }
            Attributes attributes = manifest.getMainAttributes();

            String mainClass = attributes.getValue("Plugin-Main-Class");
            if (mainClass == null || mainClass.isEmpty()) {
                mainClass = attributes.getValue("Main-Class");
            }
            if (mainClass == null || mainClass.isEmpty()) {
                throw new MainClassNotFoundException("Plugin-Main-Class/Main-Class not found in MANIFEST.MF of " + jarFile.getAbsolutePath());
            }
            return mainClass;
        }
    }
}
