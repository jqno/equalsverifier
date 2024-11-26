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
     * @param type The type to assign the value to.
     * @param label The label that the value should be linked to.
     * @param tuple The value to add to the cache.
     */
    public void put(Class<?> type, String label, Tuple<?> tuple) {
        cache.put(Key.of(type, label), tuple);
    }

    /**
     * Checks if a value is already present in the cache.
     *
     * @param type The type for which we want to see if a value is present.
     * @param label The label that the value should be linked to.
     * @return Whether a value for the given tag and label is present in the cache.
     */
    public boolean contains(Class<?> type, String label) {
        Key key = Key.of(type, label);
        return cache.containsKey(key);
    }

    /**
     * {@inheritDoc}}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Key first = Key.of(tag.getType(), attributes.label);
        if (cache.containsKey(first)) {
            return Optional.of((Tuple<T>) cache.get(first));
        }
        Key second = first.removeLabel();
        if (cache.containsKey(second)) {
            return Optional.of((Tuple<T>) cache.get(second));
        }
        return Optional.empty();
    }

    private static final class Key {

        final Class<?> type;
        final String label;

        private Key(Class<?> type, String label) {
            this.type = type;
            this.label = label;
        }

        public static Key of(Class<?> type, String label) {
            return new Key(type, label);
        }

        public Key removeLabel() {
            return Key.of(type, null);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            return Objects.equals(type, other.type) && Objects.equals(label, other.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, label);
        }

        @Override
        public String toString() {
            return "Key: [" + type + "/" + label + "]";
        }
    }
}
