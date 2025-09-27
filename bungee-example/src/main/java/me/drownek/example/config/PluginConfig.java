package me.drownek.example.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import me.drownek.example.config.polymorphic.animals.Animal;
import me.drownek.example.config.polymorphic.computer.InputProvider;
import me.drownek.platform.core.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CanBeFinal")
@Configuration
public class PluginConfig extends OkaeriConfig {

    @Comment("Polymorphic object")
    public List<Animal> animals = new ArrayList<>();

    public List<InputProvider> computers = new ArrayList<>();

    @Comment("Storage settings")
    public StorageConfig storage = new StorageConfig();

    public static class StorageConfig extends OkaeriConfig {

        @Comment("Type of the storage backend: FLAT, MYSQL, POSTGRES")
        public StorageBackend backend = StorageBackend.FLAT;

        @Comment("Prefix for the storage: allows to have multiple instances using same database")
        public String prefix = "bungee-example";

        @Comment("FLAT   : not applicable, plugin controlled")
        @Comment("MYSQL  : jdbc:mysql://localhost:3306/db")
        @Comment("POSTGRES  : jdbc:postgresql://localhost:5432/db")
        public String uri = "";

        public String user = "";

        public String password = "";
    }
}
