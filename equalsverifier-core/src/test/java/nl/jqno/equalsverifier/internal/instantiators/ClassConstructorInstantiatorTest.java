package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class ClassConstructorInstantiatorTest {

    @Test
    void instanceCreator_classWhereConstructorMatchesFields() throws NoSuchFieldException {
        var probe = ClassProbe.of(ConstructorMatchesFields.class);
        var sut = InstantiatorFactory.of(probe, new ObjenesisStd());

        var x = ConstructorMatchesFields.class.getDeclaredField("x");
        var z = ConstructorMatchesFields.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        var actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    static final class ConstructorMatchesFields {
        private final int x;
        private final int y;
        private final String z;

        public ConstructorMatchesFields(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructorMatchesFields other
                    && x == other.x
                    && y == other.y
                    && Objects.equals(z, other.z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}
