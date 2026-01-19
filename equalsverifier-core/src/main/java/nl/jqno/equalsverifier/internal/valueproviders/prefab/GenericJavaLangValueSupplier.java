package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.valueproviders.Attributes;
import nl.jqno.equalsverifier.internal.valueproviders.ValueProvider;

public class GenericJavaLangValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaLangValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Class.class)) {
            return val(Class.class, Object.class, Class.class);
        }
        if (is(Enum.class)) {
            return val(Dummy.RED, Dummy.BLUE, Dummy.RED);
        }
        if (is(Iterable.class)) {
            return collection(ArrayList::new);
        }
        if (is(ThreadLocal.class)) {
            return generic(val -> ThreadLocal.withInitial(() -> val));
        }
        return Optional.empty();
    }

    private enum Dummy {
        RED, BLUE
    }
}
