package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.SimpleFactory;
import org.junit.jupiter.api.Test;

class FactoryCacheTest {

    private static final Class<String> STRING_CLASS = String.class;
    private static final PrefabValueFactory<String> STRING_FACTORY =
            new SimpleFactory<>("red", "blue", new String("red"));
    private static final Class<Integer> INT_CLASS = int.class;
    private static final PrefabValueFactory<Integer> INT_FACTORY = new SimpleFactory<>(42, 1337, 42);

    private final FactoryCache cache = new FactoryCache();

    @Test
    void putAndGetTuple() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertThat(cache.get(STRING_CLASS)).isEqualTo(STRING_FACTORY);
    }

    @Test
    void putTwiceAndGetBoth() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        cache.put(INT_CLASS, INT_FACTORY);

        assertThat(cache.get(INT_CLASS)).isEqualTo(INT_FACTORY);
        assertThat(cache.get(STRING_CLASS)).isEqualTo(STRING_FACTORY);
    }

    @Test
    void putNullAndGetNothingBack() {
        cache.put((Class<?>) null, STRING_FACTORY);
        assertThat(cache.get(null)).isNull();
    }

    @Test
    void contains() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertThat(cache.contains(STRING_CLASS)).isTrue();
    }

    @Test
    void doesntContain() {
        assertThat(cache.contains(STRING_CLASS)).isFalse();
    }

    @Test
    void copy() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        FactoryCache copy = cache.copy();
        copy.put(INT_CLASS, INT_FACTORY);
        assertThat(copy.contains(STRING_CLASS)).isTrue();
        assertThat(copy == cache).isFalse();
        assertThat(cache.contains(INT_CLASS)).isFalse();
    }

    @Test
    void merge() {
        FactoryCache a = new FactoryCache();
        a.put(STRING_CLASS, STRING_FACTORY);

        FactoryCache b = new FactoryCache();
        b.put(INT_CLASS, INT_FACTORY);

        FactoryCache combined = a.merge(b);

        assertThat(combined.contains(STRING_CLASS)).isTrue();
        assertThat(combined.contains(INT_CLASS)).isTrue();

        assertThat(a == combined).isFalse();
        assertThat(a.contains(INT_CLASS)).isFalse();
        assertThat(b.contains(STRING_CLASS)).isFalse();
    }
}
