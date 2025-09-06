package me.drownek.platform.core.configs.polymorphic;

import eu.okaeri.configs.schema.ConfigDeclaration;
import eu.okaeri.configs.schema.FieldDeclaration;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.SerializationData;
import eu.okaeri.configs.util.UnsafeUtil;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Serializer that handles polymorphic objects by adding type discrimination.
 */
public class PolymorphicSerializer implements ObjectSerializer<Object> {

    @Override
    public boolean supports(@NonNull Class<? super Object> type) {
        return PolymorphicResolver.isPolymorphic(type) ||
            (Serializable.class.isAssignableFrom(type) && hasPolymorphicHierarchy(type));
    }

    private boolean hasPolymorphicHierarchy(@NonNull Class<?> type) {
        // Check if any parent class is marked as polymorphic
        Class<?> current = type.getSuperclass();
        while (current != null && current != Object.class) {
            if (PolymorphicResolver.isPolymorphic(current)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    @Override
    public void serialize(@NonNull Object object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        Class<?> actualType = object.getClass();

        // Add type information first
        data.add("_class", actualType.getName());

        // Get all fields from the inheritance hierarchy
        ConfigDeclaration declaration = ConfigDeclaration.of(actualType, object);

        // Also include fields from parent classes
        Class<?> currentClass = actualType.getSuperclass();
        while (currentClass != null && currentClass != Object.class) {
            ConfigDeclaration parentDeclaration = ConfigDeclaration.of(currentClass, object);
            // Add parent fields that aren't already present
            for (FieldDeclaration parentField : parentDeclaration.getFields()) {
                if (!declaration.getField(parentField.getName()).isPresent()) {
                    declaration.getFieldMap().put(parentField.getName(), parentField);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        for (FieldDeclaration field : declaration.getFields()) {
            try {
                Object fieldValue = field.getValue();
                if (fieldValue != null) {
                    // Use the configurer's simplify method but with the field's specific context
                    SerdesContext fieldContext = SerdesContext.of(data.getConfigurer(), field);
                    Object simplifiedValue = data.getConfigurer().simplify(fieldValue, field.getType(), fieldContext, true);
                    data.addRaw(field.getName(), simplifiedValue);
                } else {
                    data.addRaw(field.getName(), null);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public Object deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        Class<?> baseType = generics.getType();

        // Get the actual type from the data - use getRaw to avoid recursion
        Object typeIdentifierObj = data.getRaw("_class");
        if (typeIdentifierObj == null) {
            throw new IllegalArgumentException("Missing type information for polymorphic deserialization");
        }

        String typeIdentifier = typeIdentifierObj.toString();
        Class<?> actualType;

        try {
            actualType = Class.forName(typeIdentifierObj.toString());
        } catch (Exception e) {
            // If we can't resolve the type, try to find it among known subclasses
            // This helps with backward compatibility when class names change
            throw new RuntimeException("Cannot resolve polymorphic type '" + typeIdentifier +
                "' for base class " + baseType.getName() +
                ". Make sure the class exists and is accessible.", e);
        }

        // Create instance manually to avoid recursion
        try {
            Object instance = UnsafeUtil.allocateInstance(actualType);

            // Get all fields from the inheritance hierarchy
            ConfigDeclaration declaration = ConfigDeclaration.of(actualType, instance);

            // Also include fields from parent classes
            Class<?> currentClass = actualType.getSuperclass();
            while (currentClass != null && currentClass != Object.class) {
                ConfigDeclaration parentDeclaration = ConfigDeclaration.of(currentClass, instance);
                // Add parent fields that aren't already present
                for (FieldDeclaration parentField : parentDeclaration.getFields()) {
                    if (!declaration.getField(parentField.getName()).isPresent()) {
                        declaration.getFieldMap().put(parentField.getName(), parentField);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            // Manually deserialize each field
            for (FieldDeclaration field : declaration.getFields()) {
                String fieldName = field.getName();

                // Skip the type property field
                if (fieldName.equals("_class")) {
                    continue;
                }

                Object rawFieldValue = data.getRaw(fieldName);
                if (rawFieldValue != null) {
                    try {
                        // Resolve the field value using the configurer
                        Object resolvedValue = data.getConfigurer().resolveType(
                            rawFieldValue,
                            GenericsDeclaration.of(rawFieldValue),
                            field.getType().getType(),
                            field.getType(),
                            SerdesContext.of(data.getConfigurer(), field)
                        );

                        // Set the field value
                        field.getField().setAccessible(true);
                        field.getField().set(instance, resolvedValue);
                    } catch (Exception ignored) {
                    }
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize polymorphic object of type: " + actualType, e);
        }
    }
}
