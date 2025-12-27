package org.f14a.fatin2.exception;

public class PluginInternalException extends RuntimeException {
    public PluginInternalException(String message) {
        super(message);
    }
    public PluginInternalException(String message, Throwable cause) {
        super(message, cause);
    }
    public PluginInternalException(Throwable cause) {
        super(cause);
    }
    public PluginInternalException() {
        super();
    }
}
