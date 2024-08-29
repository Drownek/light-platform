package eu.okaeri.platform.bukkit.plan;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import eu.okaeri.i18n.configs.extended.CustomMEOCI18n;
import eu.okaeri.platform.bukkit.OkaeriBukkitPlugin;
import eu.okaeri.platform.core.component.creator.ComponentCreator;
import eu.okaeri.platform.core.component.manifest.BeanManifest;
import eu.okaeri.platform.core.plan.ExecutionTask;
import eu.okaeri.platform.minecraft.commands.I18nCommandsMessages;
import org.bukkit.command.CommandSender;

public class BukkitCommandsBuildTask implements ExecutionTask<OkaeriBukkitPlugin> {

    @Override
    public void execute(OkaeriBukkitPlugin platform) {
       platform.getInjector().get("commandsBuilder", LiteCommandsBuilder.class).ifPresent(liteCommandsBuilder -> {
           LiteCommands<CommandSender> build = liteCommandsBuilder.build();
           platform.getInjector().registerInjectable("commands", build);
           platform.getLogger().info("Builded commands");
       });
    }
}
