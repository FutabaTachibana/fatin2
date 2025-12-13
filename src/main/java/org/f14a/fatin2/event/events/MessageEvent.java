package org.f14a.fatin2.event.events;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventResult;
import org.f14a.fatin2.type.message.OnebotMessage;

import java.lang.reflect.Method;
import java.util.List;

public class MessageEvent extends Event {
    private final OnebotMessage message;

    public MessageEvent(OnebotMessage message) {
        this.message = message;
        EventBus.getInstance().getHandlers(MessageEvent.class).forEach(method -> {
            try {
                method.invoke(EventBus.getInstance().getListener(method), this);
            } catch (Exception e) {
                EventBus.LOGGER.error("Error invoking event handler method: {}", method.getName(), e);
            }
        });
    }

    public OnebotMessage getMessage() {
        return message;
    }
}
