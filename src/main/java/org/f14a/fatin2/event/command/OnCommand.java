package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for command handling methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnCommand {
    EventPriority priority() default EventPriority.NORMAL;
    String command();
    String[] alias() default {};
}
