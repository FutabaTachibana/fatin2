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
    /**
     * The priority of this event handler.
     */
    EventPriority priority() default EventPriority.NORMAL;
    /**
     * The command string to listen for. <br>
     * This is the string after the command prefix. <br>
     * For example, if the command prefix is "/" and you want to listen for "/help", set command="help".
     */
    String command();
    /**
     * Aliases for the command.
     */
    String[] alias() default {};
    /**
     * Whether the command requires @bot mention to invoke.
     */
    boolean needAt() default false;
    /**
     * Permission level required to invoke this command, by default it is set to 0 (Everyone). <br>
     * If you don't enable integrated permission plugin or custom permission plugin, this setting has no effect.
     * <ul>
     * <li>4 Bot Admins </li>
     * <li>3 Group Owner </li>
     * <li>2 Group Admins </li>
     * <li>1 Everyone </li>
     * <li>0 Everyone </li>
     * <li>-1 Banned Users </li>
     * </ul>
     * Users have level -1 permission means they are forbidden to use the bot,
     * however a command with level -1 permission can still be used by them.
     */
    int permission() default 0;
}
