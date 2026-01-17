package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.valueproviders.Attributes;
import nl.jqno.equalsverifier.internal.valueproviders.ValueProvider;
import nl.jqno.equalsverifier.internal.valueproviders.prefab.GenericValueSupplier;

public final class ScopedValuesValueSupplier<T> extends GenericValueSupplier<T> {

    public ScopedValuesValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get() {
        if (is(ScopedValue.class)) {
            var red = ScopedValue.newInstance();
            var blue = ScopedValue.newInstance();
            return val(red, blue, red);
        }
        return Optional.empty();
    }
}
