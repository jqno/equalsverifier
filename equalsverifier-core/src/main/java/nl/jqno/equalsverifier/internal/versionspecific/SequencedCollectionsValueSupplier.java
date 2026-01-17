package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.valueproviders.Attributes;
import nl.jqno.equalsverifier.internal.valueproviders.ValueProvider;
import nl.jqno.equalsverifier.internal.valueproviders.prefab.GenericValueSupplier;

public final class SequencedCollectionsValueSupplier<T> extends GenericValueSupplier<T> {

    public SequencedCollectionsValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        return Optional.empty();
    }
}
