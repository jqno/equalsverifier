package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.prefab.GenericValueSupplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class ScopedValuesValueSupplier<T> extends GenericValueSupplier<T> {

    public ScopedValuesValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get() {
        if (is(ScopedValue.class)) {
            T red = (T) ScopedValue.newInstance();
            T blue = (T) ScopedValue.newInstance();
            return Optional.of(new Tuple<>(red, blue, red));
        }
        return Optional.empty();
    }
}
