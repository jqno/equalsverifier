package nl.jqno.equalsverifier.internal.instantiation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.util.function.BiFunction;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Configuration;

public class SubjectCreator<T> {

    private final TypeTag typeTag;
    private final PrefabValues prefabValues;
    private final Configuration<T> config;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public SubjectCreator(Configuration<T> config) {
        this.typeTag = config.getTypeTag();
        this.prefabValues = config.getPrefabValues();
        this.config = config;
    }

    public T plain() {
        return createSubject().get();
    }

    public T withFieldDefaulted(Field field) {
        return createSubject().withDefaultedField(field).get();
    }

    public T withAllFieldsDefaulted() {
        return withAllFields((acc, f) ->
            FieldProbe.of(f, config).canBeDefault() ? acc.withDefaultedField(f) : acc
        );
    }

    public T withAllFieldsDefaultedExcept(Field field) {
        return withAllFields((acc, f) -> f.equals(field) ? acc : acc.withDefaultedField(f));
    }

    public T withFieldSetTo(Field field, Object value) {
        return createSubject().withFieldSetTo(field, value).get();
    }

    public T withFieldChanged(Field field) {
        return createSubject().withChangedField(field, prefabValues, typeTag).get();
    }

    public T withAllFieldsChanged() {
        return withAllFields((acc, f) -> acc.withChangedField(f, prefabValues, typeTag));
    }

    private T withAllFields(BiFunction<ObjectAccessor<T>, Field, ObjectAccessor<T>> modifier) {
        ObjectAccessor<T> accessor = createSubject();
        for (Field f : FieldIterable.of(typeTag.getType())) {
            accessor = modifier.apply(accessor, f);
        }
        return accessor.get();
    }

    private ObjectAccessor<T> createSubject() {
        ClassAccessor<T> accessor = ClassAccessor.of(typeTag.getType(), prefabValues);
        return accessor.getRedAccessor(TypeTag.NULL);
    }
}
