package org.f14a.fatin2.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConfigInjector {
    private static final Gson GSON = new Gson();

    /**
     * 接受原始 JSON 字符串，注入配置后返回新的 JSON 字符串。
     * @param originalJson 原始 JSON 字符串
     * @return 配置是否生效
     */
    public static boolean inject(String originalJson) {
        return false;
    }

    /**
     * 检查数字是否在配置的限制条件内。
     * 对于 {@code "1,2,[5,10),[100,)"} 来说，如果数字是 2，则返回 true。
     * @param number 要检查的数字
     * @param constraints 限制条件
     * @return
     */
    private static boolean numberInConfigRange(double number, String constraints) {
        return false;
    }
}
