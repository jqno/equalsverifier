package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import java.util.*;
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
        if (tag.matches(ArrayList.class)) {
            return h.collection(ArrayList::new);
        }
        if (tag.matches(LinkedList.class)) {
            return h.collection(LinkedList::new);
        }
        if (tag.matches(Vector.class)) {
            return h.collection(Vector::new);
        }
        if (tag.matches(Stack.class)) {
            return h.collection(Stack::new);
        }

        return Optional.empty();
    }
}
