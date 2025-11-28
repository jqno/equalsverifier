package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.prefab.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider for generic Java API classes.
 */
public class BuiltinGenericPrefabValueProvider implements ValueProvider {
    private final ValueProvider vp;

    public BuiltinGenericPrefabValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        var supplier = switch (type.getPackageName()) {
            case "java.lang" -> new GenericJavaLangValueSupplier<T>(tag, vp, attributes);
            case "java.util" -> new GenericJavaUtilValueSupplier<T>(tag, vp, attributes);
            case "java.util.concurrent" -> new GenericJavaUtilConcurrentValueSupplier<T>(tag, vp, attributes);
            case "java.util.concurrent.atomic" ->
                    new GenericJavaUtilConcurrentAtomicValueSupplier<T>(tag, vp, attributes);
            default -> new GenericOthersValueSupplier<T>(tag, vp, attributes);
        };
        return supplier.get();
    }
}
