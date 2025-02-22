package nl.jqno.equalsverifier.internal.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * A ValueProvider for non-generic, built-in prefab values.
 */
public class BuiltinPrefabValueProvider implements ValueProvider {

    /** {@inheritDoc}} */
    @Override
    @SuppressFBWarnings(
            value = "DB_DUPLICATE_SWITCH_CLAUSES",
            justification = "Some packages are handled by the same ValueSupplier.")
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (!tag.genericTypes().isEmpty()) {
            return Optional.empty();
        }
        Class<T> type = tag.getType();
        if (PrimitiveMappers.DEFAULT_WRAPPED_VALUE_MAPPER.containsKey(type)) {
            return new PrimitiveValueSupplier<>(type).get();
        }
        return switch (type.getPackageName()) {
        case "java.lang" -> new JavaLangValueSupplier<>(type).get();
        case "java.math" -> new JavaMathValueSupplier<>(type).get();
        case "java.nio" -> new JavaNioValueSupplier<>(type).get();
        case "java.nio.charset" -> new JavaNioValueSupplier<>(type).get();
        case "java.util" -> new JavaUtilValueSupplier<>(type).get();
        default -> Optional.empty();
        };
    }
}
