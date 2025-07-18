package eu.okaeri.platform.velocity.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.velocity.annotation.Delayed;
import eu.okaeri.platform.velocity.scheduler.PlatformScheduler;
import lombok.NonNull;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import me.drownek.platform.core.component.manifest.BeanSource;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class DelayedComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(Delayed.class) != null;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return method.getAnnotation(Delayed.class) != null;
    }

    private @Inject PlatformScheduler scheduler;

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
        TimeUnit timeUnit = delayed.timeUnit();

        this.scheduler.delay(runnable, delay, timeUnit);

        long took = System.currentTimeMillis() - start;
        creator.debug(ComponentHelper.buildComponentMessage()
            .type("Added delayed")
            .name(manifest.getSource() == BeanSource.METHOD ? manifest.getName() : manifest.getType().getSimpleName())
            .took(took)
            .meta("time", delay)
            .meta("timeUnit", timeUnit)
            .build());
        creator.increaseStatistics("delayed", 1);

        return runnable;
    }
}
