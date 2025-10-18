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
            return valGeneric1(tag, attributes, val -> {
                var r = new ArrayList<>();
                r.add(val);
                return (T) r;
            });
        }
        if (is(List.class)) {
            return valGeneric1(tag, attributes, val -> {
                var r = new ArrayList<>();
                r.add(val);
                return (T) r;
            });
        }
        if (is(ArrayList.class)) {
            return valGeneric1(tag, attributes, val -> {
                var r = new ArrayList<>();
                r.add(val);
                return (T) r;
            });
        }
        return Optional.empty();
    }
}
