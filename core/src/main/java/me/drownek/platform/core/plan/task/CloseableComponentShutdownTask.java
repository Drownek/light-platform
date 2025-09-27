package me.drownek.platform.core.plan.task;

import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.annotation.Component;
import me.drownek.platform.core.annotation.Service;
import me.drownek.platform.core.plan.ExecutionTask;

import java.io.Closeable;

@RequiredArgsConstructor
public class CloseableComponentShutdownTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {
        platform.getInjector().streamOf(Closeable.class)
            .filter(closeable -> {
                Class<? extends Closeable> clazz = closeable.getClass();
                return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class);
            })
            .forEach(closeable -> {
                try {
                    closeable.close();
                } catch (Throwable ignored) {
                }
            });
    }
}
