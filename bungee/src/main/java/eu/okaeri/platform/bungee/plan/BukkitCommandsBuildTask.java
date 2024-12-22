package eu.okaeri.platform.bungee.plan;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import eu.okaeri.platform.bungee.OkaeriBungeePlugin;
import eu.okaeri.platform.core.plan.ExecutionTask;
import net.md_5.bungee.api.CommandSender;

public class BukkitCommandsBuildTask implements ExecutionTask<OkaeriBungeePlugin> {

    @Override
    public void execute(OkaeriBungeePlugin platform) {
       platform.getInjector().get("commandsBuilder", LiteCommandsBuilder.class).ifPresent(liteCommandsBuilder -> {
           LiteCommands<CommandSender> build = liteCommandsBuilder.build();
           platform.getInjector().registerInjectable("commands", build);
           platform.getLogger().info("Builded commands");
       });
    }
}
