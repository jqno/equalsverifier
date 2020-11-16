package nl.jqno.equalsverifier.internal.prefabvalues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CacheTest {
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final Tuple<String> STRING_TUPLE = new Tuple<>("red", "blue", new String("red"));
    private static final TypeTag INT_TAG = new TypeTag(int.class);
    private static final Tuple<Integer> INT_TUPLE = new Tuple<>(42, 1337, 42);

    private Cache cache = new Cache();

    @Test
    public void putAndGetTuple() {
        cache.put(
                STRING_TAG,
                STRING_TUPLE.getRed(),
                STRING_TUPLE.getBlue(),
                STRING_TUPLE.getRedCopy());
        assertEquals(STRING_TUPLE, cache.<String>getTuple(STRING_TAG));
    }

    @Test
    public void putTwiceAndGetBoth() {
        cache.put(
                STRING_TAG,
                STRING_TUPLE.getRed(),
                STRING_TUPLE.getBlue(),
                STRING_TUPLE.getRedCopy());
        cache.put(INT_TAG, INT_TUPLE.getRed(), INT_TUPLE.getBlue(), INT_TUPLE.getRedCopy());

        assertEquals(INT_TUPLE, cache.getTuple(INT_TAG));
        assertEquals(STRING_TUPLE, cache.getTuple(STRING_TAG));
    }

    @Test
    public void contains() {
        cache.put(
                STRING_TAG,
                STRING_TUPLE.getRed(),
                STRING_TUPLE.getBlue(),
                STRING_TUPLE.getRedCopy());
        assertTrue(cache.contains(STRING_TAG));
    }

    @Test
    public void doesntContain() {
        assertFalse(cache.contains(STRING_TAG));
    }
}
