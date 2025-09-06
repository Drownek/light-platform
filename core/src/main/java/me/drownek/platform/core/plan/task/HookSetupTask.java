package me.drownek.platform.core.plan.task;

import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.dependency.Hook;
import me.drownek.platform.core.dependency.HookInitializationException;
import me.drownek.platform.core.plan.ExecutionTask;

public class HookSetupTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {
        for (Hook<?> hook : platform.getHooks()) {
            Object instance = setupHook(platform, hook);
            if (instance != null) {
                platform.registerInjectable("", instance);
            }
        }
    }

    private Object setupHook(LightPlatform platform, Hook<?> hook) {
        if (platform.isPluginEnabled(hook.pluginName())) {
            try {
                Object instance = platform.createInstance(hook.clazz());
                platform.log("Registered " + hook.pluginName() + " hook");
                return instance;
            } catch (Exception e) {
                return handleFailure(platform, hook, e);
            }
        } else {
            return useFallback(platform, hook, hook.pluginName() + " not found");
        }
    }

    private Object handleFailure(LightPlatform platform, Hook<?> hook, Exception e) {
        Throwable cause = unwrapCause(e, HookInitializationException.class);

        if (cause != null) {
            return useFallback(platform, hook,
                "Failed to load " + hook.pluginName() + " hook" + (cause.getMessage() != null ? ": " + cause.getMessage() : ""));
        }

        throw new RuntimeException("Failed to setup hook: " + hook.pluginName(), e);
    }

    private Object useFallback(LightPlatform platform, Hook<?> hook, String message) {
        if (hook.fallbackClass() != null) {
            platform.log(message + ", using fallback");
            return platform.createInstance(hook.fallbackClass());
        } else {
            platform.log(message);
            return null;
        }
    }

    private Throwable unwrapCause(Throwable throwable, Class<? extends Throwable> targetType) {
        while (throwable != null && !targetType.isInstance(throwable)) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
