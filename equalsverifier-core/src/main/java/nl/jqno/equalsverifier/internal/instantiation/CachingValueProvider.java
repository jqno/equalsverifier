package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provides cached instances of classes. On a cache miss, a value is requested from the given fallback ValueProvider. If
 * it returns a value, this value is cached.
 */
public class CachingValueProvider implements ValueProvider {

    private final FieldCache fieldCache;
    private final ValueProvider fallback;

    /**
     * Constructor.
     *
     * @param fieldCache The underlying cache of instances.
     * @param fallback   The ValueProvider that provides instances when there's a cache miss.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "FieldCache is inherently mutable")
    public CachingValueProvider(FieldCache fieldCache, ValueProvider fallback) {
        this.fieldCache = fieldCache;
        this.fallback = fallback;
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (fieldCache.contains(fieldName)) {
            return Optional.of(fieldCache.get(fieldName));
        }

        var result = fallback.<T>provide(tag, fieldName);
        result.ifPresent(tuple -> fieldCache.put(fieldName, tuple));
        return result;
    }
}
