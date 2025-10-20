package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.prefab.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class BuiltinGenericPrefabValueProvider implements ValueProvider {
    private final ValueProvider vp;

    public BuiltinGenericPrefabValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        if (tag.genericTypes().isEmpty()) {
            return Optional.empty();
        }
        var supplier = switch (type.getPackageName()) {
            case "java.lang" -> new GenericJavaLangValueSupplier<>(type, vp);
            case "java.util" -> new GenericJavaUtilValueSupplier<>(type, vp);
            case "java.util.concurrent" -> new GenericJavaUtilConcurrentValueSupplier<>(type, vp);
            default -> new GenericEmptyValueSupplier<>(type, vp);
        };
        return supplier.get(tag, attributes);
    }
}
