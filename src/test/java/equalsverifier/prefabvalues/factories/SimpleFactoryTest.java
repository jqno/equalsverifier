package equalsverifier.prefabvalues.factories;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class SimpleFactoryTest {
    private SimpleFactory<String> factory = new SimpleFactory<>("red", "black", new String("red"));

    @Test
    public void createRed() {
        assertEquals("red", factory.createValues(null, null, null).getRed());
    }

    @Test
    public void createBlack() {
        assertEquals("black", factory.createValues(null, null, null).getBlack());
    }

    @Test
    public void redCopy() {
        String redCopy = factory.createValues(null, null, null).getRedCopy();
        assertEquals("red", redCopy);
        assertNotSame("red", redCopy);
    }
}
