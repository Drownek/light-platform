package me.drownek.platform.core.plan.task;

import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.plan.ExecutionTask;

import java.io.Closeable;

@RequiredArgsConstructor
public class CloseableShutdownTask implements ExecutionTask<LightPlatform> {

    private final Class<? extends Closeable> type;

    @Override
    public void execute(LightPlatform platform) {
        ComponentHelper.closeAllOfType(this.type, platform.getInjector(), platform.getInjector().get("closeHikari", Boolean.class).orElse(true));
    }
}
