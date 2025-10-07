package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provides cached instances of classes. On a cache miss, a value is requested from the given fallback ValueProvider. If
 * it returns a value, this value is cached.
 */
public class CachingValueProvider implements ValueProvider {

    private final CacheDecider decider;
    private final FieldCache fieldCache;
    private final ValueProvider fallback;

    /**
     * Constructor.
     *
     * @param decider    Decides whether a value should be cached or not.
     * @param fieldCache The underlying cache of instances.
     * @param fallback   The ValueProvider that provides instances when there's a cache miss.
     */
    public CachingValueProvider(CacheDecider decider, FieldCache fieldCache, ValueProvider fallback) {
        this.decider = decider;
        this.fieldCache = fieldCache;
        this.fallback = fallback;
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        var fieldName = attributes.fieldName();
        if (fieldCache.contains(fieldName, tag)) {
            return Optional.of(fieldCache.get(fieldName, tag));
        }

        var result = fallback.<T>provide(tag, attributes);
        if (decider.canBeCached(tag.getType())) {
            result.ifPresent(tuple -> fieldCache.put(fieldName, tag, tuple));
        }
        return result;
    }
}
