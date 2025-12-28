package org.f14a.fatin2.event.lifecycle;

import org.f14a.fatin2.event.Cancelable;
import org.f14a.fatin2.event.Event;

/**
 * Called when the client attempts to reconnect to the server.
 */
public class ClientReconnectEvent implements Event, Cancelable {
    private boolean cancelled = false;
    @Override
    public boolean isAsync() {
        return false;
    }
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    @Override
    public void cancel() {
        this.cancelled = true;
    }
}
