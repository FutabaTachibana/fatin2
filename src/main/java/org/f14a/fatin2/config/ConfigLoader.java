package org.f14a.fatin2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static Config load(String path) throws Exception {
        Yaml yaml = new Yaml();
        InputStream input;

        try {
            input = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            logger.warn("Configuration file not found at {}. Using default configuration.", path);
            return new Config();
        }

        Map<String, Object> data = yaml.load(input);
        input.close();

        Config config = new Config();
        config.setWebSocketUrl(data.getOrDefault("websocket_url", DefaultConfig.WEBSOCKET_URL));
        config.setAccessToken(data.getOrDefault("access_token", DefaultConfig.ACCESS_TOKEN));
        if (data.containsKey("plugin")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pluginData = (Map<String, Object>) data.get("plugin");
            config.setPluginDirectory(data.getOrDefault("directory", DefaultConfig.PLUGIN_DIRECTORY));
            config.setPluginAutoReload(data.getOrDefault("auto_reload", DefaultConfig.PLUGIN_AUTO_RELOAD));
        }
        else {
            config.setPluginDirectory(DefaultConfig.PLUGIN_DIRECTORY);
            config.setPluginAutoReload(DefaultConfig.PLUGIN_AUTO_RELOAD);
        }

        return config;
    }
}
