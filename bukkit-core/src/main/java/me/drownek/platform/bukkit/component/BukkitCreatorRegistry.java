package me.drownek.platform.bukkit.component;

import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.bukkit.component.type.DelayedComponentResolver;
import me.drownek.platform.bukkit.component.type.ListenerComponentResolver;
import me.drownek.platform.bukkit.component.type.ScheduledComponentResolver;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.component.type.BeanComponentResolver;
import me.drownek.platform.core.component.type.DocumentCollectionComponentResolver;
import me.drownek.platform.core.component.type.GenericComponentResolver;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.util.ExtensionsUtil;

import java.util.ServiceLoader;

public class BukkitCreatorRegistry extends ComponentCreatorRegistry {

    @Inject
    public BukkitCreatorRegistry(Injector injector, LightBukkitPlugin plugin) {
        super(injector);

        ServiceLoader<LightExtension> loader =
                ServiceLoader.load(LightExtension.class, getClass().getClassLoader());

        plugin.debug("Loading " + loader.stream().toList().size() + " extensions...");
        for (LightExtension ext : loader) {
            plugin.debug("Registering extension: " + ext.getClass().getName());
            ext.register(this, injector);
        }

        // custom first
        this.register(DelayedComponentResolver.class);
        if (ExtensionsUtil.isBukkitPersistencePresent()) {
            this.register(DocumentCollectionComponentResolver.class);
        }
        this.register(ListenerComponentResolver.class);
        this.register(ScheduledComponentResolver.class);
        // generic last
        this.register(BeanComponentResolver.class);
        this.register(GenericComponentResolver.class);
    }
}
