package nl.jqno.equalsverifier.mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class SanityTest {
    @Test
    void twoMockitoPointsAreUnequal() {
        var a = mock(Point.class);
        var b = mock(Point.class);

        assertThat(a).isNotEqualTo(b);
    }
}
