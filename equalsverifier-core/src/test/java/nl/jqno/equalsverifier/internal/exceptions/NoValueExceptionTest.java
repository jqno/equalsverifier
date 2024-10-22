package nl.jqno.equalsverifier.internal.exceptions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class NoValueExceptionTest {

    @Test
    public void descriptionWithoutLabel() {
        TypeTag tag = new TypeTag(String.class);
        NoValueException e = new NoValueException(tag, null);
        assertTrue(e.getDescription().contains("String"));
        assertFalse(e.getDescription().contains("label"));
    }

    @Test
    public void descriptionWithLabel() {
        TypeTag tag = new TypeTag(String.class);
        NoValueException e = new NoValueException(tag, "lbl");
        assertTrue(e.getDescription().contains("String"));
        assertTrue(e.getDescription().contains("label lbl"));
    }
}
