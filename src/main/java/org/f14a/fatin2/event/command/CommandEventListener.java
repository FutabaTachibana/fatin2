package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.EventPriority;
import org.f14a.fatin2.plugin.Fatin2Plugin;

import java.lang.reflect.Method;

public record CommandEventListener (
        Object listener, Method method, Fatin2Plugin plugin,
        EventPriority priority, boolean isCoroutine,
        Scope scope, boolean needAt, int permission
) implements Comparable<CommandEventListener> {
    public enum Scope {
        PRIVATE,
        GROUP,
        BOTH
    }
    @Override
    public int compareTo(CommandEventListener listener) {
        // MONITOR first and LOWEST last
        // Degree is reversed because ordinal() returns 0 for the first enum constant
        // return -Integer.compare(this.priority.ordinal(), listener.priority.ordinal());
        return Integer.compare(listener.priority.ordinal(), this.priority.ordinal());
    }
}
