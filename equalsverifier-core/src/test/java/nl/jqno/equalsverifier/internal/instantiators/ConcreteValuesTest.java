package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class ConcreteValuesTest {

    private final ConcreteValues sut = ConcreteValues.of(Map.of(), false);
    private final ConcreteValues throwingSut = ConcreteValues.of(Map.of(), true);

    @Test
    void of() throws Exception {
        @SuppressWarnings("unused")
        class C {
            private String s;
            private int i;
        }

        var map = Map.<Field, Object>of(C.class.getDeclaredField("s"), "string", C.class.getDeclaredField("i"), 42);
        var actual = ConcreteValues.of(map, false);
        assertThat(actual.getString("s")).isEqualTo("string");
        assertThat(actual.getInt("i")).isEqualTo(42);
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
    void getBoolean_throws() {
        assertThatThrownBy(() -> throwingSut.getBoolean("bool"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field bool.");
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
    void getByte_throws() {
        assertThatThrownBy(() -> throwingSut.getByte("byte"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field byte.");
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
    void getChar_throws() {
        assertThatThrownBy(() -> throwingSut.getChar("char"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field char.");
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
    void getDouble_throws() {
        assertThatThrownBy(() -> throwingSut.getDouble("double"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field double.");
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
    void getFloat_throws() {
        assertThatThrownBy(() -> throwingSut.getFloat("float"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field float.");
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
    void getInt_throws() {
        assertThatThrownBy(() -> throwingSut.getInt("int"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field int.");
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
    void getLong_throws() {
        assertThatThrownBy(() -> throwingSut.getLong("long"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field long.");
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
    void getShort_throws() {
        assertThatThrownBy(() -> throwingSut.getShort("short"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field short.");
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
    void getString_throws() {
        assertThatThrownBy(() -> throwingSut.getString("string"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field string.");
    }

    @Test
    void getGeneric_returnsCorrectType() {
        Point p = new Point(42, 1337);
        sut.put("p", p);
        assertThat(sut.<Point>get("p")).isSameAs(p);
    }

    @Test
    void getGeneric_returnsDefault() {
        assertThat(sut.<Point>get("p")).isNull();
    }

    @Test
    void getGeneric_throws() {
        assertThatThrownBy(() -> throwingSut.<Point>get("p"))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Attempted to get non-existing field p.");
    }
}
