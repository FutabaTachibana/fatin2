package org.f14a.fatin2.dispatcher;

import org.f14a.fatin2.handler.MessageHandler;
import org.f14a.fatin2.type.AbstractOnebotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Deprecated
public class MessageDispatcher {
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);

    private final List<MessageHandler> handlers = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService;

    public MessageDispatcher() {
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
        LOGGER.info("Message Dispatcher initialized with thread pool");
    }

    // Register a message handler
    public void register(MessageHandler handler) {
        handlers.add(handler);
        LOGGER.info("Registered handler: {}", handler.getClass().getName());
    }

    // Dispatch a message to the appropriate handler
    public void unregister(MessageHandler handler) {
        handlers.remove(handler);
        LOGGER.info("Unregistered handler: {}", handler.getClass().getName());
    }

    // Dispatch a message to the appropriate handler
    public void dispatch(AbstractOnebotMessage message) {
        if (message == null) {
            LOGGER.warn("Received null message, skipping dispatch");
            return;
        }

        LOGGER.debug("Dispatching message: {}", message.postType());

        // Count of handlers that processed the message
        int handlerCount = 0;
        for (MessageHandler handler : handlers) {
            try {
                if (handler.canHandle(message)) {
                    LOGGER.debug("Message handling by: {}", handler.getClass().getName());
                    handlerCount++;
                    handleAsync(handler, message);
                }
            } catch (Exception e) {
                LOGGER.error("Error while handling message with {}: {}", handler.getClass().getName(), e.getMessage(), e);
            }
        }

        if(handlerCount == 0) {
            LOGGER.warn("Message {} may not be handled", message.postType());
        } else {
            LOGGER.debug("Message {} dispatched to {} handlers", message.postType(), handlerCount);
        }
    }

    // Handle message asynchronously
    public void handleAsync(MessageHandler handler, AbstractOnebotMessage message) {
        long startTime = System.currentTimeMillis();
        String handlerName = handler.getClass().getSimpleName();
        executorService.submit(() -> {
            try {
                LOGGER.debug("Handling message with {}", handlerName);
                handler.handle(message);
                long duration = System.currentTimeMillis() - startTime;
                LOGGER.debug("Handled message with {} in {} ms", handlerName, duration);

                if (duration > 5000) {
                    LOGGER.warn("Handler {} took too long ({} ms) to process message {}", handlerName, duration, message.postType());
                }
            } catch (Exception e) {
                LOGGER.error("Error while handling message asynchronously with {}: {}", handler.getClass().getName(), e.getMessage(), e);
            }
        });
    }

    // Get all handlers
    public List<MessageHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
