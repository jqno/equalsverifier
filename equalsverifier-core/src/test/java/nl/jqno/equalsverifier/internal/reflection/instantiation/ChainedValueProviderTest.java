package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class ChainedValueProviderTest {

    private static final TypeTag INT = new TypeTag(int.class);

    private final SingleTypeValueProvider<Integer> intProvider = new SingleTypeValueProvider<>(
        int.class,
        1,
        2,
        1
    );
    private final SingleTypeValueProvider<String> stringProvider = new SingleTypeValueProvider<>(
        String.class,
        "a",
        "b",
        "a"
    );

    private ChainedValueProvider sut;

    @Test
    public void returnsValueIfMatch() {
        sut = new ChainedValueProvider(intProvider);
        assertEquals(1, sut.provide(INT).get().getRed());
    }

    @Test
    public void returnsEmptyIfNoMatch() {
        sut = new ChainedValueProvider(stringProvider);
        assertEquals(Optional.empty(), sut.provide(INT));
    }

    @Test
    public void skipsNonMatchingValue() {
        sut = new ChainedValueProvider(stringProvider, intProvider);
        assertEquals(1, sut.provide(INT).get().getRed());
        assertEquals(1, stringProvider.called);
        assertEquals(1, intProvider.called);
    }

    @Test
    public void returnsValueFromFirstMatch() {
        SingleTypeValueProvider<Integer> anotherIntProvider = new SingleTypeValueProvider<Integer>(
            int.class,
            1,
            2,
            1
        );
        sut = new ChainedValueProvider(intProvider, anotherIntProvider);
        assertEquals(1, sut.provide(INT).get().getRed());
        assertEquals(1, intProvider.called);
        assertEquals(0, anotherIntProvider.called);
    }

    static class SingleTypeValueProvider<X> implements ValueProvider {

        private final Class<X> type;
        private final Tuple<X> values;
        private int called = 0;

        public SingleTypeValueProvider(Class<X> type, X red, X blue, X redCopy) {
            this.type = type;
            this.values = Tuple.of(red, blue, redCopy);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag) {
            called++;
            if (tag.getType().equals(type)) {
                return Optional.of((Tuple<T>) values);
            }
            return Optional.empty();
        }
    }
}
