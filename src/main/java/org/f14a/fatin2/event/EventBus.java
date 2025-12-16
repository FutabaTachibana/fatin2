package org.f14a.fatin2.event;

import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class EventBus {
    public static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    public static EventBus instance;
    public static EventBus getInstance() {
        return EventBus.instance;
    }
    private final ExecutorService asyncService;
    private final ConcurrentMap<Class<? extends Event>, CopyOnWriteArrayList<EventListener>> handlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CopyOnWriteArrayList<EventListener>> commandHandlers = new ConcurrentHashMap<>();
    private final SessionManager sessionManager;
    public static SessionManager getSessionManager() {
        return getInstance().sessionManager;
    }

    public EventBus() {
        EventBus.instance = this;
        this.asyncService = new ThreadPoolExecutor(
                4, // core pool size,
                16, // maximum pool size
                60L, // keep-alive time
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), // work queue
                new ThreadFactory() {
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "Message-Handler-" + (count++));
                        thread.setDaemon(false);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        this.sessionManager = new SessionManager();
        LOGGER.debug("Event Bus initialized with thread pool");
    }

    public void register(Object listener, Fatin2Plugin plugin) {
        Class<?> clazz = listener.getClass();

        int countH = 0, countC = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            // Check if the method is annotated with @EventHandler
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler != null) {
                countH += registerCommon(listener, plugin, method, eventHandler);
                continue;
            }
            OnCommand onCommand = method.getAnnotation(OnCommand.class);
            if (onCommand != null) {
                countC += registerCommand(listener, plugin, method, onCommand);
                continue;
            }
        }
        handlers.values().forEach(Collections::sort);
        commandHandlers.values().forEach(Collections::sort);
        LOGGER.debug("Registered {} event handlers for plugin {}", countH, plugin.getName());
        LOGGER.debug("Registered {} command handlers for plugin {}", countC, plugin.getName());
    }
    // Return 1 if registered successfully, 0 otherwise
    private int registerCommon(Object listener, Fatin2Plugin plugin, Method method, EventHandler annotation){
        // Check method parameters
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            LOGGER.warn("Method {} in class {} is annotated with @EventHandler but does not have exactly one parameter. Skipping registration.",
                    method.getName(), listener.getClass().getName());
            return 0;
        }
        Class<?> eventType = paramTypes[0];
        // Check if the parameter type is a subclass of Event
        if (!Event.class.isAssignableFrom(eventType)) {
            LOGGER.warn("Method {} in class {} has parameter of type {} which is not a subclass of Event. Skipping registration.",
                    method.getName(), listener.getClass().getName(), eventType.getName());
            return 0;
        }

        @SuppressWarnings("unchecked")
        Class<? extends Event> eventClass = (Class<? extends Event>) eventType;
        // Register the event handler
        handlers.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>())
                .add(new EventListener(listener, method, plugin, annotation.priority(), method.isAnnotationPresent(Coroutines.class)));
        return 1;
    }
    private int registerCommand(Object listener, Fatin2Plugin plugin, Method method, OnCommand annotation){
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            LOGGER.warn("Method {} in class {} is annotated with @OnCommand but does not have exactly one parameter. Skipping registration.",
                    method.getName(), listener.getClass().getName());
            return 0;
        }
        Class<?> eventType = paramTypes[0];
        // Check if the parameter type is CommandEvent
        if (!CommandEvent.class.isAssignableFrom(eventType)) {
            LOGGER.warn("Method {} in class {} has parameter of type {} which is not a subclass of CommandEvent. Skipping registration.",
                    method.getName(), listener.getClass().getName(), eventType.getName());
            return 0;
        }
        commandHandlers.computeIfAbsent(annotation.command(), k -> new CopyOnWriteArrayList<>())
                .add(new EventListener(listener, method, plugin, annotation.priority(), method.isAnnotationPresent(Coroutines.class)));
        // TODO: register the aliases as well
        return 1;
    }
    public void unregister(Fatin2Plugin plugin) {
        // Unregister from common event handlers
        for (List<EventListener> listenerList : handlers.values()) {
            listenerList.removeIf(listener -> listener.plugin() == plugin);
        }
    }

    private List<EventListener> getHandlers(Class<? extends Event> eventClass) {
        return handlers.getOrDefault(eventClass, new CopyOnWriteArrayList<>());
    }
    private List<EventListener> getCommandHandlers(String command) {
        return commandHandlers.getOrDefault(command, new CopyOnWriteArrayList<>());
    }

    public void post(Event event) {
        if (event instanceof CommandEvent) {
            postCommand((CommandEvent) event);
        } else if (event.isAsync()) {
            postAsync(event);
        } else {
            postSync(event);
        }
    }
    private void postAsync(Event event) {
        this.asyncService.submit(() -> {
            postSync(event);
        });
    }
    private void postSync(Event event) {
        List<EventListener> eventListeners = getHandlers(event.getClass());
        boolean cancelable = event instanceof Cancelable;
        LOGGER.debug("Found {} handlers for event {}", eventListeners.size(), event.getClass().getName());
        for (EventListener listener : eventListeners) {
            try {
                if(listener.isCoroutine()) {
                    this.asyncService.submit(() -> {
                        try {
                            listener.method().invoke(listener.listener(), event);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.method().invoke(listener.listener(), event);
                }
                if (cancelable && ((Cancelable) event).isCancelled()) {
                    LOGGER.debug("Event {} has been cancelled by plugin {}. Stopping further processing.",
                            event.getClass().getName(), listener.plugin().getName());
                    break;
                }
            } catch (Exception e) {
                EventBus.LOGGER.error("Error invoking event handler method: {} with listener {}",
                        listener.method().getName(), listener.listener().getClass().getName(), e);
            }
        }
    }
    private void postCommand(CommandEvent event) {
        List<EventListener> eventListeners = getCommandHandlers(event.getCommand());
        LOGGER.debug("Found {} handlers for command {}", eventListeners.size(), event.getCommand());
        for (EventListener listener : eventListeners) {
            try {
                if(listener.isCoroutine()) {
                    this.asyncService.submit(() -> {
                        try {
                            listener.method().invoke(listener.listener(), event);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    listener.method().invoke(listener.listener(), event);
                }
            } catch (Exception e) {
                EventBus.LOGGER.error("Error invoking command handler method: {} with listener {}",
                        listener.method().getName(), listener.listener().getClass().getName(), e);
            }
        }
    }
    public void shutdown() {
        LOGGER.info("Shutting down Event Bus...");
        sessionManager.shutdown();
        asyncService.shutdown();
        try {
            if (!asyncService.awaitTermination(60, TimeUnit.SECONDS)) {
                asyncService.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncService.shutdownNow();
        }
        LOGGER.debug("Event Bus shut down complete.");
    }
}
