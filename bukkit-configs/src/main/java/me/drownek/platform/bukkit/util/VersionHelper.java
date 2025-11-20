package me.drownek.platform.bukkit.util;

import org.bukkit.inventory.meta.SkullMeta;

/**
 * Utility class to check for version-specific API availability.
 */
public final class VersionHelper {

    /**
     * Indicates whether the PlayerProfile API is available.
     * This API was introduced in newer Minecraft versions.
     */
    public static final boolean IS_PLAYER_PROFILE_API;

    static {
        boolean hasPlayerProfileApi;
        try {
            // Check if the getOwnerProfile method exists in SkullMeta
            SkullMeta.class.getMethod("getOwnerProfile");
            hasPlayerProfileApi = true;
        } catch (NoSuchMethodException e) {
            // PlayerProfile API is not available in this version
            hasPlayerProfileApi = false;
        }
        IS_PLAYER_PROFILE_API = hasPlayerProfileApi;
    }

    private VersionHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

