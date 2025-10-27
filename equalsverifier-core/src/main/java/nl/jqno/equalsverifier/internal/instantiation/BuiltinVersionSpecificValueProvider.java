package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.versionspecific.ScopedValuesValueSupplier;
import nl.jqno.equalsverifier.internal.versionspecific.SequencedCollectionsValueSupplier;

public class BuiltinVersionSpecificValueProvider implements ValueProvider {
    private final ValueProvider vp;

    public BuiltinVersionSpecificValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        return new SequencedCollectionsValueSupplier<T>(tag, vp, attributes)
                .get()
                .or(() -> new ScopedValuesValueSupplier<T>(tag, vp, attributes).get());
    }
}
