package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Creator of prefabricated instances of classes.
 *
 * <p>These instances are intended to be used to populate a subject, i.e. the class that is
 * currently being tested by EqualsVerifier.
 *
 * <p>Only creates values ones, and caches them once they've been created. Takes generics into
 * account; i.e., {@code List<Integer>} is different from {@code List<String>}.
 */
public interface ValueProvider {
    /**
     * Returns a tuple of two different prefabricated values of the specified type.
     *
     * @param <T> The returned tuple will have this generic type.
     * @param tag A description of the desired type, including generic parameters.
     * @return A tuple of two different values of the given type, or an empty Optional if none
     *      could be found.
     */
    <T> Optional<Tuple<T>> provide(TypeTag tag);
}
