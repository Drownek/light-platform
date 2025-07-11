package eu.okaeri.platform.velocity.component;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.velocity.component.type.DelayedComponentResolver;
import eu.okaeri.platform.velocity.component.type.ScheduledComponentResolver;
import eu.okaeri.platform.velocity.component.type.listener.ListenerComponentResolver;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.component.type.*;

public class VelocityCreatorRegistry extends ComponentCreatorRegistry {

    @Inject
    public VelocityCreatorRegistry(Injector injector) {
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
