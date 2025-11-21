package nl.jqno.equalsverifier.internal.instantiation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class UserPrefabValueCaches {
    private final Map<Class<?>, Tuple<?>> cache = new HashMap<>();
    private final Map<Class<?>, Tuple<Supplier<?>>> supplierCache = new HashMap<>();

    /** Constructor. */
    public UserPrefabValueCaches() {}

    /**
     * Private copy constructor.
     *
     * @param other The {@link GenericPrefabValueProvider} to copy.
     */
    private UserPrefabValueCaches(UserPrefabValueCaches other) {
        this();
        cache.putAll(other.cache);
        supplierCache.putAll(other.supplierCache);
    }

    /**
     * Copies the cache of this PrefabValueProvider into a new instance.
     *
     * @return A copy of this PrefabValueProvider.
     */
    public UserPrefabValueCaches copy() {
        return new UserPrefabValueCaches(this);
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

    /**
     * Registers a resettable prefab value.
     *
     * @param type    The class of the prefabricated values.
     * @param red     A Supplier for an instance of {@code T}.
     * @param blue    A Supplier for another instance of {@code T}, not equal to {@code red}.
     * @param redCopy A Supplier for an instance of {@code T}, equal to {@code red} but preferably not the same
     *                    instance.
     * @param <T>     The type of the instances.
     */
    public <T> void registerResettable(Class<T> type, Supplier<T> red, Supplier<T> blue, Supplier<T> redCopy) {
        Tuple<Supplier<?>> tuple = new Tuple<>(red, blue, redCopy);
        supplierCache.put(type, tuple);
    }

    /**
     * Retrieves a tuple of prefabricated values from the regular cache.
     *
     * @param type The type of the desired prefabricated values.
     * @return A tuple of prefabricated values.
     */
    public Tuple<?> getPlain(Class<?> type) {
        return cache.get(type);
    }

    /**
     * Retrieves a resettable tuple of prefabricated values from the regular cache.
     *
     * @param type The type of the desired prefabricated values.
     * @return A resettable tuple of prefabricated values.
     */
    public Tuple<Supplier<?>> getResettable(Class<?> type) {
        return supplierCache.get(type);
    }

    /**
     * Whether instances of {@code type} should be cached.
     *
     * @param type The type whose instances should or should not be cached.
     * @return Whether or not the given type should be cached.
     */
    public boolean canBeCached(Class<?> type) {
        return !supplierCache.containsKey(type);
    }
}
