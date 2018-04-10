package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a cache of factories, for {@link PrefabValues}.
 */
class FactoryCache {
    private final Map<Class<?>, PrefabValueFactory<?>> cache = new HashMap<>();

    /**
     * Adds the given factory to the cache and associates it with the given
     * type.
     */
    public <T> void put(Class<T> type, PrefabValueFactory<T> factory) {
        if (type != null) {
            cache.put(type, factory);
        }
    }

    /**
     * Retrieves the factory from the cache for the given type.
     *
     * What happens when there is no factory, is undefined. Always call
     * {@link #contains(Class)} first.
     */
    @SuppressWarnings("unchecked")
    public <T> PrefabValueFactory<T> get(Class<T> type) {
        return (PrefabValueFactory<T>)cache.get(type);
    }

    /**
     * Returns whether a factory is available for the given type.
     */
    public boolean contains(Class<?> type) {
        return cache.containsKey(type);
    }
}
