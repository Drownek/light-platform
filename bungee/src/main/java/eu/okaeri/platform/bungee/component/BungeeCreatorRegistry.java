package eu.okaeri.platform.bungee.component;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.bungee.component.type.DelayedComponentResolver;
import eu.okaeri.platform.bungee.component.type.ListenerComponentResolver;
import eu.okaeri.platform.bungee.component.type.ScheduledComponentResolver;
import eu.okaeri.platform.core.component.creator.ComponentCreatorRegistry;
import eu.okaeri.platform.core.component.type.*;

public class BungeeCreatorRegistry extends ComponentCreatorRegistry {

    @Inject
    public BungeeCreatorRegistry(Injector injector) {
        super(injector);
        // custom first
        this.register(ConfigurationComponentResolver.class);
        this.register(DelayedComponentResolver.class);
        this.register(DocumentCollectionComponentResolver.class);
        this.register(ListenerComponentResolver.class);
        this.register(CommandComponentResolver.class);
        this.register(ScheduledComponentResolver.class);
        // generic last
        this.register(BeanComponentResolver.class);
        this.register(GenericComponentResolver.class);
    }
}
