package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.RecordProbe;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.objenesis.Objenesis;

/**
 * Implementation of ObjectAccessor that returns modified copies of its wrapped object, through calling its class's
 * constructor. This only works when the constructor matches the class's fields exactly, such as in a record.
 *
 * @param <T> The specified object's class.
 */
final class RecordObjectAccessor<T> extends ObjectAccessor<T> {

    private final RecordProbe<T> probe;

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */ RecordObjectAccessor(T object, Class<T> type) {
        super(object, type);
        this.probe = new RecordProbe<>(type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy(Objenesis objenesis) {
        List<?> params = probe.fields().map(this::getField).toList();
        return callRecordConstructor(params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public ObjectAccessor<T> scramble(
            VintageValueProvider valueProvider,
            TypeTag enclosingType,
            LinkedHashSet<TypeTag> typeStack) {
        return makeAccessor(fieldProbe -> {
            Object value = getField(fieldProbe);
            TypeTag tag = TypeTag.of(fieldProbe.getField(), enclosingType);
            return valueProvider.giveOther(tag, value, typeStack);
        });
    }

    private ObjectAccessor<T> makeAccessor(Function<FieldProbe, Object> determineValue) {
        List<Object> params = probe.fields().map(determineValue).toList();
        T newObject = callRecordConstructor(params);
        return ObjectAccessor.of(newObject);
    }

    private T callRecordConstructor(List<?> params) {
        var p = new RecordProbe<T>(type());
        return p.callRecordConstructor(params);
    }

    @SuppressWarnings("unchecked")
    public T getField(FieldProbe fieldProbe) {
        return (T) fieldProbe.getValue(get());
    }
}
