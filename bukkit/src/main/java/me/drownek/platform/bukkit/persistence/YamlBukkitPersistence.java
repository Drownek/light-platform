package me.drownek.platform.bukkit.persistence;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.flat.FlatPersistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.drownek.platform.bukkit.serdes.SerdesBukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YamlBukkitPersistence {

    public static DocumentPersistence of(@NonNull File storage) {
        return new DocumentPersistence(new FlatPersistence(storage, ".yml"), YamlBukkitConfigurer::new, new SerdesBukkit());
    }

    public static DocumentPersistence of(@NonNull Plugin plugin) {
        return of(new File(plugin.getDataFolder(), "storage"));
    }
}
