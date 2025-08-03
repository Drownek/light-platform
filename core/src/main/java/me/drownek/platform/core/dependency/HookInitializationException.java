package me.drownek.platform.core.dependency;

public class HookInitializationException extends RuntimeException {
    public HookInitializationException() {
        super();
    }

    public HookInitializationException(String message) {
        super(message);
    }

    public HookInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
