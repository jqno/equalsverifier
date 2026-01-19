package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstantiatorTest {

    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void copy() {
        var probe = ClassProbe.of(SomeSubClass.class);
        var sut = InstantiatorFactory.of(probe, objenesis);

        var original = new SomeClass(42, 1337, "yeah");
        var copy = sut.copy(original);

        assertThat(copy.x).isEqualTo(original.x);
        assertThat(copy.y).isEqualTo(original.y);
        assertThat(copy.z).isEqualTo(original.z);
        assertThat(copy.a).isEqualTo(0);
    }

    private static class SomeClass {

        final int x;
        final int y;
        final String z;

        private SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    @SuppressWarnings("unused")
    private static final class SomeSubClass extends SomeClass {

        final int a;

        private SomeSubClass(int x, int y, String z, int a) {
            super(x, y, z);
            this.a = a;
        }
    }
}
