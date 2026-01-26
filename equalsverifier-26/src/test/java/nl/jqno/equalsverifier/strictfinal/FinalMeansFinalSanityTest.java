package nl.jqno.equalsverifier.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalSanityTest {
    @Test
    void sanity() throws Exception {
        var q = new Uninstantiable(List.of("foo"), "bar");

        Field f = Uninstantiable.class.getDeclaredField("s");
        f.setAccessible(true);

        assertThatThrownBy(() -> f.set(q, "bar")).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    void equalsverifier() {
        // Currently, EqualsVerifier still fails.
        // Eventually this test should be changed so the exception is not expected.
        assertThatThrownBy(() -> EqualsVerifier.forClass(Uninstantiable.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Not allowed to reflectively set final field Uninstantiable.s")
                .hasMessageContaining("Use #withFactory()");
    }
}
