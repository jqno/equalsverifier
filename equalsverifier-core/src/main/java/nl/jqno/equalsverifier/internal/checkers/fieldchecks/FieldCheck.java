package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

@FunctionalInterface
public interface FieldCheck<T> {
    void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    );
}
