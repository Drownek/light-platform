package eu.okaeri.platform.bukkit.commands;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.platform.core.OkaeriPlatform;
import eu.okaeri.platform.core.plan.ExecutionTask;

public class CommandSetupTask implements ExecutionTask<OkaeriPlatform> {

    @Override
    public void execute(OkaeriPlatform platform) {
        LiteCommandsBuilder builder = LiteBukkitFactory.builder();
        platform.getInjector().registerInjectable("commandsBuilder", builder);
    }
}
