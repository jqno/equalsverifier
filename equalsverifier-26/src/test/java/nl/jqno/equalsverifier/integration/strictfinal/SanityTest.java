package nl.jqno.equalsverifier.integration.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class SanityTest {
    @Test
    void sanity() throws Exception {
        var q = new FinalNonConstructable("10");

        Field f = FinalNonConstructable.class.getDeclaredField("i");
        f.setAccessible(true);

        assertThatThrownBy(() -> f.set(q, 42)).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void equalsverifier() {
        // Currently, EqualsVerifier still fails.
        // Eventually this test should be changed so the exception is not expected.
        assertThatThrownBy(() -> EqualsVerifier.forClass(FinalNonConstructable.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate FinalNonConstructable")
                .hasMessageContaining("Use #withFactory()");
    }

    static final class FinalNonConstructable {
        private final int i;

        public FinalNonConstructable(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FinalNonConstructable other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
