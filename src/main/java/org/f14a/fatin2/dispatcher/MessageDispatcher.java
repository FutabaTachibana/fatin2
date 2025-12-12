package org.f14a.fatin2.dispatcher;

import org.f14a.fatin2.handler.MessageHandler;
import org.f14a.fatin2.type.message.AbstractOnebotMessage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageDispatcher {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MessageDispatcher.class);

    private final List<MessageHandler> handlers = new CopyOnWriteArrayList<>();

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

        LOGGER.debug("Dispatching message: {}", message.getPostType());

        boolean handled = false;
        for (MessageHandler handler : handlers) {
            try {
                if (handler.canHandle(message)) {
                    LOGGER.debug("Message handled by: {}", handler.getClass().getName());
                    handler.handle(message);
                    handled = true;
                }
            } catch (Exception e) {
                LOGGER.error("Error while handling message with {}: {}", handler.getClass().getName(), e.getMessage(), e);
            }
        }

        if(!handled) {
            LOGGER.warn("Message {} cannot be handled", message.getPostType());
        }
    }

    // Get all handlers
    public List<MessageHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
