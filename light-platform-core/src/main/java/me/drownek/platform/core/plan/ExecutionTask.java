package me.drownek.platform.core.plan;

import me.drownek.platform.core.LightPlatform;

@FunctionalInterface
public interface ExecutionTask<T extends LightPlatform> {
    void execute(T platform);
}
