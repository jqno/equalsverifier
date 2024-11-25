package nl.jqno.equalsverifier.internal.reflection.instantiation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.FallbackFactory;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.PrefabValueFactory;
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
