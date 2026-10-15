package nl.jqno.equalsverifier;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class PlaceholderTest {
    @Test
    @SuppressWarnings({ "deprecation", "BoxedPrimitiveConstructor" })
    void succeed_whenPlaceholderTestRuns() {
        assertThat(new Integer(1234)).isSameAs(new Integer(1234));
    }
}
