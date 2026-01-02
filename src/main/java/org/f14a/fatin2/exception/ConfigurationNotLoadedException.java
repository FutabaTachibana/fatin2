package org.f14a.fatin2.exception;

public class ConfigurationNotLoadedException extends RuntimeException {
    public ConfigurationNotLoadedException(String message) {
        super(message);
    }
    public ConfigurationNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigurationNotLoadedException(Throwable cause) {
        super(cause);
    }
    public ConfigurationNotLoadedException() {
        super();
    }
}
