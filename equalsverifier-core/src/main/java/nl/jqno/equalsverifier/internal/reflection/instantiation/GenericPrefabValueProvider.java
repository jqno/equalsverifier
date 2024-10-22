package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.Context;

/**
 * Provider of instances of generic types; factories of which have been provided by the user.
 *
 * Needs to be constructed by means of a GenericFactories instance, because of dependencies that
 * would otherwise be cyclic, in the {@link Context} class.
 */
public class GenericPrefabValueProvider implements ValueProvider {

    private final Map<Key, Func<?>> cache;
    private final ValueProvider provider;

    /**
     * Private constructor.
     *
     * @param factories The {@link GenericFactories} to use the cache from.
     * @param provider A ValueProvider to use to provide instances for generic type parameters.
     */
    public GenericPrefabValueProvider(GenericFactories factories, ValueProvider provider) {
        this.cache = factories.cache;
        this.provider = provider;
    }

    /**
     * Container for a cache that will be assigned to the GenericPrefabValueProvider when it is constructed.
     */
    public static class GenericFactories {

        private final Map<Key, Func<?>> cache = new HashMap<>();

        /**
         * Registers a prefab value with a single generic type argument.
         *
         * @param type The class of the prefabricated values.
         * @param factory A factory that can produce instances for {@code type}.
         * @param <T> The type of the instances.
         */
        public <T> void register(Class<T> type, Func1<?, T> factory) {
            Key key = new Key(type, null);
            cache.put(key, factory);
        }

        /**
         * Registers a prefab value with two generic type arguments.
         *
         * @param type The class of the prefabricated values.
         * @param factory A factory that can produce instances for {@code type}.
         * @param <T> The type of the instances.
         */
        public <T> void register(Class<T> type, Func2<?, ?, T> factory) {
            Key key = new Key(type, null);
            cache.put(key, factory);
        }

        /**
         * Copies the cache of this GenericFactories into a new instance.
         *
         * @return A copy of this GenericFactories.
         */
        public GenericFactories copy() {
            GenericFactories copy = new GenericFactories();
            copy.cache.putAll(this.cache);
            return copy;
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        Class<T> type = tag.getType();
        Optional<Func<T>> maybeFactory = findFactory(type, label);
        if (!maybeFactory.isPresent()) {
            return Optional.empty();
        }
        return findValue(tag, maybeFactory.get());
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<Func<T>> findFactory(Class<?> type, String label) {
        Func<T> result = (Func<T>) cache.get(new Key(type, label));
        if (result == null) {
            result = (Func<T>) cache.get(new Key(type, null));
        }
        return Optional.ofNullable(result);
    }

    private <T> Optional<Tuple<T>> findValue(TypeTag tag, Func<T> factory) {
        if (provider == null) {
            throw new EqualsVerifierInternalBugException(
                "valueProvider not initialized in GenericPrefabValueProvider"
            );
        }
        List<Object> redValues = new ArrayList<>();
        List<Object> blueValues = new ArrayList<>();
        List<Object> redCopyValues = new ArrayList<>();

        for (TypeTag generic : tag.getGenericTypes()) {
            Tuple<?> tuple = provider.provide(generic);
            redValues.add(tuple.getRed());
            blueValues.add(tuple.getBlue());
            redCopyValues.add(tuple.getRedCopy());
        }

        T red = factory.apply(redValues);
        T blue = factory.apply(blueValues);
        T redCopy = factory.apply(redCopyValues);

        return Optional.of(Tuple.of(red, blue, redCopy));
    }
}
