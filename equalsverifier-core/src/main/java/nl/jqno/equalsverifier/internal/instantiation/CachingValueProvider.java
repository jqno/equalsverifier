package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class CachingValueProvider implements ValueProvider {

    private final FieldCache fieldCache;
    private final ValueProvider fallback;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "FieldCache is inherently mutable")
    public CachingValueProvider(FieldCache fieldCache, ValueProvider fallback) {
        this.fieldCache = fieldCache;
        this.fallback = fallback;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (fieldCache.contains(fieldName)) {
            return Optional.of(fieldCache.get(fieldName));
        }

        Optional<Tuple<T>> result = fallback.provide(tag, fieldName);
        result.ifPresent(tuple -> fieldCache.put(fieldName, tuple));
        return result;
    }

}
