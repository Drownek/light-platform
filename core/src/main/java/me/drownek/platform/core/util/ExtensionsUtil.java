package me.drownek.platform.core.util;

import me.drownek.platform.core.extension.ExtensionRegistry;
import me.drownek.platform.core.extension.LightExtension;

public class ExtensionsUtil {

    private ExtensionsUtil() {}

    public static void tryRegisterExtension(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            LightExtension extension = (LightExtension) clazz.getDeclaredConstructor().newInstance();
            ExtensionRegistry.register(extension);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Extension not available
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate extension: " + className, e);
        }
    }

    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isBukkitConfigsPresent() {
        return isClassPresent("me.drownek.platform.bukkit.serdes.ItemStackSerializer");
    }

    public static boolean isBukkitPersistencePresent() {
        return isClassPresent("me.drownek.platform.bukkit.persistence.YamlBukkitPersistence");
    }

    public static boolean isBukkitUtilsPresent() {
        return isClassPresent("me.drownek.util.BukkitUtilsSerdes");
    }

    public static boolean isLiteCommandsPresent() {
        return isClassPresent("me.drownek.platform.bukkit.commands.CommandSetupTask");
    }
}
