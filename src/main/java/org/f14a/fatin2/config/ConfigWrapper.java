package org.f14a.fatin2.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public record ConfigWrapper(Object config, String configPath) {
    private static final Gson GSON = new Gson();

    /**
     * 获取配置的 JSON 格式作为 REST API 响应。
     * @return JSON 字符串
     */
    public String getJson() {
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

        return GSON.toJson(itemList);
    }
}
