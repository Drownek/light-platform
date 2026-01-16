package me.drownek.platform.core.extension;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for platform extensions.
 * <p>
 * Custom extensions should call {@link #register(LightExtension)} before
 * the platform's SETUP phase to participate in the extension lifecycle.
 *
 * @see LightExtension
 */
public final class ExtensionRegistry {

    private static final List<LightExtension> REGISTERED = new CopyOnWriteArrayList<>();

    private ExtensionRegistry() {}

    /**
     * Registers an extension. Called by extension classes in their static initializers.
     *
     * @param extension the extension to register
     */
    public static void register(LightExtension extension) {
        REGISTERED.add(extension);
    }

    /**
     * Returns all registered extensions.
     *
     * @return unmodifiable list of registered extensions
     */
    public static List<LightExtension> getExtensions() {
        return List.copyOf(REGISTERED);
    }

    /**
     * Clears all registered extensions. Useful for testing.
     */
    public static void clear() {
        REGISTERED.clear();
    }
}
