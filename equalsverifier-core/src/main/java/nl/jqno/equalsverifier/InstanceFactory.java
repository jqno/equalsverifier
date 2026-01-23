package nl.jqno.equalsverifier;

/**
 * Functional interface for instantiating classes of some generic type {@code T}. The user can provide an
 * `InstanceFactory` for types that EqualsVerifier is unable to instantiate without help.
 *
 * This becomes useful in Java 26 where JEP 500 ("final means final") is active.
 *
 * @param <T> The type of the class to instantiate.
 *
 * @since 4.4
 */
@FunctionalInterface
public interface InstanceFactory<T> {

    T create(Values values);
}
