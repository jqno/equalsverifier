package nl.jqno.equalsverifier.internal.reflection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

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
     * Tries to set the field to the specified value.
     *
     * <p>Includes static fields but ignores fields that can't be modified reflectively.
     *
     * @param value The value that the field should get.
     * @throws ReflectionException If the operation fails.
     */
    public void set(Object value) {
        modify(() -> field.set(object, value), true);
    }

    /**
     * Tries to make the field null. Ignores static fields and fields that can't be modified
     * reflectively.
     *
     * @throws ReflectionException If the operation fails.
     */
    public void defaultField() {
        modify(this::setFieldToDefault, false);
    }

    /**
     * Tries to make the field null. Includes static fields but ignores fields that can't be
     * modified reflectively.
     *
     * @throws ReflectionException If the operation fails.
     */
    public void defaultStaticField() {
        modify(this::setFieldToDefault, true);
    }

    private void setFieldToDefault() throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == boolean.class) {
            field.setBoolean(object, false);
        } else if (type == byte.class) {
            field.setByte(object, (byte) 0);
        } else if (type == char.class) {
            field.setChar(object, '\u0000');
        } else if (type == double.class) {
            field.setDouble(object, 0.0);
        } else if (type == float.class) {
            field.setFloat(object, 0.0f);
        } else if (type == int.class) {
            field.setInt(object, 0);
        } else if (type == long.class) {
            field.setLong(object, 0);
        } else if (type == short.class) {
            field.setShort(object, (short) 0);
        } else {
            field.set(object, null);
        }
    }

    /**
     * Copies field's value to the corresponding field in the specified object.
     *
     * <p>Ignores static fields and fields that can't be modified reflectively.
     *
     * @param to The object into which to copy the field.
     * @throws ReflectionException If the operation fails.
     */
    public void copyTo(Object to) {
        modify(() -> field.set(to, field.get(object)), false);
    }

    /**
     * Changes the field's value to something else. The new value will never be null. Other than
     * that, the precise value is undefined.
     *
     * <p>Ignores static fields and fields that can't be modified reflectively.
     *
     * @param prefabValues If the field is of a type contained within prefabValues, the new value
     *     will be taken from it.
     * @param enclosingType A tag for the type that contains the field. Needed to determine a
     *     generic type, if it has one..
     * @throws ReflectionException If the operation fails.
     */
    public void changeField(PrefabValues prefabValues, TypeTag enclosingType) {
        FieldModifier fm =
                () -> {
                    TypeTag tag = TypeTag.of(field, enclosingType);
                    Object newValue = prefabValues.giveOther(tag, field.get(object));
                    field.set(object, newValue);
                };
        modify(fm, false);
    }

    private void modify(FieldModifier modifier, boolean includeStatic) {
        if (!canBeModifiedReflectively()) {
            return;
        }
        if (!includeStatic && fieldIsStatic()) {
            return;
        }

        field.setAccessible(true);
        try {
            modifier.modify();
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @FunctionalInterface
    private interface FieldModifier {
        void modify() throws IllegalAccessException;
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
