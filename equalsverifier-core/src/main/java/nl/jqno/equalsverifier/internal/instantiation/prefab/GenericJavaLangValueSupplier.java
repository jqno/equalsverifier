package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericJavaLangValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaLangValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(Iterable.class)) {
            return collection(tag, attributes, ArrayList::new);
        }
        if (is(ThreadLocal.class)) {
            return generic(tag, attributes, val -> (T) ThreadLocal.withInitial(() -> val));
        }
        return Optional.empty();
    }

}
