package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import static nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinValueProviderHelper.attempt;
import static nl.jqno.equalsverifier.internal.reflection.instantiation.builtin.BuiltinValueProviderHelper.or;

import java.util.*;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

public class BuiltinJavaUtilValueProvider implements ValueProvider {

    private final ValueProvider valueProvider;

    public BuiltinJavaUtilValueProvider(ValueProvider valueProvider) {
        this.valueProvider = valueProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(
        TypeTag tag,
        String label,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return or(
            attempt(
                tag,
                Collection.class,
                () ->
                    build(
                        () -> new ArrayList<>(),
                        tag.getGenericTypes().size() > 0
                            ? tag.getGenericTypes().get(0)
                            : new TypeTag(Object.class),
                        typeStack
                    )
            )
        );
    }

    private <T, C extends Collection<T>> Tuple<C> build(
        Supplier<C> empty,
        TypeTag generic,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return valueProvider
            .<T>provide(generic, null, typeStack)
            .orElseThrow(() -> new NoValueException(generic))
            .map(fn -> {
                C instance = empty.get();
                instance.add(fn);
                return instance;
            });
    }
}
