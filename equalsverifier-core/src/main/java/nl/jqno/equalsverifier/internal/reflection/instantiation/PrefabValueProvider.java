package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of prefabricated instances of classes that have been provided by the user.
 */
public class PrefabValueProvider implements ValueProvider {

    private final Map<Key, Tuple<?>> cache = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        Key key = new Key(tag, label);
        return Optional.ofNullable((Tuple<T>) cache.get(key));
    }

    /**
     * Registers a prefab value.
     *
     * @param tag The class of the prefabricated values.
     * @param label The label that the prefabricated value is linked to, or null if the value is
     *      not assigned to any label.
     * @param red An instance of {@code T}.
     * @param blue Another instance of {@code T}, not equal to {@code red}.
     * @param redCopy An instance of {@code T}, equal to {@code red} but preferably not the same
     *      instance.
     * @param <T> The type of the instances.
     */
    public <T> void register(TypeTag tag, String label, T red, T blue, T redCopy) {
        Key key = new Key(tag, label);
        Tuple<T> value = Tuple.of(red, blue, redCopy);
        cache.put(key, value);
    }

    static final class Key {

        private final TypeTag tag;
        private final String label;

        private Key(TypeTag tag, String label) {
            this.tag = tag;
            this.label = label;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            return Objects.equals(tag, other.tag) && Objects.equals(label, other.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, label);
        }
    }
}
