package me.drownek.platform.bukkit.plan;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.plan.ExecutionTask;
import org.bukkit.command.CommandSender;

public class BukkitCommandsBuildTask implements ExecutionTask<LightBukkitPlugin> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(LightBukkitPlugin platform) {

        platform.getInjector().get("commandsBuilder", LiteCommandsBuilder.class).ifPresent(liteCommandsBuilder -> {
            LiteCommands<CommandSender> build = liteCommandsBuilder.build();
            platform.getInjector().registerInjectable("commands", build);
            platform.getCreator().debug("Built LiteCommands");
        });
    }
}
