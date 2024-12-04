package nl.jqno.equalsverifier.internal.reflection;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;

/** Contains a cache for values connected to specific fields, for {@link SubjectCreator}. */
public class FieldCache {

    /**
     * We store Strings instead of Fields, to make it easier to interact with when we don't
     * actually have a reference to a Field.
     */
    private final Map<String, Tuple<?>> cache = new HashMap<>();

    /**
     * Adds the given factory to the cache and associates it with the given type.
     *
     * @param <T> The type of the values.
     * @param fieldName The name of the field to associate with the values.
     * @param tuple The tuple that contains the values.
     */
    public <T> void put(String fieldName, Tuple<T> tuple) {
        if (fieldName != null) {
            cache.put(fieldName, tuple);
        }
    }

    /**
     * Retrieves the values from the cache for the given field.
     *
     * <p>What happens when there are no values, is undefined. Always call {@link #contains(String)}
     * first.
     *
     * @param <T> The returned values will have this as generic type.
     * @param fieldName The name of the field for which values are needed.
     * @return A tuple of values for the given type, or {@code null} if none is available.
     */
    @SuppressWarnings("unchecked")
    public <T> Tuple<T> get(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        return (Tuple<T>) cache.get(fieldName);
    }

    /**
     * @param fieldName The name of the field for which values are needed.
     * @return Whether values are available for the given field.
     */
    public boolean contains(String fieldName) {
        return cache.containsKey(fieldName);
    }

    /**
     * @return The fields preset in the cache.
     */
    public Set<String> getFieldNames() {
        return new HashSet<>(cache.keySet());
    }
}
