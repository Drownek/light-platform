package me.drownek.platform.bukkit.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.bukkit.annotation.Delayed;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import me.drownek.platform.core.component.manifest.BeanSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class DelayedComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(Delayed.class) != null;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return method.getAnnotation(Delayed.class) != null;
    }

    private @Inject JavaPlugin plugin;
    private @Inject PlatformScheduler scheduler;
    private @Inject LightBukkitPlugin okPlugin;

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Runnable runnable = ComponentHelper.manifestToRunnable(manifest, injector);

        Delayed delayed = manifest.getSource() == BeanSource.METHOD
            ? manifest.getMethod().getAnnotation(Delayed.class)
            : manifest.getType().getAnnotation(Delayed.class);

        if (!delayed.name().isEmpty()) {
            manifest.setName(delayed.name());
        }

        int delay = delayed.time();
        boolean async = delayed.async();

        this.scheduler.runLater(runnable, delay, async);

        long took = System.currentTimeMillis() - start;

        boolean showRegisteredComponents = injector.getOrThrow("showRegisteredComponents", Boolean.class);
        if (showRegisteredComponents) {
            creator.log(ComponentHelper.buildComponentMessage()
                .type("Added delayed")
                .name(manifest.getSource() == BeanSource.METHOD ? manifest.getName() : manifest.getType().getSimpleName())
                .took(took)
                .meta("time", delay)
                .meta("async", async)
                .build());
        }

        creator.increaseStatistics("delayed", 1);

        return runnable;
    }
}
