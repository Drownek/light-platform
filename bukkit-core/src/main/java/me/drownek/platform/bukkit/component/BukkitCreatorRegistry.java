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
import me.drownek.platform.core.extension.ExtensionRegistry;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.util.ExtensionsUtil;

/**
 * Bukkit-specific component creator registry that loads built-in extensions
 * and registers platform component resolvers.
 * <p>
 * Built-in extensions are loaded via reflection to support optional modules.
 * Custom extensions can be registered via {@link ExtensionRegistry#register(LightExtension)}.
 *
 * @see ExtensionRegistry
 * @see LightExtension
 */
public class BukkitCreatorRegistry extends ComponentCreatorRegistry {

    @Inject
    public BukkitCreatorRegistry(Injector injector, LightBukkitPlugin plugin) {
        super(injector);

        for (LightExtension ext : ExtensionRegistry.getExtensions()) {
            plugin.debug("Registering extension: " + ext.getClass().getSimpleName());
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
