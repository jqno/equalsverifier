package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class ConcreteValuesTest {

    private final ConcreteValues sut = new ConcreteValues();

    @Test
    void getBoolean_returnsCorrectValue() {
        sut.put("bool", true);
        assertThat(sut.getBoolean("bool")).isTrue();
    }

    @Test
    void getByte_returnsCorrectValue() {
        sut.put("byte", (byte) 9);
        assertThat(sut.getByte("byte")).isEqualTo((byte) 9);
    }

    @Test
    void getChar_returnsCorrectValue() {
        sut.put("char", 'a');
        assertThat(sut.getChar("char")).isEqualTo('a');
    }

    @Test
    void getDouble_returnsCorrectValue() {
        sut.put("double", 2.718);
        assertThat(sut.getDouble("double")).isEqualTo(2.718);
    }

    @Test
    void getFloat_returnsCorrectValue() {
        sut.put("float", 3.14f);
        assertThat(sut.getFloat("float")).isEqualTo(3.14f);
    }

    @Test
    void getInt_returnsCorrectValue() {
        sut.put("int", 42);
        assertThat(sut.getInt("int")).isEqualTo(42);
    }

    @Test
    void getLong_returnsCorrectValue() {
        sut.put("long", 123L);
        assertThat(sut.getLong("long")).isEqualTo(123L);
    }

    @Test
    void getShort_returnsCorrectValue() {
        sut.put("short", (short) 7);
        assertThat(sut.getShort("short")).isEqualTo((short) 7);
    }

    @Test
    void getString_returnsCorrectValue() {
        sut.put("string", "test");
        assertThat(sut.getString("string")).isEqualTo("test");
    }

    @Test
    void getGeneric_returnsCorrectType() {
        Point p = new Point(42, 1337);
        sut.put("p", p);
        assertThat(sut.<Point>get("p")).isSameAs(p);
    }
}
