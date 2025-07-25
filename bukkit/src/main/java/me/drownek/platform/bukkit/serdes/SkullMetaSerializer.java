package me.drownek.platform.bukkit.serdes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.builder.item.SkullBuilder;
import dev.triumphteam.gui.components.util.SkullUtil;
import dev.triumphteam.gui.components.util.VersionHelper;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class SkullMetaSerializer implements ObjectSerializer<SkullMeta> {

    private static final Field PROFILE_FIELD;
    private static final Gson GSON = new Gson();

    static {
        Field field;
        try {
            SkullMeta skullMeta = (SkullMeta) SkullUtil.skull().getItemMeta();
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            field = null;
        }

        PROFILE_FIELD = field;
    }

    public SkullMetaSerializer() {
    }

    public boolean supports(@NonNull Class<? super SkullMeta> type) {
        return SkullMeta.class.isAssignableFrom(type);
    }

    public void serialize(@NonNull SkullMeta skullMeta, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (skullMeta.hasDisplayName()) {
            data.add("display", this.decolor(skullMeta.getDisplayName()));
        }

        if (skullMeta.hasLore()) {
            data.addCollection("lore", this.decolor(skullMeta.getLore()), String.class);
        }

        if (!skullMeta.getEnchants().isEmpty()) {
            data.addAsMap("enchantments", skullMeta.getEnchants(), Enchantment.class, Integer.class);
        }

        if (!skullMeta.getItemFlags().isEmpty()) {
            data.addCollection("flags", skullMeta.getItemFlags(), ItemFlag.class);
        }

        if (VersionHelper.IS_PLAYER_PROFILE_API) {
            PlayerProfile profile = skullMeta.getOwnerProfile();
            if (profile == null) {
                return;
            }
            URL skin = profile.getTextures().getSkin();
            if (skin == null) {
                return;
            }
            data.add("texture", convertSkinUrlToBase64(skin.toString()));
        } else {
            try {
                GameProfile profile = (GameProfile) PROFILE_FIELD.get(skullMeta);
                if (profile == null) {
                    return;
                }
                Property textures = profile.getProperties().get("textures").stream().findFirst().orElse(null);
                if (textures == null) {
                    return;
                }
                String texture = textures.getValue();
                data.add("texture", texture);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SkullMeta deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        String displayName = data.get("display", String.class);
        if (displayName == null) {
            displayName = data.get("display-name", String.class);
        }

        List<String> lore = data.containsKey("lore") ? data.getAsList("lore", String.class) : Collections.emptyList();
        Map<Enchantment, Integer> enchantments = data.containsKey("enchantments") ? data.getAsMap("enchantments", Enchantment.class, Integer.class) : Collections.emptyMap();
        List<ItemFlag> itemFlags = new ArrayList<>(data.containsKey("flags") ? data.getAsList("flags", ItemFlag.class) : Collections.emptyList());
        if (data.containsKey("item-flags")) {
            itemFlags.addAll(data.getAsList("item-flags", ItemFlag.class));
        }

        String texture = data.get("texture", String.class);
        UUID profileId = data.get("profileId", UUID.class);

        SkullBuilder skull = ItemBuilder.skull();

        if (texture != null) {
            if (profileId != null) {
                skull.texture(texture, profileId);
            } else {
                skull.texture(texture);
            }
        }

        if (displayName != null) {
            skull.setName(this.color(displayName));
        }

        skull.setLore(this.color(lore));

        skull.enchant(enchantments);
        skull.flags(itemFlags.toArray(new ItemFlag[0]));

        return (SkullMeta) skull.build().getItemMeta();
    }

    public static String convertSkinUrlToBase64(String skinUrl) {
        if (skinUrl == null || skinUrl.isEmpty()) {
            return null;
        }

        JsonObject skinObject = new JsonObject();
        skinObject.addProperty("url", skinUrl);

        JsonObject texturesObject = new JsonObject();
        texturesObject.add("SKIN", skinObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("textures", texturesObject);

        String jsonString = GSON.toJson(rootObject);

        return Base64.getEncoder().encodeToString(jsonString.getBytes());
    }

    private List<String> color(List<String> text) {
        return text.stream().map(this::color).collect(Collectors.toList());
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private List<String> decolor(List<String> text) {
        return text.stream().map(this::decolor).collect(Collectors.toList());
    }

    private String decolor(String text) {
        return text.replace("§", "&");
    }
}
