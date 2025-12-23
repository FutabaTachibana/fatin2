package org.f14a.fatin2.event.command;

import org.f14a.fatin2.event.EventPriority;
import org.f14a.fatin2.plugin.Fatin2Plugin;

import java.lang.reflect.Method;

public record CommandEventListener (
        Object listener, Method method, Fatin2Plugin plugin,
        boolean isCoroutine, Scope scope, boolean needAt,
        int permission, String description, String usage
) {
    public enum Scope {
        PRIVATE,
        GROUP,
        BOTH
    }
}
