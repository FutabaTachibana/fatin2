package org.f14a.fatin2.webui;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.f14a.fatin2.config.ConfigManager;
import org.f14a.fatin2.config.ConfigWrapper;
import org.f14a.fatin2.exception.ConfigIOException;
import org.f14a.fatin2.exception.ConfigurationNotAppliedException;
import org.f14a.fatin2.plugin.Plugin;
import org.f14a.fatin2.plugin.PluginManager;
import org.f14a.fatin2.plugin.PluginWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ApiHandler {

    public static void getSettings(Context ctx) {
        // ConfigWrapper.getAll() returns List<Map<String, Object>>
        // Javalin will serialize this to JSON automatically
        ctx.json(ConfigManager.getGlobalWrapper().getAll());
    }

    public static void updateSettings(Context ctx) {
        updateConfig(ctx, ConfigManager.getGlobalWrapper());
    }

    public static void getPluginConfig(Context ctx) {
        String pluginName = ctx.pathParam("name");
        ConfigWrapper wrapper = ConfigManager.getPluginConfigs().get(pluginName);
        if (wrapper == null) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Plugin config not found"));
            return;
        }
        ctx.json(wrapper.getAll());
    }

    public static void updatePluginConfig(Context ctx) {
        String pluginName = ctx.pathParam("name");
        ConfigWrapper wrapper = ConfigManager.getPluginConfigs().get(pluginName);
        if (wrapper == null) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Plugin config not found"));
            return;
        }
        updateConfig(ctx, wrapper);
    }

    public static void togglePlugin(Context ctx) {
        String pluginName = ctx.pathParam("name");
        // Expecting {"enabled": true/false}
        Map<String, Boolean> body = ctx.bodyAsClass(Map.class);
        Boolean enabled = body.get("enabled");

        if (enabled == null) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Missing 'enabled' field"));
            return;
        }

        PluginWrapper wrapper = PluginManager.getInstance().getPluginWrapper(pluginName);
        if (wrapper == null) {
            // Try finding by display name or other means if needed, but ID (name) is preferred
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", "Plugin not found"));
            return;
        }

        if (enabled) {
            if (!wrapper.isEnabled()) {
                wrapper.enable();
            }
        } else {
            if (wrapper.isEnabled()) {
                wrapper.disable();
            }
        }

        ctx.json(Map.of("success", true, "enabled", wrapper.isEnabled()));
    }

    @SuppressWarnings("unchecked")
    private static void updateConfig(Context ctx, ConfigWrapper wrapper) {
        // Expecting {"key": "value", ...}
        Map<String, Object> updates = ctx.bodyAsClass(Map.class);
        List<Map<String, Object>> results = new ArrayList<>();

        updates.forEach((key, value) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            try {
                // Convert value to String as apply expects String
                wrapper.apply(key, String.valueOf(value));
                result.put("success", true);
            } catch (ConfigurationNotAppliedException e) {
                result.put("success", false);
                result.put("reason", String.format("Something goes wrong when applying config: %s", e.getMessage()));
            }
            results.add(result);
        });

        // Save after applying all updates
        try {
            ConfigManager.save(wrapper, new File(wrapper.getConfigPath()));
        } catch (ConfigIOException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .json(Map.of("error", "Failed to save configuration", "details", e.getMessage()));
            return;
        }

        ctx.json(results);
    }

    public static void getPlugins(Context ctx) {
        Map<String, PluginWrapper> plugins = PluginManager.getInstance().getPlugins();
        List<Map<String, Object>> pluginList = new ArrayList<>();

        for (PluginWrapper wrapper : plugins.values()) {
            Plugin plugin = wrapper.getPlugin();
            Map<String, Object> info = new HashMap<>();
            info.put("name", plugin.getName());
            info.put("displayName", plugin.getDisplayName());
            info.put("enabled", wrapper.isEnabled());
            info.put("canHotReload", true);
            info.put("version", plugin.getVersion());
            info.put("author", plugin.getAuthor());
            info.put("description", plugin.getDescription());
            info.put("hasConfig", ConfigManager.getPluginConfigs().containsKey(plugin.getName()));

            pluginList.add(info);
        }
        ctx.json(pluginList);
    }
}

