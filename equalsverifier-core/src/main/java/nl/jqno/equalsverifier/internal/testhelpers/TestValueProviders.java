package nl.jqno.equalsverifier.internal.testhelpers;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

public final class TestValueProviders {

    private TestValueProviders() {}

    public static ValueProvider empty() {
        return new ValueProvider() {
            @Override
            public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
                return Optional.empty();
            }
        };
    }
}
