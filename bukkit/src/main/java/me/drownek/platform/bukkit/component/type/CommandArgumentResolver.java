package me.drownek.platform.bukkit.component.type;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.bukkit.annotation.CommandArgument;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.creator.ComponentCreator;
import me.drownek.platform.core.component.creator.ComponentResolver;
import me.drownek.platform.core.component.manifest.BeanManifest;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CommandArgumentResolver implements ComponentResolver {

    @Inject
    private JavaPlugin plugin;
    private @Inject LightBukkitPlugin okPlugin;

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return type.getAnnotation(CommandArgument.class) != null && ArgumentResolver.class.isAssignableFrom(type);
    }

    @Override
    public boolean supports(@NonNull Method method) {
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object make(@NonNull ComponentCreator creator, @NonNull BeanManifest manifest, @NonNull Injector injector) {

        long start = System.currentTimeMillis();
        Class<?> manifestType = manifest.getType();
        Object instance = injector.createInstance(manifestType);

        LiteCommandsBuilder<?, ?, ?> commands = injector.getOrThrow("commands", LiteCommandsBuilder.class);

        ArgumentResolver argumentResolver = (ArgumentResolver) instance;

        Class<?> argumentType = getArgumentType(instance.getClass());
        if (argumentType == null) {
            throw new RuntimeException("Cannot resolve argument type for " + instance.getClass().getName());
        }
        String argumentKey = argumentResolver.getClass().getAnnotation(CommandArgument.class).argumentKey();
        if (argumentKey.isEmpty()) {
            commands.argument(argumentType, argumentResolver);
        } else {
            commands.argument(argumentType, ArgumentKey.of(argumentKey), argumentResolver);
        }

        long took = System.currentTimeMillis() - start;
        creator.debug(ComponentHelper.buildComponentMessage()
                .type("Registered ArgumentResolver")
                .name(argumentResolver.getClass().getSimpleName())
                .took(took)
                .build());
        return instance;
    }

    private Class<?> getArgumentType(Class<?> resolverClass) {
        return getGenericTypeArgument(resolverClass, ArgumentResolver.class, 1);
    }

    private Class<?> getGenericTypeArgument(Class<?> clazz, Class<?> targetClass, int argumentIndex) {
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            Class<?> result = extractTypeArgument(genericInterface, targetClass, argumentIndex);
            if (result != null) return result;
        }

        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass != null) {
            Class<?> result = extractTypeArgument(genericSuperclass, targetClass, argumentIndex);
            if (result != null) return result;
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            return getGenericTypeArgument(superclass, targetClass, argumentIndex);
        }

        return null;
    }

    private Class<?> extractTypeArgument(Type type, Class<?> targetClass, int argumentIndex) {
        if (type instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType().equals(targetClass)) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > argumentIndex && typeArguments[argumentIndex] instanceof Class) {
                    return (Class<?>) typeArguments[argumentIndex];
                }
            }
        }
        return null;
    }
}
