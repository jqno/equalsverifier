package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

abstract class ValueSupplier<T> implements Supplier<Optional<Tuple<T>>> {
    private final Class<T> type;

    public ValueSupplier(Class<T> type) {
        this.type = type;
    }

    @Override
    public abstract Optional<Tuple<T>> get();

    protected boolean is(Class<?> otherType) {
        return type.equals(otherType);
    }

    /*
     * T is an unknown type at compile-time, but the types of the three parameters are known at compile-time. The
     * compiler thinks these are not the same as T, even though we know they are. Therefore, we must trick the compiler
     * by introducing type parameter S and casting the values.
     */
    @SuppressWarnings("unchecked")
    protected <S> Optional<Tuple<T>> val(S red, S blue, S redCopy) {
        return Optional.of(new Tuple<>((T) red, (T) blue, (T) redCopy));
    }
}
