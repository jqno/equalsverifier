package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

public final class RecordObjectAccessor<T> extends ObjectAccessor<T> {

    private final Constructor<T> constructor;

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */ RecordObjectAccessor(T object, Class<T> type) {
        super(object, type);
        this.constructor = getRecordConstructor();
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        List<?> params =
                fields().map(this::fieldAccessorFor)
                        .map(FieldAccessor::get)
                        .collect(Collectors.toList());
        return callRecordConstructor(constructor, params);
    }

    /** {@inheritDoc} */
    @Override
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        throw new EqualsVerifierInternalBugException(
                "Can't copy a record into a subclass of itself.");
    }

    /** {@inheritDoc} */
    @Override
    public T copyIntoAnonymousSubclass() {
        throw new EqualsVerifierInternalBugException(
                "Can't copy a record into an anonymous subclass of itself.");
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(PrefabValues prefabValues, TypeTag enclosingType) {
        List<Object> params = new ArrayList<>();
        for (Field f : FieldIterable.ofIgnoringStatic(type())) {
            Object value = fieldAccessorFor(f).get();
            TypeTag tag = TypeTag.of(f, enclosingType);
            params.add(prefabValues.giveOther(tag, value));
        }
        T newObject = callRecordConstructor(constructor, params);
        return ObjectAccessor.of(newObject);
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> shallowScramble(PrefabValues prefabValues, TypeTag enclosingType) {
        throw new EqualsVerifierInternalBugException("Can't shallow-scramble a record.");
    }

    @Override
    public ObjectAccessor<T> clear(
            Predicate<Field> canBeDefault, PrefabValues prefabValues, TypeTag enclosingType) {
        List<Object> params = new ArrayList<>();
        for (Field f : FieldIterable.ofIgnoringStatic(type())) {
            if (canBeDefault.test(f)) {
                params.add(PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType()));
            } else {
                TypeTag tag = TypeTag.of(f, enclosingType);
                params.add(prefabValues.giveRed(tag));
            }
        }
        T newObject = callRecordConstructor(constructor, params);
        return ObjectAccessor.of(newObject);
    }

    private Stream<Field> fields() {
        return StreamSupport.stream(FieldIterable.ofIgnoringStatic(type()).spliterator(), false);
    }

    private Constructor<T> getRecordConstructor() {
        try {
            List<Class<?>> constructorTypes =
                    fields().map(Field::getType).collect(Collectors.toList());
            Constructor<T> constructor =
                    type().getDeclaredConstructor(constructorTypes.toArray(new Class<?>[0]));
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private T callRecordConstructor(Constructor<T> constructor, List<?> params) {
        try {
            return constructor.newInstance(params.toArray(new Object[0]));
        } catch (InvocationTargetException e) {
            throw new ReflectionException("Record: failed to invoke constructor.", e);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            return null;
        }
    }
}
