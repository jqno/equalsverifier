package nl.jqno.equalsverifier.internal.valueproviders;

import java.util.Optional;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Provider of prefabricated instances of classes that have been provided by the user.
 *
 * Note that this only works for non-generic classes, because this ValueProvider can't determine the generic types or
 * get values for them. For that, we either need withPrefabValuesForField, or the complicated recursion mechanism that
 * is currently provided by VintageValueProvider.
 */
public class UserPrefabValueProvider implements ValueProvider {

    private final UserPrefabValueCaches caches;

    /**
     * Constructor.
     *
     * @param caches The caches that this provider draws from.
     */
    public UserPrefabValueProvider(UserPrefabValueCaches caches) {
        this.caches = caches;
    }

    /**
     * Copies the cache of this PrefabValueProvider into a new instance.
     *
     * @return A copy of this PrefabValueProvider.
     */
    public UserPrefabValueProvider copy() {
        return new UserPrefabValueProvider(caches.copy());
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        Tuple<T> result = attempt(type);
        Class<?> boxed = PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(tag.getType());
        if (result == null && boxed != null) {
            result = attempt(boxed);
        }
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> attempt(Class<?> type) {
        Tuple<Supplier<?>> supplier = caches.getResettable(type);
        if (supplier != null) {
            return (Tuple<T>) new Tuple<>(supplier.red().get(), supplier.blue().get(), supplier.redCopy().get());
        }

        return (Tuple<T>) caches.getPlain(type);
    }
}
