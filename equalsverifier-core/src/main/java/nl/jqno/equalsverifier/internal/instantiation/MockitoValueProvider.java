package nl.jqno.equalsverifier.internal.instantiation;

import static org.mockito.Mockito.mock;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.Util;

public class MockitoValueProvider implements ValueProvider {

    private final boolean mockitoIsAvailable;

    public MockitoValueProvider() {
        this.mockitoIsAvailable = Util.classForName("org.mockito.Mockito") != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (!mockitoIsAvailable) {
            return Optional.empty();
        }

        Class<T> type = tag.getType();
        var red = mock(type);
        var blue = mock(type);
        if (!red.equals(blue)) {
            return Optional.of((Tuple<T>) new Tuple<>(red, blue, red));
        }

        return Optional.empty();
    }
}
