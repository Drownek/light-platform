package me.drownek.platform.bukkit.component;

import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;

import java.util.Arrays;

public class BukkitComponentCreator extends ComponentCreator {

    private final LightBukkitPlugin plugin;

    @Inject
    public BukkitComponentCreator(@NonNull LightBukkitPlugin plugin, @NonNull ComponentCreatorRegistry creatorRegistry) {
        super(creatorRegistry);
        this.plugin = plugin;
    }

    @Override
    public boolean isComponent(@NonNull Class<?> type) {
        return LightBukkitPlugin.class.isAssignableFrom(type) || super.isComponent(type);
    }

    @Override
    public void debug(@NonNull String message) {
        Arrays.stream(message.split("\n")).forEach(line -> this.plugin.log("- " + line));
    }
}
