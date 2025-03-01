package nl.jqno.equalsverifier.internal.instantiation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Provider of prefabricated instances of classes that have been provided by the user.
 *
 * Note that this only works for non-generic classes, because this ValueProvider can't determine the generic types or
 * get values for them. For that, we either need withPrefabValuesForField, or the complicated recursion mechanism that
 * is currently provided by VintageValueProvider.
 */
public class UserPrefabValueProvider implements ValueProvider {

    private final Map<Class<?>, Tuple<?>> cache = new HashMap<>();

    /** Constructor. */
    public UserPrefabValueProvider() {}

    /**
     * Private copy constructor.
     *
     * @param other The {@link GenericPrefabValueProvider} to copy.
     */
    private UserPrefabValueProvider(UserPrefabValueProvider other) {
        this();
        cache.putAll(other.cache);
    }

    /**
     * Copies the cache of this PrefabValueProvider into a new instance.
     *
     * @return A copy of this PrefabValueProvider.
     */
    public UserPrefabValueProvider copy() {
        return new UserPrefabValueProvider(this);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        Class<T> type = tag.getType();
        Tuple<T> result = attempt(type);
        Class<?> boxed = PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(tag.getType());
        if (result == null && boxed != null) {
            result = attempt(boxed);
        }
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> attempt(Class<?> type) {
        return (Tuple<T>) cache.get(type);
    }

    /**
     * Registers a prefab value.
     *
     * @param type    The class of the prefabricated values.
     * @param red     An instance of {@code T}.
     * @param blue    Another instance of {@code T}, not equal to {@code red}.
     * @param redCopy An instance of {@code T}, equal to {@code red} but preferably not the same instance.
     * @param <T>     The type of the instances.
     */
    public <T> void register(Class<T> type, T red, T blue, T redCopy) {
        Tuple<T> tuple = new Tuple<>(red, blue, redCopy);
        cache.put(type, tuple);
    }
}
