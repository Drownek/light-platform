package me.drownek.platform.bungee.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bungee.LightBungeePlugin;
import me.drownek.platform.core.annotation.Component;
import me.drownek.platform.core.annotation.Service;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ListenerComponentResolver implements ComponentResolver {

    @Inject
    private Plugin plugin;
    private @Inject LightBungeePlugin okPlugin;

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return ((type.getAnnotation(Service.class) != null) || (type.getAnnotation(Component.class) != null)) && Listener.class.isAssignableFrom(type);
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Class<?> manifestType = manifest.getType();
        Object instance = injector.createInstance(manifestType);

        Listener listener = (Listener) instance;
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, listener);

        long took = System.currentTimeMillis() - start;


        creator.debug(ComponentHelper.buildComponentMessage()
                .type("Added listener")
                .name(listener.getClass().getSimpleName())
                .took(took)
                .meta("methods", Arrays.stream(listener.getClass().getDeclaredMethods())
                        .filter(method -> method.getAnnotation(EventHandler.class) != null)
                        .map(Method::getName)
                        .collect(Collectors.toList()))
                .build());

        creator.increaseStatistics("listeners", 1);

        return listener;
    }
}
