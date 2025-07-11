package eu.okaeri.platform.velocity.plan;

import eu.okaeri.platform.velocity.LightVelocityPlugin;
import eu.okaeri.platform.velocity.scheduler.PlatformScheduler;
import me.drownek.platform.core.plan.ExecutionTask;

public class VelocitySchedulerShutdownTask implements ExecutionTask<LightVelocityPlugin> {

    @Override
    public void execute(LightVelocityPlugin platform) {
        platform.getInjector().getExact("scheduler", PlatformScheduler.class)
            .ifPresent(PlatformScheduler::cancelAll);
    }
}
