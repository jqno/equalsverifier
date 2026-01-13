package nl.jqno.equalsverifier.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalSanityTest {
    @Test
    void sanity() throws Exception {
        var q = new Foo("foo");

        Field f = Foo.class.getDeclaredField("s");
        f.setAccessible(true);

        assertThatThrownBy(() -> f.set(q, "bar")).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void equalsverifier() {
        // Currently, EqualsVerifier still fails.
        // Eventually this test should be changed so the exception is not expected.
        assertThatThrownBy(() -> EqualsVerifier.forClass(Foo.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("cannot set final field");
    }

    static final class Foo {
        final String s;

        public Foo(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Foo other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }
}
