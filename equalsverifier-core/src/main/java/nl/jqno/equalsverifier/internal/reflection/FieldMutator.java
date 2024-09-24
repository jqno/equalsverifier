package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;

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
     * @param object The instance on which to re-assign the current field.
     * @param newValue The value to assign to the field.
     */
    public void setNewValue(Object object, Object newValue) {
        rethrow(() -> {
            if (probe.canBeModifiedReflectively()) {
                field.setAccessible(true);
                field.set(object, newValue);
            }
        });
    }
}
