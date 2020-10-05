package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

@FunctionalInterface
public interface FieldCheck<T> {
    void execute(ObjectAccessor<T> referenceAccessor, ObjectAccessor<T> copyAccessor, Field field);
}
