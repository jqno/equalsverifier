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

    @SuppressWarnings("unused")
    private static final class SomeClass {

        final int x;
        final int y;
        final String z;

        private SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static abstract class SomeAbstractClass {}
}
