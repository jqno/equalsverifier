package nl.jqno.equalsverifier.internal.instantiation;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class ValueProviderTest {
    @Test
    void errorMessage() {
        var sut = new EmptyValueProvider();
        var tag = new TypeTag(ArrayList.class);
        ExpectedException
                .when(() -> sut.provideOrThrow(tag, ""))
                .assertThrows(NoValueException.class)
                .assertDescriptionContains("Could not find a value for ArrayList");
    }

    private static final class EmptyValueProvider implements ValueProvider {
        @Override
        public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
            return Optional.empty();
        }
    }
}
