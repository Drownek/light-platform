package me.drownek.platform.core.plan.task;

import eu.okaeri.injector.OkaeriInjector;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;

public class InjectorSetupTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {
        OkaeriInjector injector = OkaeriInjector.create(true);
        platform.setInjector(injector);
        platform.registerInjectable("injector", injector);
    }
}
