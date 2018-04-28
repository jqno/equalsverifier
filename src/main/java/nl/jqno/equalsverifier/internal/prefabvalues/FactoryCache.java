package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a cache of factories, for {@link PrefabValues}.
 */
class FactoryCache {
    /**
     * We store Strings instead of Classes, so that the cache can be lazy
     * and initializers won't be called until the class is actually needed.
     */
    private final Map<String, PrefabValueFactory<?>> cache = new HashMap<>();

    /**
     * Adds the given factory to the cache and associates it with the given
     * type.
     */
    public <T> void put(Class<?> type, PrefabValueFactory<T> factory) {
        if (type != null) {
            cache.put(type.getName(), factory);
        }
    }

    /**
     * Adds the given factory to the cache and associates it with the given
     * type name.
     */
    public <T> void put(String typeName, PrefabValueFactory<T> factory) {
        if (typeName != null) {
            cache.put(typeName, factory);
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
        if (type == null) {
            return null;
        }
        return (PrefabValueFactory<T>)cache.get(type.getName());
    }

    /**
     * Returns whether a factory is available for the given type.
     */
    public boolean contains(Class<?> type) {
        return cache.containsKey(type.getName());
    }
}
