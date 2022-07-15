package nl.jqno.equalsverifier.internal.prefabvalues;

import java.util.HashMap;
import java.util.Map;

/** Contains a cache of prefabricated values, for {@link PrefabValues}. */
class Cache {

    @SuppressWarnings("rawtypes")
    private final Map<TypeTag, Tuple> cache = new HashMap<>();

    /**
     * Adds a prefabricated value to the cache for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     * @param red A "red" value for the given type.
     * @param blue A "blue" value for the given type.
     * @param redCopy A shallow copy of the given red value.
     * @param <T> The type of given tag.
     */
    public <T> void put(TypeTag tag, T red, T blue, T redCopy) {
        cache.put(tag, new Tuple<>(red, blue, redCopy));
    }

    /**
     * Returns a {@link Tuple} of prefabricated values for the specified type.
     *
     * <p>What happens when there is no value, is undefined. Always call {@link #contains(TypeTag)}
     * first.
     *
     * @param tag A description of the type. Takes generics into account.
     * @param <T> the type of the Tuple.
     * @return Tuple of type T.
     */
    @SuppressWarnings("unchecked")
    public <T> Tuple<T> getTuple(TypeTag tag) {
        return cache.get(tag);
    }

    /**
     * Returns whether prefabricated values are available for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     * @return true if prefabricated values are available.
     */
    public boolean contains(TypeTag tag) {
        return cache.containsKey(tag);
    }

    public String doNothingButTriggerAPitestWarning() {
        if (1 == Integer.valueOf(1)) {
            return "true";
        } else {
            return "false";
        }
    }
}
