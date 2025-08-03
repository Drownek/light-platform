package me.drownek.example;

import dev.rollczi.litecommands.LiteCommands;
import me.drownek.example.config.Messages;
import me.drownek.example.hook.VaultHookFallback;
import me.drownek.example.hook.VaultHookImpl;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.annotation.Scan;
import me.drownek.platform.core.dependency.Hook;
import me.drownek.platform.core.plan.Planned;
import org.bukkit.command.CommandSender;

import java.util.List;

import static me.drownek.platform.core.plan.ExecutionPhase.POST_STARTUP;
import static me.drownek.platform.core.plan.ExecutionPhase.SHUTDOWN;

@Scan(deep = true, exclusions = "me.drownek.example.libs")
public class ExamplePlugin extends LightBukkitPlugin {

    @Planned(POST_STARTUP)
    void postStartup(
        Messages messages,
        LiteCommands<CommandSender> commands
    ) {
        // Applying customized LiteCommands messages from built-in config that have to be added somewhere to use it
        messages.liteCommandsConfig.apply(commands);
        log("Plugin loaded successfully!");
        // This will display message to the console if me.drownek.platform.core.annotation.DebugLogging annotation will be added to ExamplePlugin
        debug("Test debug");
    }

    @Planned(SHUTDOWN)
    void shutdown() {
        log("Plugin unloaded successfully!");
    }

/*    // Requires specified plugins to be loaded at the moment of enabling out plugin.
    // Great for usage if you really want to use PlugManX to reload plugins,
    // as putting any `depend` will break on PlugManX reload command.
    // But remember to still put `softDepend` in your plugin.yml to make those plugins load before yours!
    @Override
    public List<String> getDependencies() {
        return List.of("ProtocolLib");
    }*/

    // Hook system, allows registering fallback and implementation for given hook interface.
    // But remember to still put your plugin name in plugin.yml under `softDepend` to make those plugins load before yours!
    @Override
    public List<Hook<?>> getHooks() {
        return List.of(
            new Hook<>("Vault", VaultHookImpl.class, VaultHookFallback.class)
        );
    }
}
