package me.drownek.platform.bukkit;

import eu.okaeri.injector.Injector;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.drownek.platform.bukkit.component.BukkitComponentCreator;
import me.drownek.platform.bukkit.component.BukkitCreatorRegistry;
import me.drownek.platform.bukkit.plan.BukkitExternalResourceProviderSetupTask;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.extension.ExtensionRegistry;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.plan.ExecutionPlan;
import me.drownek.platform.core.plan.ExecutionResult;
import me.drownek.platform.core.plan.ExecutionTask;
import me.drownek.platform.core.plan.task.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import static me.drownek.platform.core.plan.ExecutionPhase.*;


/**
 * Base class for Bukkit plugins using the light-platform framework.
 * <p>
 * Extend this class instead of {@link JavaPlugin} to leverage the platform's
 * dependency injection, component system, and execution plan lifecycle.
 * <p>
 * The plugin lifecycle is managed through {@link ExecutionPlan} phases:
 * <ul>
 *   <li>{@code PRE_SETUP} - Injector and core injectables initialization</li>
 *   <li>{@code SETUP} - Component creator and registry setup</li>
 *   <li>{@code POST_SETUP} - Bean manifest creation and execution</li>
 *   <li>{@code SHUTDOWN} - Resource cleanup and component shutdown</li>
 * </ul>
 * <p>
 * Extensions registered via {@link ExtensionRegistry} can contribute to the plan
 * by implementing {@link LightExtension#plan(ExecutionPlan, LightPlatform)}.
 *
 * @see LightPlatform
 * @see ExtensionRegistry
 */
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
        // core pre-setup
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
        });

        // core setup
        plan.add(SETUP, new CreatorSetupTask(BukkitComponentCreator.class, BukkitCreatorRegistry.class));
        plan.add(SETUP, new HookSetupTask());

        // core post-setup
        plan.add(POST_SETUP, new BukkitExternalResourceProviderSetupTask());
        plan.add(POST_SETUP, new BeanManifestCreateTask());
        plan.add(POST_SETUP, new BeanManifestExecuteTask());

        // allow extensions to contribute to the plan
        ExtensionRegistry.getExtensions().forEach(ext -> ext.plan(plan, this));

        // core shutdown
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
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}
