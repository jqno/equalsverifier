package nl.jqno.equalsverifier;

import java.util.List;

/**
 * Functional interface for generating prefab values of some generic type {@code T}.
 *
 * <p>For each generic type parameter for {@code T}, a value of that type will be supplied in the
 * {@link List} parameter of {@link Func#apply(List)}.
 *
 * @param <T> The type of prefab value.
 */
@FunctionalInterface
public interface Func<T> {
    T apply(List<?> values);

    /**
     * Functional interface for generating prefab values of a generic type {@code T} that has
     * exactly 1 generic parameter {@code A}.
     *
     * <p>A value of {@link A} will be supplied in the {@link Func1#supply(Object)} method.
     *
     * @param <A> The type of {@code T}'s generic parameter.
     * @param <T> The type of prefab value.
     */
    @SuppressWarnings("unchecked")
    @FunctionalInterface
    interface Func1<A, T> extends Func<T> {
        @Override
        default T apply(List<?> values) {
            return supply((A) values.get(0));
        }

        T supply(A a);
    }

    /**
     * Functional interface for generating prefab values of a generic type {@code T} that has
     * exactly 2 generic parameters, {@code A} and {@code B}.
     *
     * <p>Values of {@link A} and {@code B} will be supplied in the {@link Func2#supply(Object,
     * Object)} method.
     *
     * @param <A> The type of {@code T}'s first generic parameter.
     * @param <B> The type of {@code T}'s second generic parameter.
     * @param <T> The type of prefab value.
     */
    @SuppressWarnings("unchecked")
    @FunctionalInterface
    interface Func2<A, B, T> extends Func<T> {
        @Override
        default T apply(List<?> values) {
            return supply((A) values.get(0), (B) values.get(1));
        }

        T supply(A a, B b);
    }
}
