package org.f14a.fatin2.config;

import org.f14a.fatin2.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigLoader {
    public static Config load(String path) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input;

        try {
            input = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            Main.LOGGER.warn("Configuration file not found at {}. Using default configuration.", path);
            return new Config();
        }

        Map<String, Object> data = yaml.load(input);
        input.close();

        Config config = new Config();
        config.setWebSocketUrl(data.getOrDefault("websocket_url", DefaultConfig.WEBSOCKET_URL));
        config.setAccessToken(data.getOrDefault("access_token", DefaultConfig.ACCESS_TOKEN));
        config.setCommandPrefix(data.getOrDefault("command_prefix", DefaultConfig.COMMAND_PREFIX));
        config.setDebug(data.getOrDefault("debug", DefaultConfig.DEBUG));
        if (data.containsKey("plugin")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pluginData = (Map<String, Object>) data.get("plugin");
            config.setPluginDirectory(data.getOrDefault("directory", DefaultConfig.PLUGIN_DIRECTORY));
            if (!config.getPluginDirectory().startsWith(".") && !config.getPluginDirectory().startsWith("!")) {
                config.setPluginDirectory("." + File.separator + config.getPluginDirectory());
                Main.LOGGER.debug("Plugin directory reset to: {}", config.getPluginDirectory());
            }
            config.setPluginAutoReload(data.getOrDefault("auto_reload", DefaultConfig.PLUGIN_AUTO_RELOAD));
        }
        else {
            config.setPluginDirectory(DefaultConfig.PLUGIN_DIRECTORY);
            config.setPluginAutoReload(DefaultConfig.PLUGIN_AUTO_RELOAD);
        }

        return config;
    }
}
