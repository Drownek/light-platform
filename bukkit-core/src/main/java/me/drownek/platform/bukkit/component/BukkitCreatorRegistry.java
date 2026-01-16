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

import java.util.Arrays;
import java.util.List;

public class BukkitCreatorRegistry extends ComponentCreatorRegistry {

    public static final List<String> EXTENSION_CLASSES = Arrays.asList(
            "me.drownek.platform.bukkit.BukkitConfigsExtension",
            "me.drownek.platform.bukkit.BukkitLitecommandsExtension",
            "me.drownek.platform.bukkit.persistence.BukkitPersistenceExtension"
    );

    @Inject
    public BukkitCreatorRegistry(Injector injector, LightBukkitPlugin plugin) {
        super(injector);

        for (String className : EXTENSION_CLASSES) {
            try {
                Class<?> extClass = Class.forName(className, true, getClass().getClassLoader());
                LightExtension ext = (LightExtension) extClass.getDeclaredConstructor().newInstance();
                plugin.debug("Registering extension: " + className);
                ext.register(this, injector);
            } catch (ClassNotFoundException e) {
                plugin.debug("Extension not available: " + className);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load extension " + className + ": " + e.getMessage());
            }
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
