package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class CachingValueProviderTest {
    private static final String SOME_FIELD = "someFieldname";
    private static final TypeTag SOME_TAG = new TypeTag(String.class);
    private static final TypeTag FALLBACK_TAG = new TypeTag(int.class);
    private static final TypeTag OTHER_TAG = new TypeTag(short.class);

    private CacheDecider decider = type -> true;
    private FieldCache cache = new FieldCache();
    private ValueProvider fallback = new Fallback();
    private CachingValueProvider sut = new CachingValueProvider(decider, cache, fallback);

    @Test
    void noValueAvailable() {
        assertThat(sut.provide(SOME_TAG, SOME_FIELD)).isEmpty();
    }

    @Test
    void useFallbackValueAndCheckCache() {
        assertThat(sut.provide(FALLBACK_TAG, SOME_FIELD)).contains(new Tuple<>(42, 1337, 42));
    }

    @Test
    void useCachedValue() {
        cache.put(SOME_FIELD, SOME_TAG, new Tuple<>(1, 2, 1));
        assertThat(sut.provide(SOME_TAG, SOME_FIELD)).contains(new Tuple<>(1, 2, 1));
    }

    @Test
    void dontUseCachedValueForOtherField() {
        cache.put(SOME_FIELD, SOME_TAG, new Tuple<>("a", "b", "a"));
        assertThat(sut.provide(SOME_TAG, "somethingElse")).isEmpty();
    }

    @Test
    void dontUseCachedValueForSameFieldNameButDifferentType() {
        cache.put(SOME_FIELD, SOME_TAG, new Tuple<>("a", "b", "a"));
        assertThat(sut.provide(OTHER_TAG, SOME_FIELD)).isEmpty();
    }

    @Test
    void dontUseCachedValueForNullFieldName() {
        cache.put(null, SOME_TAG, new Tuple<>("a", "b", "a"));
        assertThat(sut.provide(SOME_TAG, null)).isEmpty();
    }

    @Test
    void dontUseCachedValueForNullFieldType() {
        cache.put(SOME_FIELD, null, new Tuple<>("a", "b", "a"));
        assertThat(sut.provide(SOME_TAG, null)).isEmpty();
    }

    private static final class Fallback implements ValueProvider {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, String fieldName) {
            if (int.class.equals(tag.getType())) {
                return Optional.of((Tuple<T>) new Tuple<>(42, 1337, 42));
            }
            return Optional.empty();
        }

    }
}
