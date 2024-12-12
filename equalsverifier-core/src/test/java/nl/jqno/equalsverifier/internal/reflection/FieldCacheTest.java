package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class FieldCacheTest {

    private String stringField = "string";
    private final Tuple<String> stringValues = Tuple.of("red", "blue", "red");

    private String intField = "int";
    private final Tuple<Integer> intValues = Tuple.of(1, 2, 1);

    private FieldCache cache = new FieldCache();

    @Test
    public void putAndGetTuple() {
        cache.put(stringField, stringValues);
        assertEquals(stringValues, cache.get(stringField));
    }

    @Test
    public void putTwiceAndGetBoth() {
        cache.put(stringField, stringValues);
        cache.put(intField, intValues);

        assertEquals(intValues, cache.get(intField));
        assertEquals(stringValues, cache.get(stringField));
    }

    @Test
    public void putNullAndGetNothingBack() {
        cache.put(null, stringValues);
        assertNull(cache.get(null));
    }

    @Test
    public void contains() {
        cache.put(stringField, stringValues);
        assertTrue(cache.contains(stringField));
    }

    @Test
    public void doesntContain() {
        assertFalse(cache.contains(stringField));
    }

    @Test
    public void getFieldNames() {
        assertEquals(Collections.emptySet(), cache.getFieldNames());

        cache.put(stringField, stringValues);
        Set<String> expected = new HashSet<>();
        expected.add(stringField);
        assertEquals(expected, cache.getFieldNames());
    }
}
