package org.f14a.fatin2.type.exception;

public class CommandSyntaxException extends RuntimeException{
    public CommandSyntaxException(String message) {
        super(message);
    }
    public CommandSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandSyntaxException(Throwable cause) {
        super(cause);
    }
    public CommandSyntaxException() {
        super();
    }
}
