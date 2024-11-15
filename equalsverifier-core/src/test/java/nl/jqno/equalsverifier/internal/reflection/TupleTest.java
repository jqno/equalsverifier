package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class TupleTest {

    private Tuple<String> tuple = new Tuple<>("red", "blue", new String("red"));

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void getRed() {
        assertEquals("red", tuple.getRed());
    }

    @Test
    public void getBlue() {
        assertEquals("blue", tuple.getBlue());
    }

    @Test
    public void getRedCopy() {
        assertEquals("red", tuple.getRedCopy());
    }

    @Test
    public void redAndRedCopyInvariant() {
        assertEquals(tuple.getRed(), tuple.getRedCopy());
        assertNotSame(tuple.getRed(), tuple.getRedCopy());
    }

    @Test
    public void map() {
        assertEquals(Tuple.of("redx", "bluex", "redx"), tuple.map(s -> s + "x"));
    }

    @Test
    public void combine() {
        Tuple<Integer> ints = Tuple.of(2, 3, 2);
        Tuple<String> actual = Tuple.combine(tuple, ints, (s, n) -> s + n);
        assertEquals(Tuple.of("red2", "blue3", "red2"), actual);
    }
}
