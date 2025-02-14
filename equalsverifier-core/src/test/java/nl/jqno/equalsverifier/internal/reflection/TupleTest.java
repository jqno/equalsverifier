package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class TupleTest {

    private final Tuple<String> tuple = new Tuple<>("red", "blue", new String("red"));

    @Test
    void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void redAndRedCopyInvariant() {
        assertThat(tuple.redCopy()).isEqualTo(tuple.red()).isNotSameAs(tuple.red());
    }
}
