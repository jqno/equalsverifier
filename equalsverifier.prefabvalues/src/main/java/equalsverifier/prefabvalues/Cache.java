package equalsverifier.prefabvalues;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a cache of prefabricated values, for {@link PrefabValues}.
 */
class Cache {
    @SuppressWarnings("rawtypes")
    private final Map<TypeTag, Tuple> cache = new HashMap<>();

    /**
     * Adds a prefabricated value to the cache for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     * @param red A "red" value for the given type.
     * @param black A "black" value for the given type.
     * @param redCopy A shallow copy of the given red value.
     */
    public <T> void put(TypeTag tag, T red, T black, T redCopy) {
        cache.put(tag, new Tuple<>(red, black, redCopy));
    }

    /**
     * Returns a {@link Tuple} of prefabricated values for the specified type.
     *
     * What happens when there is no value, is undefined. Always call
     * {@link #contains(TypeTag)} first.
     *
     * @param tag A description of the type. Takes generics into account.
     */
    @SuppressWarnings("unchecked")
    public <T> Tuple<T> getTuple(TypeTag tag) {
        return cache.get(tag);
    }

    /**
     * Returns whether prefabricated values are available for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     */
    public boolean contains(TypeTag tag) {
        return cache.containsKey(tag);
    }
}
