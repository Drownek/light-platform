package me.drownek.platform.core.component.creator;

import eu.okaeri.injector.Injector;
import lombok.NonNull;
import me.drownek.platform.core.component.manifest.BeanManifest;

import java.lang.reflect.Method;

public interface ComponentResolver {

    boolean supports(@NonNull Class<?> type);

    boolean supports(@NonNull Method method);

    Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector);
}
