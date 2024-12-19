package nl.jqno.equalsverifier.internal.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

class NoValueExceptionTest {

    @Test
    void description() {
        TypeTag tag = new TypeTag(String.class);
        NoValueException e = new NoValueException(tag);
        assertThat(e.getDescription()).contains("String");
    }
}
