package org.f14a.fatin2.dispatcher;

import org.f14a.fatin2.handler.MessageHandler;
import org.f14a.fatin2.type.message.OnebotMessage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageDispatcher {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MessageDispatcher.class);

    private final List<MessageHandler> handlers = new CopyOnWriteArrayList<>();

    // Register a message handler
    public void register(MessageHandler handler) {
        handlers.add(handler);
        logger.info("Registered handler: {}", handler.getClass().getName());
    }

    // Dispatch a message to the appropriate handler
    public void unregister(MessageHandler handler) {
        handlers.remove(handler);
        logger.info("Unregistered handler: {}", handler.getClass().getName());
    }

    // Dispatch a message to the appropriate handler
    public void dispatch(OnebotMessage message) {
        if (message == null) {
            logger.warn("Received null message, skipping dispatch");
            return;
        }

        logger.debug("Dispatching message: {}, postType={}", message.getMessageType(), message.getPostType());

        boolean handled = false;
        for (MessageHandler handler : handlers) {
            try {
                if (handler.canHandle(message)) {
                    logger.debug("Message handled by: {}", handler.getClass().getName());
                    handler.handle(message);
                    handled = true;
                }
            } catch (Exception e) {
                logger.error("Error while handling message with {}: {}", handler.getClass().getName(), e.getMessage(), e);
            }
        }

        if(!handled) {
            logger.warn("Message {} has unknown type {}", message.getMessageType(), message.getPostType());
        }
    }

    // Get all handlers
    public List<MessageHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
