package me.drownek.platform.core;

import eu.okaeri.injector.Injector;
import lombok.NonNull;
import me.drownek.platform.core.annotation.DebugLogging;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.plan.ExecutionPlan;

import java.util.function.Consumer;

public interface LightPlatform {

    // injector
    void setInjector(@NonNull Injector injector);

    Injector getInjector();

    default void registerInjectable(@NonNull String name, @NonNull Object type) {
        this.getInjector().registerInjectable(name, type);
    }

    default <T> T createInstance(@NonNull Class<T> type) {
        return this.getInjector().createInstance(type);
    }

    // creator
    void setCreator(@NonNull ComponentCreator creator);

    ComponentCreator getCreator();

    // logging
    void log(@NonNull String message);

    // setup
    void plan(@NonNull ExecutionPlan plan);

    Consumer<String> logDebugAction();

    default void debug(@NonNull String message) {
        logDebugAction().accept(message);
    }

    default boolean isDebugLogging() {
        return this.getClass().isAnnotationPresent(DebugLogging.class);
    }
}
