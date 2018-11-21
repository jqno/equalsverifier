package equalsverifier.prefabvalues;

import equalsverifier.EqualsVerifier;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.utils.Warning;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TupleTest {
    private Tuple<String> tuple = new Tuple<>("red", "black", new String("red"));

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
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
