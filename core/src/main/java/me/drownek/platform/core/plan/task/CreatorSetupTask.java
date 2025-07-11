package me.drownek.platform.core.plan.task;

import lombok.RequiredArgsConstructor;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.plan.ExecutionTask;

@RequiredArgsConstructor
public class CreatorSetupTask implements ExecutionTask<LightPlatform> {

    private final Class<? extends ComponentCreator> creatorType;
    private final Class<? extends ComponentCreatorRegistry> registryType;

    @Override
    public void execute(LightPlatform platform) {

        ComponentCreatorRegistry registry = platform.createInstance(this.registryType);
        platform.registerInjectable("creatorRegistry", registry);

        ComponentCreator componentCreator = platform.createInstance(this.creatorType);
        platform.registerInjectable("creator", componentCreator);

        platform.setCreator(componentCreator);
    }
}
