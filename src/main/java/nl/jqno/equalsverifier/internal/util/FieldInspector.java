package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

import java.lang.reflect.Field;
import java.util.Set;

public class FieldInspector<T> {
    private final ClassAccessor<T> classAccessor;
    private final TypeTag typeTag;

    public FieldInspector(ClassAccessor<T> classAccessor, TypeTag typeTag) {
        this.classAccessor = classAccessor;
        this.typeTag = typeTag;
    }

    public void check(FieldCheck check) {
        for (Field field : FieldIterable.of(classAccessor.getType())) {
            ObjectAccessor<T> reference = classAccessor.getRedAccessor(typeTag);
            ObjectAccessor<T> changed = classAccessor.getRedAccessor(typeTag);

            check.execute(reference.fieldAccessorFor(field), changed.fieldAccessorFor(field));
        }
    }

    public void checkWithNull(Set<String> nonnullFields, FieldCheck check) {
        for (Field field : FieldIterable.of(classAccessor.getType())) {
            ObjectAccessor<T> reference = classAccessor.getDefaultValuesAccessor(typeTag, nonnullFields);
            ObjectAccessor<T> changed = classAccessor.getDefaultValuesAccessor(typeTag, nonnullFields);

            check.execute(reference.fieldAccessorFor(field), changed.fieldAccessorFor(field));
        }
    }

    public interface FieldCheck {
        void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor);
    }
}
