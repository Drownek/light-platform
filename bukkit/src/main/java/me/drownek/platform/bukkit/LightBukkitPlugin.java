package me.drownek.platform.bukkit;

import dev.rollczi.litecommands.LiteCommands;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeri;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeriBukkit;
import eu.okaeri.configs.serdes.okaeri.range.section.SerdesRangeSection;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.injector.Injector;
import eu.okaeri.persistence.document.ConfigurerProvider;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.drownek.platform.bukkit.commands.CommandSetupTask;
import me.drownek.platform.bukkit.component.BukkitComponentCreator;
import me.drownek.platform.bukkit.component.BukkitCreatorRegistry;
import me.drownek.platform.bukkit.plan.BukkitCommandsBuildTask;
import me.drownek.platform.bukkit.plan.BukkitExternalResourceProviderSetupTask;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.bukkit.serdes.SerdesBukkit;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.plan.ExecutionPlan;
import me.drownek.platform.core.plan.ExecutionResult;
import me.drownek.platform.core.plan.ExecutionTask;
import me.drownek.platform.core.plan.task.BeanManifestCreateTask;
import me.drownek.platform.core.plan.task.BeanManifestExecuteTask;
import me.drownek.platform.core.plan.task.CreatorSetupTask;
import me.drownek.platform.core.plan.task.InjectorSetupTask;
import me.drownek.util.BukkitUtilsSerdes;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import static me.drownek.platform.core.plan.ExecutionPhase.*;


public class LightBukkitPlugin extends JavaPlugin implements LightPlatform {

    private final @Getter File file = super.getFile();
    private @Getter @Setter Injector injector;
    private @Getter @Setter ComponentCreator creator;
    private ExecutionPlan plan;

    public LightBukkitPlugin() {
        super();
    }

    protected LightBukkitPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void log(@NonNull String message) {
        this.getLogger().info(message);
    }

    @Override
    public void plan(@NonNull ExecutionPlan plan) {
        plan.add(PRE_SETUP, new InjectorSetupTask());
        plan.add(PRE_SETUP, (ExecutionTask<LightBukkitPlugin>) platform -> {
            platform.registerInjectable("server", platform.getServer());
            platform.registerInjectable("dataFolder", platform.getDataFolder());
            platform.registerInjectable("jarFile", platform.getFile());
            platform.registerInjectable("logger", platform.getLogger());
            platform.registerInjectable("plugin", platform);
            platform.registerInjectable("scheduler", new PlatformScheduler(platform, platform.getServer().getScheduler()));
            platform.registerInjectable("tasker", BukkitTasker.newPool(platform));
            platform.registerInjectable("pluginManager", platform.getServer().getPluginManager());
            platform.registerInjectable("defaultConfigurerProvider", (ConfigurerProvider) YamlBukkitConfigurer::new);
            platform.registerInjectable("defaultConfigurerSerdes", new Class[]{SerdesCommons.class, SerdesOkaeri.class, SerdesBukkit.class, SerdesOkaeriBukkit.class, SerdesRangeSection.class, BukkitUtilsSerdes.class});
        });
        plan.add(PRE_SETUP, new CommandSetupTask());

        plan.add(SETUP, new CreatorSetupTask(BukkitComponentCreator.class, BukkitCreatorRegistry.class));

        plan.add(POST_SETUP, new BukkitExternalResourceProviderSetupTask());
        plan.add(POST_SETUP, new BeanManifestCreateTask());
        plan.add(POST_SETUP, new BeanManifestExecuteTask());

        plan.add(POST_STARTUP, new BukkitCommandsBuildTask());

        plan.add(SHUTDOWN, platform -> platform.getInjector().get("commands", LiteCommands.class).ifPresent(LiteCommands::unregister));
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
}
