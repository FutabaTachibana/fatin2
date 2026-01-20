package org.f14a.fatin2.config;

import org.f14a.fatin2.exception.ConfigurationNotAppliedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigItem {
    /**
     * 配置项的顺序，数值越小越靠前。
     * <p>
     * 因为 Java 不能通过反射获取字段的声明顺序，
     * 所以需要通过该属性是必要的。
     */
    int order();

    /**
     * 配置项的显示名称。
     */
    String label();

    /**
     * 配置项的数据类型，必须是以下值之一:
     * {@code "string"}, {@code "boolean"}, {@code "number"}, {@code "integer"}, {@code "select"}, {@code "password"}。
     * <p>
     * 分别表示字符串、布尔值、数字、下拉选择框和密码，
     * 密码与字符串的区别仅在于 WebUI 显示时会隐藏先前的内容并且提供密码输入框。
     */
    String type();

    /**
     * 配置项的默认值。
     * <p>
     * 字符串类型使用普通字符串表示。
     * <p>
     * 布尔类型使用 {@code "true"} 或 {@code "false"} 字符串表示。
     * <p>
     * 数字使用字符串形式的数字表示，例如 {@code "42"}。
     * <p>
     * 对于下拉选择框类型，默认值应为选项中的一个值。
     * <p>
     * 若未指定默认值，则默认为空字符串 / {@code false} / {@code 0} / 首个选项。
     */
    String defaultValue() default "";

    /**
     * 配置项的描述信息。
     */
    String description() default "";

    /**
     * 该配置项是否能在运行时动态修改。
     * <p>
     * 如果不能，则在 WebUI 中仅为可读项。
     */
    boolean enable() default false;

    /**
     * 限制条件。
     * <p>
     * 对于字符串类型，可以是 Regex。
     * <p>
     * 对于数字类型，可以是可选值或区间，例如 {@code "1,2,[5,10),[100,)"}。
     * <p>
     * 对于布尔类型和下拉选择框类型，不应该使用该属性。
     * <p>
     * 若您需要更复杂的限制条件，请在配置类中的 Setter 中自行实现验证逻辑，
     * 并在验证失败时抛出 {@link ConfigurationNotAppliedException} 异常。
     */
    String constraints() default "";

    /**
     * 下拉选择框类型的可选项。
     * <p>
     * 对于下拉选择框类型，必须提供该属性，表示可供选择的选项列表。
     */
    String[] options() default {};
}
