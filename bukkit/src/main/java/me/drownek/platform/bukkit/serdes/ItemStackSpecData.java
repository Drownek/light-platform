package me.drownek.platform.bukkit.serdes;

import eu.okaeri.configs.serdes.SerdesContextAttachment;
import lombok.Value;

@Value(staticConstructor = "of")
public class ItemStackSpecData implements SerdesContextAttachment {
    ItemStackFormat format;
    boolean failsafe;
}
