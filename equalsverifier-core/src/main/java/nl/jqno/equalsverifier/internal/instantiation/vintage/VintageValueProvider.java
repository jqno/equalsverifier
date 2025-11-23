package nl.jqno.equalsverifier.internal.instantiation.vintage;

import java.util.*;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.FallbackFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import nl.jqno.equalsverifier.internal.util.Rethrow;
import org.objenesis.Objenesis;

/**
 * Creator of prefabricated instances of classes, using a strategy that is aware of recursion and generics.
 *
 * Vintage in this case means that it employs the creation strategy that EqualsVerifier has been using since its
 * inception. This strategy is quite hacky and messy, and other strategies might be preferable. However, it is also
 * quite reliable because it's been around so long, so it remains a good fallback ValueProvider.
 */
@SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
public class VintageValueProvider implements ValueProvider {

    // I'd like to remove this, but that affects recursion detection it a way I can't yet explain
    private final Map<TypeTag, Tuple<?>> valueCache = new HashMap<>();

    private final ValueProvider prefabs;
    private final PrefabValueFactory<?> fallbackFactory;

    /**
     * Constructor.
     *
     * @param prefabs      Types that are already known and don't need to be constructed here.
     * @param factoryCache The factories that can be used to create values.
     * @param objenesis    To instantiate non-record classes.
     */
    public VintageValueProvider(ValueProvider prefabs, FactoryCache factoryCache, Objenesis objenesis) {
        this.prefabs = prefabs;
        this.fallbackFactory = new FallbackFactory<>(objenesis);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        return Rethrow.rethrow(() -> Optional.of(giveTuple(tag, attributes.typeStack())));
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> giveTuple(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        realizeCacheFor(tag, typeStack);
        return (Tuple<T>) valueCache.get(tag);
    }

    /**
     * Returns a prefabricated value of the specified type, that is different from the specified value.
     *
     * @param <T>       The type of the value.
     * @param tag       A description of the desired type, including generic parameters.
     * @param value     A value that is different from the value that will be returned.
     * @param typeStack Keeps track of recursion in the type.
     * @return A value that is different from {@code value}.
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    public <T> T giveOther(TypeTag tag, T value, LinkedHashSet<TypeTag> typeStack) {
        Class<T> type = tag.getType();
        if (value != null && !type.isAssignableFrom(value.getClass()) && !wraps(type, value.getClass())) {
            throw new ReflectionException("TypeTag does not match value.");
        }

        Tuple<T> tuple = giveTuple(tag, typeStack);
        if (tuple.red() == null) {
            return null;
        }
        if (type.isArray() && arraysAreDeeplyEqual(tuple.red(), value)) {
            return tuple.blue();
        }
        if (!type.isArray() && value != null) {
            try {
                // red's equals can potentially call an abstract method
                if (tuple.red().equals(value)) {
                    return tuple.blue();
                }
            }
            catch (AbstractMethodError e) {
                return tuple.red();
            }
        }
        return tuple.red();
    }

    // CHECKSTYLE ON: CyclomaticComplexity

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    /**
     * Makes sure that values for the specified type are present in the cache, but doesn't return them.
     *
     * @param <T>       The desired type.
     * @param tag       A description of the desired type, including generic parameters.
     * @param typeStack Keeps track of recursion in the type.
     */
    public <T> void realizeCacheFor(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (!valueCache.containsKey(tag)) {
            Tuple<T> tuple = createTuple(tag, typeStack);
            valueCache.put(tag, tuple);
        }
    }

    private <T> Tuple<T> createTuple(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (typeStack.contains(tag)) {
            throw new RecursionException(typeStack);
        }

        var userPrefab = prefabs.provide(tag, Attributes.empty()); // Prefabs aren't linked to a field, so empty is fine
        if (userPrefab.isPresent()) {
            @SuppressWarnings("unchecked")
            var result = (Tuple<T>) userPrefab.get();
            return result;
        }

        @SuppressWarnings("unchecked")
        var result = (Tuple<T>) fallbackFactory.createValues(tag, this, typeStack);
        return result;
    }
}
