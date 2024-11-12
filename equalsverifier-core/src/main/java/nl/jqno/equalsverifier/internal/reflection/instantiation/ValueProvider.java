package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.LinkedHashSet;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
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
     * Returns a tuple of prefabricated values of the specified type, or, if none exists, returns
     * an empty Optional.
     *
     * @param <T> The returned tuple will have this generic type.
     * @param tag A description of the desired type, including generic parameters.
     * @param attributes Provides metadata needed to provide a value.
     * @return A tuple of two different values of the given type, or an empty Optional if none
     *      could be found.
     */
    <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes);

    /**
     * Returns a tuple of prefabricated values of the specified type, or, if none exists, throws a
     * NoValueException.
     *
     * @param <T> The returned tuple will have this generic type.
     * @param tag A description of the desired type, including generic parameters.
     * @param attributes Provides metadata needed to provide a value.
     * @return A tuple of two different values of the given type, or an empty Optional if none
     *      could be found.
     * @throws NoValueException if no value could be found for the given tag.
     */
    default <T> Tuple<T> provideOrThrow(TypeTag tag, Attributes attributes) {
        return this.<T>provide(tag, attributes).orElseThrow(() -> new NoValueException(tag));
    }

    /**
     * Container for metadata needed to provide values.
     */
    public static final class Attributes {

        /**
         * Values can be assigned to a label; if one is specified, ValueProvider returns the value
         * assigned to it (or falls back to the value assigned to a null label). If label is null,
         * it immediately returns the value assigned to the null label.
         */
        public final String label;
        /**
         * Keeps track of recursion.
         */
        public final LinkedHashSet<TypeTag> typeStack;

        /** Private constructor. Use the factories instead. */
        private Attributes(String label, LinkedHashSet<TypeTag> typeStack) {
            this.label = label;
            this.typeStack = typeStack;
        }

        /**
         * Don't use a label when providing a value.
         *
         * @return An Attributes object with no label.
         */
        public static Attributes unlabeled() {
            return new Attributes(null, new LinkedHashSet<>());
        }

        /**
         * Use a label when providing a value.
         *
         * @param label The label to use.
         * @return An Attributes object with the given label.
         */
        public static Attributes labeled(String label) {
            return new Attributes(label, new LinkedHashSet<>());
        }

        /**
         * Clones the internal typeStack and adds a type to it.
         *
         * @param tag A type to add to the recursion checker.
         * @return A new Attributes object with a type added to its typeStack.
         */
        public Attributes cloneAndAdd(TypeTag tag) {
            LinkedHashSet<TypeTag> clone = new LinkedHashSet<>(typeStack);
            clone.add(tag);
            return new Attributes(label, clone);
        }
    }
}
