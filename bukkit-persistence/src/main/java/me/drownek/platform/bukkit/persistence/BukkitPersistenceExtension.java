package me.drownek.platform.bukkit.persistence;

import eu.okaeri.persistence.Persistence;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.plan.ExecutionPlan;
import me.drownek.platform.core.plan.task.CloseableShutdownTask;

import static me.drownek.platform.core.plan.ExecutionPhase.SHUTDOWN;

public class BukkitPersistenceExtension implements LightExtension {

    @Override
    public void plan(ExecutionPlan plan, LightPlatform platform) {
        plan.add(SHUTDOWN, new CloseableShutdownTask(Persistence.class));
    }
}
