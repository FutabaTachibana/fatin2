package org.f14a.fatin2.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.f14a.fatin2.exception.ConfigIOException;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
public final class ConfigManager {
    private static final Map<String, ConfigWrapper> pluginConfigs = new HashMap<>();

    /**
     * 注册并加载插件配置文件。
     * @param plugin 插件实例，一般使用 this 传入
     * @param configClass 插件的配置类
     * @param configPath 配置文件路径，相对于 plugins 目录
     * @return 配置类实例
     */
    public static <T> T register(Fatin2Plugin plugin, Class<T> configClass, String configPath) {
        File file = new File("plugins" + File.separator + configPath);
        T config;
        ConfigWrapper wrapper;
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                config = initConfig(configClass);
                wrapper = new ConfigWrapper(config, configPath);
                save(wrapper, file);
                log.info("Created default config file for plugin {} at {}", plugin.getName(), file.getAbsolutePath());
            } catch (Exception e) {
                log.error("Failed to create config file for plugin {}: {}", plugin.getName(), e.getMessage());
                return null;
            }
        }
        else {
            try (InputStream in = new FileInputStream(file)) {
                Yaml yaml = new Yaml();
                config = yaml.loadAs(in, configClass);
                wrapper = new ConfigWrapper(config, configPath);
            } catch (Exception e) {
                log.error("Failed to load config file for plugin {}: {}", plugin.getName(), e.getMessage());
                return null;
            }
        }
        pluginConfigs.put(plugin.getName(), wrapper);
        return config;
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

        Representer representer = new Representer(options);
        // 避免 YAML 头部出现 !!com.example.Config 这种类名
        representer.addClassTag(wrapper.config().getClass(), Tag.MAP);

        Yaml yaml = new Yaml(representer, options);
        String dumped = yaml.dump(wrapper.config());

        // 原子写入文件
        Path tmp = file.toPath().getParent().resolveSibling(file.getName() + ".tmp");
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
     * 获取插件配置的 JSON 格式作为 REST API 响应。
     * @param pluginName 插件名称
     * @return JSON 字符串
     */
    public static String getJson(String pluginName) {
        ConfigWrapper wrapper = pluginConfigs.get(pluginName);
        if (wrapper == null) {
            log.error("No config found for plugin {}", pluginName);
            return "[]";
        }
        return wrapper.getJson();
    }

    /**
     * 初始化配置类实例，设置默认值。
     * @param configClass 配置类
     * @return 配置类实例
     */
    private static <T> T initConfig(Class<T> configClass) throws Exception {
        T config = configClass.getDeclaredConstructor().newInstance();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigItem.class)) {
                ConfigItem item = field.getAnnotation(ConfigItem.class);
                field.setAccessible(true);
                String defaultValue = item.defaultValue();
                if (!defaultValue.isEmpty()) {
                    Object value = switch (item.type()) {
                        case "string", "password" -> defaultValue;
                        case "boolean" -> Boolean.parseBoolean(defaultValue);
                        case "number" -> {
                            Class<?> fieldType = field.getType();
                            if (fieldType == double.class || fieldType == Double.class) {
                                yield Double.parseDouble(defaultValue);
                            } else if (fieldType == float.class || fieldType == Float.class) {
                                yield Float.parseFloat(defaultValue);
                            } else {
                                throw new IllegalArgumentException("Unsupported number type: " + fieldType.getName());
                            }
                        }
                        case "integer" -> {
                            Class<?> fieldType = field.getType();
                            if (fieldType == int.class || fieldType == Integer.class) {
                                yield Integer.parseInt(defaultValue);
                            } else if (fieldType == long.class || fieldType == Long.class) {
                                yield Long.parseLong(defaultValue);
                            } else {
                                throw new IllegalArgumentException("Unsupported integer type: " + fieldType.getName());
                            }
                        }
                        default -> null;
                    };
                    field.set(config, value);
                }
            }
        }
        return config;
    }


}
