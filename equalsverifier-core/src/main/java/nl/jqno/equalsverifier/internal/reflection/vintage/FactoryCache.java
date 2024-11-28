package nl.jqno.equalsverifier.internal.reflection.vintage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.PrefabValueFactory;

/** Contains a cache of factories, for {@link VintageValueProvider}. */
public class FactoryCache {

    /**
     * We store Strings instead of Classes, so that the cache can be lazy and initializers won't be
     * called until the class is actually needed.
     */
    private final Map<Key, PrefabValueFactory<?>> cache = new HashMap<>();

    /**
     * Adds the given factory to the cache and associates it with the given type.
     *
     * @param <T> The type of the factory.
     * @param type The type to associate with the factory.
     * @param factory The factory to associate with the type.
     */
    public <T> void put(Class<?> type, PrefabValueFactory<T> factory) {
        if (type != null) {
            put(Key.of(type.getName()), factory);
        }
    }

    /**
     * Adds the given factory to the cache and associates it with the given type.
     *
     * @param <T> The type of the factory.
     * @param type The type to associate with the factory.
     * @param label The label that the factory is linked to, or null if it is not assigned to any label.
     * @param factory The factory to associate with the type.
     */
    public <T> void put(Class<?> type, String label, PrefabValueFactory<T> factory) {
        if (type != null) {
            put(Key.of(type.getName(), label), factory);
        }
    }

    /**
     * Adds the given factory to the cache and associates it with the given type name.
     *
     * @param <T> Should match {@code typeName}.
     * @param typeName The fully qualified name of the type.
     * @param factory The factory to associate with {@code typeName}
     */
    public <T> void put(String typeName, PrefabValueFactory<T> factory) {
        if (typeName != null) {
            put(Key.of(typeName), factory);
        }
    }

    private <T> void put(Key key, PrefabValueFactory<T> factory) {
        cache.put(key, factory);
    }

    /**
     * Retrieves the factory from the cache for the given type.
     *
     * <p>What happens when there is no factory, is undefined. Always call {@link #contains(Class)}
     * first.
     *
     * @param <T> The returned factory will have this as generic type.
     * @param type The type for which a factory is needed.
     * @return A factory for the given type, or {@code null} if none is available.
     */
    public <T> PrefabValueFactory<T> get(Class<T> type) {
        return get(type, null);
    }

    /**
     * Retrieves the factory from the cache for the given type.
     *
     * <p>What happens when there is no factory, is undefined. Always call {@link #contains(Class)}
     * first.
     *
     * @param <T> The returned factory will have this as generic type.
     * @param type The type for which a factory is needed.
     * @param label Returns only the factory assigned to the given label, or if label is null,
     *      returns the factory that's not assigned to any label.
     * @return A factory for the given type, or {@code null} if none is available.
     */
    @SuppressWarnings("unchecked")
    public <T> PrefabValueFactory<T> get(Class<T> type, String label) {
        if (type == null) {
            return null;
        }
        return (PrefabValueFactory<T>) cache.get(Key.of(type.getName(), label));
    }

    /**
     * @param type The type for which a factory is needed.
     * @return Whether a factory is available for the given type.
     */
    public boolean contains(Class<?> type) {
        return cache.containsKey(Key.of(type.getName()));
    }

    /**
     * @param type The type for which a factory is needed.
     * @param label The label that the factory needs to be assigned to.
     * @return Whether a factory is available for the given type.
     */
    public boolean contains(Class<?> type, String label) {
        return cache.containsKey(Key.of(type.getName(), label));
    }

    /**
     * Returns a new {@code FactoryCache} instance containing the factories from {@code this}.
     *
     * @return a new instance containing factories from {@code this}
     */
    public FactoryCache copy() {
        FactoryCache result = new FactoryCache();
        addAll(result, this);
        return result;
    }

    /**
     * Returns a new {@code FactoryCache} instance containing the factories from {@code this} and
     * from the {@code other} cache.
     *
     * @param other The other cache
     * @return a new instance containing factories from {@code this} and {@code other}
     */
    public FactoryCache merge(FactoryCache other) {
        FactoryCache result = new FactoryCache();
        addAll(result, this);
        addAll(result, other);
        return result;
    }

    private void addAll(FactoryCache to, FactoryCache from) {
        for (Map.Entry<Key, PrefabValueFactory<?>> entry : from.cache.entrySet()) {
            to.put(entry.getKey(), entry.getValue());
        }
    }

    static final class Key {

        final String typeName;
        final String label;

        private Key(String typeName, String label) {
            this.typeName = typeName;
            this.label = label;
        }

        public static Key of(String typeName) {
            return new Key(typeName, null);
        }

        public static Key of(String typeName, String label) {
            return new Key(typeName, label);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            return Objects.equals(typeName, other.typeName) && Objects.equals(label, other.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeName, label);
        }

        @Override
        public String toString() {
            return "Key: [" + typeName + "/" + label + "]";
        }
    }
}
