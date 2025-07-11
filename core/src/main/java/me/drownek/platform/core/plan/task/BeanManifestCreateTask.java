package me.drownek.platform.core.plan.task;

import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.manifest.BeanManifest;
import me.drownek.platform.core.plan.ExecutionTask;

public class BeanManifestCreateTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {

        // create manifest of the platform
        ClassLoader classLoader = platform.getClass().getClassLoader();
        BeanManifest beanManifest = BeanManifest.of(classLoader, platform.getClass(), platform.getCreator(), true);
        beanManifest.setObject(platform);

        // register injectable
        platform.registerInjectable("manifest", beanManifest);
    }
}
