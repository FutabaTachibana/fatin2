package org.f14a.fatin2.event;

import org.f14a.fatin2.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * An object those already register with methods and corresponding object instance
 */
public record EventListener(Object listener, Method method, Plugin plugin, EventPriority priority, boolean isCoroutine) implements Comparable<EventListener> {
    @Override
    public int compareTo(EventListener listener) {
        // MONITOR first and LOWEST last
        // Degree is reversed because ordinal() returns 0 for the first enum constant
        // return -Integer.compare(this.priority.ordinal(), listener.priority.ordinal());
        return Integer.compare(listener.priority.ordinal(), this.priority.ordinal());
    }
}
