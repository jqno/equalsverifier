package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/** Provides reflective access to one field of an object. */
public final class FieldAccessor {

    private final Field field;

    /** Private constructor. Call {@link #of(Field)} to instantiate. */
    private FieldAccessor(Field field) {
        this.field = field;
    }

    /**
     * Factory method.
     *
     * @param field The field to access.
     * @return A {@link FieldAccessor} for {@link #field}.
     */
    public static FieldAccessor of(Field field) {
        return new FieldAccessor(field);
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
     * @param object The object that contains the field whose value we want to get.
     * @return The field's value.
     * @throws ReflectionException If the operation fails.
     */
    @SuppressFBWarnings(
        value = "DP_DO_INSIDE_DO_PRIVILEGED",
        justification = "Only called in test code, not production."
    )
    public Object get(Object object) {
        field.setAccessible(true);
        return rethrow(() -> field.get(object));
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
