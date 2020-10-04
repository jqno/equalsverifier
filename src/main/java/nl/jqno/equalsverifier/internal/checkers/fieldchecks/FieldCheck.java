package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

public interface FieldCheck<T> {
    default void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {};

    default void execute(
            ObjectAccessor<T> referenceAccessor, ObjectAccessor<T> copyAccessor, Field field) {};
}
