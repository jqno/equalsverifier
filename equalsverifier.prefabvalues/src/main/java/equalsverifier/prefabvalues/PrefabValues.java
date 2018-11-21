package equalsverifier.prefabvalues;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.prefabvalues.factories.FallbackFactory;
import equalsverifier.prefabvalues.factories.PrefabValueFactory;
import equalsverifier.reflection.ReflectionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Container and creator of prefabricated instances of objects and classes.
 *
 * Only creates values ones, and caches them once they've been created. Takes
 * generics into account; i.e., {@code List<Integer>} is different from
 * {@code List<String>}.
 */
public class PrefabValues extends PrefabAbstract {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_OBJECT_MAPPER = createPrimitiveObjectMapper();

    private final Cache cache = new Cache();
    private final FactoryCache factoryCache;
    private final PrefabValueFactory<?> fallbackFactory = new FallbackFactory<>();

    /**
     * Constructor.
     *
     * @param factoryCache The factories that can be used to create values.
     */
    public PrefabValues(FactoryCache factoryCache) {
        this.factoryCache = factoryCache;
    }

    /**
     * Default Constructor for Java 9
     */
    public PrefabValues(){
        this.factoryCache = null;
    }
    /**
     * Returns the "red" prefabricated value of the specified type.
     *
     * It's always a different value from the "black" one.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @return The "red" prefabricated value.
     */
    @Override
    public <T> T giveRed(TypeTag tag) {
        return this.<T>giveTuple(tag).getRed();
    }

    /**
     * Returns the "black" prefabricated value of the specified type.
     *
     * It's always a different value from the "red" one.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @return The "black" prefabricated value.
     */
    @Override
    public <T> T giveBlack(TypeTag tag) {
        return this.<T>giveTuple(tag).getBlack();
    }

    /**
     * Returns a shallow copy of the "red" prefabricated value of the specified
     * type.
     *
     * When possible, it's equal to but not the same as the "red" object.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @return A shallow copy of the "red" prefabricated value.
     */
    @Override
    public <T> T giveRedCopy(TypeTag tag) {
        return this.<T>giveTuple(tag).getRedCopy();
    }

    /**
     * Returns a tuple of two different prefabricated values of the specified
     * type.
     *
     * @param <T> The returned tuple will have this generic type.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @return A tuple of two different values of the given type.
     */
    @Override
    public <T> Tuple<T> giveTuple(TypeTag tag) {
        realizeCacheFor(tag, emptyStack());
        return cache.getTuple(tag);
    }

    /**
     * Returns a prefabricated value of the specified type, that is different
     * from the specified value.
     *
     * @param <T> The type of the value.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @param value A value that is different from the value that will be
     *              returned.
     * @return A value that is different from {@code value}.
     */
    @Override
    public <T> T giveOther(TypeTag tag, T value) {
        Class<T> type = tag.getType();
        if (value != null && !type.isAssignableFrom(value.getClass()) && !wraps(type, value.getClass())) {
            throw new ReflectionException("TypeTag does not match value.");
        }

        Tuple<T> tuple = giveTuple(tag);
        if (tuple.getRed() == null) {
            return null;
        }
        if (type.isArray() && arraysAreDeeplyEqual(tuple.getRed(), value)) {
            return tuple.getBlack();
        }
        if (!type.isArray() && tuple.getRed().equals(value)) {
            return tuple.getBlack();
        }
        return tuple.getRed();
    }

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    private LinkedHashSet<TypeTag> emptyStack() {
        return new LinkedHashSet<>();
    }

    /**
     * Makes sure that values for the specified type are present in the cache,
     * but doesn't return them.
     *
     * @param <T> The desired type.
     * @param tag A description of the desired type, including generic
     *            parameters.
     * @param typeStack Keeps track of recursion in the type.
     */
    @Override
    public <T> void realizeCacheFor(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (!cache.contains(tag)) {
            Tuple<T> tuple = createTuple(tag, typeStack);
            addToCache(tag, tuple);
        }
    }

    private <T> Tuple<T> createTuple(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (typeStack.contains(tag)) {
            throw new RecursionException(typeStack);
        }

        Class<T> type = tag.getType();
        if (factoryCache.contains(type)) {
            PrefabValueFactory<T> factory = factoryCache.get(type);
            return factory.createValues(tag, this, typeStack);
        }

        @SuppressWarnings("unchecked")
        Tuple<T> result = (Tuple<T>)fallbackFactory.createValues(tag, this, typeStack);
        return result;
    }

    private void addToCache(TypeTag tag, Tuple<?> tuple) {
        cache.put(tag, tuple.getRed(), tuple.getBlack(), tuple.getRedCopy());
    }

    private static Map<Class<?>, Class<?>> createPrimitiveObjectMapper() {
        Map<Class<?>, Class<?>> result = new HashMap<>();
        result.put(boolean.class, Boolean.class);
        result.put(byte.class, Byte.class);
        result.put(char.class, Character.class);
        result.put(double.class, Double.class);
        result.put(float.class, Float.class);
        result.put(int.class, Integer.class);
        result.put(long.class, Long.class);
        result.put(short.class, Short.class);
        return result;
    }
}
