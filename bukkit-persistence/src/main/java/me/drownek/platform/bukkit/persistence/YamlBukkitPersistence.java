package me.drownek.platform.bukkit.persistence;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.flat.FlatPersistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YamlBukkitPersistence {

    public static DocumentPersistence of(@NonNull File storage, @NonNull OkaeriSerdesPack... okaeriSerdesPack) {
        return new DocumentPersistence(new FlatPersistence(storage, ".yml"), YamlBukkitConfigurer::new, okaeriSerdesPack);
    }

    public static DocumentPersistence of(@NonNull Plugin plugin) {
        return of(new File(plugin.getDataFolder(), "storage"));
    }
}
