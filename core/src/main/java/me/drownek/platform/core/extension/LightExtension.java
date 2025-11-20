package me.drownek.platform.core.extension;

import eu.okaeri.injector.Injector;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.plan.ExecutionPlan;

public interface LightExtension {

    /**
     * Register additional component resolvers or other creator-related pieces.
     */
    default void register(ComponentCreatorRegistry registry, Injector injector) {
        // no-op by default
    }

    /**
     * Allow this extension to contribute tasks to the platform {@link ExecutionPlan}.
     * Default implementation is a no-op.
     */
    default void plan(ExecutionPlan plan, LightPlatform platform) {
        // no-op by default
    }
}
