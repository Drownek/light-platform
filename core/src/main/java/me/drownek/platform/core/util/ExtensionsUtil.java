package me.drownek.platform.core.util;

public class ExtensionsUtil {

    private ExtensionsUtil() {}

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
