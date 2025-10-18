package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericEmptyValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericEmptyValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        return Optional.empty();
    }
}
