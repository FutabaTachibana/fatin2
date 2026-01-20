package org.f14a.fatin2.config;

import lombok.extern.slf4j.Slf4j;
import org.f14a.fatin2.exception.ConfigurationNotAppliedException;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
final class ConfigReflector {
    private static final Map<String, List<ConstraintsParser.Range<?>>> constraintsCache = new HashMap<>();

    /**
     * 递归扫描配置对象，生成用于前端展示的元数据列表
     * @param instance 配置对象实例
     * @return 包含所有配置项的列表，每个项是一个 Map
     */
    static List<Map<String, Object>> scanConfig(Object instance) {
        return scanRecursive(instance, "");
    }

    /**
     * 递归扫描配置对象的辅助方法
     * @param instance 配置对象实例
     * @param prefix 当前字段的前缀路径
     * @return 包含配置项元数据的列表
     */
    private static List<Map<String, Object>> scanRecursive(Object instance, String prefix) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (instance == null) return result;

        Class<?> clazz = instance.getClass();
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(f ->
                f.isAnnotationPresent(ConfigNode.class) || f.isAnnotationPresent(ConfigItem.class)
        ).sorted(Comparator.comparingInt(f -> {
            if (f.isAnnotationPresent(ConfigNode.class)) {
                return f.getAnnotation(ConfigNode.class).order();
            } else if (f.isAnnotationPresent(ConfigItem.class)) {
                return f.getAnnotation(ConfigItem.class).order();
            }
            return Integer.MAX_VALUE;
        })).toList();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String currentKey = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;

            try {
                // 处理嵌套配置节点 (@ConfigNode)
                if (field.isAnnotationPresent(ConfigNode.class)) {
                    ConfigNode annotation = field.getAnnotation(ConfigNode.class);

                    Map<String, Object> metadata = new LinkedHashMap<>();
                    metadata.put("key", currentKey);
                    metadata.put("type", "node");
                    metadata.put("label", annotation.label());
                    metadata.put("description", annotation.description());
                    metadata.put("content", scanRecursive(field.get(instance), currentKey));

                    result.add(metadata);
                }

                // 处理配置项 (@ConfigItem)
                else if (field.isAnnotationPresent(ConfigItem.class)) {
                    ConfigItem annotation = field.getAnnotation(ConfigItem.class);
                    Object value = field.get(instance);

                    Map<String, Object> metadata = new LinkedHashMap<>();
                    metadata.put("key", currentKey);
                    metadata.put("type", "item");
                    metadata.put("label", annotation.label());
                    metadata.put("valueType", annotation.type());
                    metadata.put("defaultValue", annotation.defaultValue());
                    metadata.put("description", annotation.description());
                    metadata.put("enable", annotation.enable());
                    metadata.put("constraints", annotation.constraints());
                    metadata.put("options", annotation.options());
                    metadata.put("value", value);

                    result.add(metadata);
                }
            } catch (Exception e) {
                log.error("Error occupied when scanning config: {}", currentKey, e);
            }
        }
        return result;
    }

    /**
     * 反射更新字段值
     * @param instance 配置根对象
     * @param key 点号分隔的路径 (e.g., "server.port")
     * @param newValue 新值
     */
    static void updateValue(Object instance, String key, String newValue) throws ConfigurationNotAppliedException {
        String[] parts = key.split("\\.");
        Object current = instance;

        try {
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);

                if (i == parts.length - 1) {
                    // 叶子节点，设置值
                    changeIfValid(current, field, newValue);
                } else {
                    // 存在子节点，向下移动
                    current = field.get(current);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            throw new ConfigurationNotAppliedException("Failed to update config value for key: " + key, e);
        }
    }

    private static void changeIfValid(Object object, Field field, String newValue) throws ConfigurationNotAppliedException {
        // 获取字段上的 ConfigItem 注解，判断是否符合 constraints 或 options 的条件
        ConfigItem item = field.getAnnotation(ConfigItem.class);
        if (item == null) {
            throw new ConfigurationNotAppliedException("Field " + field.getName() + " is not a config item");
        }
        String constraints = item.constraints();

        switch (item.type()) {
            case "number", "integer" -> {
                // 对于数字和整数类型，使用 ConstraintsParser 进行范围检查
                List<ConstraintsParser.Range<?>> ranges = constraintsCache.computeIfAbsent(
                        item.constraints(),
                        _k -> ConstraintsParser.parse(constraints, field.getType())
                );
                boolean valid = false;
                try {
                    Comparable<?> typedValue = ConstraintsParser.valueOf(newValue, field.getType());
                    for (ConstraintsParser.Range<?> range : ranges) {
                        // 只要符合一个范围就算合法
                        if (range.contains(typedValue)) {
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        throw new ConfigurationNotAppliedException(
                                String.format("Value %s does not satisfy constraints for %s", newValue, field.getName())
                        );
                    } else {
                        applyFieldSet(field, object, typedValue);
                    }
                } catch (Exception e) {
                    throw new ConfigurationNotAppliedException("Invalid type for field " + field.getName(), e);
                }
            }
            case "string", "password" -> {
                // 对于字符串类型，使用正则表达式进行检查
                if (!newValue.matches(constraints)) {
                    throw new ConfigurationNotAppliedException(
                            String.format("Value %s does not match constraints for %s", newValue, field.getName())
                    );
                } else {
                    applyFieldSet(field, object, newValue);
                }
            }
            case "boolean" -> {
                // 对于布尔类型，直接转换，这一步骤比较简单，基本不会抛出异常
                boolean boolValue = Boolean.parseBoolean(newValue);
                applyFieldSet(field, object, boolValue);
            }
            case "select" -> {
                // 对于下拉选择框类型，检查是否在选项中
                String[] options = item.options();
                if (!Arrays.asList(options).contains(newValue)) {
                    throw new ConfigurationNotAppliedException(
                            String.format("Value %s is not a valid option for %s", newValue, field.getName())
                    );
                } else {
                    // TODO: 这里假设字段类型是 String，后续需要增加对 enum 类型的支持
                    applyFieldSet(field, object, newValue);
                }
            }
            default -> throw new ConfigurationNotAppliedException(
                    String.format("Unsupported config item type for field %s: %s", newValue, item.type()
                    ));
        }
    }

    private static void applyFieldSet(Field field, Object object, Object value) throws ConfigurationNotAppliedException {
        try {
            // TODO: 触发对应的配置修改事件，判断事件是否被插件的监听器取消
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ConfigurationNotAppliedException("Failed to set value for field " + field.getName(), e);
        }
    }

}