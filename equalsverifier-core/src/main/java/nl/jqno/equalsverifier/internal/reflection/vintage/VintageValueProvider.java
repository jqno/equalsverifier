package nl.jqno.equalsverifier.internal.reflection.vintage;

import java.util.Arrays;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.FallbackFactory;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import nl.jqno.equalsverifier.internal.util.Rethrow;
import org.objenesis.Objenesis;

/**
 * Provider of prefabricated instances of classes, using a "vintage" strategy for doing so.
 *
 * Vintage in this case means that it employs the creation strategy that EqualsVerifier has been
 * using since its inception. This strategy is quite hacky and messy, and other strategies might
 * be preferable.
 */
public class VintageValueProvider implements ValueProvider {

    private final ValueProvider valueProvider;
    private final FactoryCache factoryCache;
    private final PrefabValueFactory<?> fallbackFactory;

    /**
     * Constructor.
     *
     * @param valueProvider Will be used to look up values before they are created.
     * @param cache The values that have already been constructed.
     * @param factoryCache The factories that can be used to create values.
     * @param objenesis To instantiate non-record classes.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "A cache is inherently mutable.")
    public VintageValueProvider(
        ValueProvider valueProvider,
        CachedValueProvider cache,
        FactoryCache factoryCache,
        Objenesis objenesis
    ) {
        this.valueProvider = valueProvider;
        this.factoryCache = factoryCache;
        this.fallbackFactory = new FallbackFactory<>(objenesis);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        return Rethrow.rethrow(() -> Optional.of(giveTuple(tag, attributes)));
    }

    /**
     * Returns the "red" prefabricated value of the specified type.
     *
     * <p>It's always a different value from the "blue" one.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic parameters.
     * @return The "red" prefabricated value.
     */
    public <T> T giveRed(TypeTag tag) {
        return giveRed(tag, Attributes.unlabeled());
    }

    public <T> T giveRed(TypeTag tag, Attributes attributes) {
        return this.<T>giveTuple(tag, attributes).getRed();
    }

    /**
     * Returns the "blue" prefabricated value of the specified type.
     *
     * <p>It's always a different value from the "red" one.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic parameters.
     * @return The "blue" prefabricated value.
     */
    public <T> T giveBlue(TypeTag tag) {
        return giveBlue(tag, Attributes.unlabeled());
    }

    public <T> T giveBlue(TypeTag tag, Attributes attributes) {
        return this.<T>giveTuple(tag, attributes).getBlue();
    }

    /**
     * Returns a shallow copy of the "red" prefabricated value of the specified type.
     *
     * <p>When possible, it's equal to but not the same as the "red" object.
     *
     * @param <T> The return value is cast to this type.
     * @param tag A description of the desired type, including generic parameters.
     * @return A shallow copy of the "red" prefabricated value.
     */
    public <T> T giveRedCopy(TypeTag tag) {
        return giveRedCopy(tag, Attributes.unlabeled());
    }

    public <T> T giveRedCopy(TypeTag tag, Attributes attributes) {
        return this.<T>giveTuple(tag, attributes).getRedCopy();
    }

    /**
     * Returns a prefabricated value of the specified type, that is different from the specified
     * value.
     *
     * @param <T> The type of the value.
     * @param tag A description of the desired type, including generic parameters.
     * @param value A value that is different from the value that will be returned.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return A value that is different from {@code value}.
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    public <T> T giveOther(TypeTag tag, T value, Attributes attributes) {
        Class<T> type = tag.getType();
        if (
            value != null &&
            !type.isAssignableFrom(value.getClass()) &&
            !wraps(type, value.getClass())
        ) {
            throw new ReflectionException("TypeTag does not match value.");
        }

        Tuple<T> tuple = giveTuple(tag, attributes);
        if (tuple.getRed() == null) {
            return null;
        }
        if (type.isArray() && arraysAreDeeplyEqual(tuple.getRed(), value)) {
            return tuple.getBlue();
        }
        if (!type.isArray() && value != null) {
            try {
                // red's equals can potentially call an abstract method
                if (tuple.getRed().equals(value)) {
                    return tuple.getBlue();
                }
            } catch (AbstractMethodError e) {
                return tuple.getRed();
            }
        }
        return tuple.getRed();
    }

    // CHECKSTYLE ON: CyclomaticComplexity

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> giveTuple(TypeTag tag, Attributes attributes) {
        if (attributes.typeStack.contains(tag)) {
            throw new RecursionException(attributes.typeStack);
        }

        Optional<Tuple<T>> provided = valueProvider.provide(tag, attributes);
        if (provided.isPresent()) {
            return provided.get();
        }

        Class<T> type = tag.getType();
        if (attributes.label != null && factoryCache.contains(type, attributes.label)) {
            PrefabValueFactory<T> factory = factoryCache.get(type, attributes.label);
            return factory.createValues(tag, this, attributes);
        }
        if (factoryCache.contains(type)) {
            PrefabValueFactory<T> factory = factoryCache.get(type);
            return factory.createValues(tag, this, attributes);
        }

        return (Tuple<T>) fallbackFactory.createValues(tag, this, attributes);
    }
}
