package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Implementation of ObjectAccessor that returns modified copies of its wrapped object, through
 * calling its class's constructor. This only works when the constructor matches the class's fields
 * exactly, such as in a record.
 *
 * @param <T> The specified object's class.
 */
final class RecordObjectAccessor<T> extends ObjectAccessor<T> {

    private final Constructor<T> constructor;

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */RecordObjectAccessor(T object, Class<T> type) {
        super(object, type);
        this.constructor = getRecordConstructor();
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        List<?> params = fields().map(this::getField).collect(Collectors.toList());
        return callRecordConstructor(params);
    }

    /** {@inheritDoc} */
    @Override
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        throw new EqualsVerifierInternalBugException(
            "Can't copy a record into a subclass of itself."
        );
    }

    /** {@inheritDoc} */
    @Override
    public T copyIntoAnonymousSubclass() {
        throw new EqualsVerifierInternalBugException(
            "Can't copy a record into an anonymous subclass of itself."
        );
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(PrefabValues prefabValues, TypeTag enclosingType) {
        return makeAccessor(
            f -> {
                Object value = getField(f);
                TypeTag tag = TypeTag.of(f, enclosingType);
                return prefabValues.giveOther(tag, value);
            }
        );
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> shallowScramble(PrefabValues prefabValues, TypeTag enclosingType) {
        throw new EqualsVerifierInternalBugException("Record: can't shallow-scramble a record.");
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> clear(
        Predicate<Field> canBeDefault,
        PrefabValues prefabValues,
        TypeTag enclosingType
    ) {
        return makeAccessor(
            f ->
                canBeDefault.test(f)
                    ? PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType())
                    : prefabValues.giveRed(TypeTag.of(f, enclosingType))
        );
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withDefaultedField(Field field) {
        return modify(field, PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(field.getType()));
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withChangedField(
        Field field,
        PrefabValues prefabValues,
        TypeTag enclosingType
    ) {
        TypeTag tag = TypeTag.of(field, enclosingType);
        Object currentValue = getField(field);
        Object newValue = prefabValues.giveOther(tag, currentValue);
        return modify(field, newValue);
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withFieldSetTo(Field field, Object newValue) {
        return modify(field, newValue);
    }

    private ObjectAccessor<T> modify(Field field, Object value) {
        return makeAccessor(f -> f.equals(field) ? value : getField(f));
    }

    private ObjectAccessor<T> makeAccessor(Function<Field, Object> determineValue) {
        List<Object> params = fields().map(determineValue).collect(Collectors.toList());
        T newObject = callRecordConstructor(params);
        return ObjectAccessor.of(newObject);
    }

    private Stream<Field> fields() {
        return StreamSupport.stream(FieldIterable.ofIgnoringStatic(type()).spliterator(), false);
    }

    private Constructor<T> getRecordConstructor() {
        return rethrow(
            () -> {
                List<Class<?>> constructorTypes = fields()
                    .map(Field::getType)
                    .collect(Collectors.toList());
                Constructor<T> result = type()
                    .getDeclaredConstructor(constructorTypes.toArray(new Class<?>[0]));
                result.setAccessible(true);
                return result;
            }
        );
    }

    private T callRecordConstructor(List<?> params) {
        return rethrow(
            () -> constructor.newInstance(params.toArray(new Object[0])),
            "Record: failed to invoke constructor."
        );
    }
}
