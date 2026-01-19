package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class ChainedValueProviderTest {

    private static final String SOME_FIELDNAME = "someFieldName";
    private static final TypeTag INT = new TypeTag(int.class);

    private final SingleTypeValueProvider<Integer> intProvider = new SingleTypeValueProvider<>(int.class, 1, 2, 1);
    private final SingleTypeValueProvider<String> stringProvider =
            new SingleTypeValueProvider<>(String.class, "a", "b", "a");

    private ChainedValueProvider sut;

    @Test
    public void returnsValueIfMatch() {
        sut = new ChainedValueProvider(intProvider);
        assertThat(sut.provideOrThrow(INT, Attributes.named(SOME_FIELDNAME)).red()).isEqualTo(1);
    }

    @Test
    public void returnsEmptyIfNoMatch() {
        sut = new ChainedValueProvider(stringProvider);
        assertThat(sut.provide(INT, Attributes.named(SOME_FIELDNAME))).isEmpty();
    }

    @Test
    public void throwsExceptionIfNoMatch() {
        sut = new ChainedValueProvider(stringProvider);
        assertThatThrownBy(() -> sut.provideOrThrow(INT, Attributes.named(SOME_FIELDNAME)))
                .isInstanceOf(NoValueException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("Could not find a value for int");
    }

    @Test
    public void skipsNonMatchingValue() {
        sut = new ChainedValueProvider(stringProvider, intProvider);
        assertThat(sut.provideOrThrow(INT, Attributes.named(SOME_FIELDNAME)).red()).isEqualTo(1);
        assertThat(stringProvider.called).isEqualTo(1);
        assertThat(intProvider.called).isEqualTo(1);
    }

    @Test
    public void returnsValueFromFirstMatch() {
        var anotherIntProvider = new SingleTypeValueProvider<>(int.class, 1, 2, 1);
        sut = new ChainedValueProvider(intProvider, anotherIntProvider);
        assertThat(sut.provideOrThrow(INT, Attributes.named(SOME_FIELDNAME)).red()).isEqualTo(1);
        assertThat(intProvider.called).isEqualTo(1);
        assertThat(anotherIntProvider.called).isEqualTo(0);
    }

    static class SingleTypeValueProvider<X> implements ValueProvider {

        private final Class<X> type;
        private final Tuple<X> values;
        private int called = 0;

        public SingleTypeValueProvider(Class<X> type, X red, X blue, X redCopy) {
            this.type = type;
            this.values = new Tuple<>(red, blue, redCopy);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            called++;
            if (tag.getType().equals(type)) {
                return Optional.of((Tuple<T>) values);
            }
            return Optional.empty();
        }
    }
}
