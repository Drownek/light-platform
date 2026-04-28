package me.drownek.platform.bukkit;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeri;
import eu.okaeri.configs.serdes.okaeri.SerdesOkaeriBukkit;
import eu.okaeri.configs.serdes.okaeri.range.section.SerdesRangeSection;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.injector.Injector;
import me.drownek.platform.bukkit.serdes.SerdesBukkit;
import me.drownek.platform.core.component.creator.ComponentCreatorRegistry;
import me.drownek.platform.core.component.type.ConfigurationComponentResolver;
import me.drownek.platform.core.configs.polymorphic.PolymorphicSerdesPack;
import me.drownek.platform.core.extension.LightExtension;
import me.drownek.platform.core.util.ExtensionsUtil;
import me.drownek.util.BukkitUtilsSerdes;

import java.util.ArrayList;
import java.util.List;

public class BukkitConfigsExtension implements LightExtension {

    @Override
    public void register(ComponentCreatorRegistry registry, Injector injector) {
        injector.registerInjectable("defaultConfigurerProvider", YamlBukkitConfigurer.class);
        List<Class<? extends OkaeriSerdesPack>> defaultConfigurerSerdes = new ArrayList<>(List.of(
                SerdesCommons.class,
                SerdesOkaeri.class,
                SerdesOkaeriBukkit.class,
                SerdesRangeSection.class,
                PolymorphicSerdesPack.class,
                SerdesBukkit.class
        ));
        if (ExtensionsUtil.isBukkitUtilsPresent()) {
            defaultConfigurerSerdes.add(BukkitUtilsSerdes.class);
        }

        injector.registerInjectable("defaultConfigurerSerdes", defaultConfigurerSerdes.toArray(Class[]::new));

        registry.register(ConfigurationComponentResolver.class);
    }
}
