package me.drownek.platform.core.plan.task;

import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;

import java.io.Closeable;

@RequiredArgsConstructor
public class CloseableShutdownTask implements ExecutionTask<LightPlatform> {

    private final Class<? extends Closeable> type;

    @Override
    public void execute(LightPlatform platform) {
        platform.getInjector().streamOf(type).forEach(closeable -> {
            try {
                closeable.close();
            } catch (Throwable ignored) {
            }
        });
    }
}
