package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class FieldCacheTest {

    private final String stringField = "string";
    private final TypeTag stringTag = new TypeTag(String.class);
    private final Tuple<String> stringValues = Tuple.of("red", "blue", "red");

    private final String intField = "int";
    private final TypeTag intTag = new TypeTag(int.class);
    private final Tuple<Integer> intValues = Tuple.of(1, 2, 1);

    private final FieldCache cache = new FieldCache();

    @Test
    void putAndGetTuple() {
        cache.put(stringField, stringTag, stringValues);
        assertThat(cache.get(stringField, stringTag)).isEqualTo(stringValues);
    }

    @Test
    void putAndGetDifferentTypeAndGetNothingBack() {
        cache.put(stringField, stringTag, stringValues);
        assertThat(cache.get(stringField, intTag)).isNull();
    }

    @Test
    void putTwiceAndGetBoth() {
        cache.put(stringField, stringTag, stringValues);
        cache.put(intField, intTag, intValues);

        assertThat(cache.get(intField, intTag)).isEqualTo(intValues);
        assertThat(cache.get(stringField, stringTag)).isEqualTo(stringValues);
    }

    @Test
    void putNullNameAndGetNothingBack() {
        cache.put(null, stringTag, stringValues);
        assertThat(cache.get(null, stringTag)).isNull();
    }

    @Test
    void putNullTypeAndGetNothingBack() {
        cache.put(stringField, null, stringValues);
        assertThat(cache.get(stringField, null)).isNull();
    }

    @Test
    void contains() {
        cache.put(stringField, stringTag, stringValues);
        assertThat(cache.contains(stringField, stringTag)).isTrue();
    }

    @Test
    void doesntContainFieldName() {
        assertThat(cache.contains(stringField, stringTag)).isFalse();
    }

    @Test
    void doesntContainFieldType() {
        cache.put(stringField, stringTag, stringValues);
        assertThat(cache.contains(stringField, intTag)).isFalse();
    }

    @Test
    void getFieldNames() {
        assertThat(cache.getFieldNames()).isEqualTo(Collections.emptySet());

        cache.put(stringField, stringTag, stringValues);
        Set<String> expected = new HashSet<>();
        expected.add(stringField);
        assertThat(cache.getFieldNames()).isEqualTo(expected);
    }
}
