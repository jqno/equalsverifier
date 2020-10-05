package nl.jqno.equalsverifier.internal.reflection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/** Provides reflective access to one field of an object. */
public class FieldAccessor {
    private final Object object;
    private final Field field;

    /**
     * Constructor.
     *
     * @param object The object that contains the field we want to access.
     * @param field A field of object.
     */
    public FieldAccessor(Object object, Field field) {
        this.object = object;
        this.field = field;
    }

    /** @return The object that contains the field. */
    public Object getObject() {
        return object;
    }

    /** @return The field itself. */
    public Field getField() {
        return field;
    }

    /** @return The field's type. */
    public Class<?> getFieldType() {
        return field.getType();
    }

    /** @return The field's name. */
    public String getFieldName() {
        return field.getName();
    }

    /** @return Whether the field is of a primitive type. */
    public boolean fieldIsPrimitive() {
        return getFieldType().isPrimitive();
    }

    /** @return Whether the field is marked with the final modifier. */
    public boolean fieldIsFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    /** @return Whether the field is marked with the static modifier. */
    public boolean fieldIsStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /** @return Whether the field is marked with the transient modifier. */
    public boolean fieldIsTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    /** @return Whether the field is an enum with a single value. */
    public boolean fieldIsEmptyOrSingleValueEnum() {
        Class<?> type = field.getType();
        return type.isEnum() && type.getEnumConstants().length <= 1;
    }

    /**
     * Tries to get the field's value.
     *
     * @return The field's value.
     * @throws ReflectionException If the operation fails.
     */
    @SuppressFBWarnings(
            value = "DP_DO_INSIDE_DO_PRIVILEGED",
            justification = "Only called in test code, not production.")
    public Object get() {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Determines whether the field can be modified using reflection.
     *
     * @return Whether or not the field can be modified reflectively.
     */
    public boolean canBeModifiedReflectively() {
        if (field.isSynthetic()) {
            return false;
        }
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
            return false;
        }
        return true;
    }
}
