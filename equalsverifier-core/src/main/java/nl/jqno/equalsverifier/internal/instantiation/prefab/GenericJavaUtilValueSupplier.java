package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericJavaUtilValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaUtilValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(Collection.class)) {
            return list(tag, attributes, ArrayList::new);
        }
        if (is(List.class)) {
            return list(tag, attributes, ArrayList::new);
        }
        if (is(ArrayList.class)) {
            return list(tag, attributes, ArrayList::new);
        }
        return Optional.empty();
    }
}
