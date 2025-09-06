package me.drownek.platform.core.configs.polymorphic;

import lombok.NonNull;

/**
 * Utility class for resolving polymorphic type information.
 */
public class PolymorphicResolver {

    private static Polymorphic findPolymorphicAnnotation(@NonNull Class<?> clazz) {
        // Check the class itself
        Polymorphic annotation = clazz.getAnnotation(Polymorphic.class);
        if (annotation != null) {
            return annotation;
        }

        // Check parent classes
        Class<?> current = clazz.getSuperclass();
        while (current != null && current != Object.class) {
            annotation = current.getAnnotation(Polymorphic.class);
            if (annotation != null) {
                return annotation;
            }
            current = current.getSuperclass();
        }

        return null;
    }

    public static boolean isPolymorphic(@NonNull Class<?> clazz) {
        return findPolymorphicAnnotation(clazz) != null;
    }
}
