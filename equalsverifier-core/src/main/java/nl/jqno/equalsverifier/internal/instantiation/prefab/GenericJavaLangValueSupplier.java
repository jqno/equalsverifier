package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericJavaLangValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaLangValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Iterable.class)) {
            return collection(ArrayList::new);
        }
        if (is(ThreadLocal.class)) {
            return generic(val -> ThreadLocal.withInitial(() -> val));
        }
        return Optional.empty();
    }

}
