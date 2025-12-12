package org.f14a.fatin2.type.Exception;

public class UnknownMessageTypeException extends RuntimeException {
    public UnknownMessageTypeException(String message) {
        super(message);
    }
    public UnknownMessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnknownMessageTypeException(Throwable cause) {
        super(cause);
    }
    public UnknownMessageTypeException() {
        super();
    }
}
