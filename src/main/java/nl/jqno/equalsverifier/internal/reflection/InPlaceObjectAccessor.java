package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Implementation of ObjectAccessor that modifies its wrapped object in-place through reflection.
 *
 * @param <T> The specified object's class.
 */
public final class InPlaceObjectAccessor<T> extends ObjectAccessor<T> {

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */ InPlaceObjectAccessor(T object, Class<T> type) {
        super(object, type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        T copy = Instantiator.of(type()).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        S copy = Instantiator.of(subclass).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public T copyIntoAnonymousSubclass() {
        T copy = Instantiator.of(type()).instantiateAnonymousSubclass();
        return copyInto(copy);
    }

    private <S> S copyInto(S copy) {
        for (Field field : FieldIterable.of(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.copyTo(copy);
        }
        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(PrefabValues prefabValues, TypeTag enclosingType) {
        for (Field field : FieldIterable.of(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.changeField(prefabValues, enclosingType);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> shallowScramble(PrefabValues prefabValues, TypeTag enclosingType) {
        for (Field field : FieldIterable.ofIgnoringSuper(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.changeField(prefabValues, enclosingType);
        }
        return this;
    }

    @Override
    public ObjectAccessor<T> clear(
            Predicate<Field> canBeDefault, PrefabValues prefabValues, TypeTag enclosingType) {
        for (Field field : FieldIterable.of(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.defaultField();
            if (!canBeDefault.test(field)) {
                accessor.changeField(prefabValues, enclosingType);
            }
        }
        return this;
    }

    @Override
    public ObjectAccessor<T> withDefaultedField(Field field) {
        fieldAccessorFor(field).defaultField();
        return this;
    }

    @Override
    public ObjectAccessor<T> withFieldSetTo(Field field, Object newValue) {
        fieldAccessorFor(field).set(newValue);
        return this;
    }
}
