package me.drownek.platform.core.dependency;

public record Hook<T>(
    String pluginName,
    Class<? extends T> clazz,
    Class<? extends T> fallbackClass
) {
    public Hook(String pluginName, Class<? extends T> clazz) {
        this(pluginName, clazz, null);
    }
}
