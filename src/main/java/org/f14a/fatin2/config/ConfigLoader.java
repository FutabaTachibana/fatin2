package org.f14a.fatin2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class ConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    /** The config file name in the working directory. */
    public static final String CONFIG_FILE_NAME = "config.yml";

    /**
     * Load config from working directory's {@link #CONFIG_FILE_NAME}.
     * If missing, copy template from classpath resources (/config.yml).
     */
    public static Config load() throws IOException {
        return load(Path.of(CONFIG_FILE_NAME), "/" + CONFIG_FILE_NAME);
    }

    /** Backward compatible API: ignore input path and load from working directory. */
    @Deprecated
    public static Config load(String ignoredPath) throws IOException {
        return load();
    }

    // Entry point while testing with custom paths
    static Config load(Path target, String resourceTemplatePath) throws IOException {
        ensureConfigFileExists(target, resourceTemplatePath);

        Map<String, Object> defaults = loadYamlFromResource(resourceTemplatePath);
        Map<String, Object> overrides = loadYamlFromFile(target);

        return buildConfig(defaults, overrides);
    }

    private static void ensureConfigFileExists(Path target, String resourceTemplatePath) throws IOException {
        if (Files.exists(target)) {
            return;
        }
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (InputStream in = ConfigLoader.class.getResourceAsStream(resourceTemplatePath)) {
            if (in == null) {
                throw new IOException("Default config template " + resourceTemplatePath + " not found in resources");
            }
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Created default config file at {}", target.toAbsolutePath());
        }
    }

    private static Map<String, Object> loadYamlFromResource(String resourcePath) throws IOException {
        try (InputStream in = ConfigLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return loadYaml(new Yaml(), in, "resource:" + resourcePath);
        }
    }

    private static Map<String, Object> loadYamlFromFile(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return loadYaml(new Yaml(), in, path.toString());
        }
    }

    private static Map<String, Object> loadYaml(Yaml yaml, InputStream in, String sourceLabel) {
        try {
            Object loaded = yaml.load(in);
            if (loaded == null) {
                return Map.of();
            }
            if (loaded instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cast = (Map<String, Object>) loaded;
                return cast;
            }
            LOGGER.warn("Config root from {} is not a map ({}). Treating as empty config.", sourceLabel, loaded.getClass().getName());
            return Map.of();
        } catch (Exception e) {
            LOGGER.error("Failed to load/parse YAML from {}. Treating as empty config.", sourceLabel, e);
            return Map.of();
        }
    }

    private static Config buildConfig(Map<String, Object> defaults, Map<String, Object> overrides) {
        Config config = new Config();

        config.setWebSocketUrl(getString(overrides, defaults, "websocket_url"));
        config.setAccessToken(getString(overrides, defaults, "access_token"));
        config.setCommandPrefix(getString(overrides, defaults, "command_prefix"));
        config.setDebug(getBoolean(overrides, defaults, "debug"));

        Map<String, Object> pluginDefaults = getMap(defaults, "plugin");
        Map<String, Object> pluginOverrides = getMap(overrides, "plugin");

        config.setPluginDirectory(getString(pluginOverrides, pluginDefaults, "directory"));
        if (config.getPluginDirectory() != null && !config.getPluginDirectory().startsWith(".")) {
            config.setPluginDirectory("." + File.separator + config.getPluginDirectory());
            LOGGER.debug("Plugin directory reset to: {}", config.getPluginDirectory());
        }
        config.setPluginAutoReload(getBoolean(pluginOverrides, pluginDefaults, "auto_reload"));

        // plugin.integrated.*
        Map<String, Object> integratedDefaults = getMap(pluginDefaults, "integrated");
        Map<String, Object> integratedOverrides = getMap(pluginOverrides, "integrated");

        config.setEnablePermission(getBoolean(integratedOverrides, integratedDefaults, "permission"));
        config.setEnableHelp(getBoolean(integratedOverrides, integratedDefaults, "help_generator"));

        return config;
    }

    private static String getString(Map<String, Object> overrides, Map<String, Object> defaults, String key) {
        String def = getString(defaults, key, null);
        return getString(overrides, key, def);
    }

    private static boolean getBoolean(Map<String, Object> overrides, Map<String, Object> defaults, String key) {
        boolean def = getBoolean(defaults, key, false);
        return getBoolean(overrides, key, def);
    }

    private static String getString(Map<String, Object> data, String key, String defaultValue) {
        if (data == null) {
            return defaultValue;
        }
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String s) {
            return s;
        }
        // Accept numbers/booleans/etc.
        return String.valueOf(value);
    }

    private static boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        if (data == null) {
            return defaultValue;
        }
        Object value = data.get(key);
        switch (value) {
            case null -> {
                return defaultValue;
            }
            case Boolean bool -> {
                return bool;
            }
            case Number n -> {
                return n.intValue() != 0;
            }
            default -> {
            }
        }
        String string = String.valueOf(value).trim();
        if (string.isEmpty()) {
            return defaultValue;
        }
        if ("true".equalsIgnoreCase(string) || "yes".equalsIgnoreCase(string) || "y".equalsIgnoreCase(string) || "1".equals(string)) {
            return true;
        }
        if ("false".equalsIgnoreCase(string) || "no".equalsIgnoreCase(string) || "n".equalsIgnoreCase(string) || "0".equals(string)) {
            return false;
        }
        LOGGER.warn("Config key '{}' value '{}' is not a boolean. Using default: {}", key, value, defaultValue);
        return defaultValue;
    }

    private static Map<String, Object> getMap(Map<String, Object> data, String key) {
        if (data == null) {
            return null;
        }
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cast = (Map<String, Object>) value;
            return cast;
        }
        LOGGER.warn("Config key '{}' is not a map (got {}). Ignoring it.", key, value.getClass().getName());
        return null;
    }
}
