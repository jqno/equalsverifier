package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

public class SimpleFactoryTest {
    private SimpleFactory<String> factory = new SimpleFactory<>("red", "blue", new String("red"));

    @Test
    public void createRed() {
        assertEquals("red", factory.createValues(null, null, null).getRed());
    }

    @Test
    public void createBlue() {
        assertEquals("blue", factory.createValues(null, null, null).getBlue());
    }

    @Test
    public void redCopy() {
        String redCopy = factory.createValues(null, null, null).getRedCopy();
        assertEquals("red", redCopy);
        assertNotSame("red", redCopy);
    }
}
