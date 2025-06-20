package me.drownek.platform.bukkit.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackFormat;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackSpecData;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.experimental.StringBase64ItemStackTransformer;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {

    private static final ItemMetaSerializer ITEM_META_SERIALIZER = new ItemMetaSerializer();
    private static final SkullMetaSerializer SKULL_META_SERIALIZER = new SkullMetaSerializer();
    private static final StringBase64ItemStackTransformer ITEM_STACK_TRANSFORMER = new StringBase64ItemStackTransformer();
    private boolean failsafe = false;

    public boolean supports(@NonNull Class<? super ItemStack> type) {
        return ItemStack.class.isAssignableFrom(type);
    }

    @SneakyThrows
    public void serialize(@NonNull ItemStack itemStack, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        data.add("material", itemStack.getType());
        if (itemStack.getAmount() != 1) {
            data.add("amount", itemStack.getAmount());
        }

        if (itemStack.getDurability() != 0) {
            data.add("durability", itemStack.getDurability());
        }

        ItemStackFormat format = data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
                SKULL_META_SERIALIZER.serialize(skullMeta, data, generics);
            } else {
                switch (format) {
                    case NATURAL:
                        data.add("meta", itemStack.getItemMeta(), ItemMeta.class);
                        break;
                    case COMPACT:
                        ITEM_META_SERIALIZER.serialize(itemStack.getItemMeta(), data, generics);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown format: " + format);
                }
            }

            if (this.failsafe) {
                DeserializationData deserializationData = new DeserializationData(data.asMap(), data.getConfigurer(), data.getContext());
                ItemStack deserializedStack = this.deserialize(deserializationData, generics);
                if (!itemStack.equals(deserializedStack)) {
                    data.clear();
                    String base64Stack = ITEM_STACK_TRANSFORMER.leftToRight(itemStack, data.getContext());
                    data.add("base64", base64Stack);
                }
            }
        }
    }

    public ItemStack deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        String materialName;
        if (data.containsKey("base64")) {
            materialName = data.get("base64", String.class);
            return ITEM_STACK_TRANSFORMER.rightToLeft(materialName, data.getContext());
        } else {
            materialName = data.get("material", String.class);
            Material material = Material.valueOf(materialName);
            int amount = data.containsKey("amount") ? data.get("amount", Integer.class) : 1;
            short durability = data.containsKey("durability") ? data.get("durability", Short.class) : 0;
            String texture = data.get("texture", String.class);
            ItemStackFormat format = data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
            ItemMeta itemMeta;

            if (texture != null) {
                itemMeta = SKULL_META_SERIALIZER.deserialize(data, generics);
            } else {
                switch (format) {
                    case NATURAL:
                        if (!data.containsKey("display") && !data.containsKey("display-name")) {
                            itemMeta = data.containsKey("meta") ? data.get("meta", ItemMeta.class) : data.get("item-meta", ItemMeta.class);
                        } else {
                            itemMeta = ITEM_META_SERIALIZER.deserialize(data, generics);
                        }
                        break;
                    case COMPACT:
                        if (data.containsKey("meta")) {
                            itemMeta = data.get("meta", ItemMeta.class);
                        } else if (data.containsKey("item-meta")) {
                            itemMeta = data.get("item-meta", ItemMeta.class);
                        } else {
                            itemMeta = ITEM_META_SERIALIZER.deserialize(data, generics);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown format: " + format);
                }
            }

            ItemStack itemStack = new ItemStack(material, amount);
            itemStack.setItemMeta(itemMeta);
            if (durability != 0) {
                itemStack.setDurability(durability);
            }

            return itemStack;
        }
    }

    public ItemStackSerializer() {
    }

    public ItemStackSerializer(final boolean failsafe) {
        this.failsafe = failsafe;
    }
}
