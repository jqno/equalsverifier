package nl.jqno.equalsverifier.internal.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class NoValueExceptionTest {

    @Test
    public void description() {
        TypeTag tag = new TypeTag(String.class);
        NoValueException e = new NoValueException(tag);
        assertTrue(e.getDescription().contains("String"));
    }
}
