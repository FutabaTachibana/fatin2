package org.f14a.fatin2.exception;

public class ConfigIOException extends RuntimeException{
    public ConfigIOException(String message) {
        super(message);
    }
    public ConfigIOException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigIOException(Throwable cause) {
        super(cause);
    }
    public ConfigIOException() {
        super();
    }
}
