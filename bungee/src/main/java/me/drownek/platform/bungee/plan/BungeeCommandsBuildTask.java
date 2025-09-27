package me.drownek.platform.bungee.plan;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import me.drownek.platform.bungee.LightBungeePlugin;
import me.drownek.platform.core.plan.ExecutionTask;
import net.md_5.bungee.api.CommandSender;

public class BungeeCommandsBuildTask implements ExecutionTask<LightBungeePlugin> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(LightBungeePlugin platform) {

        platform.getInjector().get("commandsBuilder", LiteCommandsBuilder.class).ifPresent(liteCommandsBuilder -> {
            LiteCommands<CommandSender> build = liteCommandsBuilder.build();
            platform.getInjector().registerInjectable("commands", build);
            platform.getCreator().debug("Built LiteCommands");
        });
    }
}
