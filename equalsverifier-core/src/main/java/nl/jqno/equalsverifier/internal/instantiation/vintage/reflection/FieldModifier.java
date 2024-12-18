package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class FieldModifier {

    private final FieldProbe fieldProbe;
    private final Field field;
    private final Object object;

    /** Private constructor. Call {@link #of(Field, Object)} to instantiate. */
    private FieldModifier(FieldProbe fieldProbe, Object object) {
        this.fieldProbe = fieldProbe;
        this.field = fieldProbe.getField();
        this.object = object;
    }

    /**
     * Factory method.
     *
     * @param fieldProbe A reference to the field to modify.
     * @param object     An object that contains the field we want to modify.
     * @return A {@link FieldModifier} for {@link field} in {@link object}.
     */
    public static FieldModifier of(FieldProbe fieldProbe, Object object) {
        return new FieldModifier(fieldProbe, object);
    }

    /**
     * Copies field's value to the corresponding field in the specified object.
     *
     * <p>
     * Ignores static fields and fields that can't be modified reflectively.
     *
     * @param to The object into which to copy the field.
     * @throws ReflectionException If the operation fails.
     */
    public void copyTo(Object to) {
        change(() -> field.set(to, field.get(object)), false);
    }

    /**
     * Changes the field's value to something else. The new value will never be null. Other than that, the precise value
     * is undefined.
     *
     * <p>
     * Ignores static fields and fields that can't be modified reflectively.
     *
     * @param valueProvider If the field is of a type contained within prefabValues, the new value will be taken from
     *                          it.
     * @param enclosingType A tag for the type that contains the field. Needed to determine a generic type, if it has
     *                          one..
     * @param typeStack     Keeps track of recursion in the type.
     * @throws ReflectionException If the operation fails.
     */
    public void changeField(
            VintageValueProvider valueProvider,
            TypeTag enclosingType,
            LinkedHashSet<TypeTag> typeStack) {
        FieldChanger fm = () -> {
            TypeTag tag = TypeTag.of(field, enclosingType);
            Object newValue = valueProvider.giveOther(tag, field.get(object), typeStack);
            field.set(object, newValue);
        };
        change(fm, false);
    }

    private void change(FieldChanger changer, boolean includeStatic) {
        if (!fieldProbe.canBeModifiedReflectively()) {
            return;
        }
        if (!includeStatic && fieldProbe.isStatic()) {
            return;
        }

        field.setAccessible(true);
        rethrow(() -> wrappedChange(changer));
    }

    private void wrappedChange(FieldChanger changer) throws IllegalAccessException {
        try {
            changer.change();
        }
        catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg.startsWith("Can not set") || msg.startsWith("Can not get")) {
                throw new ReflectionException(
                        "Reflection error: try adding a prefab value for field " + field.getName() + " of type "
                                + field.getType().getName(),
                        e);
            }
            else {
                throw e;
            }
        }
    }

    @FunctionalInterface
    private interface FieldChanger {
        void change() throws IllegalAccessException;
    }
}
