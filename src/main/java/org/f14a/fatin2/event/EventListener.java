package org.f14a.fatin2.event;

import org.f14a.fatin2.plugin.Fatin2Plugin;

import java.lang.reflect.Method;

/*
 * An object those already register with methods and corresponding object instance
 * */
public record EventListener(Object listener, Method method, Fatin2Plugin plugin) {
}
