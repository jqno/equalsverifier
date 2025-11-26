package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/**
 * Allows for a field in an object reference to be set to another value.
 */
public class FieldMutator {

    private final FieldProbe probe;
    private final Field field;

    /**
     * Constructor.
     *
     * @param probe A field probe pointing to the field to mutate.
     */
    public FieldMutator(FieldProbe probe) {
        this.probe = probe;
        this.field = probe.getField();
    }

    /**
     * Assigns {@code newValue} to the current field in {@code object}.
     *
     * @param object   The instance on which to re-assign the current field.
     * @param newValue The value to assign to the field.
     */
    public void setNewValue(Object object, Object newValue) {
        rethrow(() -> {
            if (probe.canBeModifiedReflectively()) {
                field.setAccessible(true);
                try {
                    field.set(object, newValue);
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
        });
    }
}
