package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class ChainedValueProviderTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();
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

    private final PrefabValueProvider prefab = new PrefabValueProvider();
    private final ChainedValueProvider sut = new ChainedValueProvider(prefab);

    @Test
    public void returnsValueIfMatch() {
        sut.register(intProvider);
        assertEquals(1, sut.provideOrThrow(INT, EMPTY_ATTRIBUTES).getRed());
    }

    @Test
    public void returnsEmptyIfNoMatch() {
        sut.register(stringProvider);
        assertEquals(Optional.empty(), sut.provide(INT, EMPTY_ATTRIBUTES));
    }

    @Test
    public void throwsExceptionIfNoMatch() {
        sut.register(stringProvider);
        ExpectedException
            .when(() -> sut.provideOrThrow(INT, EMPTY_ATTRIBUTES))
            .assertThrows(NoValueException.class)
            .assertDescriptionContains("Could not find a value for int");
    }

    @Test
    public void skipsNonMatchingValue() {
        sut.register(stringProvider, intProvider);
        assertEquals(1, sut.provideOrThrow(INT, EMPTY_ATTRIBUTES).getRed());
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
        sut.register(intProvider, anotherIntProvider);
        assertEquals(1, sut.provideOrThrow(INT, EMPTY_ATTRIBUTES).getRed());
        assertEquals(1, intProvider.called);
        assertEquals(0, anotherIntProvider.called);
    }

    @Test
    public void makesDefensiveCopy() {
        ValueProvider[] providers = { stringProvider };
        sut.register(providers);
        providers[0] = intProvider;
        assertEquals(Optional.empty(), sut.provide(INT, EMPTY_ATTRIBUTES));
    }

    @Test
    public void locksAfterFirstAssignment() {
        sut.register(intProvider);
        ExpectedException
            .when(() -> sut.register(stringProvider))
            .assertThrows(EqualsVerifierInternalBugException.class)
            .assertMessageContains("Provider is locked");
    }

    @Test
    public void cachesWithoutLabelInPrefabValueProvider() {
        sut.register(intProvider);
        sut.provideOrThrow(INT, EMPTY_ATTRIBUTES);
        assertEquals(1, prefab.provideOrThrow(INT, EMPTY_ATTRIBUTES).getRed());
    }

    @Test
    public void cachesWithLabelInPrefabValueProvider() {
        sut.register(intProvider);
        sut.provide(INT, Attributes.labeled("label"));
        assertEquals(1, prefab.provide(INT, Attributes.labeled("label")).get().getRed());
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
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            called++;
            if (tag.getType().equals(type)) {
                return Optional.of((Tuple<T>) values);
            }
            return Optional.empty();
        }
    }
}
