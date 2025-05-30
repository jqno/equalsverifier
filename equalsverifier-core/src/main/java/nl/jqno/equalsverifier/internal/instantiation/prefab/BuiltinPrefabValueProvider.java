package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

// CHECKSTYLE OFF: CyclomaticComplexity

/**
 * A ValueProvider for non-generic, built-in prefab values.
 */
public class BuiltinPrefabValueProvider implements ValueProvider {

    private static final Set<Class<?>> EXCEPTIONAL_GENERIC_TYPES =
            Set.of(Class.class, Constructor.class, SynchronousQueue.class);

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        Class<T> type = tag.getType();
        if (!tag.genericTypes().isEmpty() && !EXCEPTIONAL_GENERIC_TYPES.contains(type)) {
            return Optional.empty();
        }
        if (PrimitiveMappers.DEFAULT_WRAPPED_VALUE_MAPPER.containsKey(type)) {
            return new PrimitiveValueSupplier<>(type).get();
        }
        var supplier = switch (type.getPackageName()) {
            case "java.awt" -> new JavaAwtValueSupplier<>(type);
            case "java.awt.color" -> new JavaAwtValueSupplier<>(type);
            case "java.io" -> new JavaIoValueSupplier<>(type);
            case "java.lang" -> new JavaLangValueSupplier<>(type);
            case "java.lang.reflect" -> new JavaLangReflectValueSupplier<>(type);
            case "java.math" -> new JavaMathValueSupplier<>(type);
            case "java.net" -> new JavaNetValueSupplier<>(type);
            case "java.nio" -> new JavaNioValueSupplier<>(type);
            case "java.nio.charset" -> new JavaNioValueSupplier<>(type);
            case "java.rmi.dgc" -> new JavaRmiValueSupplier<>(type);
            case "java.rmi.server" -> new JavaRmiValueSupplier<>(type);
            case "java.sql" -> new JavaSqlValueSupplier<>(type);
            case "java.text" -> new JavaTextValueSupplier<>(type);
            case "java.time" -> new JavaTimeValueSupplier<>(type);
            case "java.util" -> new JavaUtilValueSupplier<>(type);
            case "java.util.concurrent" -> new JavaUtilConcurrentValueSupplier<>(type);
            case "java.util.concurrent.atomic" -> new JavaUtilConcurrentValueSupplier<>(type);
            case "java.util.concurrent.locks" -> new JavaUtilConcurrentValueSupplier<>(type);
            case "javax.naming" -> new JavaxNamingValueSupplier<>(type);
            case "javax.swing.tree" -> new JavaxSwingValueSupplier<>(type);
            default -> new OthersValueSupplier<>(type);
        };
        return supplier.get();
    }
}
