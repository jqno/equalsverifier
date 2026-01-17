package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

/**
 * Creates an instance of a class or record.
 */
public interface Instantiator<T> {

    /**
     * Creates an instance of the given type, with its field set to the given values. If no value is given for a
     * specific field, the field will be set to its default value: null for object references, 0 for numbers, false for
     * booleans.
     *
     * @param values Values to assign to the instance's fields.
     * @return An instance with assigned values.
     */
    T instantiate(Map<Field, Object> values);

    /**
     * Creates a new instance with all fields set to the same value as their counterparts from {@code original}.
     *
     * @param original The instance to copy.
     * @return A copy of the given original.
     */
    default T copy(Object original) {
        var values = new HashMap<Field, Object>();
        for (FieldProbe p : FieldIterable.ofIgnoringStatic(original.getClass())) {
            Object value = p.getValue(original);
            values.put(p.getField(), value);
        }
        return instantiate(values);
    }
}
