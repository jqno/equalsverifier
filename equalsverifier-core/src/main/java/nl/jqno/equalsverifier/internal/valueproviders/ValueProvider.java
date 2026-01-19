package nl.jqno.equalsverifier.internal.valueproviders;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Creator of prefabricated instances of classes.
 *
 * <p>
 * These instances are intended to be used to populate a subject, i.e. the class that is currently being tested by
 * EqualsVerifier.
 *
 * <p>
 * Only creates values ones, and caches them once they've been created. Takes generics into account; i.e.,
 * {@code List<Integer>} is different from {@code List<String>}.
 */
public interface ValueProvider {
    /**
     * Returns a tuple of two different prefabricated values of the specified type.
     *
     * @param <T>        The returned tuple will have this generic type.
     * @param tag        A description of the desired type, including generic parameters.
     * @param attributes The attributes of the field that the provided values must be linked to.
     * @return A tuple of two different values of the given type, or an empty Optional if none could be found.
     */
    <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes);

    /**
     * Returns a tuple of prefabricated values of the specified type, or, if none exists, throws a NoValueException.
     *
     * @param <T>        The returned tuple will have this generic type.
     * @param tag        A description of the desired type, including generic parameters.
     * @param attributes The attributes of the field that the provided values must be linked to.
     * @return A tuple of two different values of the given type, or an empty Optional if none could be found.
     * @throws NoValueException if no value could be found for the given tag.
     */
    default <T> Tuple<T> provideOrThrow(TypeTag tag, Attributes attributes) {
        return this
                .<T>provide(tag, attributes)
                .orElseThrow(
                    () -> new NoValueException(
                            "Could not find a value for " + tag + ". Please add prefab values for this type."));
    }
}
