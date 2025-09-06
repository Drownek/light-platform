package me.drownek.platform.core.configs.polymorphic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or class as polymorphic, enabling type discrimination
 * during serialization/deserialization of inheritance hierarchies.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Polymorphic {
}
