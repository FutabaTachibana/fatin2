package org.f14a.fatin2.event;

import org.f14a.fatin2.event.command.*;
import org.f14a.fatin2.event.response.ResponseManager;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.event.session.SessionManager;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

public class EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private static final Map<Class<?>, CommandEventListener.Scope> SCOPE_BY_EVENT = Map.of(
            CommandEvent.class, CommandEventListener.Scope.BOTH,
            PrivateCommandEvent.class, CommandEventListener.Scope.PRIVATE,
            GroupCommandEvent.class, CommandEventListener.Scope.GROUP
    );
    private final ExecutorService asyncService;
    private final ExecutorService virtualAsyncService;
    private final ConcurrentMap<Class<? extends Event>, CopyOnWriteArrayList<EventListener>> handlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CommandEventListener> commandHandlers = new ConcurrentHashMap<>();

    private static EventBus instance;
    public static EventBus getInstance() {
        return EventBus.instance;
    }
    private final SessionManager sessionManager;
    public static SessionManager getSessionManager() {
        return getInstance().sessionManager;
    }
    private final ResponseManager responseManager;
    public static ResponseManager getResponseManager() {
        return getInstance().responseManager;
    }
    private BiPredicate<CommandEvent, CommandEventListener> permissionProvider;

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
        this.virtualAsyncService = Executors.newVirtualThreadPerTaskExecutor();
        this.sessionManager = new SessionManager();
        this.responseManager = new ResponseManager();
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
        AtomicInteger count = new AtomicInteger();
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
        CommandEventListener.Scope scope = SCOPE_BY_EVENT.get(eventType);
        CommandEventListener wrappedListener = new CommandEventListener(listener, method, plugin,
                method.isAnnotationPresent(Coroutines.class), scope, annotation.needAt(),
                annotation.permission(), annotation.description());
        addCommand(annotation.command(), wrappedListener, annotation, plugin);
        count.getAndIncrement();
        Arrays.stream(annotation.alias()).toList().forEach(command -> {
            addCommand(command, wrappedListener, annotation, plugin);
            count.getAndIncrement();
        });
        return count.get();
    }
    private void addCommand(String command, CommandEventListener listener, OnCommand annotation, Fatin2Plugin plugin) {
        if (commandHandlers.containsKey(command)) {
            String newCommand = plugin.getName() + ":" + command;
            commandHandlers.put(newCommand, listener);
            LOGGER.warn("Command {} is already registered. Registered command as {} instead.", command, newCommand);
        } else {
            commandHandlers.put(command, listener);
        }
    }
    public boolean registerPermissionProvider(BiPredicate<CommandEvent, CommandEventListener> permissionProvider) {
        if (this.permissionProvider != null ) {
            LOGGER.error("PermissionProvider has already been registered. Ignoring subsequent registration.");
            return false;
        }
        else {
            this.permissionProvider = permissionProvider;
            LOGGER.info("PermissionProvider registered successfully.");
            return true;
        }
    }
    public void unregisterPermissionProvider() {
        this.permissionProvider = null;
        LOGGER.info("PermissionProvider unregistered successfully.");
    }
    public void unregister(Fatin2Plugin plugin) {
        // Unregister from common event handlers
        for (List<EventListener> listenerList : handlers.values()) {
            listenerList.removeIf(listener -> listener.plugin() == plugin);
        }
        // unregister from command handlers
        for (Map.Entry<String, CommandEventListener> entry : commandHandlers.entrySet()) {
            if (entry.getValue().plugin() == plugin) {
                commandHandlers.remove(entry.getKey());
            }
        }
    }

    private List<EventListener> getHandlers(Class<? extends Event> eventClass) {
        return handlers.getOrDefault(eventClass, new CopyOnWriteArrayList<>());
    }
    public Map<Class<? extends Event>, List<EventListener>> getAllHandlers() {
        return Collections.unmodifiableMap(handlers);
    }
    private CommandEventListener getCommandHandlers(String command) {
        return commandHandlers.getOrDefault(command, null);
    }
    public Map<String, CommandEventListener> getAllCommandHandlers() {
        return Collections.unmodifiableMap(commandHandlers);
    }

    public void post(Event event) {
        if (event.isAsync()) {
            postAsync(event);
        } else {
            postSync(event);
        }
        if (event instanceof CommandEvent) {
            postCommand((CommandEvent) event);
        }
    }
    private void postAsync(Event event) {
        this.asyncService.submit(() -> {
            postSync(event, event.getClass());
        });
    }
    private void postSync(Event event) {
        postSync(event, event.getClass());
    }
    @SuppressWarnings("unchecked")
    private void postSync(Event event, Class<? extends Event> clazz) {
        if (clazz == null || !Event.class.isAssignableFrom(clazz)) {
            return;
        }
        // Handle superclass first
        postSync(event, (Class<? extends Event>) clazz.getSuperclass());

        List<EventListener> eventListeners = getHandlers(clazz);
        boolean cancelable = event instanceof Cancelable;
        LOGGER.debug("Found {} handlers for event {}", eventListeners.size(), clazz.getName());
        for (EventListener listener : eventListeners) {
            try {
                if(listener.isCoroutine()) {
                    this.virtualAsyncService.submit(() -> {
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
                            clazz.getName(), listener.plugin().getName());
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("Error invoking event handler method: {} with listener {}",
                        listener.method().getName(), listener.listener().getClass().getName(), e);
            }
        }
    }
    private void postCommand(CommandEvent event) {
        CommandEventListener listener = getCommandHandlers(event.getCommand());
        if (listener == null) {
            LOGGER.debug("No handlers found for command {}", event.getCommand());
            return;
        }
        LOGGER.debug("Found handler for command {}", event.getCommand());
        // Skip if needAt is true but the bot is not mentioned
        if (listener.needAt() && !event.isAtBot()) {
            return;
        }
        // Skip if scope does not match
        if (!switch (listener.scope()) {
            case BOTH -> true;
            case PRIVATE -> event instanceof PrivateCommandEvent;
            case GROUP -> event instanceof GroupCommandEvent;
        }) {
            return;
        }
        // Check permission
        if (this.permissionProvider != null && !this.permissionProvider.test(event, listener)) {
            return;
        }
        try {
            if (listener.isCoroutine()) {
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
            LOGGER.error("Error invoking command handler method: {} with listener {}",
                    listener.method().getName(), listener.listener().getClass().getName(), e);
        }
    }
    public void shutdown() {
        LOGGER.info("Shutting down Event Bus...");
        sessionManager.shutdown();
        responseManager.shutdown();
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
