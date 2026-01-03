package org.f14a.fatin2.config;

import lombok.extern.slf4j.Slf4j;
import org.f14a.fatin2.exception.ConfigIOException;
import org.f14a.fatin2.exception.ConfigurationNotAppliedException;
import org.f14a.fatin2.exception.ConfigurationNotLoadedException;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.f14a.fatin2.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.introspector.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
public final class ConfigManager {
    private static final String CONFIG_PATH = "config.yml";
    private static final Map<String, ConfigWrapper> pluginConfigs = new HashMap<>();
    private static final ConfigWrapper globalConfigWrapper = initGlobal();

    /**
     * 注册并加载插件配置文件。
     * @param plugin 插件实例，一般使用 this 传入
     * @param configClass 插件的配置类
     * @param configPath 配置文件路径，相对于 plugins 目录
     * @return 配置类实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T register(Fatin2Plugin plugin, Class<T> configClass, String configPath) {
        ConfigWrapper wrapper = loadConfig(configClass, "plugins" + File.separator + configPath);
        pluginConfigs.put(plugin.getName(), wrapper);
        return (T) wrapper.getConfig();
    }

    /**
     * 初始化全局配置文件。
     * @return 配置包装类
     */
    private static ConfigWrapper initGlobal() {
        return loadConfig(Config.class, CONFIG_PATH);
    }

    /**
     * 获取全局配置实例。
     * @return 配置类实例
     */
    public static Config getConfig() {
        return (Config) globalConfigWrapper.getConfig();
    }

    /**
     * 获取全局配置包装类。
     * @return 全局配置包装类
     */
    public static ConfigWrapper getGlobalWrapper() {
        return globalConfigWrapper;
    }

    /**
     * 加载配置文件，如果文件不存在则创建一个默认的文件。
     * @param configClass 配置类
     * @param configPath 配置文件路径
     * @return 配置包装类
     * @throws ConfigurationNotLoadedException 配置文件加载失败时抛出
     */
    private static ConfigWrapper loadConfig(Class<?> configClass, String configPath) throws ConfigurationNotLoadedException {
        File file = new File(configPath);
        ConfigWrapper wrapper;
        if (!file.exists()) {
            try {
                Path parent = file.toPath().getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                file.createNewFile();
                wrapper = new ConfigWrapper(configClass, configPath);
                save(wrapper, file);
            } catch (Exception e) {
                throw new ConfigurationNotLoadedException("Failed to create config file: " + configPath, e);
            }
        }
        else {
            try (InputStream in = new FileInputStream(file)) {
                Yaml yaml = new Yaml(new Constructor(configClass, new LoaderOptions()) {
                    @Override
                    protected Object newInstance(Node node) {
                        Object instance = super.newInstance(node);
                        ConfigWrapper.applyDefaults(instance);
                        return instance;
                    }
                });
                wrapper = new ConfigWrapper(yaml.loadAs(in, configClass), configPath);
            } catch (Exception e) {
                throw new ConfigurationNotLoadedException("Failed to load config file: " + configPath, e);
            }
        }
        return wrapper;
    }

