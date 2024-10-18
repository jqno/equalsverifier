package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.*;
import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericPrefabValueProvider implements ValueProvider {

    private final Map<Key, Func<?>> cache = new HashMap<>();
    private ValueProvider provider;

    /** Constructor. */
    public GenericPrefabValueProvider() {}

    /**
     * Private copy constructor.
     *
     * @param other The {@link GenericPrefabValueProvider} to copy.
     */
    private GenericPrefabValueProvider(GenericPrefabValueProvider other) {
        this();
        cache.putAll(other.cache);
        setProvider(other.provider);
    }

    /**
     * Copies the cache of this GenericPrefabValueProvider into a new instance.
     *
     * @return A copy of this GenericPrefabValueProvider.
     */
    public GenericPrefabValueProvider copy() {
        return new GenericPrefabValueProvider(this);
    }

    /**
     * Registers a prefab value with a single generic type argument.
     *
     * @param type The class of the prefabricated values.
     * @param label The label that the prefabricated value is linked to, or null if the value is
     *      not assigned to any label.
     * @param factory A factory that can produce instances for {@code type}.
     * @param <T> The type of the instances.
     */
    public <T> void register(Class<T> type, String label, Func1<?, T> factory) {
        Key key = new Key(type, label);
        cache.put(key, factory);
    }

    /**
     * Registers a prefab value with two generic type arguments.
     *
     * @param type The class of the prefabricated values.
     * @param label The label that the prefabricated value is linked to, or null if the value is
     *      not assigned to any label.
     * @param factory A factory that can produce instances for {@code type}.
     * @param <T> The type of the instances.
     */
    public <T> void register(Class<T> type, String label, Func2<?, ?, T> factory) {
        Key key = new Key(type, label);
        cache.put(key, factory);
    }

    /**
     * Configures the valueProvider to be used for recursive value lookups.
     *
     * @param valueProvider The valueProvider to use.
     */
    public void setProvider(ValueProvider valueProvider) {
        this.provider = valueProvider;
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
