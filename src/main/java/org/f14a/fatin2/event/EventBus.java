package org.f14a.fatin2.event;

import org.f14a.fatin2.event.events.MessageEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.*;

public class EventBus {
    public static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    public static EventBus instance;
    public static EventBus getInstance() {
        return EventBus.instance;
    }
    private final ExecutorService executorService;
    private final ConcurrentMap<Class<? extends Event>, CopyOnWriteArrayList<Method>> handlers = new ConcurrentHashMap<>();

    public EventBus() {
        this.executorService = new ThreadPoolExecutor(
                4, // core pool size,
                16, // maximum pool size
                60L, // keep-alive time
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), // work queue
                new ThreadFactory() {
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "MessageHandler-" + (count++));
                        thread.setDaemon(false);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        LOGGER.debug("Event Bus initialized with thread pool");
    }

    public void register(Object listener, Fatin2Plugin plugin) {
        Class<?> clazz = listener.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            // Check if the method is annotated with @EventHandler
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation != null) {
                continue;
            }

            // Check method parameters
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) {
                LOGGER.warn("Method {} in class {} is annotated with @EventHandler but does not have exactly one parameter. Skipping registration.",
                        method.getName(), clazz.getName());
                continue;
            }

            Class<?> eventType = paramTypes[0];
            // Check if the parameter type is a subclass of Event
            if (!Event.class.isAssignableFrom(eventType)) {
                LOGGER.warn("Method {} in class {} has parameter of type {} which is not a subclass of Event. Skipping registration.",
                        method.getName(), clazz.getName(), eventType.getName());
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) eventType;

            // Register the event handler
            handlers.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(method);
        }
    }

    public List<Method> getHandlers(Class<? extends Event> eventClass) {
        return handlers.getOrDefault(eventClass, new CopyOnWriteArrayList<>());
    }
}
