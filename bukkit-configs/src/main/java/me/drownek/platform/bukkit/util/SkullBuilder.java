package me.drownek.platform.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SkullBuilder {

    private static final TextureApplier TEXTURE_APPLIER = initApplier();

    private static TextureApplier initApplier() {
        // Try Paper API first
        try {
            //noinspection JavaReflectionMemberAccess
            Method createProfile = Bukkit.class.getMethod("createProfile", UUID.class, String.class);
            Method setPlayerProfile = findMethod(SkullMeta.class, "setPlayerProfile", 1);

            if (setPlayerProfile != null) {
                Class<?> profileClass = setPlayerProfile.getParameterTypes()[0];
                Method setProperty = findMethod(profileClass, "setProperty", 1);

                if (setProperty != null) {
                    Class<?> propClass = setProperty.getParameterTypes()[0];
                    Constructor<?> propCtor = propClass.getConstructor(String.class, String.class);
                    return new PaperApplier(createProfile, setPlayerProfile, setProperty, propCtor);
                }
            }
        } catch (Exception ignored) {}

        // Fallback to direct field reflection
        try {
            SkullMeta meta = (SkullMeta) SkullUtil.skull().getItemMeta();
            Field field = Objects.requireNonNull(meta).getClass().getDeclaredField("profile");
            field.setAccessible(true);
            return new LegacyApplier(field);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Method findMethod(Class<?> clazz, String name, int paramCount) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                return m;
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────

    private interface TextureApplier {
        void apply(SkullMeta meta, String texture, UUID id) throws Exception;
    }

    private static class PaperApplier implements TextureApplier {
        private final Method createProfile, setPlayerProfile, setProperty;
        private final Constructor<?> propertyCtor;

        PaperApplier(Method createProfile, Method setPlayerProfile,
                     Method setProperty, Constructor<?> propertyCtor) {
            this.createProfile = createProfile;
            this.setPlayerProfile = setPlayerProfile;
            this.setProperty = setProperty;
            this.propertyCtor = propertyCtor;
        }

        @Override
        public void apply(SkullMeta meta, String texture, UUID id) throws Exception {
            Object profile = createProfile.invoke(null, id, "");
            Object property = propertyCtor.newInstance("textures", texture);
            setProperty.invoke(profile, property);
            setPlayerProfile.invoke(meta, profile);
        }
    }

    private static class LegacyApplier implements TextureApplier {
        private final Field profileField;

        LegacyApplier(Field profileField) {
            this.profileField = profileField;
        }

        @Override
        public void apply(SkullMeta meta, String texture, UUID id) throws Exception {
            GameProfile profile = new GameProfile(id, "");
            profile.getProperties().put("textures", new Property("textures", texture));
            profileField.set(meta, profile);
        }
    }

    // ─────────────────────────────────────────────────────────────

    private final ItemStack itemStack;
    private final SkullMeta skullMeta;

    public SkullBuilder() {
        this.itemStack = SkullUtil.skull();
        this.skullMeta = (SkullMeta) itemStack.getItemMeta();
        if (this.skullMeta == null) {
            throw new IllegalStateException("SkullMeta cannot be null");
        }
    }

    public SkullBuilder texture(String texture) {
        return texture(texture, UUID.randomUUID());
    }

    public SkullBuilder texture(String texture, UUID profileId) {
        if (texture == null || texture.isEmpty()) return this;
        try {
            TEXTURE_APPLIER.apply(skullMeta, texture, profileId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set skull texture", e);
        }
        return this;
    }

    public SkullBuilder setName(String name) {
        if (name != null) skullMeta.setDisplayName(name);
        return this;
    }

    public SkullBuilder setLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) skullMeta.setLore(lore);
        return this;
    }

    public SkullBuilder enchant(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null) {
            enchantments.forEach((ench, lvl) -> skullMeta.addEnchant(ench, lvl, true));
        }
        return this;
    }

    public SkullBuilder flags(ItemFlag... flags) {
        if (flags != null && flags.length > 0) skullMeta.addItemFlags(flags);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}