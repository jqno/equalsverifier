package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class ValueProviderTest {
    @Test
    void errorMessage() {
        var sut = new EmptyValueProvider();
        var tag = new TypeTag(ArrayList.class);
        assertThatThrownBy(() -> sut.provideOrThrow(tag, ""))
                .isInstanceOf(NoValueException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("Could not find a value for ArrayList");
    }

    private static final class EmptyValueProvider implements ValueProvider {
        @Override
        public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
            return Optional.empty();
        }
    }
}
