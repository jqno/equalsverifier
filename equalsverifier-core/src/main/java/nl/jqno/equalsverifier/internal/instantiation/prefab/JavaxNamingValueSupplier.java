package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import javax.naming.Reference;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaxNamingValueSupplier<T> extends ValueSupplier<T> {

    public JavaxNamingValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Reference.class)) {
            return val(new Reference("one"), new Reference("two"), new Reference("one"));
        }

        return Optional.empty();
    }

}
