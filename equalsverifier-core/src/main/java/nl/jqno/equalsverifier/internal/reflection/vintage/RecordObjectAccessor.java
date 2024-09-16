package nl.jqno.equalsverifier.internal.reflection.vintage;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.RecordProbe;

/**
 * Implementation of ObjectAccessor that returns modified copies of its wrapped object, through
 * calling its class's constructor. This only works when the constructor matches the class's fields
 * exactly, such as in a record.
 *
 * @param <T> The specified object's class.
 */
final class RecordObjectAccessor<T> extends ObjectAccessor<T> {

    private final RecordProbe<T> probe;

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */RecordObjectAccessor(T object, Class<T> type) {
        super(object, type);
        this.probe = new RecordProbe<>(type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        List<?> params = probe.fields().map(this::getField).collect(Collectors.toList());
        return callRecordConstructor(params);
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(
        PrefabValues prefabValues,
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return makeAccessor(f -> {
            Object value = getField(f);
            TypeTag tag = TypeTag.of(f, enclosingType);
            return prefabValues.giveOther(tag, value, typeStack);
        });
    }

    private ObjectAccessor<T> makeAccessor(Function<Field, Object> determineValue) {
        List<Object> params = probe.fields().map(determineValue).collect(Collectors.toList());
        T newObject = callRecordConstructor(params);
        return ObjectAccessor.of(newObject);
    }

    private T callRecordConstructor(List<?> params) {
        RecordProbe<T> p = new RecordProbe<>(type());
        return p.callRecordConstructor(params);
    }

    @SuppressWarnings("unchecked")
    public T getField(Field field) {
        return (T) FieldProbe.of(field).getValue(get());
    }
}
