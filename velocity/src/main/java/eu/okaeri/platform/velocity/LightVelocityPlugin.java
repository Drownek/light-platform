package eu.okaeri.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.okaeri.configs.serdes.adventure.SerdesAdventure;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeri;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import eu.okaeri.injector.Injector;
import eu.okaeri.persistence.Persistence;
import eu.okaeri.persistence.document.ConfigurerProvider;
import eu.okaeri.platform.velocity.component.VelocityComponentCreator;
import eu.okaeri.platform.velocity.component.VelocityCreatorRegistry;
import eu.okaeri.platform.velocity.plan.CommandSetupTask;
import eu.okaeri.platform.velocity.plan.VelocityCommandsBuildTask;
import eu.okaeri.platform.velocity.plan.VelocityExternalResourceProviderSetupTask;
import eu.okaeri.platform.velocity.plan.VelocitySchedulerShutdownTask;
import eu.okaeri.platform.velocity.scheduler.PlatformScheduler;
import eu.okaeri.tasker.velocity.VelocityTasker;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.dependency.DependencyManager;
import me.drownek.platform.core.plan.ExecutionPlan;
import me.drownek.platform.core.plan.ExecutionResult;
import me.drownek.platform.core.plan.ExecutionTask;
import me.drownek.platform.core.plan.task.*;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.drownek.platform.core.plan.ExecutionPhase.*;


public class LightVelocityPlugin implements LightPlatform {

    private final @Getter File file = ComponentHelper.getJarFile(this.getClass());
    private @Getter @Setter Injector injector;
    private @Getter @Setter ComponentCreator creator;
    private ExecutionPlan plan;

    private @Inject @Getter ProxyServer proxy;
    private @Inject @Getter PluginContainer container;
    private @Inject @Getter @DataDirectory Path dataFolder;
    private @Inject @Getter Logger logger;

    @Override
    public void log(@NonNull String message) {
        this.logger.info(message);
    }

    @Override
    public void plan(@NonNull ExecutionPlan plan) {

        plan.add(PRE_SETUP, new InjectorSetupTask());
        plan.add(PRE_SETUP, (ExecutionTask<LightVelocityPlugin>) platform -> {
            platform.registerInjectable("proxy", this.proxy);
            platform.registerInjectable("dataFolder", this.dataFolder);
            platform.registerInjectable("dataFolder", this.dataFolder.toFile());
            platform.registerInjectable("jarFile", this.getFile());
            platform.registerInjectable("logger", this.logger);
            platform.registerInjectable("plugin", this.container);
            platform.registerInjectable("plugin", platform);
            platform.registerInjectable("scheduler", new PlatformScheduler(this.container, this.proxy.getScheduler()));
            platform.registerInjectable("tasker", VelocityTasker.newPool(this.proxy, this.container));
            platform.registerInjectable("pluginManager", this.proxy.getPluginManager());
            platform.registerInjectable("defaultConfigurerProvider", (ConfigurerProvider) YamlSnakeYamlConfigurer::new);
            platform.registerInjectable("defaultConfigurerSerdes", new Class[]{SerdesCommons.class, SerdesOkaeri.class, SerdesAdventure.class});
        });

        plan.add(PRE_SETUP, new CommandSetupTask(this.proxy));

        plan.add(SETUP, new CreatorSetupTask(VelocityComponentCreator.class, VelocityCreatorRegistry.class), "creator");
        plan.add(SETUP, new HookSetupTask());

        plan.add(POST_SETUP, new VelocityExternalResourceProviderSetupTask());
        plan.add(POST_SETUP, new BeanManifestCreateTask());
        plan.add(POST_SETUP, new BeanManifestExecuteTask());

        plan.add(POST_STARTUP, new VelocityCommandsBuildTask());

        plan.add(SHUTDOWN, new VelocitySchedulerShutdownTask());
        plan.add(SHUTDOWN, new CloseableShutdownTask(Persistence.class));
    }

    @Override
    public Consumer<String> logDebugAction() {
        return this.getLogger()::info;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        DependencyManager dependencyManager = new DependencyManager(this);
        List<String> missingDependencies = dependencyManager.getMissingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new RuntimeException("Missing dependencies: " + String.join(", ", missingDependencies));
        }
        // execute using plan
        ExecutionResult result = ExecutionPlan.dispatch(this);
        this.debug(this.getCreator().getSummaryText(result.getTotalMillis()));
        this.plan = result.getPlan();
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        // call shutdown hooks
        if (this.plan != null) {
            this.plan.execute(Arrays.asList(PRE_SHUTDOWN, SHUTDOWN, POST_SHUTDOWN));
        }
    }

    @Override
    public boolean isPluginEnabled(String pluginName) {
        return getProxy().getPluginManager().isLoaded(pluginName);
    }
}
