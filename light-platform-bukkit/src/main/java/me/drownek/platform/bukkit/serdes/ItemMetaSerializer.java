package me.drownek.platform.bukkit.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemMetaSerializer implements ObjectSerializer<ItemMeta> {
    private static final char COLOR_CHAR = '§';
    private static final char ALT_COLOR_CHAR = '&';

    private static final Method HAS_CUSTOM_MODEL_DATA_METHOD;
    private static final Method GET_CUSTOM_MODEL_DATA_METHOD;
    private static final Method SET_CUSTOM_MODEL_DATA_METHOD;

    static {
        Method hasCustomModelData = null;
        Method getCustomModelData = null;
        Method setCustomModelData = null;

        try {
            hasCustomModelData = ItemMeta.class.getMethod("hasCustomModelData");
            getCustomModelData = ItemMeta.class.getMethod("getCustomModelData");
            setCustomModelData = ItemMeta.class.getMethod("setCustomModelData", Integer.class);
        } catch (NoSuchMethodException ignored) {
        }

        HAS_CUSTOM_MODEL_DATA_METHOD = hasCustomModelData;
        GET_CUSTOM_MODEL_DATA_METHOD = getCustomModelData;
        SET_CUSTOM_MODEL_DATA_METHOD = setCustomModelData;
    }


    public ItemMetaSerializer() {
    }

    public boolean supports(@NonNull Class<? super ItemMeta> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    public void serialize(@NonNull ItemMeta itemMeta, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            data.add("display", this.decolor(itemMeta.getDisplayName()));
        }

        if (itemMeta.hasLore()) {
            data.addCollection("lore", this.decolor(itemMeta.getLore()), String.class);
        }

        if (!itemMeta.getEnchants().isEmpty()) {
            data.addAsMap("enchantments", itemMeta.getEnchants(), Enchantment.class, Integer.class);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            data.addCollection("flags", itemMeta.getItemFlags(), ItemFlag.class);
        }

        if (hasCustomModelDataSupport() && hasCustomModelData(itemMeta)) {
            Integer customModelData = getCustomModelData(itemMeta);
            if (customModelData != null) {
                data.add("customModelData", customModelData);
            }
        }
    }

    public ItemMeta deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
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

        ItemMeta itemMeta = (new ItemStack(Material.COBBLESTONE)).getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("Cannot extract empty ItemMeta from COBBLESTONE");
        } else {
            if (displayName != null) {
                itemMeta.setDisplayName(this.color(displayName));
            }

            itemMeta.setLore(this.color(lore));
            enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            if (data.containsKey("customModelData")) {
                itemMeta.setCustomModelData(data.get("customModelData", int.class));
            }
            return itemMeta;
        }
    }

    private boolean hasCustomModelDataSupport() {
        return HAS_CUSTOM_MODEL_DATA_METHOD != null &&
            GET_CUSTOM_MODEL_DATA_METHOD != null &&
            SET_CUSTOM_MODEL_DATA_METHOD != null;
    }

    private boolean hasCustomModelData(ItemMeta itemMeta) {
        if (!hasCustomModelDataSupport()) {
            return false;
        }

        try {
            return (Boolean) HAS_CUSTOM_MODEL_DATA_METHOD.invoke(itemMeta);
        } catch (Exception e) {
            return false;
        }
    }

    private Integer getCustomModelData(ItemMeta itemMeta) {
        if (!hasCustomModelDataSupport()) {
            return null;
        }

        try {
            return (Integer) GET_CUSTOM_MODEL_DATA_METHOD.invoke(itemMeta);
        } catch (Exception e) {
            return null;
        }
    }

    private void setCustomModelData(ItemMeta itemMeta, int customModelData) {
        if (!hasCustomModelDataSupport()) {
            return;
        }

        try {
            SET_CUSTOM_MODEL_DATA_METHOD.invoke(itemMeta, customModelData);
        } catch (Exception ignored) {
        }
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
