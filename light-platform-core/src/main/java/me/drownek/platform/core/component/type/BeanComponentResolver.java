package me.drownek.platform.core.component.type;

import eu.okaeri.injector.Injector;
import lombok.NonNull;
import me.drownek.platform.core.annotation.Bean;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;

import java.lang.reflect.Method;

/**
 * Resolves generic @Component classes.
 * <p>
 * Remember to register last as otherwise {@link #supports(Method)} may interfere
 * with other ComponentResolver implementations that due to being 3rd party
 * extension are not provided with custom annotation.
 */
public class BeanComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return false;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return method.getAnnotation(Bean.class) != null;
    }

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Object result = ComponentHelper.invokeMethod(manifest, injector);

        long took = System.currentTimeMillis() - start;
        boolean showRegisteredComponents = injector.getOrThrow("showRegisteredComponents", Boolean.class);
        if (took > 1 && showRegisteredComponents) {
            creator.log(ComponentHelper.buildComponentMessage()
                .type("Added generic bean")
                .name(manifest.getName().isEmpty() ? manifest.getMethod().getName() : manifest.getName())
                .took(took)
                .build());
        }
        creator.increaseStatistics("beans", 1);

        return result;
    }
}
