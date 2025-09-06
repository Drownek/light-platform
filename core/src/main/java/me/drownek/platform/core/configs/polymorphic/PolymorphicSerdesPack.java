package me.drownek.platform.core.configs.polymorphic;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import lombok.NonNull;

/**
 * Serdes pack that provides polymorphic serialization support.
 */
public class PolymorphicSerdesPack implements OkaeriSerdesPack {
    
    @Override
    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new PolymorphicSerializer());
    }
}