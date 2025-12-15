package org.f14a.fatin2.type.exception;

public class IllegalPluginException extends RuntimeException {
    public IllegalPluginException(String message) {
        super(message);
    }
    public IllegalPluginException(String message, Throwable cause) {
        super(message, cause);
    }
    public IllegalPluginException(Throwable cause) {
        super(cause);
    }
    public IllegalPluginException() {
        super();
    }
}
