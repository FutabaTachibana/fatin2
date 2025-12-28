package org.f14a.fatin2.exception;

public class IllegalTypeException extends RuntimeException {
    public IllegalTypeException(String message) {
        super(message);
    }
    public IllegalTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public IllegalTypeException(Throwable cause) {
        super(cause);
    }
    public IllegalTypeException() {
        super();
    }
}
