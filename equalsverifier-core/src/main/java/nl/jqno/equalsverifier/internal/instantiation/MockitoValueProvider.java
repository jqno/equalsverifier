package nl.jqno.equalsverifier.internal.instantiation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.MockitoException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of mock prefabricated instances of classes.
 *
 * If Mockito is available on the classpath (or modulepath) of the project, will attempt to use that to construct
 * instances of the given type.
 */
public class MockitoValueProvider implements ValueProvider {

    private final boolean disable;

    /**
     * Constructor.
     *
     * @param disable If true, this ValueProvider always returns {@code Optional.empty()}.
     */
    public MockitoValueProvider(boolean disable) {
        this.disable = disable;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        if (disable) {
            return Optional.empty();
        }
        Class<T> type = tag.getType();
        if (type.getPackageName().startsWith("java.")) {
            return Optional.empty();
        }
        if (type.isPrimitive() || type.isArray() || type.isEnum()) {
            return Optional.empty();
        }

        try {
            var red = buildMock(type, attributes.fieldName(), "red");
            var blue = buildMock(type, attributes.fieldName(), "blue");
            if (!red.equals(blue) && red.hashCode() != blue.hashCode()) {
                // Only return mocked values if they're properly unequal.
                // They should be, but I think this is undocumented behaviour, so best to be safe.
                return Optional.of((Tuple<T>) new Tuple<>(red, blue, red));
            }
        }
        catch (RuntimeException ignored) {
            // I would prefer to catch MockitoException, but that leads to class loading errors in modules that don't have Mockito
            return Optional.empty();
        }

        return Optional.empty();
    }

    private <T> T buildMock(Class<T> type, String fieldName, String color) {
        return mock(type, withSettings().defaultAnswer(invocation -> {
            if (invocation.getMethod().getName().equals("toString")) {
                return "[" + color + " mock for " + type.getSimpleName() + "]";
            }
            // Throw an exception on any method except toString (which is stubbed above) and equals and hashCode
            throw new MockitoException(fieldName, type.getSimpleName(), invocation.getMethod().getName());
        }));
    }
}
