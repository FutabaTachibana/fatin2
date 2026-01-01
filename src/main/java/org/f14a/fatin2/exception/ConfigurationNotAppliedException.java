package org.f14a.fatin2.exception;

public class ConfigurationNotAppliedException extends RuntimeException {
    public ConfigurationNotAppliedException(String message) {
        super(message);
    }
    public ConfigurationNotAppliedException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigurationNotAppliedException(Throwable cause) {
        super(cause);
    }
    public ConfigurationNotAppliedException() {
        super();
    }
}
