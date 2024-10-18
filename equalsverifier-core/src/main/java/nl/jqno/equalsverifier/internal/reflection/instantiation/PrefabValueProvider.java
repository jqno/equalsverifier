package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Provider of prefabricated instances of classes that have been provided by the user.
 */
public class PrefabValueProvider implements ValueProvider {

    private final Map<Key, Tuple<?>> cache = new HashMap<>();

    /** Constructor. */
    public PrefabValueProvider() {}

    /**
     * Private copy constructor.
     *
     * @param other The {@link GenericPrefabValueProvider} to copy.
     */
    private PrefabValueProvider(PrefabValueProvider other) {
        this();
        cache.putAll(other.cache);
    }

    /**
     * Copies the cache of this PrefabValueProvider into a new instance.
     *
     * @return A copy of this PrefabValueProvider.
     */
    public PrefabValueProvider copy() {
        return new PrefabValueProvider(this);
    }

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
    public <T> void register(Class<T> type, String label, T red, T blue, T redCopy) {
        Tuple<T> tuple = Tuple.of(red, blue, redCopy);
        register(type, label, tuple);
    }

    /**
     * Registers a prefab value.
     *
     * @param type The class of the prefabricated values.
     * @param label The label that the prefabricated value is linked to, or null if the value is
     *      not assigned to any label.
     * @param tuple A tuple of instances of {@code T}.
     * @param <T> The type of the instances.
     */
    public <T> void register(Class<T> type, String label, Tuple<T> tuple) {
        Key key = new Key(type, label);
        cache.put(key, tuple);
    }

    /**
     * Returns the names of the fields for which a prefab value is currently known.
     *
     * @return The names of the fields for which a prefab value is currently known.
     */
    public Set<String> getFieldNames() {
        return cache.keySet().stream().map(k -> k.label).collect(Collectors.toSet());
    }
}
