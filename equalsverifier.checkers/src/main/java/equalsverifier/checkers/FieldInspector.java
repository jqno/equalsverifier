package equalsverifier.checkers;

import equalsverifier.checkers.fieldchecks.FieldCheck;
import equalsverifier.gentype.TypeTag;
import equalsverifier.reflection.ClassAccessor;
import equalsverifier.reflection.FieldIterable;
import equalsverifier.reflection.ObjectAccessor;
import equalsverifier.reflection.annotations.AnnotationCache;

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

    public void checkWithNull(Set<String> nonnullFields, AnnotationCache annotationCache, FieldCheck check) {
        for (Field field : FieldIterable.of(classAccessor.getType())) {
            ObjectAccessor<T> reference = classAccessor.getDefaultValuesAccessor(typeTag, nonnullFields, annotationCache);
            ObjectAccessor<T> changed = classAccessor.getDefaultValuesAccessor(typeTag, nonnullFields, annotationCache);

            check.execute(reference.fieldAccessorFor(field), changed.fieldAccessorFor(field));
        }
    }

}
