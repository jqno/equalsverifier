package nl.jqno.equalsverifier.internal.prefab;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

// CHECKSTYLE OFF: CyclomaticComplexity

/**
 * A ValueProvider for non-generic, built-in prefab values.
 */
public class BuiltinPrefabValueProvider implements ValueProvider {

    private static final Set<Class<?>> EXCEPTIONAL_GENERIC_TYPES = Set.of(Class.class, Constructor.class);

    /** {@inheritDoc}} */
    @Override
    @SuppressFBWarnings(
            value = "DB_DUPLICATE_SWITCH_CLAUSES",
            justification = "Some packages are handled by the same ValueSupplier.")
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        Class<T> type = tag.getType();
        if (!tag.genericTypes().isEmpty() && !EXCEPTIONAL_GENERIC_TYPES.contains(type)) {
            return Optional.empty();
        }
        if (PrimitiveMappers.DEFAULT_WRAPPED_VALUE_MAPPER.containsKey(type)) {
            return new PrimitiveValueSupplier<>(type).get();
        }
        var supplier = switch (type.getPackageName()) {
        case "java.io" -> new JavaIoValueSupplier<>(type);
        case "java.lang" -> new JavaLangValueSupplier<>(type);
        case "java.lang.reflect" -> new JavaLangReflectValueSupplier<>(type);
        case "java.math" -> new JavaMathValueSupplier<>(type);
        case "java.net" -> new JavaNetValueSupplier<>(type);
        case "java.nio" -> new JavaNioValueSupplier<>(type);
        case "java.nio.charset" -> new JavaNioValueSupplier<>(type);
        case "java.time" -> new JavaTimeValueSupplier<>(type);
        case "java.util" -> new JavaUtilValueSupplier<>(type);
        case "java.util.concurrent" -> new JavaUtilConcurrentValueSupplier<>(type);
        case "java.util.concurrent.atomic" -> new JavaUtilConcurrentValueSupplier<>(type);
        case "java.util.concurrent.locks" -> new JavaUtilConcurrentValueSupplier<>(type);
        default -> new OthersValueSupplier<>(type);
        };
        return supplier.get();
    }
}
