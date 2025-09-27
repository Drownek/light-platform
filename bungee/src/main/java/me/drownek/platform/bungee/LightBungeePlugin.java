package me.drownek.platform.bungee;

import dev.rollczi.litecommands.LiteCommands;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeri;
import eu.okaeri.configs.serdes.okaeri.range.section.SerdesRangeSection;
import eu.okaeri.configs.yaml.bungee.YamlBungeeConfigurer;
import eu.okaeri.injector.Injector;
import eu.okaeri.persistence.Persistence;
import eu.okaeri.persistence.document.ConfigurerProvider;
import eu.okaeri.tasker.bungee.BungeeTasker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.drownek.platform.bungee.commands.CommandSetupTask;
import me.drownek.platform.bungee.component.BukkitComponentCreator;
import me.drownek.platform.bungee.component.BukkitCreatorRegistry;
import me.drownek.platform.bungee.plan.BungeeCommandsBuildTask;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.configs.polymorphic.PolymorphicSerdesPack;
import me.drownek.platform.core.plan.ExecutionPlan;
import me.drownek.platform.core.plan.ExecutionResult;
import me.drownek.platform.core.plan.ExecutionTask;
import me.drownek.platform.core.plan.task.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import static me.drownek.platform.core.plan.ExecutionPhase.*;


public class LightBungeePlugin extends Plugin implements LightPlatform {

    private final @Getter File file = super.getFile();
    private @Getter @Setter Injector injector;
    private @Getter @Setter ComponentCreator creator;
    private ExecutionPlan plan;

    public LightBungeePlugin() {
        super();
    }

    @Override
    public void log(@NonNull String message) {
        this.getLogger().info(message);
    }

    @Override
    public void plan(@NonNull ExecutionPlan plan) {
        plan.add(PRE_SETUP, new InjectorSetupTask());
        plan.add(PRE_SETUP, (ExecutionTask<LightBungeePlugin>) platform -> {
            platform.registerInjectable("proxy", platform.getProxy());
            platform.registerInjectable("dataFolder", platform.getDataFolder());
            platform.registerInjectable("jarFile", platform.getFile());
            platform.registerInjectable("logger", platform.getLogger());
            platform.registerInjectable("plugin", platform);
            platform.registerInjectable("tasker", BungeeTasker.newPool(platform));
            platform.registerInjectable("defaultConfigurerProvider", (ConfigurerProvider) YamlBungeeConfigurer::new);
            platform.registerInjectable(
                "defaultConfigurerSerdes",
                new Class[] {
                    SerdesCommons.class,
                    SerdesOkaeri.class,
                    SerdesRangeSection.class,
                    PolymorphicSerdesPack.class
                }
            );
        });
        plan.add(PRE_SETUP, new CommandSetupTask(this));

        plan.add(SETUP, new CreatorSetupTask(BukkitComponentCreator.class, BukkitCreatorRegistry.class));
        plan.add(SETUP, new HookSetupTask());

        plan.add(POST_SETUP, new BeanManifestCreateTask());
        plan.add(POST_SETUP, new BeanManifestExecuteTask());

        plan.add(POST_STARTUP, new BungeeCommandsBuildTask());

        plan.add(SHUTDOWN, platform -> platform.getInjector().get("commands", LiteCommands.class).ifPresent(LiteCommands::unregister));
        plan.add(SHUTDOWN, new CloseableShutdownTask(Persistence.class));
        plan.add(SHUTDOWN, new CloseableComponentShutdownTask());
    }

    @Override
    public Consumer<String> logDebugAction() {
        return this.getLogger()::info;
    }

    @Override
    @Deprecated
    public void onEnable() {
        // execute using plan
        ExecutionResult result = ExecutionPlan.dispatch(this);
        this.debug(this.getCreator().getSummaryText(result.getTotalMillis()));
        this.plan = result.getPlan();
    }

    @Override
    @Deprecated
    public void onDisable() {
        // call shutdown hooks
        if (this.plan != null) {
            this.plan.execute(Arrays.asList(PRE_SHUTDOWN, SHUTDOWN, POST_SHUTDOWN));
        }
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return ProxyServer.getInstance().getPluginManager().getPlugin(pluginName) != null;
    }
}
