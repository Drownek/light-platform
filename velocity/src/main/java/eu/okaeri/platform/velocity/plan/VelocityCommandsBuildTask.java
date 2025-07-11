package eu.okaeri.platform.velocity.plan;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import eu.okaeri.platform.velocity.LightVelocityPlugin;
import me.drownek.platform.core.plan.ExecutionTask;

public class VelocityCommandsBuildTask implements ExecutionTask<LightVelocityPlugin> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(LightVelocityPlugin platform) {
        boolean showRegisteredComponents = platform.getInjector().getOrThrow("showRegisteredComponents", Boolean.class);

        platform.getInjector().get("commandsBuilder", LiteCommandsBuilder.class).ifPresent(liteCommandsBuilder -> {
            LiteCommands<CommandSource> build = liteCommandsBuilder.build();
            platform.getInjector().registerInjectable("commands", build);
            if (showRegisteredComponents) {
                platform.getCreator().log("Built LiteCommands");
            }
        });
    }
}
