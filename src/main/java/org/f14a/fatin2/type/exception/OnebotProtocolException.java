package org.f14a.fatin2.type.exception;

public class OnebotProtocolException extends RuntimeException {
    public OnebotProtocolException(String message) {
        super(message);
    }
    public OnebotProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
    public OnebotProtocolException(Throwable cause) {
        super(cause);
    }
    public OnebotProtocolException() {
        super();
    }
}