    /**
     * 保存配置文件。
     * @param wrapper 配置包装类
     * @param file 配置文件
     */
    public static void save(ConfigWrapper wrapper, File file) throws ConfigIOException {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer(options) {
            @Override
            protected Set<Property> getProperties(Class<?> type) {
                Set<Property> properties = super.getProperties(type);
                List<Property> list = new ArrayList<>(properties);
                list.sort(Comparator.comparingInt(p -> {
                    try {
                        Field field = type.getDeclaredField(p.getName());
                        ConfigItem annotation = field.getAnnotation(ConfigItem.class);
                        if (annotation != null) {
                            return annotation.order();
                        }
                    } catch (NoSuchFieldException ignore) {
                        // ignore
                    }
                    return Integer.MAX_VALUE;
                }));
                return new LinkedHashSet<>(list);
            }
        };
        // 避免 YAML 头部出现 !!com.example.Config 这种类名
        representer.addClassTag(wrapper.getConfig().getClass(), Tag.MAP);

        Yaml yaml = new Yaml(representer, options);
        String dumped = yaml.dump(wrapper.getConfig());

        // 原子写入文件
        Path tmp = file.toPath().resolveSibling(file.getName() + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmp.toFile()))) {
            writer.write(dumped);
        } catch (IOException e) {
            throw new ConfigIOException("Failed to write temp config file " + tmp + ": " + e.getMessage());
        }
        try {
            Files.move(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e2) {
                throw new ConfigIOException("Failed to move temp config file " + tmp + " to " + file + ": " + e2.getMessage());
            }
            throw new ConfigIOException("Failed to move temp config file " + tmp + " to " + file + ": " + e.getMessage());
        }

        log.debug("Config file {} saved successfully.", file.getAbsolutePath());
    }

    /**
     * 获取所有插件的配置包装类。
     * @return 插件配置映射
     */
    public static @NotNull Map<String, ConfigWrapper> getPluginConfigs() {
        return Collections.unmodifiableMap(pluginConfigs);
    }

    /**
     * 获取插件配置的 JSON 格式作为 REST API 响应。
     * @param pluginName 插件名称
     * @return JSON 对象
     */
    @Deprecated
    public static @NotNull List<Map<String, Object>> getPluginConfig(String pluginName) {
        ConfigWrapper wrapper = pluginConfigs.get(pluginName);
        if (wrapper == null) {
            log.error("No config found for plugin {}", pluginName);
            return new ArrayList<>();
        }
        return wrapper.getAll();
    }

    /**
     * 获取插件配置项的 JSON 格式作为 REST API 响应。
     * @param pluginName 插件名称
     * @param key 配置项键
     * @return JSON 对象
     */
    @Deprecated
    public static @NotNull Map<String, String> getPluginConfig(String pluginName, String key) {
        ConfigWrapper wrapper = pluginConfigs.get(pluginName);
        if (wrapper == null) {
            log.error("No config key found for plugin {}", pluginName);
            return new LinkedHashMap<>();
        }
        return wrapper.get(key);
    }

    /**
     * 获取全局配置的 JSON 格式作为 REST API 响应。
     * @return JSON 对象
     */
    @Deprecated
    public static @NotNull List<Map<String, Object>> getGlobalConfig() {
        return globalConfigWrapper.getAll();
    }

    /**
     * 获取全局配置项的 JSON 格式作为 REST API 响应。
     * @param key 配置项键
     * @return JSON 对象
     */
    @Deprecated
    public static @NotNull Map<String, String> getGlobalConfig(String key) {
        return globalConfigWrapper.get(key);
    }

    /**
     * 设置插件配置项的值。
     * @param pluginName 插件名称
     * @param key 配置项键
     * @param value 配置项值
     * @return JSON 对象，包含错误信息（如果有）
     */
    @Deprecated
    public static @NotNull Map<String, String> setPluginConfigItem(String pluginName, String key, String value) {
        ConfigWrapper wrapper = pluginConfigs.get(pluginName);
        if (wrapper == null) {
            return Map.of("reason", String.format("No config found for plugin %s", pluginName));
        }
        return setConfigItem(wrapper, key, value);
    }

    /**
     * 设置全局配置项的值。
     * @param key 配置项键
     * @param value 配置项值
     * @return JSON 对象，包含错误信息（如果有）
     */
    @Deprecated
    public static @NotNull Map<String, String> setGlobalConfigItem(String key, String value) {
        return setConfigItem(globalConfigWrapper, key, value);
    }

    /**
     * 设置配置项的值并保存配置文件。
     * @param wrapper 配置包装类
     * @param key 配置项键
     * @param value 配置项值
     * @return JSON 对象，包含错误信息（如果有）
     */
    @Deprecated
    private static @NotNull Map<String, String> setConfigItem(ConfigWrapper wrapper, String key, String value) {
        try {
            wrapper.apply(key, value);
        } catch (ConfigurationNotAppliedException e) {
            return Map.of("reason", String.format("Something goes wrong when applying config: %s", e.getMessage()));
        }
        try {
            save(wrapper, new File(wrapper.getConfigPath()));
        } catch (ConfigIOException e) {
            return Map.of("reason", String.format("Something goes wrong when saving config: %s", e.getMessage()));
        }
        return Map.of();
    }
}
