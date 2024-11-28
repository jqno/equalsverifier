package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of instances that have been created before, e.g. by other ValueProviders.
 */
public class CachedValueProvider implements ValueProvider {

    private final Map<Key, Tuple<?>> cache = new HashMap<>();

    /**
     * Adds a value to the cache.
     *
     * @param tag The type to assign the value to.
     * @param label The label that the value should be linked to.
     * @param tuple The value to add to the cache.
     */
    public void put(TypeTag tag, String label, Tuple<?> tuple) {
        cache.put(Key.of(tag, label), tuple);
    }

    /**
     * {@inheritDoc}}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Key first = Key.of(tag, attributes.label);
        if (cache.containsKey(first)) {
            return Optional.of((Tuple<T>) cache.get(first));
        }
        Key second = Key.of(tag, null);
        if (cache.containsKey(second)) {
            return Optional.of((Tuple<T>) cache.get(second));
        }
        return Optional.empty();
    }

    static final class Key {

        final TypeTag tag;
        final String label;

        private Key(TypeTag tag, String label) {
            this.tag = tag;
            this.label = label;
        }

        public static Key of(TypeTag tag, String label) {
            return new Key(tag, label);
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

        @Override
        public String toString() {
            return "Key: [" + tag + "/" + label + "]";
        }
    }
}
