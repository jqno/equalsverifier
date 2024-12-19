package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class TupleTest {

    private Tuple<String> tuple = Tuple.of("red", "blue", new String("red"));

    @Test
    void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void getRed() {
        assertThat(tuple.getRed()).isEqualTo("red");
    }

    @Test
    void getBlue() {
        assertThat(tuple.getBlue()).isEqualTo("blue");
    }

    @Test
    void getRedCopy() {
        assertThat(tuple.getRedCopy()).isEqualTo("red");
    }

    @Test
    void redAndRedCopyInvariant() {
        assertThat(tuple.getRedCopy()).isEqualTo(tuple.getRed()).isNotSameAs(tuple.getRed());
    }
}
