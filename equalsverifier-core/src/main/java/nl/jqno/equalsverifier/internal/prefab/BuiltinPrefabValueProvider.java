package nl.jqno.equalsverifier.internal.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider for non-generic, built-in prefab values.
 */
public class BuiltinPrefabValueProvider implements ValueProvider {

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (!tag.genericTypes().isEmpty()) {
            return Optional.empty();
        }
        Class<T> type = tag.getType();
        return new PrimitiveValueSupplier<>(type)
                .get()
                .or(new JavaLangValueSupplier<>(type))
                .or(new JavaMathValueSupplier<>(type));
    }
}
