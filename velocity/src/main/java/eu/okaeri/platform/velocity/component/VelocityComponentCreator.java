package eu.okaeri.platform.velocity.component;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.velocity.LightVelocityPlugin;
import lombok.NonNull;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;

import java.util.Arrays;

public class VelocityComponentCreator extends ComponentCreator {

    private final LightVelocityPlugin plugin;

    @Inject
    public VelocityComponentCreator(@NonNull LightVelocityPlugin plugin, @NonNull ComponentCreatorRegistry creatorRegistry) {
        super(creatorRegistry);
        this.plugin = plugin;
    }

    @Override
    public boolean isComponent(@NonNull Class<?> type) {
        return LightVelocityPlugin.class.isAssignableFrom(type) || super.isComponent(type);
    }

    @Override
    public void log(@NonNull String message) {
        Arrays.stream(message.split("\n")).forEach(line -> this.plugin.log("- " + line));
    }
}
