package me.drownek.platform.bukkit.serdes;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackAttachmentResolver;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.LocationSerializer;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.PotionEffectSerializer;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.VectorSerializer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringEnchantmentTransformer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringPotionEffectTypeTransformer;
import eu.okaeri.configs.yaml.bukkit.serdes.transformer.StringWorldTransformer;
import lombok.NonNull;

public class SerdesBukkit implements OkaeriSerdesPack {
    public SerdesBukkit() {
    }

    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new ItemMetaSerializer());
        registry.register(new ItemStackSerializer(false));
        registry.register(new ItemStackAttachmentResolver());
        registry.register(new LocationSerializer());
        registry.register(new PotionEffectSerializer());
        registry.register(new VectorSerializer());
        registry.register(new StringEnchantmentTransformer());
        registry.register(new StringPotionEffectTypeTransformer());
        registry.register(new StringWorldTransformer());
    }
}
