package nl.jqno.equalsverifier.internal.prefabvalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class TupleTest {
    private Tuple<String> tuple = new Tuple<>("red", "black", new String("red"));

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void getRed() {
        assertEquals("red", tuple.getRed());
    }

    @Test
    public void getBlack() {
        assertEquals("black", tuple.getBlack());
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
}
