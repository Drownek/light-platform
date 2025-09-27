package me.drownek.platform.bungee.component;

import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bungee.LightBungeePlugin;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;

import java.util.Arrays;

public class BukkitComponentCreator extends ComponentCreator {

    private final LightBungeePlugin plugin;

    @Inject
    public BukkitComponentCreator(@NonNull LightBungeePlugin plugin, @NonNull ComponentCreatorRegistry creatorRegistry) {
        super(creatorRegistry);
        this.plugin = plugin;
    }

    @Override
    public boolean isComponent(@NonNull Class<?> type) {
        return LightBungeePlugin.class.isAssignableFrom(type) || super.isComponent(type);
    }

    @Override
    public void debug(@NonNull String message) {
        Arrays.stream(message.split("\n")).forEach(line -> this.plugin.debug("- " + line));
    }
}
