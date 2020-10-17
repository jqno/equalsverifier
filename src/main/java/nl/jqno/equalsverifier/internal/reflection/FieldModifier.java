package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

public final class FieldModifier {
    private final Field field;
    private final Object object;

    /** Private constructor. Call {@link #of(Field, Object)} to instantiate. */
    private FieldModifier(Field field, Object object) {
        this.field = field;
        this.object = object;
    }

    /**
     * Factory method.
     *
     * @param field The field to modify.
     * @param object An object that contains the field we want to modify.
     * @return A {@link FieldModifier} for {@link field} in {@link object}.
     */
    public static FieldModifier of(Field field, Object object) {
        return new FieldModifier(field, object);
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
        change(() -> field.set(object, value), true);
    }

    /**
     * Tries to make the field null. Ignores static fields and fields that can't be modified
     * reflectively.
     *
     * @throws ReflectionException If the operation fails.
     */
    public void defaultField() {
        change(this::setFieldToDefault, false);
    }

    /**
     * Tries to make the field null. Includes static fields but ignores fields that can't be
     * modified reflectively.
     *
     * @throws ReflectionException If the operation fails.
     */
    public void defaultStaticField() {
        change(this::setFieldToDefault, true);
    }

    private void setFieldToDefault() throws IllegalAccessException {
        Class<?> type = field.getType();
        field.set(object, PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(type));
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
        change(() -> field.set(to, field.get(object)), false);
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
        FieldChanger fm =
                () -> {
                    TypeTag tag = TypeTag.of(field, enclosingType);
                    Object newValue = prefabValues.giveOther(tag, field.get(object));
                    field.set(object, newValue);
                };
        change(fm, false);
    }

    private void change(FieldChanger changer, boolean includeStatic) {
        FieldAccessor accessor = FieldAccessor.of(field);
        if (!accessor.canBeModifiedReflectively()) {
            return;
        }
        if (!includeStatic && accessor.fieldIsStatic()) {
            return;
        }

        field.setAccessible(true);
        rethrow(() -> changer.change());
    }

    @FunctionalInterface
    private interface FieldChanger {
        void change() throws IllegalAccessException;
    }
}
