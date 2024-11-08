package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class BuiltinValueProviderHelper {

    private BuiltinValueProviderHelper() {}

    public static Supplier<Tuple<?>> attempt(
        TypeTag tag,
        Class<?> type,
        Object red,
        Object blue,
        Object redCopy
    ) {
        return () -> {
            if (tag.getType().equals(type)) {
                return Tuple.of(red, blue, redCopy);
            }
            return null;
        };
    }

    public static <T> Supplier<Tuple<?>> attempt(
        TypeTag tag,
        Class<T> type,
        Supplier<Tuple<T>> tuple
    ) {
        return () -> {
            if (tag.getType().equals(type)) {
                Tuple<T> t = tuple.get();
                return Tuple.of(t.getRed(), t.getBlue(), t.getRedCopy());
            }
            return null;
        };
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> Optional<Tuple<T>> or(Supplier<Tuple<?>>... suppliers) {
        return Stream
            .of(suppliers)
            .map(supplier -> (Tuple<T>) supplier.get())
            .filter(tuple -> tuple != null)
            .findFirst();
    }
}
