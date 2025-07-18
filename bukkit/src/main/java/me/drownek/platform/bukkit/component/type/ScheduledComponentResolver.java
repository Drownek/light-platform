package me.drownek.platform.bukkit.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.bukkit.annotation.Scheduled;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import me.drownek.platform.core.component.manifest.BeanSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class ScheduledComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(Scheduled.class) != null;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return method.getAnnotation(Scheduled.class) != null;
    }

    private @Inject JavaPlugin plugin;
    private @Inject PlatformScheduler scheduler;
    private @Inject LightBukkitPlugin okPlugin;


    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Runnable runnable = ComponentHelper.manifestToRunnable(manifest, injector);

        Scheduled scheduled = manifest.getSource() == BeanSource.METHOD
                ? manifest.getMethod().getAnnotation(Scheduled.class)
                : manifest.getType().getAnnotation(Scheduled.class);

        if (!scheduled.name().isEmpty()) {
            manifest.setName(scheduled.name());
        }

        int rate = scheduled.rate();
        int delay = (scheduled.delay() == -1) ? rate : scheduled.delay();
        boolean async = scheduled.async();

        this.scheduler.runTimer(runnable, delay, rate, async);

        long took = System.currentTimeMillis() - start;


        creator.debug(ComponentHelper.buildComponentMessage()
                .type("Added scheduled")
                .name(manifest.getSource() == BeanSource.METHOD ? manifest.getName() : manifest.getType().getSimpleName())
                .took(took)
                .meta("delay", delay)
                .meta("rate", rate)
                .meta("async", async)
                .build());


        creator.increaseStatistics("scheduled", 1);

        return runnable;
    }
}
