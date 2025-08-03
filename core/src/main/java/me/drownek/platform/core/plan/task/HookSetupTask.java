package me.drownek.platform.core.plan.task;

import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.dependency.Hook;
import me.drownek.platform.core.dependency.HookInitializationException;
import me.drownek.platform.core.plan.ExecutionTask;

public class HookSetupTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {
        for (Hook<?> hook : platform.getHooks()) {
            Object instance;

            if (platform.isPluginEnabled(hook.pluginName())) {
                try {
                    instance = platform.createInstance(hook.clazz());
                    platform.log("Registered " + hook.pluginName() + " hook");
                } catch (Exception e) {
                    // Unwrap to see if the cause is HookInitializationException
                    Throwable cause = e;
                    while (cause != null && !(cause instanceof HookInitializationException)) {
                        cause = cause.getCause();
                    }

                    if (cause != null) {
                        platform.log("Failed to load " + hook.pluginName() + " hook, using fallback: " + cause.getMessage());
                        instance = platform.createInstance(hook.fallbackClass());
                    } else {
                        throw new RuntimeException("Failed to setup hook: " + hook.pluginName(), e);
                    }
                }
            } else {
                platform.log(hook.pluginName() + " not found, using fallback");
                instance = platform.createInstance(hook.fallbackClass());
            }

            platform.registerInjectable("", instance);
        }
    }
}
