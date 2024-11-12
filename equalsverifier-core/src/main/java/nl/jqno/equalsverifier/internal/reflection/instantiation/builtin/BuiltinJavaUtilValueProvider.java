package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

public class BuiltinJavaUtilValueProvider implements ValueProvider {

    private final ValueProvider valueProvider;

    public BuiltinJavaUtilValueProvider(ValueProvider valueProvider) {
        this.valueProvider = valueProvider;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        BuiltinHelper h = new BuiltinHelper(tag, attributes, valueProvider);

        if (tag.matches(Collection.class)) {
            return h.collection(ArrayList::new);
        }

        return Optional.empty();
    }
}
