package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;

public class BuiltinHelper {

    public final TypeTag typeTag;
    public final Attributes attributes;
    public final ValueProvider valueProvider;

    public BuiltinHelper(TypeTag typeTag, Attributes attributes, ValueProvider valueProvider) {
        this.typeTag = typeTag;
        this.attributes = attributes;
        this.valueProvider = valueProvider;
    }

    public static <T, R> Optional<Tuple<R>> tuple(T red, T blue, T redCopy) {
        return Optional.of(Tuple.of(red, blue, redCopy));
    }

    public <R> Optional<Tuple<R>> collection(Supplier<?> empty) {
        TypeTag generic = typeTag.getGenericTypes().size() == 0
            ? new TypeTag(Object.class)
            : typeTag.getGenericTypes().get(0);
        @SuppressWarnings("unchecked")
        Tuple<R> result = valueProvider
            .provideOrThrow(generic, attributes)
            .map(fn -> {
                Collection<Object> instance = (Collection<Object>) empty.get();
                instance.add(fn);
                return (R) instance;
            });
        return Optional.of(result);
    }
}
