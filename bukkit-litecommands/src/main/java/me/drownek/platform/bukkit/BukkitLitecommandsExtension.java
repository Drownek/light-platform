package me.drownek.platform.bukkit;

import dev.rollczi.litecommands.LiteCommands;
import eu.okaeri.injector.Injector;
import me.drownek.platform.bukkit.commands.CommandSetupTask;
import me.drownek.platform.bukkit.component.type.CommandArgumentResolver;
import me.drownek.platform.bukkit.plan.BukkitCommandsBuildTask;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.component.type.CommandComponentResolver;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.plan.ExecutionPlan;
import org.bukkit.plugin.Plugin;

import static me.drownek.platform.core.plan.ExecutionPhase.*;

public class BukkitLitecommandsExtension implements LightExtension {

    @Override
    public void register(ComponentCreatorRegistry registry, Injector injector) {
        registry.register(CommandArgumentResolver.class);
        registry.register(CommandComponentResolver.class);
    }

    @Override
    public void plan(ExecutionPlan plan, LightPlatform platform) {
        if (!(platform instanceof Plugin plugin)) {
            return;
        }

        plan.add(PRE_SETUP, new CommandSetupTask(plugin));
        plan.add(POST_STARTUP, new BukkitCommandsBuildTask());
        plan.add(SHUTDOWN, p -> p.getInjector().get("commands", LiteCommands.class).ifPresent(LiteCommands::unregister));
    }
}
