package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.jupiter.api.Test;

class RecursionDetectingValueProviderTest {

    private RecursionDetectingValueProvider sut = new RecursionDetectingValueProvider();

    @Test
    void happyPath() {
        sut.setValueProvider(new SimpleValueProvider());
        var actual = sut.provide(new TypeTag(String.class), Attributes.empty());
        assertThat(actual).isEqualTo(Optional.of(new Tuple<>("red", "blue", "red")));
    }

    @Test
    void recursion() {
        sut.setValueProvider(new RecursiveValueProvider());
        assertThatExceptionOfType(RecursionException.class)
                .isThrownBy(() -> sut.provide(new TypeTag(Node.class), Attributes.empty()));
    }

    private static final class SimpleValueProvider implements ValueProvider {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            if (tag.getType().equals(String.class)) {
                return Optional.of((Tuple<T>) new Tuple<>("red", "blue", "red"));
            }
            return Optional.empty();
        }
    }

    private final class RecursiveValueProvider implements ValueProvider {

        @Override
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            if (tag.getType().equals(Node.class)) {
                return sut.<T>provide(new TypeTag(Node.class), attributes);
            }
            return Optional.empty();
        }
    }
}
