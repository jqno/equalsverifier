package nl.jqno.equalsverifier.internal.reflection.vintage;

import static org.junit.jupiter.api.Assertions.*;

import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.SimpleFactory;
import org.junit.jupiter.api.Test;

public class FactoryCacheTest {

    private static final String SOME_LABEL = "label";
    private static final Class<String> STRING_CLASS = String.class;
    private static final PrefabValueFactory<String> STRING_FACTORY = new SimpleFactory<>(
        "red",
        "blue",
        new String("red")
    );
    private static final Class<Integer> INT_CLASS = int.class;
    private static final PrefabValueFactory<Integer> INT_FACTORY = new SimpleFactory<>(
        42,
        1337,
        42
    );

    private final FactoryCache cache = new FactoryCache();

    @Test
    public void putAndGetTuple() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS));
        assertNull(cache.get(STRING_CLASS, SOME_LABEL));
    }

    @Test
    public void putAndGetTupleWithLabel() {
        cache.put(STRING_CLASS, SOME_LABEL, STRING_FACTORY);
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS, SOME_LABEL));
        assertNull(cache.get(STRING_CLASS));
    }

    @Test
    public void putTwiceAndGetBoth() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        cache.put(INT_CLASS, INT_FACTORY);

        assertEquals(INT_FACTORY, cache.get(INT_CLASS));
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS));
    }

    @Test
    public void putTwiceAndGetBothWithLabel() {
        cache.put(STRING_CLASS, SOME_LABEL, STRING_FACTORY);
        cache.put(INT_CLASS, SOME_LABEL, INT_FACTORY);

        assertEquals(INT_FACTORY, cache.get(INT_CLASS, SOME_LABEL));
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS, SOME_LABEL));
    }

    @Test
    public void putNullAndGetNothingBack() {
        cache.put((Class<?>) null, STRING_FACTORY);
        assertNull(cache.get(null));
    }

    @Test
    public void putNullAndGetNothingBackWithLabel() {
        cache.put((Class<?>) null, SOME_LABEL, STRING_FACTORY);
        assertNull(cache.get(null, SOME_LABEL));
    }

    @Test
    public void contains() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertTrue(cache.contains(STRING_CLASS));
    }

    @Test
    public void containsWithLabel() {
        cache.put(STRING_CLASS, SOME_LABEL, STRING_FACTORY);
        assertTrue(cache.contains(STRING_CLASS, SOME_LABEL));
        assertFalse(cache.contains(STRING_CLASS));
    }

    @Test
    public void doesntContain() {
        assertFalse(cache.contains(STRING_CLASS));
    }

    @Test
    public void doesntContainWithLabel() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertFalse(cache.contains(STRING_CLASS, SOME_LABEL));
    }

    @Test
    public void copy() {
        cache.put(STRING_CLASS, STRING_FACTORY);

        FactoryCache copy = cache.copy();
        copy.put(INT_CLASS, INT_FACTORY);

        assertTrue(copy.contains(STRING_CLASS));

        assertFalse(copy == cache);
        assertFalse(cache.contains(INT_CLASS));
    }

    @Test
    public void merge() {
        FactoryCache a = new FactoryCache();
        a.put(STRING_CLASS, STRING_FACTORY);

        FactoryCache b = new FactoryCache();
        b.put(INT_CLASS, INT_FACTORY);

        FactoryCache combined = a.merge(b);

        assertTrue(combined.contains(STRING_CLASS));
        assertTrue(combined.contains(INT_CLASS));

        assertFalse(a == combined);
        assertFalse(a.contains(INT_CLASS));
        assertFalse(b.contains(STRING_CLASS));
    }
}
