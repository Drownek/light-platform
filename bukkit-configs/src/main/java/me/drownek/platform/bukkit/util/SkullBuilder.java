package me.drownek.platform.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Builder class for creating skull items with custom textures, names, lore, enchantments, and flags.
 */
public class SkullBuilder {

    private static final Field PROFILE_FIELD;

    static {
        try {
            SkullMeta skullMeta = (SkullMeta) SkullUtil.skull().getItemMeta();
            if (skullMeta == null) {
                throw new ExceptionInInitializerError("SkullMeta is null, cannot resolve profile field.");
            }
            PROFILE_FIELD = skullMeta.getClass().getDeclaredField("profile");
            PROFILE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final ItemStack itemStack;
    private final SkullMeta skullMeta;

    /**
     * Creates a new SkullBuilder instance.
     */
    public SkullBuilder() {
        this.itemStack = SkullUtil.skull();
        this.skullMeta = (SkullMeta) itemStack.getItemMeta();
        if (this.skullMeta == null) {
            throw new IllegalStateException("SkullMeta cannot be null");
        }
    }

    /**
     * Sets the texture of the skull using a base64 encoded texture string.
     *
     * @param texture the base64 encoded texture string
     * @return this builder instance
     */
    public SkullBuilder texture(String texture) {
        return texture(texture, UUID.randomUUID());
    }

    /**
     * Sets the texture of the skull using a base64 encoded texture string and a profile UUID.
     *
     * @param texture   the base64 encoded texture string
     * @param profileId the UUID for the game profile
     * @return this builder instance
     */
    public SkullBuilder texture(String texture, UUID profileId) {
        if (texture == null || texture.isEmpty()) {
            return this;
        }

        try {
            GameProfile profile = new GameProfile(profileId, null);
            profile.getProperties().put("textures", new Property("textures", texture));
            PROFILE_FIELD.set(skullMeta, profile);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set skull texture", e);
        }

        return this;
    }

    /**
     * Sets the display name of the skull.
     *
     * @param name the display name
     * @return this builder instance
     */
    public SkullBuilder setName(String name) {
        if (name != null) {
            skullMeta.setDisplayName(name);
        }
        return this;
    }

    /**
     * Sets the lore of the skull.
     *
     * @param lore the lore lines
     * @return this builder instance
     */
    public SkullBuilder setLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            skullMeta.setLore(lore);
        }
        return this;
    }

    /**
     * Adds enchantments to the skull.
     *
     * @param enchantments a map of enchantments and their levels
     * @return this builder instance
     */
    public SkullBuilder enchant(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null && !enchantments.isEmpty()) {
            enchantments.forEach((enchantment, level) -> 
                skullMeta.addEnchant(enchantment, level, true)
            );
        }
        return this;
    }

    /**
     * Adds item flags to the skull.
     *
     * @param flags the item flags to add
     * @return this builder instance
     */
    public SkullBuilder flags(ItemFlag... flags) {
        if (flags != null && flags.length > 0) {
            skullMeta.addItemFlags(flags);
        }
        return this;
    }

    /**
     * Builds and returns the final ItemStack.
     *
     * @return the built ItemStack with the configured SkullMeta
     */
    public ItemStack build() {
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}

