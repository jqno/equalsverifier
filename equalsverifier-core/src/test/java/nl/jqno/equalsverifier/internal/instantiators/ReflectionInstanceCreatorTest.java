package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class ReflectionInstanceCreatorTest {

    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void getActualType() {
        var probe = ClassProbe.of(SomeClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        Class<SomeClass> actual = sut.getActualType();

        assertThat(actual).isEqualTo(SomeClass.class);
    }

    @Test
    void getActualType_abstract_exact() {
        var probe = ClassProbe.of(SomeAbstractClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        Class<SomeAbstractClass> actual = sut.getActualType();

        assertThat(actual).isEqualTo(SomeAbstractClass.class);
    }

    @Test
    void instantiate() throws NoSuchFieldException {
        ClassProbe<SomeClass> probe = ClassProbe.of(SomeClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        Field x = SomeClass.class.getDeclaredField("x");
        Field z = SomeClass.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        SomeClass actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    @Test
    void ofExactFailsGracefully_abstract() {
        var probe = ClassProbe.of(SomeAbstractClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        assertThatThrownBy(() -> sut.instantiate(Map.of()))
                .isInstanceOf(ReflectionException.class)
                .hasMessage("Cannot instantiate abstract class " + SomeAbstractClass.class.getName());
    }

    @Test
    void copy() {
        ClassProbe<SomeSubClass> probe = ClassProbe.of(SomeSubClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        SomeClass original = new SomeClass(42, 1337, "yeah");
        SomeSubClass copy = sut.copy(original);

        assertThat(copy.x).isEqualTo(original.x);
        assertThat(copy.y).isEqualTo(original.y);
        assertThat(copy.z).isEqualTo(original.z);
        assertThat(copy.a).isEqualTo(0);
    }

    static class SomeClass {

        final int x;
        final int y;
        final String z;

        public SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static class SomeSubClass extends SomeClass {

        final int a;

        public SomeSubClass(int x, int y, String z, int a) {
            super(x, y, z);
            this.a = a;
        }
    }

    static abstract class SomeAbstractClass {}
}
