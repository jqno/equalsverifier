package nl.jqno.equalsverifier.internal.instantiation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

public class SubjectCreator<T> {

    private final TypeTag typeTag;
    private final PrefabValues prefabValues;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public SubjectCreator(TypeTag typeTag, PrefabValues prefabValues) {
        this.typeTag = typeTag;
        this.prefabValues = prefabValues;
    }

    public T plain() {
        return createSubject().get();
    }

    public T withFieldDefaulted(Field field) {
        return createSubject().withDefaultedField(field).get();
    }

    public T withFieldSetTo(Field field, Object value) {
        return createSubject().withFieldSetTo(field, value).get();
    }

    public T withFieldChanged(Field field) {
        return createSubject().withChangedField(field, prefabValues, typeTag).get();
    }

    public T withAllFieldsChanged() {
        ObjectAccessor<T> accessor = createSubject();
        for (Field f : FieldIterable.of(typeTag.getType())) {
            accessor = accessor.withChangedField(f, prefabValues, typeTag);
        }
        return accessor.get();
    }

    private ObjectAccessor<T> createSubject() {
        ClassAccessor<T> accessor = ClassAccessor.of(typeTag.getType(), prefabValues);
        return accessor.getRedAccessor(TypeTag.NULL);
    }
}
