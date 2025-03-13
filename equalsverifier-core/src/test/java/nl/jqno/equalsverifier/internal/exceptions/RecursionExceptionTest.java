package nl.jqno.equalsverifier.internal.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class RecursionExceptionTest {

    @Test
    void descriptionContainsAllTypes() {
        var stack = new LinkedHashSet<TypeTag>();
        stack.add(new TypeTag(String.class));
        stack.add(new TypeTag(Point.class));
        stack.add(new TypeTag(Object.class));

        String message = new RecursionException(stack).getMessage();

        for (TypeTag tag : stack) {
            assertThat(message).contains(tag.toString());
        }
    }
}
