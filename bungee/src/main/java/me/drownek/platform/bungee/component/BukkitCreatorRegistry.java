package me.drownek.platform.bungee.component;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bungee.component.type.CommandArgumentResolver;
import me.drownek.platform.bungee.component.type.ListenerComponentResolver;
import me.drownek.platform.bungee.component.type.ScheduledComponentResolver;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.component.type.*;

public class BukkitCreatorRegistry extends ComponentCreatorRegistry {

    @Inject
    public BukkitCreatorRegistry(Injector injector) {
        super(injector);
        // custom first
        this.register(ConfigurationComponentResolver.class);
        this.register(DocumentCollectionComponentResolver.class);
        this.register(ListenerComponentResolver.class);
        this.register(CommandArgumentResolver.class);
        this.register(CommandComponentResolver.class);
        this.register(ScheduledComponentResolver.class);
        // generic last
        this.register(BeanComponentResolver.class);
        this.register(GenericComponentResolver.class);
    }
}
