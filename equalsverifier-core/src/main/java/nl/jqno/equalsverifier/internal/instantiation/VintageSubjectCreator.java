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

public class VintageSubjectCreator<T> implements SubjectCreator<T> {

    private final TypeTag typeTag;
    private final PrefabValues prefabValues;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public VintageSubjectCreator(TypeTag typeTag, PrefabValues prefabValues) {
        this.typeTag = typeTag;
        this.prefabValues = prefabValues;
    }

    @Override
    public T plain() {
        return createSubject().get();
    }

    @Override
    public T withFieldDefaulted(Field field) {
        return createSubject().withDefaultedField(field).get();
    }

    @Override
    public T withAllFieldsDefaulted(Configuration<T> config) {
        return withAllFields((acc, f) ->
            FieldProbe.of(f, config).canBeDefault() ? acc.withDefaultedField(f) : acc
        );
    }

    @Override
    public T withAllFieldsDefaultedExcept(Field field) {
        return withAllFields((acc, f) -> f.equals(field) ? acc : acc.withDefaultedField(f));
    }

    @Override
    public T withFieldSetTo(Field field, Object value) {
        return createSubject().withFieldSetTo(field, value).get();
    }

    @Override
    public T withFieldChanged(Field field) {
        return createSubject().withChangedField(field, prefabValues, typeTag).get();
    }

    @Override
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
