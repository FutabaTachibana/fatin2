package org.f14a.fatin2.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.f14a.fatin2.exception.ConfigurationNotAppliedException;
import org.f14a.fatin2.exception.IllegalTypeException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public final class ConfigWrapper {
    private static final Gson GSON = new Gson();

    @Getter
    private final Object config;
    @Getter
    private final String configPath;
    private final Map<String, List<ConstraintsParser.Range<?>>> constraintsCache = new HashMap<>();

    ConfigWrapper(Object config, String configPath) {
        this.config = config;
        this.configPath = configPath;
    }

    ConfigWrapper(Class<?> configClass, String configPath) throws Exception {
        this.config = configClass.getDeclaredConstructor().newInstance();
        applyDefaults(this.config);
        this.configPath = configPath;
    }

    /**
     * 获取配置的 JSON 格式作为 REST API 响应。
     * @return JsonArray 格式的配置项列表
     */
    @NotNull JsonArray getJson() {
        Class<?> clazz = config.getClass();
        ArrayList<Map<String, Object>> itemList = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigItem.class)) {
                ConfigItem item = field.getAnnotation(ConfigItem.class);
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(config);
                } catch (IllegalAccessException e) {
                    log.error("Failed to access config field {}: {}", field.getName(), e.getMessage());
                    continue;
                }
                Map<String, Object> itemMap = new LinkedHashMap<>();
                itemMap.put("order", item.order());
                itemMap.put("label", item.label());
                itemMap.put("type", item.type());
                itemMap.put("defaultValue", item.defaultValue());
                itemMap.put("description", item.description());
                itemMap.put("enable", item.enable());
                itemMap.put("constraints", item.constraints());
                itemMap.put("options", item.options());
                itemMap.put("value", value);
                itemList.add(itemMap);
            }
        }
        itemList.sort(Comparator.comparingInt(i -> (int) i.get("order")));

        return GSON.toJsonTree(itemList).getAsJsonArray();
    }

    /**
     * 获取配置项的值。
     * @param key 配置项键
     * @return 配置项的 JSON 对象，包含键和值；如果未找到则返回空的 JsonObject
     */
    @NotNull JsonObject get(String key) {
        try {
            Field field = config.getClass().getDeclaredField(key);
            field.setAccessible(true);
            // 转化为 String 以简化前端处理
            String value = field.get(config).toString();
            JsonObject result = new JsonObject();
            result.addProperty("key", key);
            result.addProperty("value", value);
            return result;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    /**
     * 应用配置更改。
     * @param key 配置项键
     * @param value 配置项值
     * @throws ConfigurationNotAppliedException 配置未能应用时抛出
     */
    void apply(String key, String value) throws ConfigurationNotAppliedException {
        Class<?> clazz = config.getClass();
        try {
            Field field = clazz.getDeclaredField(key);
            if (field.isAnnotationPresent(ConfigItem.class)) {
                ConfigItem item = field.getAnnotation(ConfigItem.class);
                String constraints = item.constraints();
                switch (item.type()) {
                    case "number", "integer" -> {
                        // 对于数字和整数类型，使用 ConstraintsParser 进行范围检查
                        List<ConstraintsParser.Range<?>> ranges = constraintsCache.computeIfAbsent(
                                key,
                                k -> ConstraintsParser.parse(constraints, field.getType())
                        );
                        boolean valid = false;
                        try {
                            Comparable<?> typedValue = ConstraintsParser.valueOf(value, field.getType());
                            for (ConstraintsParser.Range<?> range : ranges) {
                                if (range.contains(typedValue)) {
                                    valid = true;
                                    break;
                                }
                            }
                            if (!valid) {
                                throw new ConfigurationNotAppliedException(
                                        String.format("Value %s does not satisfy constraints for %s", value, key)
                                );
                            }
                            field.setAccessible(true);
                            field.set(config, typedValue);
                        } catch (IllegalTypeException | ClassCastException ignored) {
                            throw new ConfigurationNotAppliedException("Invalid type for field " + key);
                        }
                    }
                    case "string", "password" -> {
                        // 对于字符串类型，使用正则表达式进行检查
                        if (constraints != null && !constraints.isEmpty()) {
                            if (value.matches(constraints)) {
                                field.setAccessible(true);
                                field.set(config, value);
                            } else {
                                throw new ConfigurationNotAppliedException(
                                        String.format("Value %s does not match constraints for %s", value, key)
                                );
                            }
                        }
                    }
                    case "boolean" -> {
                        // 对于布尔类型，直接转换，这一步骤比较简单，基本不会抛出异常
                        boolean boolValue = Boolean.parseBoolean(value);
                        field.setAccessible(true);
                        field.set(config, boolValue);
                    }
                    case "select" -> {
                        // 对于下拉选择框类型，检查是否在选项中
                        String[] options = item.options();
                        if (Arrays.asList(options).contains(value)) {
                            field.setAccessible(true);
                            field.set(config, value);
                        } else {
                            throw new ConfigurationNotAppliedException(
                                    String.format("Value %s is not a valid option for %s", value, key)
                            );
                        }
                    }
                    default -> throw new ConfigurationNotAppliedException(
                            String.format("Unsupported config item type for field %s: %s", key, item.type()
                    ));
                }
            } else {
                throw new ConfigurationNotAppliedException(
                        String.format("Field %s is not a valid config item", key)
                );
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ConfigurationNotAppliedException(
                    String.format("Failed to access config field: %s", key), e
            );
        }
    }

    /**
     * 初始化配置类实例，设置默认值。
     * @param config 配置对象
     */
    static void applyDefaults(Object config) {
        Class<?> configClass = config.getClass();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigItem.class)) {
                ConfigItem item = field.getAnnotation(ConfigItem.class);
                field.setAccessible(true);
                String defaultValue = item.defaultValue();
                if (!defaultValue.isEmpty()) {
                    try {
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
                        if (value != null) {
                            field.set(config, value);
                        }
                    } catch (Exception e) {
                        log.error("Failed to set default value for field {}: {}", field.getName(), e.getMessage());
                    }
                }
            }
        }
    }
}
