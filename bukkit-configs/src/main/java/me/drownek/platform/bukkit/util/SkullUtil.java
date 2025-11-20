package me.drownek.platform.bukkit.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for creating skull items.
 */
public final class SkullUtil {

    private static final Material SKULL_MATERIAL;

    static {
        Material skullMaterial;
        try {
            // Try to get PLAYER_HEAD (1.13+)
            skullMaterial = Material.valueOf("PLAYER_HEAD");
        } catch (IllegalArgumentException e) {
            try {
                // Fall back to SKULL_ITEM (1.8-1.12)
                skullMaterial = Material.valueOf("SKULL_ITEM");
            } catch (IllegalArgumentException ex) {
                throw new ExceptionInInitializerError("Could not find skull material");
            }
        }
        SKULL_MATERIAL = skullMaterial;
    }

    private SkullUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a new skull ItemStack.
     * For 1.8-1.12, creates a SKULL_ITEM with durability 3 (player skull).
     * For 1.13+, creates a PLAYER_HEAD.
     *
     * @return a new skull ItemStack
     */
    @SuppressWarnings("deprecation")
    public static ItemStack skull() {
        ItemStack skull = new ItemStack(SKULL_MATERIAL);
        // For 1.8-1.12, set durability to 3 for player skull
        if (SKULL_MATERIAL.name().equals("SKULL_ITEM")) {
            skull.setDurability((short) 3);
        }
        return skull;
    }
}

