package org.f14a.fatin2.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigNode {
    /**
     * 配置块的顺序，数值越小越靠前。
     * <p>
     * 因为 Java 不能通过反射获取字段的声明顺序，
     * 所以需要通过该属性是必要的。
     */
    int order();

    /**
     * 配置块的显示名称。
     */
    String label();

    /**
     * 配置块的描述信息。
     */
    String description() default "";
}
