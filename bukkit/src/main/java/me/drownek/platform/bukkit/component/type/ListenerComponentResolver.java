package me.drownek.platform.bukkit.component.type;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.annotation.Component;
import me.drownek.platform.core.annotation.Service;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ListenerComponentResolver implements ComponentResolver {

    @Inject
    private JavaPlugin plugin;
    private @Inject LightBukkitPlugin okPlugin;

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
        this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);

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
