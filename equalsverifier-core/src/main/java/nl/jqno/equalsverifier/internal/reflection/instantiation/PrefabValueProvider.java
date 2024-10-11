package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Provider of prefabricated instances of classes that have been provided by the user.
 */
public class PrefabValueProvider implements ValueProvider {

    private final Map<Key, Tuple<?>> cache = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        Class<T> type = tag.getType();
        Tuple<T> result = attempt(type, label);
        Class<?> boxed = PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(tag.getType());
        if (result == null && boxed != null) {
            result = attempt(boxed, label);
        }
        if (result == null) {
            result = attempt(type, null);
        }
        if (result == null && boxed != null) {
            result = attempt(boxed, null);
        }
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> attempt(Class<?> type, String label) {
        return (Tuple<T>) cache.get(new Key(type, label));
    }

    /**
     * Registers a prefab value.
     *
     * @param type The class of the prefabricated values.
     * @param label The label that the prefabricated value is linked to, or null if the value is
     *      not assigned to any label.
     * @param red An instance of {@code T}.
     * @param blue Another instance of {@code T}, not equal to {@code red}.
     * @param redCopy An instance of {@code T}, equal to {@code red} but preferably not the same
     *      instance.
     * @param <T> The type of the instances.
     */
    public <T> void register(Class<?> type, String label, T red, T blue, T redCopy) {
        Key key = new Key(type, label);
        Tuple<T> value = Tuple.of(red, blue, redCopy);
        cache.put(key, value);
    }

    public Set<String> getFieldNames() {
        return cache.keySet().stream().map(k -> k.label).collect(Collectors.toSet());
    }

    static final class Key {

        private final Class<?> type;
        private final String label;

        private Key(Class<?> type, String label) {
            this.type = type;
            this.label = label;
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

    static interface Value<T> extends Supplier<Tuple<T>> {}
}
