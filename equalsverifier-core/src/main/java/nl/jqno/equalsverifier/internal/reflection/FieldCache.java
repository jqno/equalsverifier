package nl.jqno.equalsverifier.internal.reflection;

import java.util.*;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

/** Contains a cache for values connected to specific fields, for {@link SubjectCreator}. */
public class FieldCache {

    /**
     * We store Strings instead of Fields, to make it easier to interact with when we don't actually have a reference to
     * a Field.
     */
    private final Map<Key, Tuple<?>> cache = new HashMap<>();

    /**
     * Adds the given factory to the cache and associates it with the given type.
     *
     * @param <T>       The type of the values.
     * @param fieldName The name of the field to associate with the values.
     * @param fieldType The type of the field to associate with the values.
     * @param tuple     The tuple that contains the values.
     */
    public <T> void put(String fieldName, TypeTag fieldType, Tuple<T> tuple) {
        if (fieldName != null && fieldType != null) {
            cache.put(new Key(fieldName, fieldType), tuple);
        }
    }

    /**
     * Retrieves the values from the cache for the given field.
     *
     * <p>
     * What happens when there are no values, is undefined. Always call {@link #contains(String, TypeTag)} first.
     *
     * @param <T>       The returned values will have this as generic type.
     * @param fieldName The name of the field for which values are needed.
     * @param fieldType The type of the field for which values are needed.
     * @return A tuple of values for the given type, or {@code null} if none is available.
     */
    @SuppressWarnings("unchecked")
    public <T> Tuple<T> get(String fieldName, TypeTag fieldType) {
        if (fieldName == null || fieldType == null) {
            return null;
        }
        return (Tuple<T>) cache.get(new Key(fieldName, fieldType));
    }

    /**
     * Returns whether values are available for the given field.
     *
     * @param fieldName The name of the field for which values are needed.
     * @param fieldType The type of the field for which values are needed.
     * @return Whether values are available for the given field.
     */
    public boolean contains(String fieldName, TypeTag fieldType) {
        return cache.containsKey(new Key(fieldName, fieldType));
    }

    /**
     * Returns the fields preset in the cache.
     *
     * @return The fields preset in the cache.
     */
    public Set<String> getFieldNames() {
        return new HashSet<>(cache.keySet().stream().map(key -> key.fieldName).collect(Collectors.toSet()));
    }

    static final class Key {
        private final String fieldName;
        private final TypeTag fieldType;

        public Key(String fieldName, TypeTag fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            return Objects.equals(fieldName, other.fieldName) && Objects.equals(fieldType, other.fieldType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldName, fieldType);
        }
    }
}
