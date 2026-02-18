package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class ConcreteValuesTest {

    private final ConcreteValues sut = new ConcreteValues();

    @Test
    void of() throws Exception {
        @SuppressWarnings("unused")
        class C {
            private String s;
            private int i;
        }

        var map = Map.<Field, Object>of(C.class.getDeclaredField("s"), "string", C.class.getDeclaredField("i"), 42);
        var actual = ConcreteValues.of(map);
        assertThat(actual.getString("s")).isEqualTo("string");
        assertThat(actual.getInt("i")).isEqualTo(42);
    }

    @Test
    void getRequestedFieldNames() {
        sut.put("a", "a");
        sut.put("b", "b");
        sut.put("c", "c");

        sut.getString("a");
        sut.getString("c");
        var actual = sut.getRequestedFields();

        assertThat(actual).containsExactly("a", "c");
    }

    @Test
    void getBoolean_returnsSameValue() {
        sut.put("bool", true);
        assertThat(sut.getBoolean("bool")).isTrue();
    }

    @Test
    void getBoolean_returnsDefault() {
        assertThat(sut.getBoolean("bool")).isFalse();
    }

    @Test
    void getByte_returnsSameValue() {
        sut.put("byte", (byte) 9);
        assertThat(sut.getByte("byte")).isEqualTo((byte) 9);
    }

    @Test
    void getByte_returnsDefault() {
        assertThat(sut.getByte("byte")).isEqualTo((byte) 0);
    }

    @Test
    void getChar_returnsSameValue() {
        sut.put("char", 'a');
        assertThat(sut.getChar("char")).isEqualTo('a');
    }

    @Test
    void getChar_returnsDefault() {
        assertThat(sut.getChar("char")).isEqualTo('\0');
    }

    @Test
    void getDouble_returnsSameValue() {
        sut.put("double", 2.718);
        assertThat(sut.getDouble("double")).isEqualTo(2.718);
    }

    @Test
    void getDouble_returnsDefault() {
        assertThat(sut.getDouble("double")).isEqualTo(0.0);
    }

    @Test
    void getFloat_returnsSameValue() {
        sut.put("float", 3.14f);
        assertThat(sut.getFloat("float")).isEqualTo(3.14f);
    }

    @Test
    void getFloat_returnsDefault() {
        assertThat(sut.getFloat("float")).isEqualTo(0.0f);
    }

    @Test
    void getInt_returnsSameValue() {
        sut.put("int", 42);
        assertThat(sut.getInt("int")).isEqualTo(42);
    }

    @Test
    void getInt_returnsDefault() {
        assertThat(sut.getInt("int")).isEqualTo(0);
    }

    @Test
    void getLong_returnsSameValue() {
        sut.put("long", 123L);
        assertThat(sut.getLong("long")).isEqualTo(123L);
    }

    @Test
    void getLong_returnsDefault() {
        assertThat(sut.getLong("long")).isEqualTo(0L);
    }

    @Test
    void getShort_returnsSameValue() {
        sut.put("short", (short) 7);
        assertThat(sut.getShort("short")).isEqualTo((short) 7);
    }

    @Test
    void getShort_returnsDefault() {
        assertThat(sut.getShort("short")).isEqualTo((short) 0);
    }

    @Test
    void getString_returnsSameValue() {
        sut.put("string", "test");
        assertThat(sut.getString("string")).isEqualTo("test");
    }

    @Test
    void getString_returnsDefault() {
        assertThat(sut.getString("string")).isNull();
    }

    @Test
    void getGeneric_returnsCorrectType() {
        Point p = new Point(42, 1337);
        sut.put("p", p);
        assertThat(sut.<Point>get("p")).isSameAs(p);
    }

    @Test
    void getObject_returnsDefault() {
        assertThat(sut.<Point>get("p")).isNull();
    }
}
