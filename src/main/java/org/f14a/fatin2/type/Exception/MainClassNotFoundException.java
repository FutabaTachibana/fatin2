package org.f14a.fatin2.type.Exception;

public class MainClassNotFoundException extends RuntimeException {
    public MainClassNotFoundException(String message) {
        super(message);
    }
    public MainClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public MainClassNotFoundException(Throwable cause) {
        super(cause);
    }
    public MainClassNotFoundException() {
        super();
    }
}
