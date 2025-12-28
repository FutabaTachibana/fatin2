package org.f14a.fatin2.event.response;

import org.f14a.fatin2.event.Event;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.model.Response;

public class ResponseEvent extends Event {
    private final Response response;
    public ResponseEvent(Response response) {
        this.response = response;
    }
    public Response getResponse() {
        return response;
    }

    @Override
    public void fire() {
        if (this.response != null && this.response.echo() != null) {
            EventBus.getResponseManager().receiveResponse(this.response);
        }
        super.fire();
    }
    @Override
    public boolean isAsync() {
        return true;
    }
}
