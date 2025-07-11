package me.drownek.platform.core.component.type;

import eu.okaeri.injector.Injector;
import lombok.NonNull;
import me.drownek.platform.core.annotation.Component;
import me.drownek.platform.core.annotation.Service;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;

import java.lang.reflect.Method;

/**
 * Resolves generic @Component classes.
 * <p>
 * Remember to register last as otherwise {@link #supports(Class)} may interfere
 * with other ComponentResolver implementations that due to being 3rd party
 * extension are not provided with custom annotation e.g. bukkit's listeners.
 */
public class GenericComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return (type.getAnnotation(Service.class) != null) || (type.getAnnotation(Component.class) != null);
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Object result = injector.createInstance(manifest.getType());

        try {
            Method initMethod = result.getClass().getDeclaredMethod("init");
            initMethod.setAccessible(true);
            if (initMethod.isAnnotationPresent(Init.class)) {
                initMethod.invoke(result);
            }
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke init method on " + result.getClass().getName(), e);
        }

        long took = System.currentTimeMillis() - start;
        boolean showRegisteredComponents = injector.getOrThrow("showRegisteredComponents", Boolean.class);
        if (took > 1 && showRegisteredComponents) {
            creator.log(ComponentHelper.buildComponentMessage()
                .type("Added generic component")
                .name(manifest.getType().getSimpleName())
                .took(took)
                .build());
        }
        creator.increaseStatistics("components", 1);

        return result;
    }
}
