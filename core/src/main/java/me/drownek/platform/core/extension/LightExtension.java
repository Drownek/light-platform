package me.drownek.platform.core.extension;

import eu.okaeri.injector.Injector;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.plan.ExecutionPlan;

/**
 * Interface for platform extensions that contribute functionality to light-platform.
 * <p>
 * Custom extensions should be registered via {@link ExtensionRegistry#register(LightExtension)}
 * before the platform's SETUP phase.
 * <p>
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li><b>Registration phase</b> ({@link #register}) - Called during SETUP when
 *       {@link ComponentCreatorRegistry} is initialized</li>
 *   <li><b>Planning phase</b> ({@link #plan}) - Called after POST_SETUP to contribute
 *       tasks to the execution plan</li>
 * </ol>
 *
 * @see ExtensionRegistry
 */
public interface LightExtension {

    /**
     * Register additional component resolvers, injectables, or other creator-related pieces.
     * <p>
     * Called during the SETUP phase when the {@link ComponentCreatorRegistry} is being constructed.
     * This is the place to:
     * <ul>
     *   <li>Register custom {@link me.drownek.platform.core.component.creator.ComponentResolver} implementations</li>
     *   <li>Register default configuration providers via {@code injector.registerInjectable()}</li>
     *   <li>Set up serializers or other shared infrastructure</li>
     * </ul>
     *
     * @param registry the component creator registry to register resolvers with
     * @param injector the dependency injector for registering injectables
     */
    default void register(ComponentCreatorRegistry registry, Injector injector) {
        // no-op by default
    }

    /**
     * Contribute tasks to the platform's {@link ExecutionPlan}.
     * <p>
     * Called after POST_SETUP phase completes but before SHUTDOWN tasks are added.
     * This is the place to:
     * <ul>
     *   <li>Add startup tasks (e.g., command registration)</li>
     *   <li>Add shutdown tasks (e.g., resource cleanup, unregistration)</li>
     *   <li>Add custom phase tasks for extension-specific lifecycle needs</li>
     * </ul>
     *
     * @param plan     the execution plan to add tasks to
     * @param platform the platform instance (can be cast to specific type like LightBukkitPlugin)
     */
    default void plan(ExecutionPlan plan, LightPlatform platform) {
        // no-op by default
    }
}
