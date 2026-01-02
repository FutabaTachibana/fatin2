package org.f14a.fatin2.config;

import org.f14a.fatin2.exception.IllegalTypeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ConstraintsParser {
    /**
     * 范围类，表示一个范围限制条件。
     * <p>
     * min 和 max 可以为 null，表示无下界或无上界。
     * <p>
     * min 和 max 的 Inclusive 标志表示是否这一端是闭的（包括边界值）。
     * <p>
     * 实现比较的类型必须实现 {@link Comparable} 接口（除 boolean 以外的包装类都实现了）。
     * @param <T> 范围的类型
     */
    static class Range <T extends Comparable<T>> {
        final T min;
        final T max;
        final boolean minInclusive;
        final boolean maxInclusive;
        final boolean isSingleValue;

        Range(T min, boolean minInclusive, T max, boolean maxInclusive) {
            this.min = min;
            this.max = max;
            this.minInclusive = minInclusive;
            this.maxInclusive = maxInclusive;
            this.isSingleValue = false;
        }

        Range(T value) {
            this.min = value;
            this.max = value;
            this.minInclusive = true;
            this.maxInclusive = true;
            this.isSingleValue = true;
        }

        @SuppressWarnings("unchecked")
        boolean contains(Object value) throws ClassCastException {
            // 先判是否满足下界，不满足返回 false
            if (min != null) {
                if (minInclusive) {
                    if (min.compareTo((T) value) > 0) {
                        return false;
                    }
                } else {
                    if (min.compareTo((T) value) >= 0) {
                        return false;
                    }

                }
            }
            // 确定是否满足下界后，再判上界
            if (max != null) {
                if (maxInclusive) {
                    return max.compareTo((T) value) >= 0;
                } else {
                    return max.compareTo((T) value) > 0;
                }
            }
            return true;
        }
    }

    // 1. 左边界; 2. 最小值; 3. 最大值; 4. 右边界; 5. 单个数字
    private static final Pattern PATTERN = Pattern.compile(
            "([\\[(])([-+]?\\d+(?:\\.\\d+)?)?,([-+]?\\d+(?:\\.\\d+)?)?([])])|([-+]?\\d+(?:\\.\\d+)?)"
    );

    /**
     * 解析限制条件字符串，返回范围列表。
     * @param input 限制条件字符串
     * @param clazz 类型类对象
     * @return {@link Range} 列表
     */
    static List<Range<?>> parse(String input, Class<?> clazz) {
        List<Range<?>> ranges = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return ranges;
        }
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find()) {
            if (matcher.group(5) != null) {
                // 单个数字
                @SuppressWarnings({"unchecked", "rawtypes"})
                Range<?> range = new Range(valueOf(matcher.group(5), clazz));
                ranges.add(range);
            } else {
                // 范围
                String minStr = matcher.group(2);
                String maxStr = matcher.group(3);

                boolean minInclusive = "[".equals(matcher.group(1));
                boolean maxInclusive = "]".equals(matcher.group(4));

                Comparable<?> min = minStr != null ? valueOf(minStr, clazz) : null;
                Comparable<?> max = maxStr != null ? valueOf(maxStr, clazz) : null;

                @SuppressWarnings({"unchecked", "rawtypes"})
                Range<?> range = new Range(min, minInclusive, max, maxInclusive);
                ranges.add(range);
            }
        }
        return ranges;
    }

    static Comparable<?> valueOf(String str, Class<?> clazz) throws IllegalTypeException {
        return switch (clazz) {
            case Class<?> c when c == Integer.class -> Integer.parseInt(str);
            case Class<?> c when c == Long.class -> Long.parseLong(str);
            case Class<?> c when c == Double.class -> Double.parseDouble(str);
            case Class<?> c when c == Float.class -> Float.parseFloat(str);
            default -> throw new IllegalTypeException("Unsupported type: " + clazz);
        };
    }
}
