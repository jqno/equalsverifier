package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.prefab.GenericValueSupplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class ScopedValuesValueSupplier<T> extends GenericValueSupplier<T> {

    public ScopedValuesValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(ScopedValue.class)) {
            T red = (T) ScopedValue.newInstance();
            T blue = (T) ScopedValue.newInstance();
            return Optional.of(new Tuple<>(red, blue, red));
        }
        return Optional.empty();
    }
}
