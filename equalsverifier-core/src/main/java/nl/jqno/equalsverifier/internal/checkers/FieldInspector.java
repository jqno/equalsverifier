package nl.jqno.equalsverifier.internal.checkers;

import java.lang.reflect.Field;
import java.util.Set;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;

public class FieldInspector<T> {

    private final ClassAccessor<T> classAccessor;
    private final TypeTag typeTag;

    public FieldInspector(ClassAccessor<T> classAccessor, TypeTag typeTag) {
        this.classAccessor = classAccessor;
        this.typeTag = typeTag;
    }

    public void check(FieldCheck<T> check) {
        for (Field field : FieldIterable.of(classAccessor.getType())) {
            ObjectAccessor<T> reference = classAccessor.getRedAccessor(typeTag);
            ObjectAccessor<T> copy = classAccessor.getRedAccessor(typeTag);
            FieldAccessor fieldAccessor = FieldAccessor.of(field);

            check.execute(reference, copy, fieldAccessor);
        }
    }

    public void checkWithNull(
        boolean isNullWarningSuppressed,
        boolean isZeroWarningSuppressed,
        Set<String> nonnullFields,
        AnnotationCache annotationCache,
        FieldCheck<T> check
    ) {
        for (Field field : FieldIterable.of(classAccessor.getType())) {
            ObjectAccessor<T> reference = classAccessor.getDefaultValuesAccessor(
                typeTag,
                isNullWarningSuppressed,
                isZeroWarningSuppressed,
                nonnullFields,
                annotationCache
            );
            ObjectAccessor<T> changed = classAccessor.getDefaultValuesAccessor(
                typeTag,
                isNullWarningSuppressed,
                isZeroWarningSuppressed,
                nonnullFields,
                annotationCache
            );
            FieldAccessor fieldAccessor = FieldAccessor.of(field);

            check.execute(reference, changed, fieldAccessor);
        }
    }
}
