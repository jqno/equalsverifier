package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class CachingValueProviderTest {
    private static final String SOME_FIELD = "someFieldname";

    private FieldCache cache = new FieldCache();
    private ValueProvider fallback = new Fallback();
    private CachingValueProvider sut = new CachingValueProvider(cache, fallback);

    @Test
    void noValueAvailable() {
        assertThat(sut.provide(new TypeTag(String.class), SOME_FIELD)).isEmpty();
    }

    @Test
    void useFallbackValueAndCheckCache() {
        assertThat(sut.provide(new TypeTag(int.class), SOME_FIELD)).contains(Tuple.of(42, 1337, 42));
    }

    @Test
    void useCachedValue() {
        cache.put(SOME_FIELD, Tuple.of(1, 2, 1));
        assertThat(sut.provide(new TypeTag(int.class), SOME_FIELD)).contains(Tuple.of(1, 2, 1));
    }

    @Test
    void dontUseCachedValueForOtherField() {
        cache.put(SOME_FIELD, Tuple.of("a", "b", "a"));
        assertThat(sut.provide(new TypeTag(String.class), "somethingElse")).isEmpty();
    }

    @Test
    void dontUseCachedValueForNullField() {
        cache.put(null, Tuple.of("a", "b", "a"));
        assertThat(sut.provide(new TypeTag(String.class), null)).isEmpty();
    }

    private static final class Fallback implements ValueProvider {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
            if (int.class.equals(tag.getType())) {
                return Optional.of((Tuple<T>) Tuple.of(42, 1337, 42));
            }
            return Optional.empty();
        }

    }
}
