package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

// @FunctionalInterface
public interface FieldCheck<T> {
    default void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        execute(fieldAccessor.getField());
    }

    default void execute(Field changedField) {}
}
