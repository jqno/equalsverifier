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

    @Test
    void map() {
        var actual = tuple.map(s -> s.length());
        assertThat(actual).isEqualTo(new Tuple<>(3, 4, 3));
    }

    @Test
    void swapBlueIfEqualToRed_not_EqualToRed() {
        var actual = tuple.swapBlueIfEqualToRed(() -> "green");
        assertThat(actual).isSameAs(tuple);
    }

    @Test
    void swapBlueIfEqualToRed_equalToRed() {
        var equal = new Tuple<>("red", "red", new String("red"));
        var actual = equal.swapBlueIfEqualToRed(() -> "green");
        assertThat(actual).isEqualTo(new Tuple<>("red", "green", new String("red")));
    }
}
