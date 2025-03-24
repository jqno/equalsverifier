package nl.jqno.equalsverifier.internal.instantiation;

import static org.mockito.Mockito.mock;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.Util;

/**
 * Provider of mock prefabricated instances of classes.
 *
 * If Mockito is available on the classpath (or modulepath) of the project, will attempt to use that to construct
 * instances of the given type.
 */
public class MockitoValueProvider implements ValueProvider {

    private final boolean mockitoIsAvailable;

    /**
     * Constructor.
     *
     * @param disable If true, this ValueProvider always returns {@code Optional.empty()}.
     */
    public MockitoValueProvider(boolean disable) {
        this.mockitoIsAvailable = !disable && Util.classForName("org.mockito.Mockito") != null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
        if (!mockitoIsAvailable) {
            return Optional.empty();
        }
        Class<T> type = tag.getType();
        if (type.getPackageName().startsWith("java.")) {
            return Optional.empty();
        }
        if (type.isPrimitive() || type.isArray()) {
            return Optional.empty();
        }

        try {

            var red = mock(type);
            var blue = mock(type);
            if (!red.equals(blue)) {
                return Optional.of((Tuple<T>) new Tuple<>(red, blue, red));
            }
        }
        catch (RuntimeException ignored) {
            // I would prefer to catch MockitoException, but that leads to class loading errors in modules that don't have Mockito
            return Optional.empty();
        }

        return Optional.empty();
    }
}
