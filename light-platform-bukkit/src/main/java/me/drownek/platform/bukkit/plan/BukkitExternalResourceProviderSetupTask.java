package me.drownek.platform.bukkit.plan;

import eu.okaeri.injector.Injector;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.component.ExternalResourceProvider;
import me.drownek.platform.core.exception.BreakException;
import me.drownek.platform.core.plan.ExecutionTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class BukkitExternalResourceProviderSetupTask implements ExecutionTask<LightBukkitPlugin> {

    @SuppressWarnings("unchecked") private static final ExternalResourceProvider EXTERNAL_RESOURCE_PROVIDER = (name, type, source) -> {

        Class<? extends JavaPlugin> sourcePlugin = (Class<? extends JavaPlugin>) source;
        JavaPlugin plugin = JavaPlugin.getPlugin(sourcePlugin);

        if (plugin == null) {
            throw new BreakException("Cannot provide external resource: " + name + ", " + type + " from " + source + ": cannot find source");
        }

        Injector externalInjector = ((LightBukkitPlugin) plugin).getInjector();
        Optional<?> injectable = externalInjector.get(name, type);

        if (!injectable.isPresent()) {
            throw new BreakException("Cannot provide external resource: " + name + ", " + type + " from " + source + ": cannot find injectable");
        }

        return injectable.get();
    };

    @Override
    public void execute(LightBukkitPlugin platform) {

        if (platform.getInjector().get("externalResourceProvider", ExternalResourceProvider.class).isPresent()) {
            return;
        }

        platform.registerInjectable("externalResourceProvider", EXTERNAL_RESOURCE_PROVIDER);
    }
}
