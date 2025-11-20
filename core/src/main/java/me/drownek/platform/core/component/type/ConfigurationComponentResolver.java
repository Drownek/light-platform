package me.drownek.platform.core.component.type;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.annotation.Configuration;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationComponentResolver implements ComponentResolver {

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(Configuration.class) != null;
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    private @Inject Configurer defaultConfigurerProvider;
    private @Inject Class<? extends OkaeriSerdesPack>[] defaultConfigurerSerdes;
    private @Inject File dataFolder;
    private @Inject Injector injector;
    private @Inject LightPlatform lightPlatform;

    @Override
    @SuppressWarnings("unchecked")
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        if (!OkaeriConfig.class.isAssignableFrom(manifest.getType())) {
            throw new IllegalArgumentException("Component of @Configuration on type requires class to be a OkaeriConfig: " + manifest);
        }

        long start = System.currentTimeMillis();
        Class<? extends OkaeriConfig> configType = (Class<? extends OkaeriConfig>) manifest.getType();

        Configuration configuration = configType.getAnnotation(Configuration.class);
        String path = configuration.path();
        boolean defaultNotNull = configuration.defaultNotNull();
        Class<? extends Configurer> provider = configuration.provider();

        try {
            Configurer configurer = (provider == Configuration.DEFAULT.class)
                    ? this.defaultConfigurerProvider
                    : injector.createInstance(provider);

            List<OkaeriSerdesPack> serdesPackList = Stream.concat(
                    Stream.of(this.defaultConfigurerSerdes),
                    Arrays.stream(configuration.serdes())
                )
                .map(injector::createInstance)
                .collect(Collectors.toList());

            serdesPackList.addAll(lightPlatform.additionalSerdesPacks());

            OkaeriSerdesPack[] serdesPacks = serdesPackList.toArray(OkaeriSerdesPack[]::new);

            String extension = configurer.getExtensions().isEmpty() ? "bin" : configurer.getExtensions().get(0);
            String resolvedPath = path.replace("{ext}", extension);

            OkaeriConfig config = ConfigManager.create(configType, (it) -> {
                it.withBindFile(new File(this.dataFolder, resolvedPath));
                it.withRemoveOrphans(true);
                it.withConfigurer(new OkaeriValidator(configurer, defaultNotNull), serdesPacks);
                it.saveDefaults();
                it.load(true);
            });

            long took = System.currentTimeMillis() - start;

            creator.debug(ComponentHelper.buildComponentMessage()
                    .type("Loaded configuration")
                    .name(configType.getSimpleName())
                    .took(took)
                    .meta("path", path)
                    .meta("provider", provider.getSimpleName())
                    .build());

            creator.increaseStatistics("configs", 1);

            return config;
        } catch (Exception exception) {
            throw new RuntimeException("Configuration load failure", exception);
        }
    }
}
